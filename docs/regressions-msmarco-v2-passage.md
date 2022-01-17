# Anserini: Regressions for [MS MARCO (V2) Passage Ranking](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page describes regression experiments for passage ranking on the MS MARCO (V2) passage corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover bag-of-words baselines.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-v2-passage &
```

The directory `/path/to/msmarco-v2-passage/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
These regression experiments use the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt) and the [dev2 queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev2.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev.txt \
  -bm25 -bm25prf &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev2.txt \
  -bm25 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+ax.topics.msmarco-v2-passage.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+prf.topics.msmarco-v2-passage.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.0709    | 0.0611    | 0.0592    | 0.0595    |
[MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.0794    | 0.0647    | 0.0642    | 0.0632    |


MRR@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.0719    | 0.0619    | 0.0601    | 0.0607    |
[MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.0802    | 0.0654    | 0.0647    | 0.0640    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.3397    | 0.3377    | 0.3482    | 0.3495    |
[MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.3459    | 0.3435    | 0.3554    | 0.3595    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.5733    | 0.5933    | 0.6064    | 0.5968    |
[MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.5839    | 0.6049    | 0.6254    | 0.6169    |
