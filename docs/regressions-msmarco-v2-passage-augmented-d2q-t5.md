# Anserini Regressions: MS MARCO (V2) Passage Ranking

**Models**: BM25 with doc2query-T5 expansions on augmented passages

This page describes regression experiments for passage ranking _on the augmented version_ of the MS MARCO (V2) passage corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we expand the augmented passage corpus with doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-passage-augmented-d2q-t5.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-passage-augmented-d2q-t5.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-augmented-d2q-t5
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage-augmented-d2q-t5 \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 24 -storeRaw \
  >& logs/log.msmarco-v2-passage-augmented-d2q-t5 &
```

The directory `/path/to/msmarco-v2-passage-augmented-d2q-t5/` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
These regression experiments use the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt) and the [dev2 queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1160    | 0.0873    | 0.0882    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1158    | 0.0896    | 0.0900    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1172    | 0.0883    | 0.0893    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1170    | 0.0904    | 0.0908    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.5039    | 0.4774    | 0.4756    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.5158    | 0.4905    | 0.4880    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.7647    | 0.7607    | 0.7648    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.7659    | 0.7649    | 0.7666    |
