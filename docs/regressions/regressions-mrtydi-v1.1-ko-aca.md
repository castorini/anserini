# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Korean

**Models**: bag-of-words approaches using `AutoCompositeAnalyzer`

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Korean](https://github.com/castorini/mr.tydi) using `AutoCompositeAnalyzer`.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-ko-aca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-ko-aca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ko-aca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-ko \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-korean-aca/ \
  -storePositions -storeDocvectors -storeRaw -language ko -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-ko &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-korean-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ko.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ko.bm25.topics.mrtydi-v1.1-ko.train.txt \
  -bm25 -hits 100 -language ko -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-korean-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ko.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ko.bm25.topics.mrtydi-v1.1-ko.dev.txt \
  -bm25 -hits 100 -language ko -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-korean-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-ko.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-ko.bm25.topics.mrtydi-v1.1-ko.test.txt \
  -bm25 -hits 100 -language ko -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ko.train.txt runs/run.mrtydi-v1.1-ko.bm25.topics.mrtydi-v1.1-ko.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ko.dev.txt runs/run.mrtydi-v1.1-ko.bm25.topics.mrtydi-v1.1-ko.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-ko.test.txt runs/run.mrtydi-v1.1-ko.bm25.topics.mrtydi-v1.1-ko.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Korean): train](https://github.com/castorini/mr.tydi)                                             | 0.2694    |
| [Mr. TyDi (Korean): dev](https://github.com/castorini/mr.tydi)                                               | 0.3100    |
| [Mr. TyDi (Korean): test](https://github.com/castorini/mr.tydi)                                              | 0.2907    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Korean): train](https://github.com/castorini/mr.tydi)                                             | 0.6483    |
| [Mr. TyDi (Korean): dev](https://github.com/castorini/mr.tydi)                                               | 0.6898    |
| [Mr. TyDi (Korean): test](https://github.com/castorini/mr.tydi)                                              | 0.6433    |
