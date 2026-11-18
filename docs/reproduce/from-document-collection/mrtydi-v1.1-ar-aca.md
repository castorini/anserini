# <img src="../../anserini-logo.png" height="30" /> Mr. TyDi (v1.1) &mdash; Arabic

**Anserini reproductions from document collection**

+ **Models**: BM25 using `AutoCompositeAnalyzer`
+ **Config**: [`mrtydi-v1.1-ar-aca.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ar-aca.yaml)
+ **Template**: [`mrtydi-v1.1-ar-aca.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-ar-aca.template)

This page describes reproductions from the raw document collection on [Mr. TyDi (v1.1) &mdash; Arabic](https://github.com/castorini/mr.tydi).

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-ar-aca.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config mrtydi-v1.1-ar-aca
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-ar \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.mrtydi-v1.1-arabic-aca/ \
  -storePositions -storeDocvectors -storeRaw -language ar -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-ar &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-arabic-aca/ \
  -topics mrtydi-v1.1-ar.train \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-arabic-aca.model-bm25.topics-mrtydi-v1.1-ar.train.txt \
  -bm25 -hits 100 -language ar -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-arabic-aca/ \
  -topics mrtydi-v1.1-ar.dev \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-arabic-aca.model-bm25.topics-mrtydi-v1.1-ar.dev.txt \
  -bm25 -hits 100 -language ar -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-arabic-aca/ \
  -topics mrtydi-v1.1-ar.test \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-arabic-aca.model-bm25.topics-mrtydi-v1.1-ar.test.txt \
  -bm25 -hits 100 -language ar -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-ar.train runs/run.lucene-inverted.mrtydi-v1.1-arabic-aca.model-bm25.topics-mrtydi-v1.1-ar.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-ar.dev runs/run.lucene-inverted.mrtydi-v1.1-arabic-aca.model-bm25.topics-mrtydi-v1.1-ar.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-ar.test runs/run.lucene-inverted.mrtydi-v1.1-arabic-aca.model-bm25.topics-mrtydi-v1.1-ar.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                      | **BM25**   |
|:-----------------------------------------------------------------|:----------:|
| [Mr. TyDi (Arabic): train](https://github.com/castorini/mr.tydi) | 0.3448     |
| [Mr. TyDi (Arabic): dev](https://github.com/castorini/mr.tydi)   | 0.3530     |
| [Mr. TyDi (Arabic): test](https://github.com/castorini/mr.tydi)  | 0.3821     |
| **R@100**                                                        | **BM25**   |
| [Mr. TyDi (Arabic): train](https://github.com/castorini/mr.tydi) | 0.8026     |
| [Mr. TyDi (Arabic): dev](https://github.com/castorini/mr.tydi)   | 0.8061     |
| [Mr. TyDi (Arabic): test](https://github.com/castorini/mr.tydi)  | 0.7986     |
