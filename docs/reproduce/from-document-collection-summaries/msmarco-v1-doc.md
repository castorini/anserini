# ⚗️ Anserini: Reproductions from Document Collections

[<< Main Landing Page for Reproductions from Document Collections](../../ref-reproduce-from-document-collections.md)

Anserini supports end-to-end reproduction experiments on various standard IR test collections out of the box.
Each of these experiments starts from the raw document collection, builds the necessary index, performs retrieval runs, and generates evaluation results.

## MS MARCO V1 Document

|                                                                                               |                                        dev                                         |                                     DL19                                     |                                     DL20                                     |
|-----------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|
| **Unsupervised Lexical, Complete Doc**[*](experiments-msmarco-doc-doc2query-details.md)  |
| Lucene BoW baselines                                                                          |                [+](../from-document-collection/msmarco-v1-doc.md)                 |                [+](../from-document-collection/dl19-doc.md)                 |                [+](../from-document-collection/dl20-doc.md)                 |
| WordPiece baselines (pre-tokenized)                                                           |             [+](../from-document-collection/msmarco-v1-doc.wp-tok.md)             |             [+](../from-document-collection/dl19-doc.wp-tok.md)             |             [+](../from-document-collection/dl20-doc.wp-tok.md)             |
| WordPiece baselines (Huggingface tokenizer)                                                   |             [+](../from-document-collection/msmarco-v1-doc.wp-hgf.md)             |             [+](../from-document-collection/dl19-doc.wp-hgf.md)             |             [+](../from-document-collection/dl20-doc.wp-hgf.md)             |
| WordPiece + Lucene BoW baselines                                                              |             [+](../from-document-collection/msmarco-v1-doc.wp-ca.md)              |             [+](../from-document-collection/dl19-doc.wp-ca.md)              |             [+](../from-document-collection/dl20-doc.wp-ca.md)              |
| doc2query-T5                                                                                  |         [+](../from-document-collection/msmarco-v1-doc.docTTTTTquery.md)          |         [+](../from-document-collection/dl19-doc.docTTTTTquery.md)          |         [+](../from-document-collection/dl20-doc.docTTTTTquery.md)          |
| **Unsupervised Lexical, Segmented Doc**[*](experiments-msmarco-doc-doc2query-details.md) |
| Lucene BoW baselines                                                                          |           [+](../from-document-collection/msmarco-v1-doc-segmented.md)            |           [+](../from-document-collection/dl19-doc-segmented.md)            |           [+](../from-document-collection/dl20-doc-segmented.md)            |
| WordPiece baselines (pre-tokenized)                                                           |        [+](../from-document-collection/msmarco-v1-doc-segmented.wp-tok.md)        |        [+](../from-document-collection/dl19-doc-segmented.wp-tok.md)        |        [+](../from-document-collection/dl20-doc-segmented.wp-tok.md)        |
| WordPiece + Lucene BoW baselines                                                              |        [+](../from-document-collection/msmarco-v1-doc-segmented.wp-ca.md)         |        [+](../from-document-collection/dl19-doc-segmented.wp-ca.md)         |        [+](../from-document-collection/dl20-doc-segmented.wp-ca.md)         |
| doc2query-T5                                                                                  |    [+](../from-document-collection/msmarco-v1-doc-segmented.docTTTTTquery.md)     |    [+](../from-document-collection/dl19-doc-segmented.docTTTTTquery.md)     |    [+](../from-document-collection/dl20-doc-segmented.docTTTTTquery.md)     |

### Available Corpora for Download

| Corpora                                                                                                                                         |   Size | Checksum                           |
|:------------------------------------------------------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [MS MARCO V1 doc: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil-noexp.tar)                   |  11 GB | `11b226e1cacd9c8ae0a660fd14cdd710` |
| [MS MARCO V1 doc: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil.tar)                        |  19 GB | `6a00e2c0c375cb1e52c83ae5ac377ebb` |
