# ONNX Conversion for SPLADE++
This MD file will describe steps to convert particular PyTorch models (i.e., [SPLADE++](https://doi.org/10.1145/3477495.3531857)) to ONNX models and options to further optimize compute graph for Transformer-based models. For more details on how does ONNX Conversion work and how to optimize the compute graph, please refer to [ONNX Tutorials](https://github.com/onnx/tutorials#services).

## Setups

- SPLADE [Git repo](https://github.com/naver/splade):
   ```bash
   git clone https://github.com/naver/splade.git
   ```

- pip requirements:
    ```bash
    pip install torch transformers onnx onnxruntime onnxoptimizer
    ```

## Converting from PyTorch models to ONNX model
The following sections will describe how to convert SPLADE++ model to ONNX model. The steps are as follows:  

### Load the corresponding HuggingFace model

```python
from splade.splade.models.transformer_rep import Splade # this assumes you are writing the script in the same directory as where Splade was cloned
import torch

device = "cuda" if torch.cuda.is_available() else "cpu"

model_name = "naver/splade-cocondenser-ensembledistil"
model = Splade(model_name, agg="max").to(device)
model.eval()
```

### Create dummy input variables for ONNX conversion

```python
from transformers import AutoTokenizer

text = "what is AI?"
tokenizer = AutoTokenizer.from_pretrained(model_name)
dummy_input = tokenizer(text, return_tensors="pt")
input_ids = dummy_input['input_ids'].to(device)
attention_mask = dummy_input['attention_mask'].to(device)
token_type_ids = dummy_input.get('token_type_ids', torch.zeros_like(input_ids)).to(device)
```

### Define a custom forward function

```python
def custom_forward(self, input_ids, attention_mask, token_type_ids):
    return self.transformer_rep(input_ids=input_ids, attention_mask=attention_mask, token_type_ids=token_type_ids)

model.forward = custom_forward.__get__(model)
```

### Export the model to ONNX

```python
onnx_path = "splade-pp-ed.onnx"
torch.onnx.export(model,
                  (input_ids, attention_mask, token_type_ids),
                  onnx_path,
                  input_names=['input_ids', 'attention_mask', 'token_type_ids'],
                  output_names=['output'],
                  dynamic_axes={'input_ids': {0: 'batch_size', 1: 'sequence'},
                                'attention_mask': {0: 'batch_size', 1: 'sequence'},
                                'token_type_ids': {0: 'batch_size', 1: 'sequence'},
                                'output': {0: 'batch_size', 1: 'sequence'}},
                  opset_version=14)
```

### Verify the ONNX model

```python
import onnx
import onnxruntime

onnx_model = onnx.load(onnx_path)
onnx.checker.check_model(onnx_model)

ort_session = onnxruntime.InferenceSession(onnx_path)
ort_inputs = {
    'input_ids': dummy_input['input_ids'].numpy(),
    'attention_mask': dummy_input['attention_mask'].numpy(),
    'token_type_ids': dummy_input['token_type_ids'].numpy()
}
ort_outputs = ort_session.run(None, ort_inputs)
print("ONNX model output shape:", ort_outputs[0].shape)
print("ONNX model output:", ort_outputs[0])
print("ONNX model test run successful")
```

### Optimize the ONNX model (optional)

```python
from onnxruntime.transformers import optimizer

optimized_model = optimizer.optimize_model(
    'splade-pp-ed.onnx',
    model_type='bert',
    num_heads=12,
    hidden_size=768
)

# Optional: convert model to float16 (if not in fp16)
# optimized_model.convert_float_to_float16()

optimized_model.save_model_to_file('splade-pp-ed-optimized.onnx')
print("ONNX model optimization successful")
```

## Inference with ONNX Runtime
The following sections will describe how to run inference with ONNX Runtime. The steps are as follows:

```python
import onnxruntime
from transformers import AutoTokenizer
import numpy as np

splade = onnxruntime.InferenceSession('splade-pp-ed-optimized.onnx')

model_name = "naver/splade-cocondenser-ensembledistil"
tokenizer = AutoTokenizer.from_pretrained(model_name)

text = "what is AI?"
inputs = tokenizer(text, return_tensors="np")

outputs = splade.run(
    None,
    {
        "input_ids": inputs['input_ids'],
        "attention_mask": inputs['attention_mask'],
        "token_type_ids": inputs.get('token_type_ids', np.zeros_like(inputs['input_ids']))
    }
)

sparse_vector = outputs[0]
threshold = 1e-4
sparse_vector[sparse_vector < threshold] = 0

print(f"Sparse vector shape after thresholding: {sparse_vector.shape}")
print(f"Non-zero elements after thresholding: {np.count_nonzero(sparse_vector)}")
print(f"Sparse vector output after thresholding: {sparse_vector}")
```
