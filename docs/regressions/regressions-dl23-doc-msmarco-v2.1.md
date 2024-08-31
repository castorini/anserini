# Anserini Regressions: TREC 2023 DL Track on V2.1 Corpus

**Models**: various bag-of-words approaches on complete documents

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2023 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2023.html) using the MS MARCO V2.1 document corpus, which was derived from the MS MARCO V2 document corpus and prepared for the TREC 2024 RAG Track.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
An important caveat is that these document judgments were inferred from the passages.
That is, if a passage is relevant, the document containing it is considered relevant.

Here, we cover bag-of-words baselines where each document in the MS MARCO V2.1 document corpus is treated as a unit of indexing.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl23-doc-msmarco-v2.1.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl23-doc-msmarco-v2.1.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-msmarco-v2.1
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2.1-doc \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -storeRaw \
  >& logs/log.msmarco-v2.1-doc &
```

The setting of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 82 topics for which NIST has provided _inferred_ judgments as part of the [TREC 2023 Deep Learning Track](https://trec.nist.gov/data/deep2023.html), but projected over to the V2.1 version of the corpus.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc.bm25-default.topics.dl23.txt \
  -hits 1000 -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc.bm25-default+rm3.topics.dl23.txt \
  -hits 1000 -bm25 -rm3 -collection MsMarcoV2DocCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc.bm25-default+rocchio.topics.dl23.txt \
  -hits 1000 -bm25 -rocchio -collection MsMarcoV2DocCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default.topics.dl23.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default.topics.dl23.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default.topics.dl23.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rm3.topics.dl23.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc.bm25-default+rocchio.topics.dl23.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.1089    | 0.1268    | 0.1281    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.5783    | 0.4824    | 0.5054    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2914    | 0.2579    | 0.2679    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2604    | 0.2800    | 0.2889    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.5383    | 0.5529    | 0.5543    |
