import torch
from transformers import AutoTokenizer, AutoModel
import onnx
import onnxruntime
import argparse
import os

# device
device = "cuda" if torch.cuda.is_available() else "cpu" # make sure torch is compiled with cuda if you have a cuda device

def get_model_output_names(model, test_input):
    with torch.no_grad():
        outputs = model(**test_input)
    
    if isinstance(outputs, torch.Tensor):
        return ['output']
    elif hasattr(outputs, 'last_hidden_state'):
        return ['last_hidden_state']
    elif hasattr(outputs, 'logits'):
        return ['logits']
    else:
        return [f'output_{i}' for i in range(len(outputs))]

def get_dynamic_axes(input_names, output_names):
    dynamic_axes = {}
    for name in input_names:
        dynamic_axes[name] = {0: 'batch_size', 1: 'sequence'}
    for name in output_names:
        dynamic_axes[name] = {0: 'batch_size', 1: 'sequence'}
    return dynamic_axes

def convert_model_to_onnx(text, model, tokenizer, onnx_path, device):
    print(model) # this prints the model structure for better understanding (optional)
    model.eval()
    
    test_input = tokenizer(text, return_tensors="pt")
    input_names = list(test_input.keys())
    test_input = {k: v.to(device) for k, v in test_input.items()}
    
    output_names = get_model_output_names(model, test_input)
    dynamic_axes = get_dynamic_axes(input_names, output_names)
    
    model_type = model.config.model_type
    num_heads = model.config.num_attention_heads
    hidden_size = model.config.hidden_size

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
    
    onnx_model = onnx.load(onnx_path)
    meta = onnx_model.metadata_props.add()
    meta.key, meta.value = 'model_type', model_type
    meta = onnx_model.metadata_props.add()
    meta.key, meta.value = 'num_heads', str(num_heads)
    meta = onnx_model.metadata_props.add()
    meta.key, meta.value = 'hidden_size', str(hidden_size)
    
    onnx.save(onnx_model, onnx_path) # including the metadata
    print(f"Model converted and saved to {onnx_path}")

    onnx_model = onnx.load(onnx_path)
    onnx.checker.check_model(onnx_model)
    print("ONNX model checked successfully")

    # small inference session for testing
    ort_session = onnxruntime.InferenceSession(onnx_path)
    ort_inputs = {k: v.cpu().numpy() for k, v in test_input.items()}
    ort_outputs = ort_session.run(None, ort_inputs)
    print("ONNX model output shape:", ort_outputs[0].shape)
    print("ONNX model test run successful")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Convert Hugging Face model to ONNX")
    parser.add_argument("--model_name", type=str, help="Name or path of the Hugging Face model")
    parser.add_argument("--text", type=str, default="what is AI?", help="Test input text for the model")
    args = parser.parse_args()

    model = AutoModel.from_pretrained(args.model_name).to(device)
    tokenizer = AutoTokenizer.from_pretrained(args.model_name)
    model_prefix = args.model_name.split('/')[-1]
    
    os.makedirs("models", exist_ok=True)
    onnx_path = f"models/{model_prefix}.onnx"

    convert_model_to_onnx(args.text, model, tokenizer, onnx_path, device=device)
