# Anserini Regressions: CIRAL (v1.0) &mdash; Yoruba (English Translation)

This page documents BM25 regression experiments for [CIRAL (v1.0) &mdash; Yoruba](https://github.com/ciralproject/ciral) with with document translations. To be clear, the queries are in English and the corpus is in English (translated with [NLLB 1.3B](https://huggingface.co/facebook/nllb-200-1.3B)).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/ciral-v1.0-yo-en.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/ciral-v1.0-yo-en.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-yo-en
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection MrTyDiCollection \
  -input /path/to/ciral-yoruba-english \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.ciral-v1.0-yo-en/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.ciral-yoruba-english &
```

See [this page](https://github.com/ciralproject/ciral) for more details about the CIRAL corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-yo-en/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-yo-test-a.tsv \
  -topicReader TsvInt \
  -output runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-a.txt \
  -bm25 -hits 1000 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-yo-en/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-yo-test-a.tsv \
  -topicReader TsvInt \
  -output runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-a.txt \
  -bm25 -hits 1000 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-yo-en/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-yo-test-b.tsv \
  -topicReader TsvInt \
  -output runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-b.txt \
  -bm25 -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-yo-test-a.tsv runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-a.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-yo-test-a.tsv runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-a.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-yo-test-a-pools.tsv runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-a.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-yo-test-a-pools.tsv runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-a.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-yo-test-b.tsv runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-b.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-yo-test-b.tsv runs/run.ciral-yoruba-english.bm25-default.topics.ciral-v1.0-yo-test-b.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@20**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [CIRAL Yoruba: Test Set A (Shallow Judgements)](https://huggingface.co/datasets/CIRAL/ciral)                 | 0.4265    |
| [CIRAL Yoruba: Test Set A (Pools)](https://huggingface.co/datasets/CIRAL/ciral)                              | 0.4451    |
| [CIRAL Yoruba: Test Set B](https://huggingface.co/datasets/CIRAL/ciral)                                      | 0.3700    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [CIRAL Yoruba: Test Set A (Shallow Judgements)](https://huggingface.co/datasets/CIRAL/ciral)                 | 0.7832    |
| [CIRAL Yoruba: Test Set A (Pools)](https://huggingface.co/datasets/CIRAL/ciral)                              | 0.7199    |
| [CIRAL Yoruba: Test Set B](https://huggingface.co/datasets/CIRAL/ciral)                                      | 0.7348    |
