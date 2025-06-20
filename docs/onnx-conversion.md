# End to End ONNX Conversion for Neural Retrieval Models
This MD file will describe steps to convert particular PyTorch models (i.e., [SPLADE++](https://doi.org/10.1145/3477495.3531857)) to ONNX models and options to further optimize compute graph for transformer-based models. For more details on how does ONNX Conversion work and how to optimize the compute graph, please refer to [ONNX Tutorials](https://github.com/onnx/tutorials#services).

The SPLADE model takes a text input and generates sparse token-level representations as output, where each token is assigned a weight, enabling efficient information retrieval. A more in depth explantation can be found [here](https://www.pinecone.io/learn/splade/).

All scripts are available for reference under in the following directory:
```
src/main/python/onnx
```

The following tutorial will be using the scripts available in the above directory directly.

## Setups

pip requirements:
```bash
pip install torch transformers onnx onnxruntime onnxoptimizer
```

versions tested:
```bash
onnx                         1.17.0
onnxoptimizer                0.3.13
onnxruntime                  1.20.1
```

## Converting from PyTorch models to ONNX model
The following sections will describe how to convert a transformer-based model to ONNX model. 
We use the SPLADE++ model as an example. The steps are as follows:  

### Run the End to End PyTorch to ONNX Conversion with Validation
Loading and running is done easily with argparse in the following script:

```
src/main/python/onnx/convert_hf_model_to_onnx.py
```

All that needs to be provided is the model_name as seen on Hugging Face. In our example, we will be loading the SPLADE++ Ensemble Distil model found here:
```
naver/splade-cocondenser-ensembledistil
```

To run the script and produce the onnx model with validation, run the following sequence of commands:
```bash
cd src/main/python/onnx
# Now run the script with validation
python convert_hf_model_to_onnx.py --model_name naver/splade-cocondenser-ensembledistil --text "what is AI?"
```

The script will now:
1. Convert the PyTorch model to ONNX format
2. Run inference on both models with the test input ("what is AI?" by default)
3. Compute and report the L1 norm difference between PyTorch and ONNX outputs
4. Validate that the difference is below an acceptable threshold (1e-2)

Example output:
```
Some weights of BertModel were not initialized from the model checkpoint at naver/splade-cocondenser-ensembledistil and are newly initialized: ['bert.pooler.dense.bias', 'bert.pooler.dense.weight']
You should probably TRAIN this model on a down-stream task to be able to use it for predictions and inference.
Model converted and saved to models/splade-cocondenser-ensembledistil.onnx
ONNX model checked successfully
Vocabulary saved to models/splade-cocondenser-ensembledistil-vocab.txt
ONNX model output shape: (1, 6, 768)
ONNX model test run successful
L1 difference between PyTorch and ONNX outputs: 0.009487475268542767
ONNX conversion validated successfully!
```

> Note: For transformer models like SPLADE, the validation applies ReLU activation to both PyTorch and ONNX outputs before computing the L1 difference, since these models often use ReLU activation in their architecture. This ensures accurate validation of the conversion process.

If the L1 difference exceeds the threshold, a warning will be displayed indicating potential conversion issues.

### Getting Output Specification from the Model

Since we want our script to be generic and able to load any Hugging Face model with simplicity, we make it possible to fetch all required information from the model output structure with the following code:

```python
with torch.no_grad():
    outputs = model(**test_input) # We provide the text test input and the model object, via the function parameters.

if isinstance(outputs, torch.Tensor):
    return ['output']
elif hasattr(outputs, 'last_hidden_state'):
    return ['last_hidden_state']
elif hasattr(outputs, 'logits'):
    return ['logits']
else:
    return [f'output_{i}' for i in range(len(outputs))]
```

This is modularized in the function definition for ```get_model_output_names(model, test_input)```.

---

Another important component is being able to specify the dynamic axes of the input and output tensors. This is achieved by the following code:

```python
dynamic_axes = {}
for name in input_names:
    dynamic_axes[name] = {0: 'batch_size', 1: 'sequence'}
for name in output_names:
    dynamic_axes[name] = {0: 'batch_size', 1: 'sequence'}
return dynamic_axes
```

This is important for the generalization of input and output shapes to be captured for any model that is provided. This is once again modularized in the function defintion for ```get_dynamic_axes(input_names, output_names)```.

### Converting the Model to ONNX Format

As the final step, we actually perform the conversion from Torch to ONNX. This achieved by using ONNX's ```torch.onnx.export()``` method, and in our particular code, the setup for all the data passed into that method is broken up into 2 parts, as follows:

#### Part 1

The first part consists of providing all the necessary components for the input:

```python
test_input = tokenizer(text, return_tensors="pt")
input_names = list(test_input.keys())
test_input = {k: v.to(device) for k, v in test_input.items()}
```

#### Part 2

The second part consists of using the methods mentioned in the previous section for providing ```output_names``` and ```dynamic_axes```:

```python
output_names = get_model_output_names(model, test_input)
dynamic_axes = get_dynamic_axes(input_names, output_names)
```

#### Putting it all Together

As the final step, we pass all the necessary information to ```torch.onnx.export()```:

```python
torch.onnx.export(
    model,
    tuple(test_input.values()),
    onnx_path,
    input_names=input_names,
    output_names=output_names,
    dynamic_axes=dynamic_axes,
    do_constant_folding=True,
    opset_version=14
)
```

### Adding Metadata

As the final step for completing a successful export of the transformer ONNX model, we need to provide some metadata which is actually used to retrieve important information for the optimization step we will see later. Here is how the metadata is provided in the script:

```python
# First we begin by collecting the necessary information from the model's configuration
model_type = model.config.model_type
num_heads = model.config.num_attention_heads
hidden_size = model.config.hidden_size

# We then load the model to add the metadata
onnx_model = onnx.load(onnx_path)
meta = onnx_model.metadata_props.add()
meta.key, meta.value = 'model_type', model_type
meta = onnx_model.metadata_props.add()
meta.key, meta.value = 'num_heads', str(num_heads)
meta = onnx_model.metadata_props.add()
meta.key, meta.value = 'hidden_size', str(hidden_size)

# Lastly, we save the model again, which includes the metadata
onnx.save(onnx_model, onnx_path)
```

## Optimizing Converted ONNX Model

As we will see by the graph summary, optimizing the ONNX model is one great way to reduce inference time. The ONNX runtime optimizer does some very clever fusion passes which helps reduce the number of scheduled operations for execution. This significantly reduces the runtime overhead of scheduling more operators for the same work a fused operator can do.

As an example, the model we will be optimizing produces the following graph summary after we run the optimizer on it:
```
Original ONNX model summary:
Number of nodes: 1504
Number of inputs: 3
Number of outputs: 2
Initializers: 199

==================================================

Optimized ONNX model summary:
Number of nodes: 497
Number of inputs: 3
Number of outputs: 2
Initializers: 362
```

As can be seen, there has been a reduction of over 1000 nodes.

In the following section, we won't be discussing the specifics of the optimizations that the ONNX optimizer does, but we will demonstrate how to use the optimizer for achieving a much more compute efficient graph. The steps are as follows:

### Run the End to End ONNX Model Optimization Pipeline

Loading and running is done easily with argparse in the following script:
```
src/main/python/onnx/optimize_onnx_model.py
```

For this example, we will continue with the SPLADE++ Ensemble Distil model.

To run the script and produce the optimized ONNX model, run the following sequence of commands:
```bash
# Begin by going to the appropriate directory
cd src/main/python/onnx
# Now run the script
python optimize_onnx_model.py --model_path models/splade-cocondenser-ensembledistil.onnx
# To run the script that produces the graph summary for the un-optimized and optimized graphs, run the following:
python optimize_onnx_model.py --model_path models/splade-cocondenser-ensembledistil.onnx --stats
```

So what actually happens under the hood? The following sections will discuss the key parts of the above script:

### Loading the ONNX Model and Model Metadata

We begin by loading the ONNX model to be optimized and all the necessary metadata:

```python
# Load the ONNX model
onnx_model = onnx.load(model_path)
model_metadata = onnx_model.metadata_props
print(model_metadata)

model_type = next((prop.value for prop in model_metadata if prop.key == 'model_type'), None)
num_heads = next((int(prop.value) for prop in model_metadata if prop.key == 'num_heads'), None)
hidden_size = next((int(prop.value) for prop in model_metadata if prop.key == 'hidden_size'), None)
```

The above captures the metadata that we stored in the previous section for [converting the model to ONNX](#adding-metadata). 

### Run Optimizer on ONNX Model

As the final step for completing the optimization, we will be providing all the data to the ONNX optimizer:

```python
# Optimize the model
optimized_model = optimizer.optimize_model(
    model_path,
    model_type=model_type,
    num_heads=num_heads,
    hidden_size=hidden_size
)

# Optionally, we can convert to FP16 as well (not done in the script):
optimized_model.convert_float_to_float16()
```

### Save Optimized Model

Lastly, after optimizing the model, we just need to save it as an optimized ONNX model:

```python
model_name = model_path.split(".")[0]
optimized_model_path = f'{model_name}-optimized.onnx'
optimized_model.save_model_to_file(optimized_model_path)
print(f"ONNX model optimization successful. Saved to {optimized_model_path}")
```

## Running Inference

Up until this point, we have mainly covered the steps to generating an optimized ONNX model for SPLADE++ Ensemble Distil, however, how do we actually use our model in practice? The steps are as follows:  

### Run the End to End Inference

Loading and running is done easily with argparse in the following script:
```
src/main/python/onnx/run_onnx_model_inference.py
```

For this example, we will continue with the SPLADE++ Ensemble Distil model.

To run the script for running inference, run the following sequence of commands:
```bash
# Begin by going to the appropriate directory
cd src/main/python/onnx
# Now run the script
python run_onnx_model_inference.py --model_path models/splade-cocondenser-ensembledistil-optimized.onnx \
                                    --model_name naver/splade-cocondenser-ensembledistil
```

So what actually happens under the hood? The following sections will discuss the key parts of the above script:

### Create Inference Session + Load Optimized Model and Tokenizer

As a first step, we introduce the necessary items for building an inference session with the provided module in ONNX runtime, ```onnxruntime.InferenceSession```:

```python
model = onnxruntime.InferenceSession(model_path) # provide the model path to the optimized model
tokenizer = AutoTokenizer.from_pretrained(model_name) # provide the model name as seen on Hugging Face
inputs = tokenizer(text, return_tensors="np") # provide test input, in our case this is "What is AI?"
```

This is modularized in the function definition for ```run_onnx_inference(model_path, model_name, text, threshold)```.

### Running the Inference Session on the Data

As an intermediary step before generating the output vector, we are responsible for either providing or creating if not available, given that ```token_type_ids``` is a named field in the input:

```python
if 'token_type_ids' not in inputs and any('token_type_ids' in input.name for input in model.get_inputs()):
    inputs['token_type_ids'] = np.zeros_like(inputs['input_ids']) # create vector of zeroes given 'token_type_ids' is not provided
```

Following the creation of ```token_type_ids``` we produce the initial output vector with no thresholding, we use the provided ```run``` method in ```onnxruntime.InferenceSession```: 

```python
outputs = model.run(
    None,
    {name: inputs[name] for name in inputs if name in [input.name for input in model.get_inputs()]}
)
```

As a final step, we now need to threshold the output vector to sparsify the output. In other words, if a given index is below our defined threshold value ```1e-2 -> 0.01``` we default its value to ```float(0.0)```:

```python
sparse_vector = outputs[0]
sparse_vector[sparse_vector < threshold] = float(0.0)

print(f"Sparse vector shape after thresholding: {sparse_vector.shape}")
print(f"Non-zero elements after thresholding: {np.count_nonzero(sparse_vector)}")
print(f"Sparse vector output after thresholding: {sparse_vector}")
```

For our particular inference example, these are the produced outputs:

```bash
Sparse vector shape after thresholding: (1, 6, 768)
Non-zero elements after thresholding: 2257
Sparse vector output after thresholding: [[[0.         0.23089279 0.14276895 ... 0.20041081 0.55569583 0.59115154]
  [0.         0.22479157 0.15564989 ... 0.19655053 0.57261604 0.574073  ]
  [0.         0.2015025  0.1403993  ... 0.1951014  0.5457072  0.64515686]
  [0.         0.22291817 0.17013982 ... 0.18394655 0.57281554 0.4937031 ]
  [0.         0.20837721 0.15399718 ... 0.20376778 0.5603207  0.6763782 ]
  [0.         0.19091329 0.02668291 ... 0.13754089 0.26660776 0.96173954]]]
```

All of these definitions are modularized in ```run_onnx_inference(model_path, model_name, text, threshold)```.

## Quantization

### Run End-to-End Quantization

Loading and running is done easily with argparse in the following script:
```
src/main/python/onnx/quantize_onnx_model.py
```

For this example, we will continue with the SPLADE++ Ensemble Distil model.

To run the script for running inference, run the following sequence of commands:
```bash
# Begin by going to the appropriate directory
cd src/main/python/onnx
# Now run the script
python quantize_onnx_model.py --model_path models/splade-cocondenser-ensembledistil-optimized.onnx

```

So what actually happens under the hood? The following sections will discuss the key parts of the above script:

### Quantizing the Model to 8-bit

As seen below, the model name and extension are extracted from the presented optimized onnx model file, and a custom name with 8-bit is created. 

In terms of the quantization semantics, only the `model_input` and `model_output` are needed as specifications to the target model. The other two arguments are needed for specifying the desired weight datatype with `weight_type=QuantType.QInt8` as well as the default tensor type `extra_options={'DefaultTensorType': onnx.TensorProto.FLOAT}`

```python
base, ext = os.path.splitext(onnx_model_path)
quantized_model_path = f"{base}-8bit{ext}"
    
quantize_dynamic(
    model_input=onnx_model_path,
    model_output=quantized_model_path,
    weight_type=QuantType.QInt8,
    extra_options={'DefaultTensorType': onnx.TensorProto.FLOAT}
)
```

## Anserini Compatible Conversion

We have successfully converted SPLADE++ Ensemble Distil from PyTorch to ONNX and ran inference with the optimized model. 
The scripts discussed above can be used to reproduce any model available on Hugging Face. 

However, for this specific example, while the exported model is a perfectly valid ONNX model, it is not what Anserini expects. 
Specifically, this exported model is missing a layer of logic that turns the `last_hidden_state` and `pooler_output` outputs of SPLADE models into the output indices and output weights that Anserini wants. 

Thus, for compatibility with the current Anserini implementation of SPLADE encoders, the script we are about to run was created specifically to convert SPLADE models to ONNX. 
It was originally designed for SPLADE-v3 (in fact, the ONNX version of SPLADE-v3 used in Anserini *was* exported with this script!), but it should work on all models from the SPLADE family.

For consistency with the previous examples, `naver/splade-cocondenser-ensembledistil` has been hard-coded into the script, so all you need to do is run:

```bash
# Begin by going to the appropriate directory
cd src/main/python/onnx
# Now run the script
python splade_to_onnx.py
```

If you want to try exporting the other SPLADE models, simply change the hard-coded model name.

So what actually happens under the hood? The following sections will discuss the key parts of the above script:

### Wrapper Logic

As mentioned earlier, the outputs of SPLADE models need to be converted before they can be used by Anserini.
In the following chunk of code, we implement this output conversion directly in Python before converting the whole model to ONNX by making a wrapper class for the model and adding the logic as a 'layer'. 

This logic is very similar to Pyserini's implementation of its SPLADE encoder, which we provide for reference [here](https://github.com/castorini/pyserini/blob/master/pyserini/encode/_splade.py). 

```python
class SpladeExportWrapper(nn.Module):
    def __init__(self, splade_model):
        super().__init__()
        self.splade = splade_model

    def forward(self, input_ids, attention_mask, token_type_ids):
        # Use token_type_ids in a no-op way to force ONNX to retain it
        dummy = token_type_ids.sum() * 0.0
        outputs = self.splade(input_ids=input_ids, attention_mask=attention_mask)
        logits = outputs.logits
        relu = torch.relu(logits)
        log1p = torch.log1p(relu)
        sparse_vec = torch.max(log1p * attention_mask.unsqueeze(-1), dim=1).values
        nonzero = sparse_vec.nonzero(as_tuple=True)
        values = sparse_vec[nonzero]
        return nonzero[1], values + dummy
```

The rest of the conversion logic is largely similar to before, with a few nuances to ensure compatibility.
We can also use the same optimization script to optimize the model. 

```bash
python optimize_onnx_model.py --model_path models/splade-pp-ed.onnx
```

### Reproducing Transformer-Based Model Regressions 

Now, we can reproduce regressions with the newly generated ONNX model, like seen in the [regressions-msmarco-v1-passage.splade-pp-ed.onnx.md](regressions/regressions-msmarco-v1-passage.splade-pp-ed.onnx.md).

To make sure Anserini uses our generated model instead of downloading it, we want to store the newly generated models in the ```~/.cache/pyserini/encoders``` directory.

```bash
cd src/main/python/onnx/models
cp splade-pp-ed-optimized.onnx ~/.cache/pyserini/encoders/
cp splade-cocondenser-ensembledistil-vocab.txt ~/.cache/pyserini/encoders/splade-pp-ed-vocab.txt
```

Finally, we can run the end to end regression as seen in the previously mentioned documentation with the generated ONNX model and the same scores should be obtained.


### Reproduction Log
+ Results reproduced by [@valamuri2020](https://github.com/valamuri2020) on 2024-08-06 (commit [`6178b40`](https://github.com/castorini/anserini/commit/6178b407fc791d62f81e751313771165c6e2c743))
+ Results reproduced by [@b8zhong](https://github.com/b8zhong) on 2025-01-16 (commit [`7c1de0e`](https://github.com/castorini/anserini/commit/7c1de0eb94781e5868ee9962d9019e4474bd1ce7))
+ Results reproduced by [@lilyjge](https://github.com/lilyjge) on 2025-06-03 (commit [`b216a5f`](https://github.com/castorini/anserini/commit/b216a5f715f3a6e947389459fef3c2711b85b46e))
+ Results reproduced by [@wu-ming233](https://github.com/wu-ming233) on 2025-06-13 (commit [`4d9e082`](https://github.com/castorini/anserini/commit/4d9e08201051b3a158fddd6e419683fc0da9be9c))
