# <img src="../../anserini-logo.png" height="30" /> Mr. TyDi (v1.1) &mdash; Thai

**Anserini reproductions from document collection**

+ **Models**: BM25 using `AutoCompositeAnalyzer`
+ **Config**: [`mrtydi-v1.1-th-aca.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-th-aca.yaml)
+ **Template**: [`mrtydi-v1.1-th-aca.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-th-aca.template)

This page describes reproductions from the raw document collection on [Mr. TyDi (v1.1) &mdash; Thai](https://github.com/castorini/mr.tydi).

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-th-aca.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config mrtydi-v1.1-th-aca
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-th \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.mrtydi-v1.1-thai-aca/ \
  -storePositions -storeDocvectors -storeRaw -language th -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-th &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-thai-aca/ \
  -topics mrtydi-v1.1-th.train \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-thai-aca.model-bm25.topics-mrtydi-v1.1-th.train.txt \
  -bm25 -hits 100 -language th -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-thai-aca/ \
  -topics mrtydi-v1.1-th.dev \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-thai-aca.model-bm25.topics-mrtydi-v1.1-th.dev.txt \
  -bm25 -hits 100 -language th -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-thai-aca/ \
  -topics mrtydi-v1.1-th.test \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-thai-aca.model-bm25.topics-mrtydi-v1.1-th.test.txt \
  -bm25 -hits 100 -language th -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-th.train runs/run.lucene-inverted.mrtydi-v1.1-thai-aca.model-bm25.topics-mrtydi-v1.1-th.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-th.dev runs/run.lucene-inverted.mrtydi-v1.1-thai-aca.model-bm25.topics-mrtydi-v1.1-th.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-th.test runs/run.lucene-inverted.mrtydi-v1.1-thai-aca.model-bm25.topics-mrtydi-v1.1-th.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                    | **BM25**   |
|:---------------------------------------------------------------|:----------:|
| [Mr. TyDi (Thai): train](https://github.com/castorini/mr.tydi) | 0.3780     |
| [Mr. TyDi (Thai): dev](https://github.com/castorini/mr.tydi)   | 0.4034     |
| [Mr. TyDi (Thai): test](https://github.com/castorini/mr.tydi)  | 0.4468     |
| **R@100**                                                      | **BM25**   |
| [Mr. TyDi (Thai): train](https://github.com/castorini/mr.tydi) | 0.8690     |
| [Mr. TyDi (Thai): dev](https://github.com/castorini/mr.tydi)   | 0.8751     |
| [Mr. TyDi (Thai): test](https://github.com/castorini/mr.tydi)  | 0.8772     |
