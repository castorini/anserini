import argparse
import os

import onnx
import onnxruntime
import torch
from transformers import AutoModel, AutoTokenizer
import numpy as np
import logging

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

def convert_model_to_onnx(text, model, tokenizer, onnx_path, vocab_path, device):
    logging.info(model) # this prints the model structure for better understanding (optional)
    model.eval()
    
    test_input = tokenizer(text, return_tensors="pt")
    input_names = list(test_input.keys())
    test_input = {k: v.to(device) for k, v in test_input.items()}
    
    output_names = get_model_output_names(model, test_input)
    
    model_type = model.config.model_type
    num_heads = model.config.num_attention_heads
    hidden_size = model.config.hidden_size

    torch.onnx.export(
        model,
        tuple(test_input.values()),
        onnx_path,
        input_names=input_names,
        output_names=output_names,
        dynamic_axes={
            "input_ids":      {0: "batch", 1: "seq"},
            "attention_mask": {0: "batch", 1: "seq"},
            "token_type_ids": {0: "batch", 1: "seq"},
            "logits":         {0: "batch", 1: "seq"}
        },
        do_constant_folding=True,
        opset_version=14,
    )
    
    onnx_model = onnx.load(onnx_path)
    meta = onnx_model.metadata_props.add()
    meta.key, meta.value = 'model_type', model_type
    meta = onnx_model.metadata_props.add()
    meta.key, meta.value = 'num_heads', str(num_heads)
    meta = onnx_model.metadata_props.add()
    meta.key, meta.value = 'hidden_size', str(hidden_size)
    
    onnx.save(onnx_model, onnx_path) # including the metadata
    logging.info(f"Model converted and saved to {onnx_path}")

    onnx_model = onnx.load(onnx_path)
    onnx.checker.check_model(onnx_model)
    logging.info("ONNX model checked successfully")

    vocab = tokenizer.get_vocab()
    with open(vocab_path, 'w', encoding='utf-8') as f:
        for token, index in sorted(vocab.items(), key=lambda x: x[1]):
            f.write(f"{token}\n")
    logging.info(f"Vocabulary saved to {vocab_path}")

    # small inference session for testing
    ort_session = onnxruntime.InferenceSession(onnx_path)
    ort_inputs = {k: v.cpu().numpy() for k, v in test_input.items()}
    ort_outputs = ort_session.run(None, ort_inputs)
    logging.info(f"ONNX model output shape: {ort_outputs[0].shape}")
    logging.info("ONNX model test run successful")

    l1_diff = validate_onnx_conversion(model, onnx_path, test_input, tokenizer)
    validation_threshold = 1e-2
    if l1_diff > validation_threshold:
        logging.warning(f"Warning: L1 difference ({l1_diff}) exceeds threshold ({validation_threshold})")
        logging.warning("ONNX conversion may not be accurate!")
        logging.warning("This could be due to missing ReLU activation in the comparison.")
    else:
        logging.info("ONNX conversion validated successfully!")

def compute_l2_norm(vector):
    """Computes the L2 norm (Euclidean norm) of a vector."""
    return np.sqrt(np.sum(np.square(vector)))

def normalize_vector(vector):
    """Normalizes a vector to have a norm of 1."""
    norm = compute_l2_norm(vector)
    if norm == 0:
        raise ValueError("Zero vector cannot be normalized.")
    return vector / norm

def validate_onnx_conversion(pytorch_model, onnx_model_path, test_input, tokenizer):
    """Validates ONNX model outputs against PyTorch model outputs."""
    
    # Get PyTorch output with exact same processing
    with torch.no_grad():
        pytorch_outputs = pytorch_model(**test_input)
    
    ort_session = onnxruntime.InferenceSession(onnx_model_path)
    onnx_inputs = {name: test_input[name].numpy() for name in test_input if name in [i.name for i in ort_session.get_inputs()]}
    onnx_outputs = ort_session.run(None, onnx_inputs)
    
    if isinstance(pytorch_outputs, torch.Tensor):
        pytorch_outputs = pytorch_outputs.numpy()
    else:
        if hasattr(pytorch_outputs, 'last_hidden_state'):
            pytorch_outputs = pytorch_outputs.last_hidden_state.numpy()
        elif hasattr(pytorch_outputs, 'logits'):
            pytorch_outputs = pytorch_outputs.logits.numpy()
    
    pytorch_outputs = normalize_vector(pytorch_outputs)
    onnx_outputs = normalize_vector(onnx_outputs[0])
    
    l1_diff = np.mean(np.abs(pytorch_outputs - onnx_outputs))
    logging.info(f"L1 difference between PyTorch and ONNX outputs: {l1_diff}")
    return l1_diff

if __name__ == "__main__":
    logging.basicConfig(
        level=logging.INFO,
        format='%(message)s'
    )
    
    parser = argparse.ArgumentParser(description="Convert Hugging Face model to ONNX")
    parser.add_argument("--model_name", type=str, help="Name or path of the Hugging Face model")
    parser.add_argument("--text", type=str, default="what is AI?", help="Test input text for the model")
    args = parser.parse_args()

    model = AutoModel.from_pretrained(args.model_name).to(device)
    tokenizer = AutoTokenizer.from_pretrained(args.model_name)
    model_prefix = args.model_name.split('/')[-1]
    
    os.makedirs("models", exist_ok=True)
    onnx_path = f"models/{model_prefix}.onnx"
    vocab_path = f"models/{model_prefix}-vocab.txt"

    convert_model_to_onnx(args.text, model, tokenizer, onnx_path, vocab_path, device=device)
