# End to End ONNX Conversion for SPLADE++ Ensemble Distil
This MD file will describe steps to convert particular PyTorch models (i.e., [SPLADE++](https://doi.org/10.1145/3477495.3531857)) to ONNX models and options to further optimize compute graph for Transformer-based models. For more details on how does ONNX Conversion work and how to optimize the compute graph, please refer to [ONNX Tutorials](https://github.com/onnx/tutorials#services).

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

## Converting from PyTorch models to ONNX model
The following sections will describe how to convert SPLADE++ model to ONNX model. The steps are as follows:  

### Run the End to End PyTorch to ONNX Conversion
Loading and running is done easily with argparse in the following script:
```
src/main/python/onnx/convert_hf_model_to_onnx.py
```

All that needs to be provided is the model_name as seen on huggingface. In our example we will be loading the SPLADE++ Ensemble Distil model found here:
```
naver/splade-cocondenser-ensembledistil
```

To run the script and produce the onnx model, run the following sequence of commands:
```bash
# Begin by going to the appropriate directory
cd src/main/python/onnx
# Now run the script
python3 convert_hf_model_to_onnx.py --model_name naver/splade-cocondenser-ensembledistil
```

So what actually happens under the hood? The following sections will discuss the key parts of the above script:

### Getting Output Specificaton from the Model

Since we want our script to be generic, and be able to load any huggingface model with simplicity, we make it possible to fetch all required information from the model output structure with the following code:

```python
with torch.no_grad():
    outputs = model(**test_input) # We provide the text test input and the model object.

if isinstance(outputs, torch.Tensor):
    return ['output']
elif hasattr(outputs, 'last_hidden_state'):
    return ['last_hidden_state']
elif hasattr(outputs, 'logits'):
    return ['logits']
else:
    return [f'output_{i}' for i in range(len(outputs))]
```

This is modularized in the function definition for ```get_model_output_names(model, test_input)```

---

Another important component is being able to specify the dimensions of the input and output tensors. This is achieved by the following code:

```python
dynamic_axes = {}
for name in input_names:
    dynamic_axes[name] = {0: 'batch_size', 1: 'sequence'}
for name in output_names:
    dynamic_axes[name] = {0: 'batch_size', 1: 'sequence'}
return dynamic_axes
```

This is important for the generalization of input and output shapes to be captured for any model that is provided. This is once again modularized in the function defintion for ```get_dynamic_axes(input_names, output_names)```

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

The second part consists of using the methods mentioned in the previous section for provinding ```output_names``` and ```dynamic_axes```:

```python
output_names = get_model_output_names(model, test_input)
dynamic_axes = get_dynamic_axes(input_names, output_names)
```

#### Putting it all Together

As the final step, we pass all the necessary information to ```torch.onxx.export()```:

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

As the final step for completing a successful export of the SPLADE++ ONNX model, we need to provide some metadata which is actually used to retrieve important information for the optimization step we will see later. Here is how the metadata is provided in the script:

```python
# First we begin by collecting the necessary infromation from the model's configuration
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
Number of nodes: 496
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

To run the script and produce the optimized onnx model, run the following sequence of commands:
```bash
# Begin by going to the appropriate directory
cd src/main/python/onnx
# Now run the script
python3 optimize_onnx_model.py --model_path models/splade-cocondenser-ensembledistil.onnx
# If you would like to run the script that produces the graph summary for the un-optimized and optimized graphs, run the following:
python3 optimize_onnx_model.py --model_path models/splade-cocondenser-ensembledistil.onnx --stats
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

As the final step of this tutorial, we can use the ```onnxruntime.InferenceSession``` module for a simple inference session. 

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
python3 run_onnx_model_inference.py --model_path models/splade-cocondenser-ensembledistil-optimized.onnx \
                                    --model_name naver/splade-cocondenser-ensembledistil
```