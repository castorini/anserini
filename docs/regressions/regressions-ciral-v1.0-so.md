# Anserini Regressions: CIRAL (v1.0) &mdash; Somali

This page documents BM25 regression experiments for [CIRAL (v1.0) &mdash; Somali](https://github.com/ciralproject/ciral) with query translations. To be clear, the queries are in Somali (human translations) and the corpus is in Somali.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/ciral-v1.0-so.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/ciral-v1.0-so.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-so
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection MrTyDiCollection \
  -input /path/to/ciral-somali \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.ciral-v1.0-so/ \
  -storePositions -storeDocvectors -storeRaw -language so \
  >& logs/log.ciral-somali &
```

See [this page](https://github.com/ciralproject/ciral) for more details about the CIRAL corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-so/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-so-test-a-native.tsv \
  -topicReader TsvInt \
  -output runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-a-native.txt \
  -bm25 -hits 1000 -language so &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-so/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-so-test-a-native.tsv \
  -topicReader TsvInt \
  -output runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-a-native.txt \
  -bm25 -hits 1000 -language so &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-so/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-so-test-b-native.tsv \
  -topicReader TsvInt \
  -output runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-b-native.txt \
  -bm25 -hits 1000 -language so &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-so-test-a.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-a-native.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-so-test-a.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-a-native.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-so-test-a-pools.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-a-native.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-so-test-a-pools.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-a-native.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-so-test-b.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-b-native.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-so-test-b.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-test-b-native.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@20**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [CIRAL Somali: Test Set A (Shallow Judgements)](https://huggingface.co/datasets/CIRAL/ciral)                 | 0.1214    |
| [CIRAL Somali: Test Set A (Pools)](https://huggingface.co/datasets/CIRAL/ciral)                              | 0.1232    |
| [CIRAL Somali: Test Set B](https://huggingface.co/datasets/CIRAL/ciral)                                      | 0.1725    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [CIRAL Somali: Test Set A (Shallow Judgements)](https://huggingface.co/datasets/CIRAL/ciral)                 | 0.2615    |
| [CIRAL Somali: Test Set A (Pools)](https://huggingface.co/datasets/CIRAL/ciral)                              | 0.1923    |
| [CIRAL Somali: Test Set B](https://huggingface.co/datasets/CIRAL/ciral)                                      | 0.3479    |
