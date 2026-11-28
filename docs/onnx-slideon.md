# SlideOn ONNX inference

## Anserini usage

Use an Anserini build containing runtime-top-k support. Selecting `-encoder SlideOn`
automatically downloads the model, tokenizer, and config from
[`castorini/slideon-onnx`](https://huggingface.co/castorini/slideon-onnx).
No Python conversion step is needed for normal use.

Set the JVM system property `anserini.slideon.topK` **before the main class**:

```bash
# ANSERINI_JAR points to the updated Anserini fatjar.
printf 'q1\twhat is AI?\n' > queries.tsv
java -Danserini.slideon.topK=128 -cp "$ANSERINI_JAR" \
  io.anserini.encoder.EncodeQuery \
  -encoder SlideOn -queries queries.tsv -topicReader TsvString \
  -output queries.slideon.tsv
```

- Default: **64** query features.
- `0`: no final feature pruning.
- Positive values: keep at most k features, capped at the model's latent dimension.
- Negative values: rejected.

The same property applies when using `SlideOn` in `SearchCollection`. It is a JVM
property, not a `SearchCollection` argument. Changing query k does not change an
existing document index. Large unpruned queries can exceed search clause limits.

The processing order is:

```text
Model computation → runtime top-k → final FP16 rounding
                  → FP32 output → scale → integer rounding → discard zeros
```

Java uses scale 100 by default (`anserini.slideon.scale`) and ties-to-even integer
rounding. It does not prune again. Consequently, fewer than k features may remain.
ONNX breaks exact pre-rounding weight ties by lower latent ID; identical tie
selection with native PyTorch is not guaranteed.

## Migration and caching

Update/rebuild Anserini first. The new code requires the graph's `top_k` input.
Old clients that do not supply that input cannot run the new graph.

Both the hosted artifact and the local cache file are named **`slideon-optimized.onnx`**.
Existing cached files are not automatically refreshed. If you previously downloaded
the old graph, delete that model file from your encoders cache once before running
the updated client. The default path is
`~/.cache/pyserini/encoders/slideon-optimized.onnx`; if you configured a custom cache,
use its `encoders` directory instead. Keep the tokenizer and config files.

On the next run, Anserini automatically downloads the new model. New users do not
need any cleanup. Model download requires network access. Loading an old graph
fails with a message explaining that the required `top_k` input is missing.

## Direct ONNX Runtime use

Download the updated graph and supply all four inputs:

```python
import numpy as np
import onnxruntime as ort

session = ort.InferenceSession(
    "slideon-optimized.onnx", providers=["CPUExecutionProvider"])
# input_ids and attention_mask are tokenized INT64 arrays of shape [1, sequence].
indices, weights = session.run(["output_idx", "output_weights"], {
    "input_ids": input_ids,
    "attention_mask": attention_mask,
    "is_document": np.array(False, dtype=np.bool_),
    "top_k": np.array(64, dtype=np.int64),
})
```

`top_k` is a **scalar INT64**, not a length-one array. It applies to either query
or document mode; use `is_document=True` and your desired document k for documents.
Pass nonnegative values; use 0 for no final pruning. Outputs are latent feature IDs
and float weights, not token vocabulary IDs or integer impacts. Callers remain
responsible for task prefixes, tokenization, truncation, and impact quantization.

## Model maintenance

[`convert_slideon_to_onnx.py`](../src/main/python/onnx/convert_slideon_to_onnx.py) patches an existing
ONNX graph; it is **not** a PyTorch-to-ONNX exporter. It downloads a pinned
pre-pruning artifact by default, or accepts a local source with `--input`:

```bash
python -m pip install onnx numpy
python src/main/python/onnx/convert_slideon_to_onnx.py \
  --output models/slideon-runtime-topk.onnx
```

No k is fixed at conversion time. The script adds a runtime input and inserts
pruning before the final FP16 cast. It refuses to overwrite an existing output or
patch an already-patched graph. Publishing the output is a separate maintainer
step, not part of normal inference.

The targeted tests cover runtime k, quantization, and cached-model loading. They do not
establish full native-PyTorch numerical parity or retrieval-score equivalence.
