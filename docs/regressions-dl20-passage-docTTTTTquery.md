# Anserini: Regressions for [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html) w/ docTTTTTquery

This page describes document expansion experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Passage Ranking Task) on the MS MARCO passage collection using relevance judgments from NIST.
These experimental runs take advantage of [docTTTTTquery](http://doc2query.ai/) expansions.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-passage-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-passage-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -input /path/to/msmarco-passage-docTTTTTquery \
 -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 9 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage-docTTTTTquery &
```

The directory `/path/to/msmarco-passage-docTTTTTquery` should be a directory containing `jsonl` files containing the expanded passage collection.
[Instructions in the docTTTTTquery repo](http://doc2query.ai/) explain how to perform this data preparation.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt \
 -bm25 -bm25.k1 2.18 -bm25.b 0.86 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt \
 -bm25 -bm25.k1 2.18 -bm25.b 0.86 -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recip_rank -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recip_rank -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recip_rank -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recip_rank -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recip_rank -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recip_rank -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      | BM25 (Tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.4074    | 0.4295    | 0.4082    | 0.4296    | 0.4171    | 0.4347    |


NDCG@10                                 | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      | BM25 (Tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.6187    | 0.6172    | 0.6192    | 0.6177    | 0.6265    | 0.6232    |


RR                                      | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      | BM25 (Tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.7326    | 0.7424    | 0.7425    | 0.7422    | 0.7467    | 0.7327    |


R@100                                   | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      | BM25 (Tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.7044    | 0.7153    | 0.7046    | 0.7143    | 0.7044    | 0.7109    |


R@1000                                  | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      | BM25 (Tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.8452    | 0.8699    | 0.8443    | 0.8692    | 0.8393    | 0.8609    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
The setting "tuned" refers to `k1=0.82`, `b=0.68`, tuned on _on the original passages_ using the MS MARCO passage sparse judgments, as described in [this page](experiments-msmarco-passage.md).
The setting "tuned2" refers to `k1=2.18`, `b=0.86`, which was tuned via grid search to optimize recall@1000 directly _on the expanded passages_ using the MS MARCO passage sparse judgments (in 2020/12).
Settings tuned on the MS MARCO passage sparse judgments _may not_ work well on the TREC dense judgments.

Note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for DL20 doc ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., MAP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
