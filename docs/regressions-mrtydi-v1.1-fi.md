# Anserini: Regressions for [Mr. TyDi (v1.1) &mdash; Finnish](https://github.com/castorini/mr.tydi)

This page documents regression experiments for [Mr. TyDi (v1.1) &mdash; Finnish](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mrtydi-v1.1-fi.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mrtydi-v1.1-fi.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-fi \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language fi \
  >& logs/log.mrtydi-v1.1-fi &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-fi.train.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.train.txt.gz \
  -bm25 -hits 100 -language fi &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-fi.dev.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.dev.txt.gz \
  -bm25 -hits 100 -language fi &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-fi.test.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.test.txt.gz \
  -bm25 -hits 100 -language fi &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-fi.train.txt runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.train.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-fi.dev.txt runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.dev.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-fi.test.txt runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.test.txt.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MRR@100                                 | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Finnish): train](https://github.com/castorini/mr.tydi)| 0.4101    |
[Mr. TyDi (Finnish): dev](https://github.com/castorini/mr.tydi)| 0.4133    |
[Mr. TyDi (Finnish): test](https://github.com/castorini/mr.tydi)| 0.2836    |


R@100                                   | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Finnish): train](https://github.com/castorini/mr.tydi)| 0.8198    |
[Mr. TyDi (Finnish): dev](https://github.com/castorini/mr.tydi)| 0.8285    |
[Mr. TyDi (Finnish): test](https://github.com/castorini/mr.tydi)| 0.7193    |
