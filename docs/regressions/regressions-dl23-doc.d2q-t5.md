# Anserini Regressions: TREC 2023 Deep Learning Track (Document)

**Models**: BM25 on complete documents with doc2query-T5 expansions

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2023 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2023.html) using the MS MARCO V2 document corpus (with doc2query-T5 expansions).
For additional instructions on working with the MS MARCO V2 document corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
An important caveat is that these document judgments were inferred from the passages.
That is, if a passage is relevant, the document containing it is considered relevant.

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each document in the MS MARCO V2 document corpus is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl23-doc.d2q-t5.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl23-doc.d2q-t5.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl23-doc.d2q-t5
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc-d2q-t5 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-doc.d2q-t5/ \
  -storeRaw \
  >& logs/log.msmarco-v2-doc-d2q-t5 &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 82 topics for which NIST has provided _inferred_ judgments as part of the [TREC 2023 Deep Learning Track](https://trec.nist.gov/data/deep2023.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc.d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl23.txt \
  -hits 1000 -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc.d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl23.txt \
  -hits 1000 -bm25 -rm3 -collection MsMarcoV2DocCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc.d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl23.txt \
  -hits 1000 -bm25 -rocchio -collection MsMarcoV2DocCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl23.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl23.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl23.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl23.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl23.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.1273    | 0.1246    | 0.1292    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.6183    | 0.4597    | 0.4713    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.3511    | 0.2681    | 0.2808    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2773    | 0.2905    | 0.2933    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.5549    | 0.5616    | 0.5674    |
