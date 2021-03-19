# Anserini: Regressions for [DL20 (Document)](https://trec.nist.gov/data/deep2020.html)

This page describes baseline experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Document Ranking Task) on the MS MARCO document collection using relevance judgments from NIST.
Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl19-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl19-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CleanTrecCollection \
 -input /path/to/dl20-doc \
 -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.dl20-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-default.topics.dl20.txt \
 -bm25 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-default+rm3.topics.dl20.txt \
 -bm25 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-default+ax.topics.dl20.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-default+prf.topics.dl20.txt \
 -bm25 -bm25prf -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-tuned.topics.dl20.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-tuned+rm3.topics.dl20.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-tuned+ax.topics.dl20.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -axiom -axiom.deterministic -rerankCutoff 20 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.dl20-doc.bm25-tuned+prf.topics.dl20.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -bm25prf -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-default.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-default+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-default+ax.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-default+prf.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-tuned.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-tuned+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-tuned+ax.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.dl20-doc.bm25-tuned+prf.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.3791    | 0.4006    | 0.3129    | 0.3472    | 0.3630    | 0.3588    | 0.3461    | 0.3559    |


NDCG@10                                 | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.5271    | 0.5248    | 0.4266    | 0.4670    | 0.5087    | 0.5117    | 0.4934    | 0.4792    |


RR                                      | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.8521    | 0.8541    | 0.7576    | 0.7621    | 0.8641    | 0.8188    | 0.8039    | 0.7758    |


R@100                                   | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)| 0.6110    | 0.6392    | 0.5706    | 0.6104    | 0.5926    | 0.5983    | 0.6092    | 0.6175    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=3.44`, `b=0.87` (see [this page](experiments-msmarco-doc.md) for more details about tuning).

