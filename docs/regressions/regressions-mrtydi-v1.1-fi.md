# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Finnish

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Finnish](https://github.com/castorini/mr.tydi).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-fi.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-fi.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-fi
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-fi \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -storePositions -storeDocvectors -storeRaw -language fi \
  >& logs/log.mrtydi-v1.1-fi &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-fi.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.train.txt \
  -bm25 -hits 100 -language fi &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-fi.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.dev.txt \
  -bm25 -hits 100 -language fi &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-finnish/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-fi.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.test.txt \
  -bm25 -hits 100 -language fi &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-fi.train.txt runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-fi.dev.txt runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-fi.test.txt runs/run.mrtydi-v1.1-fi.bm25.topics.mrtydi-v1.1-fi.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Finnish): train](https://github.com/castorini/mr.tydi)                                            | 0.4101    |
| [Mr. TyDi (Finnish): dev](https://github.com/castorini/mr.tydi)                                              | 0.4136    |
| [Mr. TyDi (Finnish): test](https://github.com/castorini/mr.tydi)                                             | 0.2836    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Finnish): train](https://github.com/castorini/mr.tydi)                                            | 0.8198    |
| [Mr. TyDi (Finnish): dev](https://github.com/castorini/mr.tydi)                                              | 0.8285    |
| [Mr. TyDi (Finnish): test](https://github.com/castorini/mr.tydi)                                             | 0.7196    |
