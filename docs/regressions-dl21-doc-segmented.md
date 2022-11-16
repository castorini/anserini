# Anserini Regressions: TREC 2021 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on segmented documents

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2021 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2021.html) using the MS MARCO V2 _segmented_ document collection.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO V2 document collection, refer to [this page](experiments-msmarco-v2.md).

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each segment in the MS MARCO V2 _segmented_ document collection is treated as a unit of indexing
+ **Expansion Condition:** none

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl21-doc-segmented.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl21-doc-segmented.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc-segmented \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 24 -storeRaw \
  >& logs/log.msmarco-v2-doc-segmented &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 57 topics for which NIST has provided judgments as part of the TREC 2021 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2021.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl21.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl21.txt \
  -bm25 -rm3 -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc-segmented/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl21.txt \
  -bm25 -rocchio -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rm3.topics.dl21.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl21.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented.bm25-default+rocchio.topics.dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2436    | 0.2936    | 0.2974    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.8937    | 0.9076    | 0.9099    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.5776    | 0.6189    | 0.6067    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.3478    | 0.3890    | 0.3957    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.6930    | 0.7678    | 0.7740    |

Some of these regressions correspond to official TREC 2021 Deep Learning Track "baseline" submissions:

+ `dseg_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `dseg_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
