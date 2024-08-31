# Anserini Regressions: Mr. TyDi (v1.1) &mdash; English

**Models**: bag-of-words approaches using `AutoCompositeAnalyzer`

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; English](https://github.com/castorini/mr.tydi) using `AutoCompositeAnalyzer`.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-en-aca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-en-aca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-en-aca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-en \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-english-aca/ \
  -storePositions -storeDocvectors -storeRaw -language en -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-en &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-english-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-en.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-en.bm25.topics.mrtydi-v1.1-en.train.txt \
  -bm25 -hits 100 -language en -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-english-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-en.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-en.bm25.topics.mrtydi-v1.1-en.dev.txt \
  -bm25 -hits 100 -language en -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-english-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-en.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-en.bm25.topics.mrtydi-v1.1-en.test.txt \
  -bm25 -hits 100 -language en -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-en.train.txt runs/run.mrtydi-v1.1-en.bm25.topics.mrtydi-v1.1-en.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-en.dev.txt runs/run.mrtydi-v1.1-en.bm25.topics.mrtydi-v1.1-en.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-en.test.txt runs/run.mrtydi-v1.1-en.bm25.topics.mrtydi-v1.1-en.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (English): train](https://github.com/castorini/mr.tydi)                                            | 0.1624    |
| [Mr. TyDi (English): dev](https://github.com/castorini/mr.tydi)                                              | 0.1786    |
| [Mr. TyDi (English): test](https://github.com/castorini/mr.tydi)                                             | 0.1482    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (English): train](https://github.com/castorini/mr.tydi)                                            | 0.5904    |
| [Mr. TyDi (English): dev](https://github.com/castorini/mr.tydi)                                              | 0.6253    |
| [Mr. TyDi (English): test](https://github.com/castorini/mr.tydi)                                             | 0.5464    |
