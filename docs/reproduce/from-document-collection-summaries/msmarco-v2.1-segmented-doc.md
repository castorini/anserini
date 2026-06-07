# ⚗️ Anserini: Reproductions from Document Collections

[<< Main Landing Page for Reproductions from Document Collections](../../ref-reproduce-from-document-collections.md)

Anserini supports end-to-end reproduction experiments on various standard IR test collections out of the box.
Each of these experiments starts from the raw document collection, builds the necessary index, performs retrieval runs, and generates evaluation results.

## MS MARCO V2.1 Segmented Document

The MS MARCO V2.1 corpora (documents and segmented documents) were derived from the V2 documents corpus for the TREC 2024 RAG Track.
Instructions for downloading the corpus can be found [here](https://trec-rag.github.io/annoucements/2024-corpus-finalization/).
The experiments below capture topics and _passage-level_ qrels for the V2.1 segmented documents corpus.

Key:

+ ☂️ = [UMBRELA](https://github.com/castorini/umbrela) for RAG24, [UMBRELA 2.0](https://github.com/castorini/umbrela) for RAG25
+ 🔑 = keyword queries
+ 🅾️ = query encoding with ONNX

|                            | RAG24 ☂️ | RAG 24 NIST | RAG25 ☂️ | RAG25 NIST |
|----------------------------|:---:|:---:|:---:|:---:|
| baselines                  | [🔑](../from-document-collection/rag24-doc-segmented-test-umbrela.md) | [🔑](../from-document-collection/rag24-doc-segmented-test-nist.md) | [🔑](../from-document-collection/rag25-doc-segmented-test-umbrela2.md) | [🔑](../from-document-collection/rag25-doc-segmented-test-nist.md) |
| SPLADE-v3                  |  [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.splade-v3.onnx.md) |  [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.splade-v3.onnx.md) |  [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.splade-v3.onnx.md) |  [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.splade-v3.onnx.md) |
| Arctic-embed-l (`shard00`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx.md)  | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard00.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx.md) |
| Arctic-embed-l (`shard01`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard01.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard01.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx.md) |
| Arctic-embed-l (`shard02`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard02.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard02.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx.md) |
| Arctic-embed-l (`shard03`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard03.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard03.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx.md) |
| Arctic-embed-l (`shard04`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard04.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard04.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx.md) |
| Arctic-embed-l (`shard05`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard05.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard05.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx.md) |
| Arctic-embed-l (`shard06`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard06.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard06.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx.md) |
| Arctic-embed-l (`shard07`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard07.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard07.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx.md) |
| Arctic-embed-l (`shard08`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard08.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard08.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx.md) |
| Arctic-embed-l (`shard09`) | [🅾️](../from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard09.flat.onnx.md) | [🅾️](../from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard09.flat.onnx.md) | [🅾️](../from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx.md) |

Note that all Arctic-embed-l shards use flat vector indexes.

### Available Corpora for Download

| Corpora                                                                                         |   Size | Checksum                           |
|:------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [SPLADE-v3](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2.1_doc_segmented_splade-v3.tar) | 125 GB | `c62490569364a1eb0101da1ca4a894d9` |
