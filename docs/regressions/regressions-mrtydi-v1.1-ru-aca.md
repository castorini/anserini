# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Russian

**Models**: bag-of-words approaches using `AutoCompositeAnalyzer`

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Russian](https://github.com/castorini/mr.tydi) using `AutoCompositeAnalyzer`.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-ru-aca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-ru-aca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ru-aca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-ru \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-russian-aca/ \
  -storePositions -storeDocvectors -storeRaw -language ru -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-ru &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-russian-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ru.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.train.txt \
  -bm25 -hits 100 -language ru -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-russian-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ru.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.dev.txt \
  -bm25 -hits 100 -language ru -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-russian-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ru.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.test.txt \
  -bm25 -hits 100 -language ru -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ru.train.txt runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ru.dev.txt runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ru.test.txt runs/run.mrtydi-v1.1-ru.bm25.topics.mrtydi-v1.1-ru.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Russian): train](https://github.com/castorini/mr.tydi)                                            | 0.2509    |
| [Mr. TyDi (Russian): dev](https://github.com/castorini/mr.tydi)                                              | 0.2421    |
| [Mr. TyDi (Russian): test](https://github.com/castorini/mr.tydi)                                             | 0.3720    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Russian): train](https://github.com/castorini/mr.tydi)                                            | 0.6614    |
| [Mr. TyDi (Russian): dev](https://github.com/castorini/mr.tydi)                                              | 0.6676    |
| [Mr. TyDi (Russian): test](https://github.com/castorini/mr.tydi)                                             | 0.7534    |
