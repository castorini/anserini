import onnx
from onnxruntime.quantization import quantize_dynamic, QuantType
import argparse
import os

def quantize_model(onnx_model_path):
    base, ext = os.path.splitext(onnx_model_path)
    quantized_model_path = f"{base}-8bit{ext}"
    
    quantize_dynamic(
        model_input=onnx_model_path,
        model_output=quantized_model_path,
        weight_type=QuantType.QInt8,
        extra_options={'DefaultTensorType': onnx.TensorProto.FLOAT}
    )
    
    print(f"Quantized model saved to {quantized_model_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Quantize ONNX model to 8-bit")
    parser.add_argument("--model_path", type=str, required=True, help="Path to ONNX model")
    args = parser.parse_args()

    quantize_model(args.model_path)