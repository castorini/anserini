# Anserini Regressions: TREC 2024 RAG Track Test Topics

**Models**: various bag-of-words approaches on segmented documents

This page describes regression experiments for ranking _on the segmented version_ of the MS MARCO V2.1 document corpus using the RAG 24 test topics (= queries in TREC parlance), which is integrated into Anserini's regression testing framework.
This corpus was derived from the MS MARCO V2 _segmented_ document corpus and prepared for the TREC 2024 RAG Track.
Instructions for downloading the corpus can be found [here](https://trec-rag.github.io/annoucements/2024-corpus-finalization/).

Evaluation uses qrels over 89 topics from the TREC 2024 RAG Track test set.
These qrels represent manual relevance judgments from NIST assessors, contrasted with automatically generated UMBRELA judgments.
More details can be found in the following two papers:

> Shivani Upadhyay, Ronak Pradeep, Nandan Thakur, Daniel Campos, Nick Craswell, Ian Soboroff, and Jimmy Lin. [A Large-Scale Study of Relevance Assessments with Large Language Models Using UMBRELA.](https://dl.acm.org/doi/10.1145/3731120.3744605) Proceedings of the 2025 International ACM SIGIR Conference on Innovative Concepts and Theories in Information Retrieval (ICTIR 2025), pages 358-368, July 2025, Padua, Italy.

> Shivani Upadhyay, Ronak Pradeep, Nandan Thakur, Daniel Campos, Nick Craswell, Ian Soboroff, Hoa Trang Dang, and Jimmy Lin. [A Large-Scale Study of Relevance Assessments with Large Language Models: An Initial Look.](https://arxiv.org/abs/2411.08275) _arXiv:2411.08275_, November 2024.

Here, we cover bag-of-words baselines where each _segment_ in the MS MARCO V2.1 segmented document corpus is treated as a unit of indexing.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/rag24-doc-segmented-test-nist.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/rag24-doc-segmented-test-nist.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-test-nist
```

## Indexing

Typical indexing command:

```
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

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Here, we are using 89 test topics from the TREC 2024 RAG Track with manual relevance judgments from NIST assessors.
Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag24.test.txt \
  -topicReader TsvString \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag24.test.txt \
  -topicReader TsvString \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt \
  -bm25 -rm3 -collection MsMarcoV2DocCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag24.test.txt \
  -topicReader TsvString \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt \
  -bm25 -rocchio -collection MsMarcoV2DocCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| RAG 24: Test queries                                                                                         | 0.2809    | 0.2847    | 0.2877    |
| **nDCG@100**                                                                                                 | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: Test queries                                                                                         | 0.2345    | 0.2337    | 0.2422    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: Test queries                                                                                         | 0.1698    | 0.1664    | 0.1722    |
