import argparse
from onnxruntime.transformers import optimizer

# imports if you would like like print the optimized onnx model graph structure
import onnx

def optimize_onnx_model(model_path, print_stats=False):
    # Load the ONNX model
    onnx_model = onnx.load(model_path)
    model_metadata = onnx_model.metadata_props
    print(model_metadata)
    
    model_type = next((prop.value for prop in model_metadata if prop.key == 'model_type'), None)
    num_heads = next((int(prop.value) for prop in model_metadata if prop.key == 'num_heads'), None)
    hidden_size = next((int(prop.value) for prop in model_metadata if prop.key == 'hidden_size'), None)
    
    # Optimize the model
    optimized_model = optimizer.optimize_model(
        model_path,
        model_type=model_type,
        num_heads=num_heads,
        hidden_size=hidden_size
    )

    # Optional: convert model to float16 (if not in fp16)
    # optimized_model.convert_float_to_float16()

    # Save the optimized model
    model_name = model_path.split(".")[0]
    optimized_model_path = f'{model_name}-optimized.onnx'
    optimized_model.save_model_to_file(optimized_model_path)
    print(f"ONNX model optimization successful. Saved to {optimized_model_path}")

    if print_stats:
        print_model_stats(model_path, optimized_model_path)

def print_model_stats(original_path, optimized_path):
    # Load and print summary for non-optimized model (optional with --stats)
    original_onnx_model = onnx.load(original_path)
    print("\nOriginal ONNX model summary:")
    print(f"Number of nodes: {len(original_onnx_model.graph.node)}")
    print(f"Number of inputs: {len(original_onnx_model.graph.input)}")
    print(f"Number of outputs: {len(original_onnx_model.graph.output)}")
    print(f"Initializers: {len(original_onnx_model.graph.initializer)}")

    print("\n" + "="*50 + "\n")

    # Load and print summary for optimized model (optional with --stats)
    optimized_onnx_model = onnx.load(optimized_path)
    print("Optimized ONNX model summary:")
    print(f"Number of nodes: {len(optimized_onnx_model.graph.node)}")
    print(f"Number of inputs: {len(optimized_onnx_model.graph.input)}")
    print(f"Number of outputs: {len(optimized_onnx_model.graph.output)}")
    print(f"Initializers: {len(optimized_onnx_model.graph.initializer)}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Optimize ONNX model")
    parser.add_argument("--model_path", type=str, help="Path to the ONNX model to optimize")
    parser.add_argument("--stats", action="store_true", help="Print model statistics")
    args = parser.parse_args()

    optimize_onnx_model(args.model_path, args.stats)
