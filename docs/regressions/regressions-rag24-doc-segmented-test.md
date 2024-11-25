# Anserini Regressions: TREC 2024 RAG Track Test Topics

**Models**: various bag-of-words approaches on segmented documents

This page describes regression experiments for document ranking _on the segmented version_ of the MS MARCO V2.1 document corpus using the test queries, which is integrated into Anserini's regression testing framework.
This corpus was derived from the MS MARCO V2 _segmented_ document corpus and prepared for the TREC 2024 RAG Track.

Here, we cover bag-of-words baselines where each _segment_ in the MS MARCO V2.1 segmented document corpus is treated as a unit of indexing.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/rag24-doc-segmented-test.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/rag24-doc-segmented-test.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-test
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

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
These evaluation resources are from the original V2 corpus, but have been "projected" over to the V2.1 corpus.

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
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.test.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.test.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| RAG 24: Test queries                                                                                         | 0.0861    | 0.0873    | 0.0929    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: Test queries                                                                                         | 0.7010    | 0.6687    | 0.6791    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: Test queries                                                                                         | 0.3290    | 0.3256    | 0.3307    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: Test queries                                                                                         | 0.1395    | 0.1318    | 0.1384    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: Test queries                                                                                         | 0.3467    | 0.3521    | 0.3667    |
