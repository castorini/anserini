# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Japanese

**Models**: bag-of-words approaches using `AutoCompositeAnalyzer`

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Japanese](https://github.com/castorini/mr.tydi) using `AutoCompositeAnalyzer`.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-ja-aca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-ja-aca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ja-aca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-ja \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-japanese-aca/ \
  -storePositions -storeDocvectors -storeRaw -language ja -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-ja &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-japanese-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ja.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.train.txt \
  -bm25 -hits 100 -language ja -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-japanese-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ja.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.dev.txt \
  -bm25 -hits 100 -language ja -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-japanese-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ja.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.test.txt \
  -bm25 -hits 100 -language ja -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ja.train.txt runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ja.dev.txt runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ja.test.txt runs/run.mrtydi-v1.1-ja.bm25.topics.mrtydi-v1.1-ja.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Japanese): train](https://github.com/castorini/mr.tydi)                                           | 0.2402    |
| [Mr. TyDi (Japanese): dev](https://github.com/castorini/mr.tydi)                                             | 0.2477    |
| [Mr. TyDi (Japanese): test](https://github.com/castorini/mr.tydi)                                            | 0.2316    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Japanese): train](https://github.com/castorini/mr.tydi)                                           | 0.7571    |
| [Mr. TyDi (Japanese): dev](https://github.com/castorini/mr.tydi)                                             | 0.7694    |
| [Mr. TyDi (Japanese): test](https://github.com/castorini/mr.tydi)                                            | 0.7007    |
