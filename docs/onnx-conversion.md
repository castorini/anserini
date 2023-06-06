# ONNX Conversion for SPLADE++
This MD file will describe steps to convert particular PyTorch models (i.e., [SPLADE++](https://doi.org/10.1145/3477495.3531857)) to ONNX models and options to further optimize compute graph for Transformer-based models. For more details on how does ONNX Conversion work and how to optimize the compute graph, please refer to [ONNX Tutorials](https://github.com/onnx/tutorials#services).

## Setups
- pip requirements:
    ```
    onnx                          1.13.1
    onnxoptimizer                 0.3.10
    onnxruntime                   1.11.1
    ```
- SPLADE [Git repo](https://github.com/naver/splade):
   ```bash
   git clone https://github.com/naver/splade.git
   ```

## Converting from PyTorch models to ONNX model
The following sections will describe how to convert SPLADE++ model to ONNX model. The steps are as follows:  

### Load the corresponding HuggingFace model
```python
from splade.models.transformer_rep import Splade
import torch

model_type_or_dir = "naver/splade-cocondenser-ensembledistil"
model = Splade(model_type_or_dir, agg="max", fp16=True)
model.eval()
```

### Creat dummy input variables to be used for ONNX conversion
```python
input_ids = torch.randint(1,100, size=(1,50))
token_type_ids = torch.full((1,50), 0)
attention_mask = torch.full((1,50), 1)
inputs = {"input_ids": input_ids, "token_type_ids": token_type_ids, "attention_mask": attention_mask}

```

### Export the model to ONNX
```python
torch.onnx.export(model, inputs, 'splade-pp-ed.onnx', input_names=['input_ids', 'token_type_ids','attention_mask'], 
                  output_names=['output_idx','output_weights'], dynamic_axes={'input_ids': {0: 'batch_size', 1: 'length'}, 
                                                        'token_type_ids': {0:'batch_size', 1: 'length'}, 
                'attention_mask': {0: 'batch_size', 1: 'length'}}, opset_version=12)
```

### Optimize the ONNX model (Optional)
```python
from onnxruntime.transformers import optimizer

optimized_model = optimizer.optimize_model(
    'splade-pp-ed-optimized.onnx',
    model_type='bert',
    num_heads=12,
    hidden_size=768
)

# Optional: convert model to float16 (if not in fp16)
# optimized_model.convert_float_to_float16()

optimized_model.save_model_to_file('splade-pp-ed-optimized-fp16.onnx')
```

## Inference with ONNX Runtime
The following sections will describe how to run inference with ONNX Runtime. The steps are as follows:
```python
import onnxruntime

splade = onnxruntime.InferenceSession('path-to-splade-pp-ed-optimized-fp16.onnx')
tokenizer = AutoTokenizer.from_pretrained(model_type_or_dir)

inputs = tokenizer("This is a sample input", return_tensors="np")
splade.run(None , {"input_ids": inputs['input_ids'], 
                   "token_type_ids": inputs['token_type_ids'], 
                   "attention_mask": inputs['attention_mask']})
```