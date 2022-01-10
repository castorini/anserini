# Anserini: Regressions for [Mr. TyDi (v1.1) &mdash; Swahili](https://github.com/castorini/mr.tydi)

This page documents regression experiments for [Mr. TyDi (v1.1) &mdash; Swahili](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mrtydi-v1.1-sw.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mrtydi-v1.1-sw.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-sw \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language sw \
  >& logs/log.mrtydi-v1.1-sw &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-sw.train.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.train.txt.gz \
  -bm25 -hits 100 -language sw &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-sw.dev.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.dev.txt.gz \
  -bm25 -hits 100 -language sw &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-sw.test.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.test.txt.gz \
  -bm25 -hits 100 -language sw &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-sw.train.txt runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.train.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-sw.dev.txt runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.dev.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-sw.test.txt runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.test.txt.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MRR@100                                 | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Swahili): train](https://github.com/castorini/mr.tydi)| 0.2610    |
[Mr. TyDi (Swahili): dev](https://github.com/castorini/mr.tydi)| 0.2693    |
[Mr. TyDi (Swahili): test](https://github.com/castorini/mr.tydi)| 0.3893    |


R@100                                   | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Swahili): train](https://github.com/castorini/mr.tydi)| 0.5903    |
[Mr. TyDi (Swahili): dev](https://github.com/castorini/mr.tydi)| 0.5789    |
[Mr. TyDi (Swahili): test](https://github.com/castorini/mr.tydi)| 0.7642    |
