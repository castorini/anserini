# Anserini Regressions: TREC 2022 Deep Learning Track (Document)

**Models**: BM25 on complete documents with doc2query-T5 expansions

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2022 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2022.html) using the MS MARCO V2 document corpus (with doc2query-T5 expansions).
For additional instructions on working with the MS MARCO V2 document corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
An important caveat is that these document judgments were inferred from the passages.
That is, if a passage is relevant, the document containing it is considered relevant.

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each document in the MS MARCO V2 document corpus is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl22-doc.d2q-t5.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl22-doc.d2q-t5.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl22-doc.d2q-t5
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
The regression experiments here evaluate on the 76 topics for which NIST has provided _inferred_ judgments as part of the [TREC 2022 Deep Learning Track](https://trec.nist.gov/data/deep2022.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc.d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl22.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl22.txt \
  -hits 1000 -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc.d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl22.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl22.txt \
  -hits 1000 -bm25 -rm3 -collection MsMarcoV2DocCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc.d2q-t5/ \
  -topics tools/topics-and-qrels/topics.dl22.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl22.txt \
  -hits 1000 -bm25 -rocchio -collection MsMarcoV2DocCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl22.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl22.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl22.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default.topics.dl22.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl22.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl22.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl22.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rm3.topics.dl22.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl22.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl22.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl22.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-doc.txt runs/run.msmarco-v2-doc-d2q-t5.bm25-default+rocchio.topics.dl22.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL22 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.0977    | 0.0904    | 0.0944    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.6666    | 0.5657    | 0.5700    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.3539    | 0.2758    | 0.2797    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2041    | 0.1957    | 0.2022    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.4301    | 0.4263    | 0.4338    |
