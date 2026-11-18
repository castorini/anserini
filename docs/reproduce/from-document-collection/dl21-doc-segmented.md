# <img src="../../anserini-logo.png" height="30" /> TREC 2021 Deep Learning Track (Document)

**Anserini reproductions from document collection**

+ **Models**: bag-of-words on segmented documents
+ **Config**: [`dl21-doc-segmented.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.yaml)
+ **Template**: [`dl21-doc-segmented.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/dl21-doc-segmented.template)

This page describes reproductions from the raw document collection on the [TREC 2021 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2021.html) using the MS MARCO V2 _segmented_ document corpus.
For additional instructions on working with the MS MARCO V2 document corpus, refer to [this page](../../../docs/experiments-msmarco-v2.md).

There are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each segment in the MS MARCO V2 _segmented_ document corpus is treated as a unit of indexing
+ **Expansion Condition:** none

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/dl21-doc-segmented.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config dl21-doc-segmented
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc-segmented \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented/ \
  -storeRaw \
  >& logs/log.msmarco-v2-doc-segmented &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).
The regression experiments here evaluate on the 57 topics for which NIST has provided judgments as part of the [TREC 2021 Deep Learning Track](https://trec.nist.gov/data/deep2021.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented/ \
  -topics dl21 \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default.topics-dl21.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented/ \
  -topics dl21 \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rm3.topics-dl21.txt \
  -bm25 -rm3 -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented/ \
  -topics dl21 \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rocchio.topics-dl21.txt \
  -bm25 -rocchio -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m map dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default.topics-dl21.txt
bin/trec_eval -c -m recall.100 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default.topics-dl21.txt
bin/trec_eval -c -m recall.1000 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default.topics-dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default.topics-dl21.txt

bin/trec_eval -c -M 100 -m map dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rm3.topics-dl21.txt
bin/trec_eval -c -m recall.100 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rm3.topics-dl21.txt
bin/trec_eval -c -m recall.1000 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rm3.topics-dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rm3.topics-dl21.txt

bin/trec_eval -c -M 100 -m map dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rocchio.topics-dl21.txt
bin/trec_eval -c -m recall.100 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rocchio.topics-dl21.txt
bin/trec_eval -c -m recall.1000 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rocchio.topics-dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc runs/run.lucene-inverted.msmarco-v2-doc-segmented.model-bm25-default+rocchio.topics-dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                          | **BM25 (default)** | **+RM3**   | **+Rocchio** |
|:---------------------------------------------------------------------|:------------------:|:----------:|:------------:|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.2436             | 0.2936     | 0.2974       |
| **MRR@100**                                                          | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.8937             | 0.9076     | 0.9099       |
| **nDCG@10**                                                          | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.5776             | 0.6189     | 0.6067       |
| **R@100**                                                            | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.3478             | 0.3890     | 0.3957       |
| **R@1000**                                                           | **BM25 (default)** | **+RM3**   | **+Rocchio** |
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning) | 0.6930             | 0.7678     | 0.7740       |

Some of these regressions correspond to official TREC 2021 Deep Learning Track "baseline" submissions:

+ `dseg_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `dseg_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
