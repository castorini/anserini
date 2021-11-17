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
nohup sh target/appassembler/bin/IndexCollection -collection MsMarcoV2DocCollection \
 -input /path/to/dl21-doc \
 -index indexes/lucene-index.msmarco-v2-doc.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.dl21-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.dl21-doc.bm25-default.topics.dl21.txt \
 -hits 1000 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.dl21-doc.bm25-default+rm3.topics.dl21.txt \
 -hits 1000 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.dl21-doc.bm25-default+ax.topics.dl21.txt \
 -hits 1000 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.dl21-doc.bm25-default+prf.topics.dl21.txt \
 -hits 1000 -bm25 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+rm3.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+ax.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+ax.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+ax.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+ax.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+prf.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+prf.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+prf.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.dl21-doc.bm25-default+prf.topics.dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.2126    | 0.2453    | 0.2034    | 0.2079    |


MRR@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.8367    | 0.7994    | 0.7434    | 0.7869    |


NDCG@10                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.5116    | 0.5339    | 0.4804    | 0.4850    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.3195    | 0.3374    | 0.3002    | 0.3096    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.6739    | 0.7335    | 0.7089    | 0.7040    |

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
