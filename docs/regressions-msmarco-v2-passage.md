# Anserini Regressions: MS MARCO (V2) Passage Ranking

**Models**: various bag-of-words approaches on original passages

This page describes regression experiments for passage ranking on the MS MARCO (V2) passage corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover bag-of-words baselines.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 24 -storeRaw \
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
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rm3.topics.msmarco-v2-passage.dev2.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
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

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.msmarco-v2-passage.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.0709    | 0.0621    | 0.0622    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.0794    | 0.0651    | 0.0663    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.0719    | 0.0630    | 0.0631    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.0802    | 0.0659    | 0.0671    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.3397    | 0.3370    | 0.3413    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.3459    | 0.3435    | 0.3516    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.5733    | 0.5947    | 0.5968    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.5839    | 0.6062    | 0.6104    |
