"""Download SlideOn and insert runtime top-k pruning before final FP16 rounding.

Requires: onnx, numpy. Example:
  python src/main/python/onnx/slideon_to_onnx.py --output models/slideon-runtime-topk.onnx

The output is a new deployment artifact, not an update to the hosted model or
Anserini's cache. Publish/version it separately before changing the Java URL.
Java downloads the published model automatically. Set its runtime k with:
  -Danserini.slideon.topK=128
Java defaults to 64; 0 disables pruning. Java requires the new top_k input.
Java caches the model as slideon-optimized.onnx. Remove any older cached graph
once after upgrading so Java downloads the new artifact.
This script patches an existing ONNX graph; it does not export from PyTorch.
Scalar INT64 input top_k applies to either mode: 0 disables pruning, positive
values keep at most k features (clamped to n_latents). Negative values also
bypass pruning; callers should reject them. ONNX TopK breaks ties by lower ID;
this does not guarantee PyTorch parity for exact pre-rounding ties.
"""
import argparse
from pathlib import Path
import tempfile
import urllib.request

import numpy as np
import onnx
from onnx import TensorProto, helper, numpy_helper

# Pin the pre-pruning source: main now contains the patched graph.
MODEL_URL = (
    "https://huggingface.co/castorini/slideon-onnx/resolve/"
    "055b0de9d1084276579f1c3cf03a1b5d5be5319d/slideon-optimized.onnx"
)
PREFIX = "slideon_query_prune/"


def add_runtime_pruning(model):
    """Patch the known sparse-output tail; fail closed for unfamiliar graphs."""
    producers = {out: node for node in model.graph.node for out in node.output}
    names = set(producers) | {x.name for x in model.graph.initializer}
    if any(name.startswith(PREFIX) for name in names) or any(x.name == "top_k" for x in model.graph.input):
        raise ValueError("Graph already contains runtime pruning")
    metadata = {entry.key: entry.value for entry in model.metadata_props}
    dimensions = int(metadata.get("n_latents", "0"))
    if metadata.get("model_type") != "slideon" or dimensions <= 0:
        raise ValueError("Expected SlideOn metadata with positive n_latents")
    mode = next((x for x in model.graph.input if x.name == "is_document"), None)
    if (mode is None or mode.type.tensor_type.elem_type != TensorProto.BOOL
            or len(mode.type.tensor_type.shape.dim) != 0):
        raise ValueError("Expected scalar BOOL is_document")
    gather = producers.get("output_weights")
    if gather is None or gather.op_type != "Gather" or list(gather.input)[1:] != ["output_idx"]:
        raise ValueError("Expected sparse output_weights Gather")
    fp32 = producers.get(gather.input[0])
    if fp32 is None or fp32.op_type != "Cast" or _cast_type(fp32) != TensorProto.FLOAT:
        raise ValueError("Expected final FP32 cast")
    fp16 = producers.get(fp32.input[0])
    if fp16 is None or fp16.op_type != "Cast" or _cast_type(fp16) != TensorProto.FLOAT16:
        raise ValueError("Expected final FP16 rounding cast")
    if not any(n.op_type == "NonZero" and list(n.input) == [fp32.output[0]]
               for n in model.graph.node):
        raise ValueError("Expected nonzero filtering after rounding")

    source = fp16.input[0]
    p = PREFIX
    model.graph.input.append(helper.make_tensor_value_info("top_k", TensorProto.INT64, []))
    nodes = [
        helper.make_node("Greater", ["top_k", p + "zero"], [p + "enabled"]),
        helper.make_node("Min", ["top_k", p + "dimensions"], [p + "clamped"]),
        helper.make_node("Where", [p + "enabled", p + "clamped", p + "dimensions"],
                         [p + "effective_k"]),
        helper.make_node("Unsqueeze", [p + "effective_k", p + "axes"], [p + "k"]),
        helper.make_node("TopK", [source, p + "k"], [p + "values", p + "indices"],
                         name=p + "TopK", axis=0, largest=1, sorted=1),
        helper.make_node("ScatterElements", [p + "zeros", p + "indices", p + "values"],
                         [p + "sparse"], name=p + "Scatter", axis=0),
        helper.make_node("Where", [p + "enabled", p + "sparse", source],
                         [p + "selected"], name=p + "Where"),
    ]
    model.graph.initializer.extend([
        numpy_helper.from_array(np.array(0, dtype=np.int64), p + "zero"),
        numpy_helper.from_array(np.array(dimensions, dtype=np.int64), p + "dimensions"),
        numpy_helper.from_array(np.array([0], dtype=np.int64), p + "axes"),
        numpy_helper.from_array(np.zeros(dimensions, dtype=np.float32), p + "zeros"),
    ])
    position = next(i for i, n in enumerate(model.graph.node) if n is fp16 or n == fp16)
    fp16.input[0] = p + "selected"
    for i, node in enumerate(nodes):
        model.graph.node.insert(position + i, node)
    metadata.update(top_k="runtime scalar INT64; 0=unpruned", pruning="before_final_fp16")
    helper.set_model_props(model, metadata)
    onnx.checker.check_model(model)
    return model


def _cast_type(node):
    return next((a.i for a in node.attribute if a.name == "to"), None)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", type=Path, help="Local source instead of automatic HF download")
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    if args.output.exists():
        parser.error("Output already exists; choose a new artifact path")
    with tempfile.TemporaryDirectory() as directory:
        source = args.input
        if source is None:
            source = Path(directory) / "slideon-optimized.onnx"
            print(f"Downloading {MODEL_URL}", flush=True)
            urllib.request.urlretrieve(MODEL_URL, source)
        model = add_runtime_pruning(onnx.load(source))
        args.output.parent.mkdir(parents=True, exist_ok=True)
        onnx.save(model, args.output)
    print(f"Saved {args.output}")


if __name__ == "__main__":
    main()
