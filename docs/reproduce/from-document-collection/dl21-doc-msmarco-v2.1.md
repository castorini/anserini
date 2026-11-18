# <img src="../../anserini-logo.png" height="30" /> TREC 2021 DL Track on V2.1 Corpus

**Anserini reproductions from document collection**

+ **Models**: bag-of-words on complete documents
+ **Config**: [`dl21-doc-msmarco-v2.1.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-msmarco-v2.1.yaml)
+ **Template**: [`dl21-doc-msmarco-v2.1.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/dl21-doc-msmarco-v2.1.template)

This page describes reproductions from the raw document collection on the [TREC 2021 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2021.html) using the MS MARCO V2.1 document corpus.
This corpus was derived from the MS MARCO V2 document corpus and prepared for the TREC 2024 RAG Track.
In these experiments, each document in the MS MARCO V2.1 document corpus is treated as a unit of indexing.

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/dl21-doc-msmarco-v2.1.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config dl21-doc-msmarco-v2.1
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2.1-doc \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -storeRaw \
  >& logs/log.msmarco-v2.1-doc &
```

The setting of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).
The regression experiments here evaluate on the 57 topics for which NIST has provided judgments as part of the [TREC 2021 Deep Learning Track](https://trec.nist.gov/data/deep2021.html), but projected over to the V2.1 version of the corpus.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -topics dl21 \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default.topics-dl21.txt \
  -hits 1000 -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -topics dl21 \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rm3.topics-dl21.txt \
  -hits 1000 -bm25 -rm3 -collection MsMarcoV2DocCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -topics dl21 \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rocchio.topics-dl21.txt \
  -hits 1000 -bm25 -rocchio -collection MsMarcoV2DocCollection &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m map dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default.topics-dl21.txt
bin/trec_eval -c -m recall.100 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default.topics-dl21.txt
bin/trec_eval -c -m recall.1000 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default.topics-dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default.topics-dl21.txt

bin/trec_eval -c -M 100 -m map dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rm3.topics-dl21.txt
bin/trec_eval -c -m recall.100 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rm3.topics-dl21.txt
bin/trec_eval -c -m recall.1000 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rm3.topics-dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rm3.topics-dl21.txt

bin/trec_eval -c -M 100 -m map dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rocchio.topics-dl21.txt
bin/trec_eval -c -m recall.100 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rocchio.topics-dl21.txt
bin/trec_eval -c -m recall.1000 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rocchio.topics-dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc-msmarco-v2.1 runs/run.lucene-inverted.msmarco-v2.1-doc.model-bm25-default+rocchio.topics-dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                          | **BM25 (default)** | **+RM3**   | **+Rocchio** |
|:---------------------------------------------------------------------|:------------------:|:----------:|:------------:|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.2281             | 0.2652     | 0.2678       |
| **MRR@100**                                                          | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.8466             | 0.8044     | 0.8030       |
| **nDCG@10**                                                          | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.5183             | 0.5324     | 0.5393       |
| **R@100**                                                            | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.3502             | 0.3727     | 0.3867       |
| **R@1000**                                                           | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.6915             | 0.7623     | 0.7623       |
