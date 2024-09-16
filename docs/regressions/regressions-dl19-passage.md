# Anserini Regressions: TREC 2019 Deep Learning Track (Passage)

**Models**: various bag-of-words approaches

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2019 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl19-passage.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl19-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl19-passage
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 9 \
  -collection JsonCollection \
  -input /path/to/msmarco-passage \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, which is in `tsv` format.
[This page](../../docs/experiments-msmarco-passage.md) explains how to perform this conversion.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+rm3.topics.dl19-passage.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+rocchio.topics.dl19-passage.txt \
  -bm25 -rocchio &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+rocchio-neg.topics.dl19-passage.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+ax.topics.dl19-passage.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+prf.topics.dl19-passage.txt \
  -bm25 -bm25prf &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned.topics.dl19-passage.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl19-passage.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+rocchio.topics.dl19-passage.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rocchio &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+rocchio-neg.topics.dl19-passage.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+ax.topics.dl19-passage.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -axiom -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+prf.topics.dl19-passage.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rm3.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio-neg.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio-neg.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio-neg.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+rocchio-neg.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+ax.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default+prf.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio-neg.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio-neg.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio-neg.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+rocchio-neg.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.dl19-passage.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.dl19-passage.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.3013    | 0.3416    | 0.3474    | 0.3464    | 0.3745    | 0.3561    | 0.2903    | 0.3339    | 0.3396    | 0.3403    | 0.3632    | 0.3684    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.5058    | 0.5216    | 0.5275    | 0.5280    | 0.5511    | 0.5372    | 0.4973    | 0.5147    | 0.5275    | 0.5277    | 0.5461    | 0.5536    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.4910    | 0.5259    | 0.5257    | 0.5266    | 0.5351    | 0.5404    | 0.4974    | 0.5177    | 0.5264    | 0.5292    | 0.5404    | 0.5420    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.7501    | 0.8136    | 0.8007    | 0.8027    | 0.8241    | 0.7929    | 0.7450    | 0.7950    | 0.7948    | 0.7987    | 0.8138    | 0.7988    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, tuned using the MS MARCO passage sparse judgments, as described in [this page](../../docs/experiments-msmarco-passage.md).

Settings tuned on the MS MARCO passage sparse judgments _may not_ work well on the TREC dense judgments.

❗ Retrieval metrics here are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
For computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

These regressions correspond to official TREC 2019 Deep Learning Track submissions by `BASELINE` group:

+ `bm25base_p` = BM25 (default), `k1=0.9`, `b=0.4`
+ `bm25base_rm3_p` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
+ `bm25base_ax_p` = BM25 (default) + Ax, `k1=0.9`, `b=0.4`
+ `bm25base_prf_p` = BM25 (default) + PRF, `k1=0.9`, `b=0.4`
+ `bm25tuned_p` = BM25 (tuned), `k1=0.82`, `b=0.68`
+ `bm25tuned_rm3_p` = BM25 (tuned) + RM3, `k1=0.82`, `b=0.68`
+ `bm25tuned_ax_p` = BM25 (tuned) + Ax, `k1=0.82`, `b=0.68`
+ `bm25tuned_prf_p` = BM25 (tuned) + PRF, `k1=0.82`, `b=0.68`

Note this regression was revamped as part of [#1730](https://github.com/castorini/anserini/issues/1730), but the results did not change.
