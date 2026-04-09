# Anserini Regressions: TREC 2025 RAG Track Test Topics

**Models**: various bag-of-words approaches on segmented documents

This page describes regression experiments for ranking _on the segmented version_ of the MS MARCO V2.1 document corpus using the RAG 25 test topics (= narratives), which is integrated into Anserini's regression testing framework.
This corpus was derived from the MS MARCO V2 _segmented_ document corpus and re-used for the TREC 2025 RAG Track.
Instructions for downloading the corpus can be found [here](https://trec-rag.github.io/annoucements/2025-rag25-corpus/).

Evaluation uses qrels over 22 topics from the TREC 2025 RAG Track test set.
These qrels represent manual relevance judgments from NIST assessors, contrasted with automatically generated UMBRELA judgments.
More details can be found in the following paper:

> Shivani Upadhyay, Nandan Thakur, Ronak Pradeep, Nick Craswell, Daniel Campos and Jimmy Lin. [Overview of the TREC 2025 Retrieval Augmented Generation (RAG) Track.](https://arxiv.org/abs/2603.09891) _arXiv:2603.09891_, March 2026.

Here, we cover bag-of-words baselines where each _segment_ in the MS MARCO V2.1 segmented document corpus is treated as a unit of indexing.

The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/rag25-doc-segmented-test-umbrela2.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config rag25-doc-segmented-test-umbrela2
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2.1-doc-segmented \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -storeRaw \
  >& logs/log.msmarco-v2.1-doc-segmented &
```

The setting of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](../../../docs/common-indexing-options.md).

## Retrieval

Here, we are using 22 test topics from the TREC 2025 RAG Track with manual relevance judgments from NIST assessors.
Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag25.test.jsonl \
  -topicReader JsonString \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag25.test.jsonl.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag25.test.jsonl \
  -topicReader JsonString \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag25.test.jsonl.txt \
  -bm25 -rm3 -collection MsMarcoV2DocCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag25.test.jsonl \
  -topicReader JsonString \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag25.test.jsonl.txt \
  -bm25 -rocchio -collection MsMarcoV2DocCollection &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.30 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag25.test.jsonl.txt

bin/trec_eval -c -m ndcg_cut.30 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag25.test.jsonl.txt

bin/trec_eval -c -m ndcg_cut.30 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag25.test.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@30**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-------------------|-----------|-------------|
| RAG 25: Test queries                                                                                         | 0.3250            | 0.2736    | 0.3306      |
| **nDCG@100**                                                                                                 | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 25: Test queries                                                                                         | 0.2835            | 0.2345    | 0.2767      |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 25: Test queries                                                                                         | 0.1167            | 0.0909    | 0.1092      |
