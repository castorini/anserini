# Anserini: Regressions for [Mr. TyDi (v1.1) &mdash; Telugu](https://github.com/castorini/mr.tydi)

This page documents regression experiments for [Mr. TyDi (v1.1) &mdash; Telugu](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mrtydi-v1.1-te.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mrtydi-v1.1-te.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-te \
  -index indexes/lucene-index.mrtydi-v1.1-telugu/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language te \
  >& logs/log.mrtydi-v1.1-te &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-telugu/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-te.train.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.train.txt.gz \
  -bm25 -hits 100 -language te &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-telugu/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-te.dev.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.dev.txt.gz \
  -bm25 -hits 100 -language te &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-telugu/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-te.test.txt.gz -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.test.txt.gz \
  -bm25 -hits 100 -language te &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-te.train.txt runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.train.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-te.dev.txt runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.dev.txt.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-te.test.txt runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.test.txt.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MRR@100                                 | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Telugu): train](https://github.com/castorini/mr.tydi)| 0.2847    |
[Mr. TyDi (Telugu): dev](https://github.com/castorini/mr.tydi)| 0.2737    |
[Mr. TyDi (Telugu): test](https://github.com/castorini/mr.tydi)| 0.3434    |


R@100                                   | BM25      |
:---------------------------------------|-----------|
[Mr. TyDi (Telugu): train](https://github.com/castorini/mr.tydi)| 0.7049    |
[Mr. TyDi (Telugu): dev](https://github.com/castorini/mr.tydi)| 0.7040    |
[Mr. TyDi (Telugu): test](https://github.com/castorini/mr.tydi)| 0.7577    |
