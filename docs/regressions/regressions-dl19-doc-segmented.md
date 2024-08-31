# Anserini Regressions: TREC 2019 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on segmented documents

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2019 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2019.html).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](../../docs/experiments-msmarco-doc.md).

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is first segmented into passages, each passage is treated as a unit of indexing
+ **Expansion Condition:** none

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery), in the context of doc2query-T5.
In the passage (i.e., segment) indexing condition, we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking; this is known as the MaxP technique.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl19-doc-segmented.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl19-doc-segmented.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](../../docs/experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-segmented \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-segmented &
```

The directory `/path/to/msmarco-doc-segmented/` should be a directory containing the segmented corpus in Anserini's jsonl format.
See [this page](../../docs/experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-default.topics.dl19-doc.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-default+rm3.topics.dl19-doc.txt \
  -bm25 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-default+rocchio.topics.dl19-doc.txt \
  -bm25 -rocchio -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-default+rocchio-neg.topics.dl19-doc.txt \
  -bm25 -rocchio -rocchio.useNegative -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-default+ax.topics.dl19-doc.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-default+prf.topics.dl19-doc.txt \
  -bm25 -bm25prf -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-tuned.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-tuned+rm3.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-tuned+rocchio.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -rocchio -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-tuned+rocchio-neg.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -rocchio -rocchio.useNegative -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-tuned+ax.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -axiom -axiom.deterministic -rerankCutoff 20 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented.bm25-tuned+prf.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -bm25prf -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rm3.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rm3.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rm3.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rm3.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio-neg.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio-neg.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio-neg.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+rocchio-neg.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+ax.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+ax.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+ax.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+ax.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+prf.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+prf.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+prf.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-default+prf.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rm3.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rm3.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rm3.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rm3.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio-neg.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio-neg.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio-neg.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+rocchio-neg.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+ax.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+ax.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+ax.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+ax.topics.dl19-doc.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+prf.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+prf.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+prf.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-segmented.bm25-tuned+prf.topics.dl19-doc.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.2449    | 0.2892    | 0.2889    | 0.2899    | 0.2981    | 0.2827    | 0.2398    | 0.2655    | 0.2672    | 0.2674    | 0.2975    | 0.2828    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.5302    | 0.5684    | 0.5570    | 0.5626    | 0.5556    | 0.5599    | 0.5389    | 0.5392    | 0.5421    | 0.5430    | 0.5574    | 0.5476    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.3840    | 0.4388    | 0.4415    | 0.4400    | 0.4490    | 0.4476    | 0.3903    | 0.4125    | 0.4107    | 0.4133    | 0.4491    | 0.4361    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.6871    | 0.7368    | 0.7423    | 0.7395    | 0.7764    | 0.7311    | 0.6565    | 0.7037    | 0.7115    | 0.7093    | 0.7526    | 0.7361    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=2.16`, `b=0.61`, tuned in 2020/12 using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval).

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that in the official evaluation for document ranking, all runs were truncated to top-100 hits per query (whereas all top-1000 hits per query were retained for passage ranking).
Thus, average precision is computed to depth 100 (i.e., AP@100); nDCG@10 remains unaffected.
Remember that we keep qrels of _all_ relevance grades, unlike the case for passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.
Here, we retrieve 1000 hits per query, but measure AP at cutoff 100 (e.g., AP@100).
Thus, the experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

Note that [#1721](https://github.com/castorini/anserini/issues/1721) slightly change the results, since we corrected underlying issues with data preparation.
