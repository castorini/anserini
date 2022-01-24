# Anserini: Regressions for [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)

This page describes baseline experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Passage Ranking Task) on the MS MARCO passage collection using relevance judgments from NIST.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-passage \
  -index indexes/lucene-index.msmarco-passage/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage &
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
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default.topics.dl20.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+rm3.topics.dl20.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+ax.topics.dl20.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+prf.topics.dl20.txt \
  -bm25 -bm25prf &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+ax.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+prf.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.2856    | 0.3019    | 0.3240    | 0.3117    | 0.2876    | 0.3056    | 0.3322    | 0.3136    |


nDCG@10                                 | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.4796    | 0.4821    | 0.4834    | 0.4721    | 0.4876    | 0.4808    | 0.5027    | 0.4788    |


MRR                                     | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.6585    | 0.6360    | 0.6096    | 0.6157    | 0.6594    | 0.6278    | 0.6328    | 0.6252    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.5599    | 0.6046    | 0.6428    | 0.5783    | 0.5669    | 0.6333    | 0.6468    | 0.5782    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.7863    | 0.8217    | 0.8483    | 0.8074    | 0.8031    | 0.8286    | 0.8455    | 0.8121    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, tuned using the MS MARCO passage sparse judgments, as described in [this page](experiments-msmarco-passage.md).

Settings tuned on the MS MARCO passage sparse judgments _may not_ work well on the TREC dense judgments.

Note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for DL20 doc ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., MAP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).

Some of these regressions correspond to official TREC 2020 Deep Learning Track submissions by team `anserini`:

+ `p_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `p_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
