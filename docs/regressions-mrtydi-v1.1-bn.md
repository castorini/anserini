# Anserini: Regressions for [Mr. TyDi (v1.1) &mdash; Bengali](https://github.com/castorini/mr.tydi)

This page documents regression experiments for [Mr. TyDi (v1.1) &mdash; Bengali](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mrtydi-v1.1-bn.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mrtydi-v1.1-bn.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-bn \
  -index indexes/lucene-index.mrtydi-v1.1-bengali/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language bn \
  >& logs/log.mrtydi-v1.1-bn &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-bengali/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-bn.train.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-bn.bm25.topics.mrtydi-v1.1-bn.train.txt.gz \
  -bm25 -hits 100 -language bn &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-bengali/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-bn.dev.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-bn.bm25.topics.mrtydi-v1.1-bn.dev.txt.gz \
  -bm25 -hits 100 -language bn &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-bengali/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-bn.test.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-bn.bm25.topics.mrtydi-v1.1-bn.test.txt.gz \
  -bm25 -hits 100 -language bn &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-bn.train.txt runs/run.mrtydi-v1.1-bn.bm25.topics.mrtydi-v1.1-bn.train.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-bn.dev.txt runs/run.mrtydi-v1.1-bn.bm25.topics.mrtydi-v1.1-bn.dev.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-bn.test.txt runs/run.mrtydi-v1.1-bn.bm25.topics.mrtydi-v1.1-bn.test.txt.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MRR@100                                 | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Bengali): train](https://github.com/castorini/mr.tydi)| 0.3566    |
[Mr. TyDi (Bengali): dev](https://github.com/castorini/mr.tydi)| 0.3385    |
[Mr. TyDi (Bengali): test](https://github.com/castorini/mr.tydi)| 0.4182    |


R@100                                   | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Bengali): train](https://github.com/castorini/mr.tydi)| 0.8336    |
[Mr. TyDi (Bengali): dev](https://github.com/castorini/mr.tydi)| 0.8432    |
[Mr. TyDi (Bengali): test](https://github.com/castorini/mr.tydi)| 0.8694    |
