# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Arabic

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Arabic](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-ar.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-ar.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ar
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-ar \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-arabic/ \
  -storePositions -storeDocvectors -storeRaw -language ar \
  >& logs/log.mrtydi-v1.1-ar &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-arabic/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ar.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ar.bm25.topics.mrtydi-v1.1-ar.train.txt \
  -bm25 -hits 100 -language ar &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-arabic/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ar.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ar.bm25.topics.mrtydi-v1.1-ar.dev.txt \
  -bm25 -hits 100 -language ar &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-arabic/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ar.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ar.bm25.topics.mrtydi-v1.1-ar.test.txt \
  -bm25 -hits 100 -language ar &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ar.train.txt runs/run.mrtydi-v1.1-ar.bm25.topics.mrtydi-v1.1-ar.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ar.dev.txt runs/run.mrtydi-v1.1-ar.bm25.topics.mrtydi-v1.1-ar.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ar.test.txt runs/run.mrtydi-v1.1-ar.bm25.topics.mrtydi-v1.1-ar.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Arabic): train](https://github.com/castorini/mr.tydi)                                             | 0.3356    |
| [Mr. TyDi (Arabic): dev](https://github.com/castorini/mr.tydi)                                               | 0.3462    |
| [Mr. TyDi (Arabic): test](https://github.com/castorini/mr.tydi)                                              | 0.3682    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Arabic): train](https://github.com/castorini/mr.tydi)                                             | 0.7944    |
| [Mr. TyDi (Arabic): dev](https://github.com/castorini/mr.tydi)                                               | 0.7872    |
| [Mr. TyDi (Arabic): test](https://github.com/castorini/mr.tydi)                                              | 0.7928    |
