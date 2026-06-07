# ⚗️ Anserini: Reproductions from Document Collections

[<< Main Landing Page for Reproductions from Document Collections](../../ref-reproduce-from-document-collections.md)

Anserini supports end-to-end reproduction experiments on various standard IR test collections out of the box.
Each of these experiments starts from the raw document collection, builds the necessary index, performs retrieval runs, and generates evaluation results.

## MS MARCO V2 Document

|                                         |                                             dev                                             |                                         DL21                                          |                                         DL22                                          |                                         DL23                                          |
|-----------------------------------------|:-------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------:|
| **Unsupervised Lexical, Complete Doc**  |                                                                                             |                                                                                       |                                                                                       |                                                                                       |
| baselines                               |                     [+](../from-document-collection/msmarco-v2-doc.md)                     |                     [+](../from-document-collection/dl21-doc.md)                     |                     [+](../from-document-collection/dl22-doc.md)                     |                     [+](../from-document-collection/dl23-doc.md)                     |
| doc2query-T5                            |                 [+](../from-document-collection/msmarco-v2-doc.d2q-t5.md)                  |                 [+](../from-document-collection/dl21-doc.d2q-t5.md)                  |                 [+](../from-document-collection/dl22-doc.d2q-t5.md)                  |                 [+](../from-document-collection/dl23-doc.d2q-t5.md)                  |
| **Unsupervised Lexical, Segmented Doc** |                                                                                             |                                                                                       |                                                                                       |                                                                                       |
| baselines                               |                [+](../from-document-collection/msmarco-v2-doc-segmented.md)                |                [+](../from-document-collection/dl21-doc-segmented.md)                |                [+](../from-document-collection/dl22-doc-segmented.md)                |                [+](../from-document-collection/dl23-doc-segmented.md)                |
| doc2query-T5                            |            [+](../from-document-collection/msmarco-v2-doc-segmented.d2q-t5.md)             |            [+](../from-document-collection/dl21-doc-segmented.d2q-t5.md)             |            [+](../from-document-collection/dl22-doc-segmented.d2q-t5.md)             |            [+](../from-document-collection/dl23-doc-segmented.d2q-t5.md)             |

### Available Corpora for Download

| Corpora                                                                                                                                         |   Size | Checksum                           |
|:------------------------------------------------------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [MS MARCO V2 doc: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_doc_segmented_unicoil_noexp_0shot_v2.tar)       |  55 GB | `97ba262c497164de1054f357caea0c63` |
| [MS MARCO V2 doc: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_doc_segmented_unicoil_0shot_v2.tar)            |  72 GB | `c5639748c2cbad0152e10b0ebde3b804` |
