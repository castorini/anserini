# Anserini: Regressions for [Mr. TyDi (v1.1) &mdash; Japanese](https://github.com/castorini/mr.tydi)

This page documents regression experiments for [Mr. TyDi (v1.1) &mdash; Japanese](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mrtydi-v1.1-ja.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mrtydi-v1.1-ja.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-ja \
  -index indexes/lucene-index.mrtydi-v1.1-japanese/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language ja \
  >& logs/log.mrtydi-v1.1-ja &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-japanese/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-ja.train.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.train.txt.gz \
  -bm25 -hits 100 -language ja &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-japanese/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-ja.dev.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.dev.txt.gz \
  -bm25 -hits 100 -language ja &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-japanese/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-ja.test.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.test.txt.gz \
  -bm25 -hits 100 -language ja &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ja.train.txt runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.train.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ja.dev.txt runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.dev.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ja.test.txt runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.test.txt.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MRR@100                                 | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Japanese): train](https://github.com/castorini/mr.tydi)| 0.2236    |
[Mr. TyDi (Japanese): dev](https://github.com/castorini/mr.tydi)| 0.2241    |
[Mr. TyDi (Japanese): test](https://github.com/castorini/mr.tydi)| 0.2112    |


R@100                                   | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Japanese): train](https://github.com/castorini/mr.tydi)| 0.7282    |
[Mr. TyDi (Japanese): dev](https://github.com/castorini/mr.tydi)| 0.7274    |
[Mr. TyDi (Japanese): test](https://github.com/castorini/mr.tydi)| 0.6451    |
