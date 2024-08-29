# Anserini Regressions: TREC 2020 Deep Learning Track (Document)

**Models**: BM25 on complete documents with doc2query-T5 expansions

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2020 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2020.html).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](../../docs/experiments-msmarco-doc.md).

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl20-doc.docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl20-doc.docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](../../docs/experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl20-doc.docTTTTTquery
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 7 \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-docTTTTTquery \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-doc.docTTTTTquery/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-docTTTTTquery &
```

The directory `/path/to/msmarco-doc-docTTTTTquery/` should be a directory containing the expanded document corpus in Anserini's jsonl format.
See [this page](../../docs/experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.docTTTTTquery/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.dl20.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.docTTTTTquery/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.dl20.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.docTTTTTquery/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt \
  -bm25 -rocchio &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.docTTTTTquery/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.dl20.txt \
  -bm25 -bm25.k1 4.68 -bm25.b 0.87 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.docTTTTTquery/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 4.68 -bm25.b 0.87 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.docTTTTTquery/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt \
  -bm25 -bm25.k1 4.68 -bm25.b 0.87 -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.4230    | 0.4230    | 0.4218    | 0.4099    | 0.4100    | 0.4151    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.5885    | 0.5427    | 0.5416    | 0.5852    | 0.5745    | 0.5733    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.6414    | 0.6554    | 0.6627    | 0.6178    | 0.6132    | 0.6230    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.8403    | 0.8631    | 0.8641    | 0.8105    | 0.8238    | 0.8316    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=4.68`, `b=0.87`, tuned in 2020/12 using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval).

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that in the official evaluation for document ranking, all runs were truncated to top-100 hits per query (whereas all top-1000 hits per query were retained for passage ranking).
Thus, average precision is computed to depth 100 (i.e., AP@100); nDCG@10 remains unaffected.
Remember that we keep qrels of _all_ relevance grades, unlike the case for passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.
Here, we retrieve 1000 hits per query, but measure AP at cutoff 100 (e.g., AP@100).
Thus, the experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2102.07662).

Some of these regressions correspond to official TREC 2020 Deep Learning Track submissions by team `anserini`:

+ `d_d2q_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `d_d2q_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`

Note that [#1721](https://github.com/castorini/anserini/issues/1721) slightly change the results, since we corrected underlying issues with data preparation.
