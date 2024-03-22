# Anserini Regressions: TREC 2023 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on segmented documents

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2023 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2023.html) using the MS MARCO V2 _segmented_ document corpus.
For additional instructions on working with the MS MARCO V2 document corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
An important caveat is that these document judgments were inferred from the passages.
That is, if a passage is relevant, the document containing it is considered relevant.

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each segment in the MS MARCO V2 _segmented_ document corpus is treated as a unit of indexing
+ **Expansion Condition:** none

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl23-doc-segmented.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl23-doc-segmented.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc-segmented \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -threads 24 -storeRaw \
  >& logs/log.msmarco-v2-doc-segmented &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 82 topics for which NIST has provided _inferred_ judgments as part of the [TREC 2023 Deep Learning Track](https://trec.nist.gov/data/deep2023.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl23.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl23.txt \
  -bm25 -rm3 -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl23.txt \
  -bm25 -rocchio -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
target/appassembler/bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl23.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl23.txt

target/appassembler/bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl23.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl23.txt

target/appassembler/bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl23.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl23.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl23.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.1341    | 0.1652    | 0.1658    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.6477    | 0.6467    | 0.6284    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.3405    | 0.3452    | 0.3451    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2884    | 0.3129    | 0.3220    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.5662    | 0.5755    | 0.5834    |
