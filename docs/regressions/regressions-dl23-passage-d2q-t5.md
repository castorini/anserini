# Anserini Regressions: TREC 2023 Deep Learning Track (Passage)

**Models**: BM25 with doc2query-T5 expansions on original passages

This page describes document expansion experiments (with doc2query-T5), integrated into Anserini's regression testing framework, on the [TREC 2023 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2023.html) using the MS MARCO V2 passage corpus.
For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl23-passage-d2q-t5.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl23-passage-d2q-t5.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-d2q-t5
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage-d2q-t5 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -threads 24 -storeRaw \
  >& logs/log.msmarco-v2-passage-d2q-t5 &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 82 topics for which NIST has provided judgments as part of the [TREC 2023 Deep Learning Track](https://trec.nist.gov/data/deep2023.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl23.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl23.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl23.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
```

Evaluation can be performed using `trec_eval`:

```
target/appassembler/bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl23.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default.topics.dl23.txt

target/appassembler/bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl23.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rm3.topics.dl23.txt

target/appassembler/bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl23.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-d2q-t5.bm25-default+rocchio.topics.dl23.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.1085    | 0.1135    | 0.1109    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.5234    | 0.4961    | 0.4650    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3156    | 0.3132    | 0.3136    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.2946    | 0.3114    | 0.3145    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.5608    | 0.5978    | 0.5942    |
