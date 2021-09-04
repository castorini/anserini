# Anserini: Regressions for [MS MARCO Document Ranking (V2)](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page documents regression experiments for document ranking on the MS MARCO Document (V2) corpus, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc-v2.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc-v2.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection MsMarcoDocV2Collection \
 -input /path/to/msmarco-doc-v2 \
 -index indexes/lucene-index.msmarco-doc-v2.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-v2 &
```

The directory `/path/to/msmarco-doc-v2/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2.bm25-default.topics.msmarco-doc-v2.dev.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2.bm25-default+ax.topics.msmarco-doc-v2.dev.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2.bm25-default+prf.topics.msmarco-doc-v2.dev.txt \
 -bm25 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default.topics.msmarco-doc-v2.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+ax.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+ax.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+ax.topics.msmarco-doc-v2.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+prf.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+prf.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2.bm25-default+prf.topics.msmarco-doc-v2.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1552    | 0.0966    | 0.0665    | 0.0834    |


MRR@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1572    | 0.0974    | 0.0675    | 0.0845    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.5956    | 0.5121    | 0.4075    | 0.4681    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.8054    | 0.7694    | 0.6852    | 0.7385    |
