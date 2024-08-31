# Anserini Regressions: MIRACL (v1.0) &mdash; Korean

**Models**: bag-of-words approaches using `AutoCompositeAnalyzer`

This page documents BM25 regression experiments for [MIRACL (v1.0) &mdash; Korean](https://github.com/project-miracl/miracl) using `AutoCompositeAnalyzer`.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/miracl-v1.0-ko-aca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/miracl-v1.0-ko-aca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ko-aca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/miracl-v1.0-ko \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.miracl-v1.0-ko-aca/ \
  -storePositions -storeDocvectors -storeRaw -language ko -useAutoCompositeAnalyzer \
  >& logs/log.miracl-v1.0-ko &
```

See [this page](https://github.com/project-miracl/miracl) for more details about the MIRACL corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.miracl-v1.0-ko-aca/ \
  -topics tools/topics-and-qrels/topics.miracl-v1.0-ko-dev.tsv \
  -topicReader TsvInt \
  -output runs/run.miracl-v1.0-ko.bm25.topics.miracl-v1.0-ko-dev.txt \
  -bm25 -hits 100 -language ko -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.miracl-v1.0-ko-dev.tsv runs/run.miracl-v1.0-ko.bm25.topics.miracl-v1.0-ko-dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.miracl-v1.0-ko-dev.tsv runs/run.miracl-v1.0-ko.bm25.topics.miracl-v1.0-ko-dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MIRACL (Korean): dev](https://github.com/project-miracl/miracl)                                             | 0.4485    |
| **R@100**                                                                                                    | **BM25**  |
| [MIRACL (Korean): dev](https://github.com/project-miracl/miracl)                                             | 0.8218    |
