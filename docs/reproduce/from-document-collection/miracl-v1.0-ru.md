# <img src="../../anserini-logo.png" height="30" /> MIRACL (v1.0) &mdash; Russian

**Anserini reproductions from document collection**

+ **Models**: BM25
+ **Config**: [`miracl-v1.0-ru.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ru.yaml)
+ **Template**: [`miracl-v1.0-ru.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/miracl-v1.0-ru.template)

This page describes reproductions from the raw document collection on [MIRACL (v1.0) &mdash; Russian](https://github.com/project-miracl/miracl).

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/miracl-v1.0-ru.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config miracl-v1.0-ru
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection MrTyDiCollection \
  -input /path/to/miracl-v1.0-ru \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.miracl-v1.0-ru/ \
  -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.miracl-v1.0-ru &
```

See [this page](https://github.com/project-miracl/miracl) for more details about the MIRACL corpus.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.miracl-v1.0-ru/ \
  -topics miracl-v1.0-ru-dev \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.miracl-v1.0-ru.model-bm25.topics-miracl-v1.0-ru-dev.txt \
  -bm25 -hits 100 -language ru &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 miracl-v1.0-ru-dev runs/run.lucene-inverted.miracl-v1.0-ru.model-bm25.topics-miracl-v1.0-ru-dev.txt
bin/trec_eval -c -m recall.100 miracl-v1.0-ru-dev runs/run.lucene-inverted.miracl-v1.0-ru.model-bm25.topics-miracl-v1.0-ru-dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                       | **BM25**   |
|:------------------------------------------------------------------|:----------:|
| [MIRACL (Russian): dev](https://github.com/project-miracl/miracl) | 0.3342     |
| **R@100**                                                         | **BM25**   |
| [MIRACL (Russian): dev](https://github.com/project-miracl/miracl) | 0.6614     |
