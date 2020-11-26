# Anserini: Regressions for [DL19 (Passage)](https://github.com/microsoft/TREC-2019-Deep-Learning)

This page describes experiments, integrated into Anserini's regression testing framework, for the TREC 2019 Deep Learning Track (Passage Ranking Task) on the MS MARCO passage collection using relevance judgments from NIST.
Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl19-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl19-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -input /path/to/dl19-passage \
 -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 9 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.dl19-passage &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, which is in `tsv` format.
[This page](experiments-msmarco-passage.md) explains how to perform this conversion.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-default.topics.dl19-passage.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-default+rm3.topics.dl19-passage.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-default+ax.topics.dl19-passage.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-default+prf.topics.dl19-passage.txt \
 -bm25 -bm25prf &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-tuned.topics.dl19-passage.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-tuned+rm3.topics.dl19-passage.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-tuned+ax.topics.dl19-passage.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
 -output runs/run.dl19-passage.bm25-tuned+prf.topics.dl19-passage.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-default.topics.dl19-passage.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-default+rm3.topics.dl19-passage.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-default+ax.topics.dl19-passage.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-default+prf.topics.dl19-passage.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-tuned.topics.dl19-passage.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-tuned+rm3.topics.dl19-passage.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-tuned+ax.topics.dl19-passage.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.dl19-passage.bm25-tuned+prf.topics.dl19-passage.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)| 0.3773    | 0.4270    | 0.4651    | 0.4533    | 0.3766    | 0.4249    | 0.4722    | 0.4522    |


NDCG@10                                 | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)| 0.5058    | 0.5180    | 0.5511    | 0.5372    | 0.4973    | 0.5231    | 0.5461    | 0.5536    |


RR                                      | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)| 0.8245    | 0.8167    | 0.7736    | 0.8170    | 0.8457    | 0.8229    | 0.8218    | 0.8178    |


R@100                                   | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)| 0.4531    | 0.4761    | 0.4995    | 0.4974    | 0.4603    | 0.4747    | 0.5065    | 0.4969    |


R@1000                                  | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)| 0.7389    | 0.7882    | 0.8129    | 0.7845    | 0.7384    | 0.7762    | 0.8094    | 0.7894    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=0.82`, `b=0.68` (see [this page](experiments-msmarco-passage.md) for more details about tuning).

