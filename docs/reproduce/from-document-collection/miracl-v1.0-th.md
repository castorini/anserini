# <img src="../../anserini-logo.png" height="30" /> MIRACL (v1.0) &mdash; Thai

**Anserini reproductions from raw documents**

+ **Models**: BM25
+ **Config**: [`miracl-v1.0-th.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-th.yaml)
+ **Template**: [`miracl-v1.0-th.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/miracl-v1.0-th.template)

This page documents BM25 regression experiments for [MIRACL (v1.0) &mdash; Thai](https://github.com/project-miracl/miracl).
This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/miracl-v1.0-th.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config miracl-v1.0-th
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection MrTyDiCollection \
  -input /path/to/miracl-v1.0-th \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.miracl-v1.0-th/ \
  -storePositions -storeDocvectors -storeRaw -language th \
  >& logs/log.miracl-v1.0-th &
```

See [this page](https://github.com/project-miracl/miracl) for more details about the MIRACL corpus.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.miracl-v1.0-th/ \
  -topics miracl-v1.0-th-dev \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.miracl-v1.0-th.model-bm25.topics-miracl-v1.0-th-dev.txt \
  -bm25 -hits 100 -language th &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 miracl-v1.0-th-dev runs/run.lucene-inverted.miracl-v1.0-th.model-bm25.topics-miracl-v1.0-th-dev.txt
bin/trec_eval -c -m recall.100 miracl-v1.0-th-dev runs/run.lucene-inverted.miracl-v1.0-th.model-bm25.topics-miracl-v1.0-th-dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                    | **BM25**   |
|:---------------------------------------------------------------|:----------:|
| [MIRACL (Thai): dev](https://github.com/project-miracl/miracl) | 0.4838     |
| **R@100**                                                      | **BM25**   |
| [MIRACL (Thai): dev](https://github.com/project-miracl/miracl) | 0.8874     |
