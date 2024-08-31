# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Swahili

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Swahili](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-sw.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-sw.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-sw
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-sw \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -storePositions -storeDocvectors -storeRaw -language sw \
  >& logs/log.mrtydi-v1.1-sw &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-sw.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.train.txt \
  -bm25 -hits 100 -language sw &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-sw.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.dev.txt \
  -bm25 -hits 100 -language sw &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-swahili/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-sw.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.test.txt \
  -bm25 -hits 100 -language sw &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-sw.train.txt runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-sw.dev.txt runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-sw.test.txt runs/run.mrtydi-v1.1-sw.bm25.topics.mrtydi-v1.1-sw.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Swahili): train](https://github.com/castorini/mr.tydi)                                            | 0.2610    |
| [Mr. TyDi (Swahili): dev](https://github.com/castorini/mr.tydi)                                              | 0.2693    |
| [Mr. TyDi (Swahili): test](https://github.com/castorini/mr.tydi)                                             | 0.3893    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Swahili): train](https://github.com/castorini/mr.tydi)                                            | 0.5903    |
| [Mr. TyDi (Swahili): dev](https://github.com/castorini/mr.tydi)                                              | 0.5789    |
| [Mr. TyDi (Swahili): test](https://github.com/castorini/mr.tydi)                                             | 0.7642    |
