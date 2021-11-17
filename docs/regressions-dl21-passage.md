# Anserini: Regressions for [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)

This page describes baseline experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Passage Ranking Task) on the MS MARCO passage collection using relevance judgments from NIST.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection MsMarcoV2PassageCollection \
 -input /path/to/msmarco-v2-passage \
 -index indexes/lucene-index.msmarco-v2-passage.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-v2-passage &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, which is in `tsv` format.
[This page](experiments-msmarco-passage.md) explains how to perform this conversion.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.msmarco-v2-passage.bm25-default.topics.dl21.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl21.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.msmarco-v2-passage.bm25-default+ax.topics.dl21.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
 -output runs/run.msmarco-v2-passage.bm25-default+prf.topics.dl21.txt \
 -bm25 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -l 2 -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -l 2 -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -l 2 -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -c -l 2 -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.1357    | 0.1632    | 0.1907    | 0.1821    |


MRR@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.5060    | 0.4925    | 0.5733    | 0.5532    |


NDCG@10                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.4458    | 0.4480    | 0.4851    | 0.4740    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.3332    | 0.3381    | 0.3577    | 0.3535    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.6113    | 0.6611    | 0.6785    | 0.6531    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, tuned using the MS MARCO passage sparse judgments, as described in [this page](experiments-msmarco-passage.md).

Settings tuned on the MS MARCO passage sparse judgments _may not_ work well on the TREC dense judgments.

Note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for DL20 doc ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., MAP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).

Some of these regressions correspond to official TREC 2020 Deep Learning Track submissions by team `anserini`:

+ `p_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `p_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
