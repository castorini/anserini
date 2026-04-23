import torch
from transformers import AutoModel, AutoTokenizer

# Load model
model_name = "BAAI/bge-large-en-v1.5"
model = AutoModel.from_pretrained(model_name)
tokenizer = AutoTokenizer.from_pretrained(model_name)
model.eval()

# Dummy input for tracing
dummy = tokenizer("Hello world!", return_tensors="pt")

# Wrapper to auto-generate attention_mask
class Wrapper(torch.nn.Module):
    def __init__(self, model):
        super().__init__()
        self.model = model

    def forward(self, input_ids):
        # Attention mask: 1 where input_ids != 0 (not padding)
        attention_mask = (input_ids != 0).long()
        outputs = self.model(input_ids=input_ids, attention_mask=attention_mask)
        return outputs.last_hidden_state

# Initialize wrapper
wrapped = Wrapper(model)

# Export to ONNX
torch.onnx.export(
    wrapped,
    args=(dummy["input_ids"],),
    f="bge-large-en-v1.5.onnx",
    input_names=["input_ids"],
    output_names=["last_hidden_state"],
    dynamic_axes={
        "input_ids": {0: "batch", 1: "sequence"},
        "last_hidden_state": {0: "batch", 1: "sequence"}
    },
    opset_version=17
)

print("✅ Exported wrapper model as bge-large-en-v1.5-wrapped.onnx")
