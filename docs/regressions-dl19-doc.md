# Anserini: Regressions for [DL19 (Document)](https://github.com/microsoft/TREC-2019-Deep-Learning)

This page describes experiments, integrated into Anserini's regression testing framework, for the TREC 2019 Deep Learning Track (Document Ranking Task) on the MS MARCO document collection using relevance judgments from NIST.
Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl19-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl19-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CleanTrecCollection \
 -input /path/to/dl19-doc \
 -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.dl19-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-default.topics.dl19-doc.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-default+rm3.topics.dl19-doc.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-default+ax.topics.dl19-doc.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-default+prf.topics.dl19-doc.txt \
 -bm25 -bm25prf &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-tuned.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-tuned+rm3.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-tuned+ax.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.dl19-doc.bm25-tuned+prf.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-default.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-default+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-default+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-default+prf.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-tuned.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-tuned+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-tuned+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.dl19-doc.bm25-tuned+prf.topics.dl19-doc.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.3309    | 0.3870    | 0.3516    | 0.3624    | 0.3138    | 0.3697    | 0.3860    | 0.3858    |


NDCG@10                                 | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.5190    | 0.5169    | 0.4730    | 0.5105    | 0.5140    | 0.5485    | 0.5245    | 0.5280    |


RR                                      | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.8046    | 0.7718    | 0.7428    | 0.7775    | 0.8872    | 0.8074    | 0.7492    | 0.8007    |


R@100                                   | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.3948    | 0.4189    | 0.3945    | 0.4004    | 0.3862    | 0.4193    | 0.4399    | 0.4287    |


R@1000                                  | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.6966    | 0.7504    | 0.7323    | 0.7357    | 0.6810    | 0.7282    | 0.7545    | 0.7553    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=3.44`, `b=0.87` (see [this page](experiments-msmarco-doc.md) for more details about tuning).

