# ⚗️ Anserini: Reproductions from Document Collections

[<< Main Landing Page for Reproductions from Document Collections](../../ref-reproduce-from-document-collections.md)

Anserini supports end-to-end reproduction experiments on various standard IR test collections out of the box.
Each of these experiments starts from the raw document collection, builds the necessary index, performs retrieval runs, and generates evaluation results.

## BRIGHT

BRIGHT is a retrieval benchmark described [here](https://arxiv.org/abs/2407.12883).

Key:

+ BM25
+ SPLADE-v3 = SPLADE-v3: ONNX (🅾️)
+ BGE (flat) = BGE-large-en-v1.5 (flat vector indexes): ONNX (🅾️)

| Corpus             |                               BM25                               |                                                                             SPLADE-v3                                                                              |                                                                                          BGE (flat)                                                                                          |
|--------------------|:----------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| **StackExchange**  |                                                                  |                                                                                                                                                                    |                                                                                                                                                                                              |
| Biology            |       [🔑](../from-document-collection/bright-biology.md)       |              [🅾️](../from-document-collection/bright-biology.splade-v3.onnx.md)             |              [🅾️](../from-document-collection/bright-biology.bge-large-en-v1.5.flat.onnx.md)             |
| Earth Science      |    [🔑](../from-document-collection/bright-earth-science.md)    |        [🅾️](../from-document-collection/bright-earth-science.splade-v3.onnx.md)       |        [🅾️](../from-document-collection/bright-earth-science.bge-large-en-v1.5.flat.onnx.md)       |
| Economics          |      [🔑](../from-document-collection/bright-economics.md)      |            [🅾️](../from-document-collection/bright-economics.splade-v3.onnx.md)           |            [🅾️](../from-document-collection/bright-economics.bge-large-en-v1.5.flat.onnx.md)           |
| Psychology         |     [🔑](../from-document-collection/bright-psychology.md)      |           [🅾️](../from-document-collection/bright-psychology.splade-v3.onnx.md)          |           [🅾️](../from-document-collection/bright-psychology.bge-large-en-v1.5.flat.onnx.md)          |
| Robotics           |      [🔑](../from-document-collection/bright-robotics.md)       |             [🅾️](../from-document-collection/bright-robotics.splade-v3.onnx.md)            |             [🅾️](../from-document-collection/bright-robotics.bge-large-en-v1.5.flat.onnx.md)            |
| Stack Overflow     |    [🔑](../from-document-collection/bright-stackoverflow.md)    |        [🅾️](../from-document-collection/bright-stackoverflow.splade-v3.onnx.md)       |        [🅾️](../from-document-collection/bright-stackoverflow.bge-large-en-v1.5.flat.onnx.md)       |
| Sustainable Living | [🔑](../from-document-collection/bright-sustainable-living.md)  |   [🅾️](../from-document-collection/bright-sustainable-living.splade-v3.onnx.md)  |   [🅾️](../from-document-collection/bright-sustainable-living.bge-large-en-v1.5.flat.onnx.md)  |
| **Coding**         |                                                                  |                                                                                                                                                                    |                                                                                                                                                                                              |
| LeetCode           |      [🔑](../from-document-collection/bright-leetcode.md)       |             [🅾️](../from-document-collection/bright-leetcode.splade-v3.onnx.md)            |             [🅾️](../from-document-collection/bright-leetcode.bge-large-en-v1.5.flat.onnx.md)            |
| Pony               |        [🔑](../from-document-collection/bright-pony.md)         |                 [🅾️](../from-document-collection/bright-pony.splade-v3.onnx.md)                |                 [🅾️](../from-document-collection/bright-pony.bge-large-en-v1.5.flat.onnx.md)                |
| **Theorems**       |                                                                  |                                                                                                                                                                    |                                                                                                                                                                                              |
| AoPS               |        [🔑](../from-document-collection/bright-aops.md)         |                 [🅾️](../from-document-collection/bright-aops.splade-v3.onnx.md)                |                 [🅾️](../from-document-collection/bright-aops.bge-large-en-v1.5.flat.onnx.md)                |
| TheoremQA-Q        | [🔑](../from-document-collection/bright-theoremqa-questions.md) |  [🅾️](../from-document-collection/bright-theoremqa-questions.splade-v3.onnx.md) |  [🅾️](../from-document-collection/bright-theoremqa-questions.bge-large-en-v1.5.flat.onnx.md) |
| TheoremQA-T        | [🔑](../from-document-collection/bright-theoremqa-theorems.md)  |   [🅾️](../from-document-collection/bright-theoremqa-theorems.splade-v3.onnx.md)  |   [🅾️](../from-document-collection/bright-theoremqa-theorems.bge-large-en-v1.5.flat.onnx.md)  |

### Available Corpora for Download

| Corpora                                                                                                                     |   Size | Checksum                           |
|:----------------------------------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [Post-Processed Corpora](https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-corpus.tar)       | 284 MB | `568b594709a9977369033117bfb6889c` |
| [SPLADE-v3](https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-splade-v3.tar)                 | 1.5 GB | `434cd776b5c40f8112d2bf888c58a516` |
| [BGE-large-en-v1.5](https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-bge-large-en-v1.5.tar) |  13 GB | `0ce2634d34d3d467cd1afd74f2f63c7b` |

The BRIGHT corpora above were processed from Hugging Face with [these scripts](https://github.com/ielab/llm-rankers/tree/main/Rank-R1/bright).
