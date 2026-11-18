# <img src="../../anserini-logo.png" height="30" /> Mr. TyDi (v1.1) &mdash; Indonesian

**Anserini reproductions from document collection**

+ **Models**: BM25 using `AutoCompositeAnalyzer`
+ **Config**: [`mrtydi-v1.1-id-aca.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-id-aca.yaml)
+ **Template**: [`mrtydi-v1.1-id-aca.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-id-aca.template)

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Indonesian](https://github.com/castorini/mr.tydi) using `AutoCompositeAnalyzer`.

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/mrtydi-v1.1-id-aca.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config mrtydi-v1.1-id-aca
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-id \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.mrtydi-v1.1-indonesian-aca/ \
  -storePositions -storeDocvectors -storeRaw -language id -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-id &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-indonesian-aca/ \
  -topics mrtydi-v1.1-id.train \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-indonesian-aca.model-bm25.topics-mrtydi-v1.1-id.train.txt \
  -bm25 -hits 100 -language id -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-indonesian-aca/ \
  -topics mrtydi-v1.1-id.dev \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-indonesian-aca.model-bm25.topics-mrtydi-v1.1-id.dev.txt \
  -bm25 -hits 100 -language id -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.mrtydi-v1.1-indonesian-aca/ \
  -topics mrtydi-v1.1-id.test \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.mrtydi-v1.1-indonesian-aca.model-bm25.topics-mrtydi-v1.1-id.test.txt \
  -bm25 -hits 100 -language id -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-id.train runs/run.lucene-inverted.mrtydi-v1.1-indonesian-aca.model-bm25.topics-mrtydi-v1.1-id.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-id.dev runs/run.lucene-inverted.mrtydi-v1.1-indonesian-aca.model-bm25.topics-mrtydi-v1.1-id.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 mrtydi-v1.1-id.test runs/run.lucene-inverted.mrtydi-v1.1-indonesian-aca.model-bm25.topics-mrtydi-v1.1-id.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                          | **BM25**   |
|:---------------------------------------------------------------------|:----------:|
| [Mr. TyDi (Indonesian): train](https://github.com/castorini/mr.tydi) | 0.2948     |
| [Mr. TyDi (Indonesian): dev](https://github.com/castorini/mr.tydi)   | 0.2868     |
| [Mr. TyDi (Indonesian): test](https://github.com/castorini/mr.tydi)  | 0.3824     |
| **R@100**                                                            | **BM25**   |
| [Mr. TyDi (Indonesian): train](https://github.com/castorini/mr.tydi) | 0.7962     |
| [Mr. TyDi (Indonesian): dev](https://github.com/castorini/mr.tydi)   | 0.7990     |
| [Mr. TyDi (Indonesian): test](https://github.com/castorini/mr.tydi)  | 0.8504     |
