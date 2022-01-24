# Anserini: Regressions for [MS MARCO (V2) Document Ranking](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page describes regression experiments for document ranking _on the segmented version_ of the MS MARCO (V2) document corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover bag-of-words baselines.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-doc-segmented.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-doc-segmented.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc-segmented \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-v2-doc-segmented &
```

The directory `/path/to/msmarco-v2-doc-segmented/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
These regression experiments use the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt) and the [dev2 queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt \
  -bm25 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev.txt \
  -bm25 -bm25prf -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -bm25prf -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.msmarco-v2-doc.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+ax.topics.msmarco-v2-doc.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.bm25-default+prf.topics.msmarco-v2-doc.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1875    | 0.1648    | 0.1344    | 0.1528    |
[MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1903    | 0.1679    | 0.1345    | 0.1565    |


MRR@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1896    | 0.1664    | 0.1360    | 0.1547    |
[MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1930    | 0.1701    | 0.1366    | 0.1587    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.6555    | 0.6558    | 0.5593    | 0.5945    |
[MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.6629    | 0.6561    | 0.5800    | 0.6134    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.8542    | 0.8605    | 0.8161    | 0.8270    |
[MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.8549    | 0.8633    | 0.8210    | 0.8280    |
