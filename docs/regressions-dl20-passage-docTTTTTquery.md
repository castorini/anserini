# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Models**: BM25 with doc2query-T5 expansions

This page describes document expansion experiments, integrated into Anserini's regression testing framework, on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2020.html).
These experiments take advantage of [docTTTTTquery](http://doc2query.ai/) (also called doc2query-T5) expansions.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-passage-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-passage-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-docTTTTTquery
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-passage-docTTTTTquery \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage-docTTTTTquery &
```

The directory `/path/to/msmarco-passage-docTTTTTquery` should be a directory containing `jsonl` files containing the expanded passage collection.
[Instructions in the docTTTTTquery repo](http://doc2query.ai/) explain how to perform this data preparation.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt \
  -bm25 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio-neg.topics.dl20.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio-neg.topics.dl20.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt \
  -bm25 -bm25.k1 2.18 -bm25.b 0.86 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 2.18 -bm25.b 0.86 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio.topics.dl20.txt \
  -bm25 -bm25.k1 2.18 -bm25.b 0.86 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio-neg.topics.dl20.txt \
  -bm25 -bm25.k1 2.18 -bm25.b 0.86 -rocchio -rocchio.useNegative -rerankCutoff 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rocchio-neg.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rocchio-neg.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio-neg.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rocchio-neg.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.4074    | 0.4286    | 0.4246    | 0.4272    | 0.4082    | 0.4298    | 0.4269    | 0.4279    | 0.4171    | 0.4348    | 0.4376    | 0.4366    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.6187    | 0.6131    | 0.6102    | 0.6147    | 0.6192    | 0.6128    | 0.6152    | 0.6180    | 0.6265    | 0.6235    | 0.6224    | 0.6279    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7044    | 0.7161    | 0.7239    | 0.7170    | 0.7046    | 0.7148    | 0.7227    | 0.7223    | 0.7044    | 0.7105    | 0.7126    | 0.7125    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.8452    | 0.8700    | 0.8675    | 0.8700    | 0.8443    | 0.8696    | 0.8694    | 0.8689    | 0.8393    | 0.8605    | 0.8641    | 0.8657    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, tuned on _on the original passages_ using the MS MARCO passage sparse judgments, as described in [this page](experiments-msmarco-passage.md).
+ The setting "tuned2" refers to `k1=2.18`, `b=0.86`, tuned via grid search to optimize recall@1000 directly _on the expanded passages_ using the MS MARCO passage sparse judgments (in 2020/12).

Settings tuned on the MS MARCO passage sparse judgments _may not_ work well on the TREC dense judgments.

Note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2102.07662).

Some of these regressions correspond to official TREC 2020 Deep Learning Track submissions by team `anserini`:

+ `p_d2q_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `p_d2q_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`

Note this regression was revamped as part of [#1730](https://github.com/castorini/anserini/issues/1730), but the results did not change.
