# Anserini Regressions: TREC 2024 RAG Track Test Topics

**Model**: Snowflake's [Arctic-embed-l](https://huggingface.co/Snowflake/snowflake-arctic-embed-l) with flat indexes (using ONNX for on-the-fly query encoding)

This page describes regression experiments for ranking _on the segmented version_ of the MS MARCO V2.1 document corpus using the RAG 24 test topics (= queries in TREC parlance), which is integrated into Anserini's regression testing framework.
This corpus was derived from the MS MARCO V2 _segmented_ document corpus and prepared for the TREC 2024 RAG Track.

We build on embeddings made available by Snowflake on [Hugging Face Datasets](https://huggingface.co/datasets/Snowflake/msmarco-v2.1-snowflake-arctic-embed-l), which contains vectors already encoded by the Arctic-embed-l model.
The complete dataset comprises 60 parquet files (from `00` to `59`).
Due to its large size (472 GB), we have divided the vectors into ten shards, each comprised of six files:
for example `shard00` spans `00.parquet` to `05.parquet`; `shard01` spans the next six parquet files, etc.

This page documents experiments for `shard00`; we expect the corpus to be in `msmarco_v2.1_doc_segmented.arctic-embed-l/shard00` (relative to the base collection path).
In these experiments, we are performing query inference "on-the-fly" with ONNX, using flat vector indexes.

Evaluation uses (automatically generated) UMBRELA qrels over all 301 topics from the TREC 2024 RAG Track test set.
UMBRELA is described in the following paper:

> Shivani Upadhyay, Ronak Pradeep, Nandan Thakur, Daniel Campos, Nick Craswell, Ian Soboroff, and Jimmy Lin. A Large-Scale Study of Relevance Assessments with Large Language Models Using UMBRELA. _Proceedings of the 2025 International ACM SIGIR Conference on Innovative Concepts and Theories in Information Retrieval (ICTIR 2025)_, 2025.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx
```

## Indexing

Typical indexing command:

```
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

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Here, we are using all 301 test topics from the TREC 2024 RAG Track with (automatically generated) UMBRELA relevance judgments.
Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchFlatDenseVectors \
  -index indexes/lucene-flat.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l \
  -topics tools/topics-and-qrels/topics.rag24.test.txt \
  -topicReader TsvString \
  -output runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag24.test.txt \
  -topics rag24.test -topicReader TsvString -topicField title -encoder ArcticEmbedLEncoder &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag24.test.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag24.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.arctic-embed-l-flat-onnx.topics.rag24.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@20**                                                                                                  | **ArcticEmbedL**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| RAG 24: Test queries                                                                                         | 0.2981    |
| **nDCG@100**                                                                                                 | **ArcticEmbedL**|
| RAG 24: Test queries                                                                                         | 0.1782    |
| **R@100**                                                                                                    | **ArcticEmbedL**|
| RAG 24: Test queries                                                                                         | 0.0742    |
