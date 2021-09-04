# Anserini: Regressions for [MS MARCO Document Ranking (V2) Segmented](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page documents regression experiments for document ranking _on the segmented version_ of the MS MARCO Document (V2) corpus, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc-v2-segmented.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc-v2-segmented.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection MsMarcoDocV2Collection \
 -input /path/to/msmarco-doc-v2-segmented \
 -index indexes/lucene-index.msmarco-doc-v2-segmented.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-v2-segmented &
```

The directory `/path/to/msmarco-doc-v2-segmented/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2-segmented.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2-segmented.bm25-default.topics.msmarco-doc-v2.dev.txt \
 -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2-segmented.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2-segmented.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt \
 -bm25 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2-segmented.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2-segmented.bm25-default+ax.topics.msmarco-doc-v2.dev.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-v2-segmented.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc-v2.dev.txt \
 -output runs/run.msmarco-doc-v2-segmented.bm25-default+prf.topics.msmarco-doc-v2.dev.txt \
 -bm25 -bm25prf -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default.topics.msmarco-doc-v2.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+rm3.topics.msmarco-doc-v2.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+ax.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+ax.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+ax.topics.msmarco-doc-v2.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+prf.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+prf.topics.msmarco-doc-v2.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-doc-v2.dev.txt runs/run.msmarco-doc-v2-segmented.bm25-default+prf.topics.msmarco-doc-v2.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1875    | 0.1648    | 0.1344    | 0.1528    |


MRR@100                                 | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1896    | 0.1664    | 0.1360    | 0.1547    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.6555    | 0.6558    | 0.5593    | 0.5945    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Doc V2: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.8542    | 0.8605    | 0.8161    | 0.8270    |
