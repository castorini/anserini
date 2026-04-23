import argparse
import onnx
from onnxruntime.transformers import optimizer

def optimize_onnx_model(model_path: str, print_stats=False):
    # Load the ONNX model
    onnx_model = onnx.load(model_path)
    model_metadata = {prop.key: prop.value for prop in onnx_model.metadata_props}
    print(f"Model metadata: {model_metadata}")

    # If metadata missing, fall back to BERT-like defaults
    model_type = model_metadata.get("model_type", "bert")
    num_heads = int(model_metadata.get("num_heads", 16))       # bge-large-en-v1.5 → 16 heads
    hidden_size = int(model_metadata.get("hidden_size", 1024)) # bge-large-en-v1.5 → 1024 hidden dim

    print(f"Optimizing with model_type={model_type}, num_heads={num_heads}, hidden_size={hidden_size}")

    # Optimize the model
    optimized_model = optimizer.optimize_model(
        model_path,
        model_type=model_type,
        num_heads=num_heads,
        hidden_size=hidden_size,
    )
    
    # Save optimized model
    model_name = model_path.rsplit(".onnx", 1)[0]
    optimized_model_path = f"{model_name}-optimized.onnx"
    optimized_model.save_model_to_file(optimized_model_path)
    print(f"✅ ONNX model optimization successful. Saved to {optimized_model_path}")

    if print_stats:
        print_model_stats(model_path, optimized_model_path)


def print_model_stats(original_path: str, optimized_path: str):
    # Load and print summary for original model
    orig = onnx.load(original_path)
    print("\nOriginal ONNX model summary:")
    print(f"Nodes: {len(orig.graph.node)}")
    print(f"Inputs: {len(orig.graph.input)}")
    print(f"Outputs: {len(orig.graph.output)}")
    print(f"Initializers: {len(orig.graph.initializer)}")

    print("\n" + "="*50 + "\n")

    # Load and print summary for optimized model
    opt = onnx.load(optimized_path)
    print("Optimized ONNX model summary:")
    print(f"Nodes: {len(opt.graph.node)}")
    print(f"Inputs: {len(opt.graph.input)}")
    print(f"Outputs: {len(opt.graph.output)}")
    print(f"Initializers: {len(opt.graph.initializer)}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Optimize ONNX model")
    parser.add_argument("--model_path", type=str, required=True, help="Path to the ONNX model to optimize")
    parser.add_argument("--stats", action="store_true", help="Print model statistics")
    args = parser.parse_args()

    optimize_onnx_model(args.model_path, args.stats)
