# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2 Document Ranking

**Anserini reproductions from document collection**

+ **Models**: bag-of-words on segmented documents with doc2query-T5 expansions
+ **Config**: [`msmarco-v2-doc-segmented.d2q-t5.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.d2q-t5.yaml)
+ **Template**: [`msmarco-v2-doc-segmented.d2q-t5.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/msmarco-v2-doc-segmented.d2q-t5.template)

This page describes reproductions from the raw document collection for document ranking on the dev queries using the MS MARCO V2 _segmented_ document corpus (with doc2query-T5 expansions).
For additional instructions on working with the MS MARCO V2 document corpus, refer to [this page](../../../docs/experiments-msmarco-v2.md).

There are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each segment in the MS MARCO V2 _segmented_ document corpus is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/msmarco-v2-doc-segmented.d2q-t5.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config msmarco-v2-doc-segmented.d2q-t5
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc-segmented-d2q-t5 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5/ \
  -storeRaw \
  >& logs/log.msmarco-v2-doc-segmented-d2q-t5 &
```

The directory `/path/to/msmarco-v2-doc-segmented-d2q-t5/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5/ \
  -topics msmarco-v2-doc.dev \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5/ \
  -topics msmarco-v2-doc.dev2 \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev2.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m recall.100 msmarco-v2-doc.dev runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev.txt
bin/trec_eval -c -m recall.1000 msmarco-v2-doc.dev runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev.txt
bin/trec_eval -c -m recall.100 msmarco-v2-doc.dev2 runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev2.txt
bin/trec_eval -c -m recall.1000 msmarco-v2-doc.dev2 runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev2.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.model-bm25-default.topics-msmarco-v2-doc.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                          | **BM25 (default)** |
|:-------------------------------------------------------------------------------------|:------------------:|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)  | 0.2203             |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html) | 0.2205             |
| **MRR@100**                                                                          | **BM25 (default)** |
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)  | 0.2226             |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html) | 0.2234             |
| **R@100**                                                                            | **BM25 (default)** |
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)  | 0.7297             |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html) | 0.7316             |
| **R@1000**                                                                           | **BM25 (default)** |
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)  | 0.8982             |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html) | 0.8952             |
