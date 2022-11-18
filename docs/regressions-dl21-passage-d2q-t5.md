# Anserini Regressions: TREC 2021 Deep Learning Track (Passage)

**Models**: BM25 with doc2query-T5 expansions on original passages

This page describes document expansion experiments (with doc2query-T5), integrated into Anserini's regression testing framework, on the [TREC 2021 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2021.html) using the MS MARCO V2 passage collection.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO V2 passage collection, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl21-passage-d2q-t5.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl21-passage-d2q-t5.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-d2q-t5
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage-d2q-t5 \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 24 -storeRaw \
  >& logs/log.msmarco-v2-passage-d2q-t5 &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 53 topics for which NIST has provided judgments as part of the TREC 2021 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2021.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl21.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl21.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl21.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.1874    | 0.2285    | 0.2282    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.6848    | 0.6548    | 0.6780    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.4816    | 0.5098    | 0.5159    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.4076    | 0.4499    | 0.4444    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.7078    | 0.7537    | 0.7548    |
