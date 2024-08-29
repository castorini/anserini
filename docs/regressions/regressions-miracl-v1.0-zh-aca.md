# Anserini Regressions: MIRACL (v1.0) &mdash; Chinese

**Models**: bag-of-words approaches using `AutoCompositeAnalyzer`

This page documents BM25 regression experiments for [MIRACL (v1.0) &mdash; Chinese](https://github.com/project-miracl/miracl) using `AutoCompositeAnalyzer`.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/miracl-v1.0-zh-aca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/miracl-v1.0-zh-aca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-zh-aca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/miracl-v1.0-zh \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.miracl-v1.0-zh-aca/ \
  -storePositions -storeDocvectors -storeRaw -language zh -useAutoCompositeAnalyzer \
  >& logs/log.miracl-v1.0-zh &
```

See [this page](https://github.com/project-miracl/miracl) for more details about the MIRACL corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.miracl-v1.0-zh-aca/ \
  -topics tools/topics-and-qrels/topics.miracl-v1.0-zh-dev.tsv \
  -topicReader TsvString \
  -output runs/run.miracl-v1.0-zh.bm25.topics.miracl-v1.0-zh-dev.txt \
  -bm25 -hits 100 -language zh -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.miracl-v1.0-zh-dev.tsv runs/run.miracl-v1.0-zh.bm25.topics.miracl-v1.0-zh-dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.miracl-v1.0-zh-dev.tsv runs/run.miracl-v1.0-zh.bm25.topics.miracl-v1.0-zh-dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MIRACL (Chinese): dev](https://github.com/project-miracl/miracl)                                            | 0.1801    |
| **R@100**                                                                                                    | **BM25**  |
| [MIRACL (Chinese): dev](https://github.com/project-miracl/miracl)                                            | 0.5599    |
