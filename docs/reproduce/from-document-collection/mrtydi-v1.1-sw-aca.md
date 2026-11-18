# <img src="../../anserini-logo.png" height="30" /> Mr. TyDi (v1.1) &mdash; Swahili

**Anserini reproductions from document collection**

+ **Models**: bag-of-words using `AutoCompositeAnalyzer`
+ **Config**: [`mrtydi-v1.1-sw-aca.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-sw-aca.yaml)
+ **Template**: [`mrtydi-v1.1-sw-aca.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-sw-aca.template)

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Swahili](https://github.com/castorini/mr.tydi) using `AutoCompositeAnalyzer`.
This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-sw-aca.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config mrtydi-v1.1-sw-aca
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-sw \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.mrtydi-v1.1-swahili-aca/ \
  -storePositions -storeDocvectors -storeRaw -language sw -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-sw &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-swahili-aca/ \
  -topics mrtydi-v1.1-sw.train \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-swahili-aca.model-bm25.topics-mrtydi-v1.1-sw.train.txt \
  -bm25 -hits 100 -language sw -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-swahili-aca/ \
  -topics mrtydi-v1.1-sw.dev \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-swahili-aca.model-bm25.topics-mrtydi-v1.1-sw.dev.txt \
  -bm25 -hits 100 -language sw -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-swahili-aca/ \
  -topics mrtydi-v1.1-sw.test \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-swahili-aca.model-bm25.topics-mrtydi-v1.1-sw.test.txt \
  -bm25 -hits 100 -language sw -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-sw.train runs/run.lucene-inverted.mrtydi-v1.1-swahili-aca.model-bm25.topics-mrtydi-v1.1-sw.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-sw.dev runs/run.lucene-inverted.mrtydi-v1.1-swahili-aca.model-bm25.topics-mrtydi-v1.1-sw.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-sw.test runs/run.lucene-inverted.mrtydi-v1.1-swahili-aca.model-bm25.topics-mrtydi-v1.1-sw.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                       | **BM25**   |
|:------------------------------------------------------------------|:----------:|
| [Mr. TyDi (Swahili): train](https://github.com/castorini/mr.tydi) | 0.2997     |
| [Mr. TyDi (Swahili): dev](https://github.com/castorini/mr.tydi)   | 0.2928     |
| [Mr. TyDi (Swahili): test](https://github.com/castorini/mr.tydi)  | 0.4257     |
| **R@100**                                                         | **BM25**   |
| [Mr. TyDi (Swahili): train](https://github.com/castorini/mr.tydi) | 0.7020     |
| [Mr. TyDi (Swahili): dev](https://github.com/castorini/mr.tydi)   | 0.6984     |
| [Mr. TyDi (Swahili): test](https://github.com/castorini/mr.tydi)  | 0.8396     |
