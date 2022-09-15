# Anserini Regressions: TREC 2020 Deep Learning Track (Document)

**Models**: BM25 on segmented documents with doc2query-T5 expansions

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2020 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2020.html).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is first segmented into passages, each passage is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery), in the context of doc2query-T5.
In the passage (i.e., segment) indexing condition, we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking; this is known as the MaxP technique.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-doc-segmented-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-doc-segmented-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented-docTTTTTquery
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-segmented-docTTTTTquery \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-segmented-docTTTTTquery &
```

The directory `/path/to/msmarco-doc-segmented-docTTTTTquery/` should be a directory containing the expanded segmented corpus in Anserini's jsonl format.
See [this page](experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default.topics.dl20.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rm3.topics.dl20.txt \
  -bm25 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt \
  -bm25 -rocchio -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned.topics.dl20.txt \
  -bm25 -bm25.k1 2.56 -bm25.b 0.59 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 2.56 -bm25.b 0.59 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt \
  -bm25 -bm25.k1 2.56 -bm25.b 0.59 -rocchio -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.4150    | 0.4271    | 0.4297    | 0.4047    | 0.4016    | 0.4084    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.5957    | 0.5851    | 0.5873    | 0.5943    | 0.5711    | 0.5809    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.6201    | 0.6442    | 0.6475    | 0.6195    | 0.6383    | 0.6432    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.8046    | 0.8266    | 0.8365    | 0.7968    | 0.8156    | 0.8233    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=2.56`, `b=0.59`, tuned in 2020/12 using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval).

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that in the official evaluation for document ranking, all runs were truncated to top-100 hits per query (whereas all top-1000 hits per query were retained for passage ranking).
Thus, average precision is computed to depth 100 (i.e., AP@100); nDCG@10 remains unaffected.
Remember that we keep qrels of _all_ relevance grades, unlike the case for passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.
Here, we retrieve 1000 hits per query, but measure AP at cutoff 100 (e.g., AP@100).
Thus, the experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2102.07662).

Note that [#1721](https://github.com/castorini/anserini/issues/1721) slightly change the results, since we corrected underlying issues with data preparation.
