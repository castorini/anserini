# ⚗️ Anserini: Reproductions from Document Collections

[<< Main Landing Page for Reproductions from Document Collections](../../ref-reproduce-from-document-collections.md)

Anserini supports end-to-end reproduction experiments on various standard IR test collections out of the box.
Each of these experiments starts from the raw document collection, builds the necessary index, performs retrieval runs, and generates evaluation results.

## MS MARCO V2 Passage

|                                                      |                                        dev                                         |                                     DL21                                     |                                     DL22                                     |                                     DL23                                     |
|------------------------------------------------------|:----------------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|
| **Unsupervised Lexical, Original Corpus**            |                                                                                    |                                                                              |                                                                              |                                                                              |
| baselines                                            |              [+](../from-document-collection/msmarco-v2-passage.md)               |              [+](../from-document-collection/dl21-passage.md)               |              [+](../from-document-collection/dl22-passage.md)               |              [+](../from-document-collection/dl23-passage.md)               |
| doc2query-T5                                         |           [+](../from-document-collection/msmarco-v2-passage.d2q-t5.md)           |           [+](../from-document-collection/dl21-passage.d2q-t5.md)           |           [+](../from-document-collection/dl22-passage.d2q-t5.md)           |           [+](../from-document-collection/dl23-passage.d2q-t5.md)           |
| **Unsupervised Lexical, Augmented Corpus**           |                                                                                    |                                                                              |                                                                              |                                                                              |
| baselines                                            |         [+](../from-document-collection/msmarco-v2-passage-augmented.md)          |         [+](../from-document-collection/dl21-passage-augmented.md)          |         [+](../from-document-collection/dl22-passage-augmented.md)          |         [+](../from-document-collection/dl23-passage-augmented.md)          |
| doc2query-T5                                         |      [+](../from-document-collection/msmarco-v2-passage-augmented.d2q-t5.md)      |      [+](../from-document-collection/dl21-passage-augmented.d2q-t5.md)      |      [+](../from-document-collection/dl22-passage-augmented.d2q-t5.md)      |      [+](../from-document-collection/dl23-passage-augmented.d2q-t5.md)      |
| **Learned Sparse**                                   |                                                                                    |                                                                              |                                                                              |                                                                              |
| SPLADE++ CoCondenser-EnsembleDistil (ONNX)           |     [✓](../from-document-collection/msmarco-v2-passage.splade-pp-ed.onnx.md)      |     [✓](../from-document-collection/dl21-passage.splade-pp-ed.onnx.md)      |     [✓](../from-document-collection/dl22-passage.splade-pp-ed.onnx.md)      |     [✓](../from-document-collection/dl23-passage.splade-pp-ed.onnx.md)      |
| SPLADE++ CoCondenser-SelfDistil (ONNX)               |     [✓](../from-document-collection/msmarco-v2-passage.splade-pp-sd.onnx.md)      |     [✓](../from-document-collection/dl21-passage.splade-pp-sd.onnx.md)      |     [✓](../from-document-collection/dl22-passage.splade-pp-sd.onnx.md)      |     [✓](../from-document-collection/dl23-passage.splade-pp-sd.onnx.md)      |

### Available Corpora for Download

| Corpora                                                                                                              |  Size | Checksum                           |
|:---------------------------------------------------------------------------------------------------------------------|------:|:-----------------------------------|
| [uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_noexp_0shot.tar)      | 24 GB | `d9cc1ed3049746e68a2c91bf90e5212d` |
| [uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_0shot.tar)           | 41 GB | `1949a00bfd5e1f1a230a04bbc1f01539` |
| [SPLADE++ CoCondenser-EnsembleDistil](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2_passage_splade_pp_ed.tar) | 66 GB | `2cdb2adc259b8fa6caf666b20ebdc0e8` |
| [SPLADE++ CoCondenser-SelfDistil](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2_passage_splade_pp_sd.tar)     | 76 GB | `061930dd615c7c807323ea7fc7957877` |
