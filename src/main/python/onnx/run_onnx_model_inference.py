import onnxruntime
from transformers import AutoTokenizer
import numpy as np
import argparse

def run_onnx_inference(model_path, model_name, text, threshold):
    model = onnxruntime.InferenceSession(model_path)
    tokenizer = AutoTokenizer.from_pretrained(model_name)
    inputs = tokenizer(text, return_tensors="np")
    
    if 'token_type_ids' not in inputs and any('token_type_ids' in input.name for input in model.get_inputs()):
        inputs['token_type_ids'] = np.zeros_like(inputs['input_ids'])

    outputs = model.run(
        None,
        {name: inputs[name] for name in inputs if name in [input.name for input in model.get_inputs()]}
    )

    sparse_vector = outputs[0]
    sparse_vector[sparse_vector < threshold] = 0

    print(f"Sparse vector shape after thresholding: {sparse_vector.shape}")
    print(f"Non-zero elements after thresholding: {np.count_nonzero(sparse_vector)}")
    print(f"Sparse vector output after thresholding: {sparse_vector}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run ONNX model inference")
    parser.add_argument("--model_path", type=str, help="Path to the ONNX model")
    parser.add_argument("--model_name", type=str, help="Name of the Hugging Face model")
    parser.add_argument("--text", type=str, default="what is AI?", help="Input text for inference")
    parser.add_argument("--threshold", type=float, default=1e-4, help="Threshold for sparse vector")
    args = parser.parse_args()

    run_onnx_inference(args.model_path, args.model_name, args.text, args.threshold)
