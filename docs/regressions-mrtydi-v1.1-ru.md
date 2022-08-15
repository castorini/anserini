# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Russian

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Russian](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mrtydi-v1.1-ru.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mrtydi-v1.1-ru.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ru
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-ru \
  -index indexes/lucene-index.mrtydi-v1.1-russian/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.mrtydi-v1.1-ru &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-ru.train.txt.gz \
  -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.train.txt \
  -bm25 -hits 100 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-ru.dev.txt.gz \
  -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.dev.txt \
  -bm25 -hits 100 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-ru.test.txt.gz \
  -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.test.txt \
  -bm25 -hits 100 -language ru &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ru.train.txt runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.train.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ru.dev.txt runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ru.test.txt runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Russian): train](https://github.com/castorini/mr.tydi)                                            | 0.2229    |
| [Mr. TyDi (Russian): dev](https://github.com/castorini/mr.tydi)                                              | 0.2202    |
| [Mr. TyDi (Russian): test](https://github.com/castorini/mr.tydi)                                             | 0.3163    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Russian): train](https://github.com/castorini/mr.tydi)                                            | 0.5779    |
| [Mr. TyDi (Russian): dev](https://github.com/castorini/mr.tydi)                                              | 0.5760    |
| [Mr. TyDi (Russian): test](https://github.com/castorini/mr.tydi)                                             | 0.6541    |
