# Anserini Regressions: MS MARCO (V2) Document Ranking

**Models**: various bag-of-words approaches on complete documents

This page describes regression experiments for document ranking on the MS MARCO (V2) document corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover bag-of-words baselines.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-v2-doc &
```

The directory `/path/to/msmarco-v2-doc/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
These regression experiments use the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt) and the [dev2 queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev.txt \
  -bm25 -bm25prf &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+rocchio.topics.msmarco-v2-doc.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| MAP@100                                                                                                      | BM25 (default)| +RM3      | +Rocchio  | +Ax       | +PRF      |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.1552    | 0.0966    | 0.0965    | 0.0665    | 0.0834    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.1639    | 0.1011    | 0.1037    | 0.0722    | 0.0876    |


| MRR@100                                                                                                      | BM25 (default)| +RM3      | +Rocchio  | +Ax       | +PRF      |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.1572    | 0.0974    | 0.0974    | 0.0675    | 0.0845    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.1659    | 0.1028    | 0.1052    | 0.0733    | 0.0892    |


| R@100                                                                                                        | BM25 (default)| +RM3      | +Rocchio  | +Ax       | +PRF      |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.5956    | 0.5121    | 0.5135    | 0.4075    | 0.4681    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.5970    | 0.5245    | 0.5259    | 0.4192    | 0.4831    |


| R@1000                                                                                                       | BM25 (default)| +RM3      | +Rocchio  | +Ax       | +PRF      |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.8054    | 0.7694    | 0.7697    | 0.6852    | 0.7385    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.8029    | 0.7736    | 0.7762    | 0.6960    | 0.7482    |
