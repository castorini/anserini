# Anserini Regressions: CIRAL (v1.0) &mdash; Somali

This page documents BM25 monolingual regression experiments for [CIRAL (v1.0) &mdash; Somali](https://github.com/ciralproject/ciral).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/ciral-v1.0-so.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/ciral-v1.0-so.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-so
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/ciral-somali \
  -index indexes/lucene-index.ciral-v1.0-so/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -storePositions -storeDocvectors -storeRaw -language so \
  >& logs/log.ciral-somali &
```

See [this page](https://github.com/ciralproject/ciral) for more details about the CIRAL corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-so/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-so-dev-native.tsv \
  -topicreader TsvInt \
  -output runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-dev-native.txt \
  -bm25 -hits 1000 -language so &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-so-dev.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-dev-native.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.ciral-v1.0-so-dev.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-dev-native.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-so-dev.tsv runs/run.ciral-somali.bm25-default.topics.ciral-v1.0-so-dev-native.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@20**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [CIRAL Somali: Dev](https://huggingface.co/datasets/CIRAL/ciral)                                             | 0.1500    |
| **MRR@10**                                                                                                   | **BM25 (default)**|
| [CIRAL Somali: Dev](https://huggingface.co/datasets/CIRAL/ciral)                                             | 0.4000    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [CIRAL Somali: Dev](https://huggingface.co/datasets/CIRAL/ciral)                                             | 0.1850    |
