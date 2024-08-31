# Anserini Regressions: TREC 2021 DL Track on V2.1 Corpus

**Models**: various bag-of-words approaches on segmented documents

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2021 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2021.html) using the MS MARCO V2.1 _segmented_ document corpus, which was derived from the MS MARCO V2 segmented document corpus and prepared for the TREC 2024 RAG Track.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

Here, we cover bag-of-words baselines where each _segment_ in the MS MARCO V2.1 segmented document corpus is treated as a unit of indexing.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl21-doc-segmented-msmarco-v2.1.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl21-doc-segmented-msmarco-v2.1.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented-msmarco-v2.1
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2.1-doc-segmented \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -storeRaw \
  >& logs/log.msmarco-v2.1-doc-segmented &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 57 topics for which NIST has provided judgments as part of the [TREC 2021 Deep Learning Track](https://trec.nist.gov/data/deep2021.html), but projected over to the V2.1 version of the corpus.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.dl21.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.dl21.txt \
  -bm25 -rm3 -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.dl21.txt \
  -bm25 -rocchio -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.dl21.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.dl21.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.dl21.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.dl21.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.dl21.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.dl21.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.dl21.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.dl21.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2609    | 0.3084    | 0.3123    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.9026    | 0.9289    | 0.9289    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.5778    | 0.6137    | 0.6048    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.3811    | 0.4200    | 0.4260    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.7115    | 0.7839    | 0.7924    |

Some of these regressions correspond to official TREC 2021 Deep Learning Track "baseline" submissions:

+ `dseg_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `dseg_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
