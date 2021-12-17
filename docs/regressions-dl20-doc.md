# Anserini: Regressions for [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)

This page describes experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Document Ranking Task) on the MS MARCO document collection using relevance judgments from NIST.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

Note that there are four different regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** none

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection \
  -collection CleanTrecCollection \
  -input /path/to/msmacro-doc \
  -index indexes/lucene-index.msmarco-doc \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmacro-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc \
  -topicreader TsvInt  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -output runs/run.msmacro-doc.bm25-default.topics.dl20.txt \
  -bm25 -hits 100 &

nohup target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc \
  -topicreader TsvInt  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -output runs/run.msmacro-doc.bm25-default+rm3.topics.dl20.txt \
  -bm25 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc \
  -topicreader TsvInt  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -output runs/run.msmacro-doc.bm25-tuned.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -hits 100 &

nohup target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc \
  -topicreader TsvInt  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -output runs/run.msmacro-doc.bm25-tuned+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc \
  -topicreader TsvInt  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -output runs/run.msmacro-doc.bm25-tuned2.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -hits 100 &

nohup target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc \
  -topicreader TsvInt  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -output runs/run.msmacro-doc.bm25-tuned2+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rm3 -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rm3.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.3791    | 0.4006    | 0.3630    | 0.3588    | 0.3583    | 0.3618    |


NDCG@10                                 | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.5271    | 0.5248    | 0.5087    | 0.5117    | 0.5078    | 0.5202    |


MRR                                     | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.8521    | 0.8541    | 0.8641    | 0.8188    | 0.8541    | 0.8458    |


R@100                                   | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.6110    | 0.6392    | 0.5926    | 0.5983    | 0.5860    | 0.5998    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=3.44`, `b=0.87`, tuned on 2019/06 and used for TREC 2019 Deep Learning Track baseline runs.
+ The setting "tuned2" refers to `k1=4.46`, `b=0.82`, tuned using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval) on 2019/12; see [this page](experiments-msmarco-doc.md) additional details.

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that retrieval metrics are computed to depth 100 hits per query (as opposed to 1000 hits per query for DL20 passage ranking).
Also, remember that we keep qrels of _all_ relevance grades, unlike the case for DL20 passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.

Some of these regressions correspond to official TREC 2020 Deep Learning Track submissions by team `anserini`:

+ `d_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `d_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
