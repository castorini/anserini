# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Thai

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Thai](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-th.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-th.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-th
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-th \
  -index indexes/lucene-index.mrtydi-v1.1-thai/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language th \
  >& logs/log.mrtydi-v1.1-th &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-thai/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-th.train.txt.gz \
  -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-th.bm25.topics.mrtydi-v1.1-th.train.txt \
  -bm25 -hits 100 -language th &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-thai/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-th.dev.txt.gz \
  -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-th.bm25.topics.mrtydi-v1.1-th.dev.txt \
  -bm25 -hits 100 -language th &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-thai/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-th.test.txt.gz \
  -topicreader TsvInt \
  -output runs/run.mrtydi-v1.1-th.bm25.topics.mrtydi-v1.1-th.test.txt \
  -bm25 -hits 100 -language th &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-th.train.txt runs/run.mrtydi-v1.1-th.bm25.topics.mrtydi-v1.1-th.train.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-th.dev.txt runs/run.mrtydi-v1.1-th.bm25.topics.mrtydi-v1.1-th.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-th.test.txt runs/run.mrtydi-v1.1-th.bm25.topics.mrtydi-v1.1-th.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Thai): train](https://github.com/castorini/mr.tydi)                                               | 0.3543    |
| [Mr. TyDi (Thai): dev](https://github.com/castorini/mr.tydi)                                                 | 0.3586    |
| [Mr. TyDi (Thai): test](https://github.com/castorini/mr.tydi)                                                | 0.4012    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Thai): train](https://github.com/castorini/mr.tydi)                                               | 0.8349    |
| [Mr. TyDi (Thai): dev](https://github.com/castorini/mr.tydi)                                                 | 0.8536    |
| [Mr. TyDi (Thai): test](https://github.com/castorini/mr.tydi)                                                | 0.8529    |
