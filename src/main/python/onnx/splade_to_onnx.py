from transformers import AutoModelForMaskedLM, AutoTokenizer
import onnx
import torch
import torch.nn as nn
import torch

model = AutoModelForMaskedLM.from_pretrained("naver/splade-v3")
tokenizer = AutoTokenizer.from_pretrained("naver/splade-v3")
model.eval()

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
        sparse_vec = torch.max(log1p, dim=1).values
        nonzero = sparse_vec.nonzero(as_tuple=True)
        values = sparse_vec[nonzero]
        return nonzero[1], values + dummy
    
wrapped_model = SpladeExportWrapper(model)
wrapped_model.eval()

dummy_inputs = tokenizer("example", return_tensors="pt")
input_ids = dummy_inputs["input_ids"]
attention_mask = dummy_inputs["attention_mask"]
token_type_ids = torch.zeros_like(input_ids)

onnx_path = "splade-v3.onnx"

torch.onnx.export(
    wrapped_model,
    (input_ids, attention_mask, token_type_ids),  
    onnx_path,
    input_names=["input_ids", "attention_mask", "token_type_ids"],
    output_names=["output_idx", "output_weights"],
    dynamic_axes={
        "input_ids": {1: "seq_len"},
        "attention_mask": {1: "seq_len"},
        "token_type_ids": {1: "seq_len"},
        "output_idx": {0: "sparse_len"},
        "output_weights": {0: "sparse_len"},
    },
    do_constant_folding=True,
    opset_version=14,
    custom_opsets={"ai.onnx.ml": 4}
)

model_type = model.config.model_type
num_heads = model.config.num_attention_heads
hidden_size = model.config.hidden_size

onnx_model = onnx.load(onnx_path)
meta = onnx_model.metadata_props.add()
meta.key, meta.value = 'model_type', model_type
meta = onnx_model.metadata_props.add()
meta.key, meta.value = 'num_heads', str(num_heads)
meta = onnx_model.metadata_props.add()
meta.key, meta.value = 'hidden_size', str(hidden_size)

onnx.save(onnx_model, onnx_path)  # including the metadata

onnx_model = onnx.load(onnx_path)
onnx.checker.check_model(onnx_model)
print("ONNX model checked successfully")
print(f"Model converted and saved to {onnx_path}")