# Anserini Regressions: TREC 2023 Deep Learning Track (Passage)

**Models**: various bag-of-words approaches on original passages

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2023 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2023.html) using the MS MARCO V2 passage corpus.
For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl23-passage.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl23-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl23-passage
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -storeRaw \
  >& logs/log.msmarco-v2-passage &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 82 topics for which NIST has provided judgments as part of the [TREC 2023 Deep Learning Track](https://trec.nist.gov/data/deep2023.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.dl23.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl23.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.dl23.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl23.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl23.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl23.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl23.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl23.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl23.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl23.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl23.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl23.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.0793    | 0.0806    | 0.0801    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3803    | 0.3467    | 0.3512    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.2627    | 0.2602    | 0.2653    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.2329    | 0.2279    | 0.2379    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.4346    | 0.4748    | 0.4810    |
