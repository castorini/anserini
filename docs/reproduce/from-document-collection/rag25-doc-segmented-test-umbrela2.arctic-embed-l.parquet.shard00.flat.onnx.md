# Anserini Regressions: TREC 2025 RAG Track Test Topics

**Model**: Snowflake's [Arctic-embed-l](https://huggingface.co/Snowflake/snowflake-arctic-embed-l) with flat indexes (using ONNX for on-the-fly query encoding)

This page describes regression experiments for ranking _on the segmented version_ of the MS MARCO V2.1 document corpus using the RAG 25 test topics (= narratives), which is integrated into Anserini's regression testing framework.
This corpus was derived from the MS MARCO V2 _segmented_ document corpus and re-used for the TREC 2025 RAG Track.
Instructions for downloading the corpus can be found [here](https://trec-rag.github.io/annoucements/2025-rag25-corpus/).

We build on embeddings made available by Snowflake on [Hugging Face Datasets](https://huggingface.co/datasets/Snowflake/msmarco-v2.1-snowflake-arctic-embed-l), which contains vectors already encoded by the Arctic-embed-l model.
The complete dataset comprises 60 parquet files (from `00` to `59`).
Due to its large size (472 GB), we have divided the vectors into ten shards, each comprised of six files:
for example `shard00` spans `00.parquet` to `05.parquet`; `shard01` spans the next six parquet files, etc.

This page documents experiments for `shard00`; we expect the corpus to be in `msmarco_v2.1_doc_segmented.arctic-embed-l/shard00` (relative to the base collection path).
In these experiments, we are performing query inference "on-the-fly" with ONNX, using flat vector indexes.

Evaluation uses qrels over 22 topics from the TREC 2025 RAG Track test set.
More details can be found in the following paper:

> Shivani Upadhyay, Nandan Thakur, Ronak Pradeep, Nick Craswell, Daniel Campos and Jimmy Lin. [Overview of the TREC 2025 Retrieval Augmented Generation (RAG) Track.](https://arxiv.org/abs/2603.09891) _arXiv:2603.09891_, March 2026.

The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard00.flat.onnx.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard00.flat.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard00.flat.onnx
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexFlatDenseVectors \
  -threads 6 \
  -collection ParquetDenseVectorCollection \
  -input /path/to/msmarco-v2.1-doc-segmented-shard00.arctic-embed-l \
  -generator DenseVectorDocumentGenerator \
  -index indexes/lucene-flat.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l \
  -docidField doc_id -vectorField embedding -normalizeVectors \
  >& logs/log.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l &
```

The setting of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Here, we are using all 22 test topics from the TREC 2025 RAG Track with (automatically generated) UMBRELA relevance judgments.
Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchFlatDenseVectors \
  -index indexes/lucene-flat.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l \
  -topics tools/topics-and-qrels/topics.rag25.test.jsonl \
  -topicReader JsonString \
  -output runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag25.test.jsonl.txt \
  -topics rag25.test -topicReader JsonString -topicField title -encoder ArcticEmbedLEncoder &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.30 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag25.test.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@30**                                                                                                  | **ArcticEmbedL**|
|:-------------------------------------------------------------------------------------------------------------|-----------------|
| RAG 25: Test queries                                                                                         | 0.2846          |
| **nDCG@100**                                                                                                 | **ArcticEmbedL**|
| RAG 25: Test queries                                                                                         | 0.1941          |
| **R@100**                                                                                                    | **ArcticEmbedL**|
| RAG 25: Test queries                                                                                         | 0.0606          |
