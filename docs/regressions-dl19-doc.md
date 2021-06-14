# Anserini: Regressions for [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)

This page describes experiments, integrated into Anserini's regression testing framework, for the TREC 2019 Deep Learning Track (Document Ranking Task) on the MS MARCO document collection using relevance judgments from NIST.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

Note that there are four different regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** none

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl19-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl19-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CleanTrecCollection \
 -input /path/to/msmarco-doc \
 -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt \
 -bm25 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-default+rm3.topics.dl19-doc.txt \
 -bm25 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-default+ax.topics.dl19-doc.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-default+prf.topics.dl19-doc.txt \
 -bm25 -bm25prf -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-tuned.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-tuned+rm3.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-tuned+ax.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -axiom -axiom.deterministic -rerankCutoff 20 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
 -output runs/run.msmarco-doc.bm25-tuned+prf.topics.dl19-doc.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -bm25prf -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+prf.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.dl19-doc.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.2443    | 0.2772    | 0.2452    | 0.2541    | 0.2318    | 0.2700    | 0.2816    | 0.2758    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.3948    | 0.4189    | 0.3945    | 0.4004    | 0.3862    | 0.4193    | 0.4399    | 0.4287    |


NDCG@10                                 | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.5190    | 0.5169    | 0.4730    | 0.5105    | 0.5140    | 0.5485    | 0.5245    | 0.5280    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=3.44`, `b=0.87`, tuned using the MS MARCO document sparse judgments on 2019/06.

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that retrieval metrics are computed to depth 100 hits per query (as opposed to 1000 hits per query for DL19 passage ranking).
Also, remember that we keep qrels of _all_ relevance grades, unlike the case for DL19 passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.
These results correspond to the Anserini baselines reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

These regressions correspond to official TREC 2019 Deep Learning Track submissions by `BASELINE` group:

+ `bm25base` = BM25 (default), `k1=0.9`, `b=0.4`
+ `bm25base_rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
+ `bm25base_ax` = BM25 (default) + Ax, `k1=0.9`, `b=0.4`
+ `bm25base_prf` = BM25 (default) + PRF, `k1=0.9`, `b=0.4`
+ `bm25tuned_p` = BM25 (tuned), `k1=3.44`, `b=0.87`
+ `bm25tuned_rm3` = BM25 (tuned) + RM3, `k1=3.44`, `b=0.87`
+ `bm25tuned_ax` = BM25 (tuned) + Ax, `k1=3.44`, `b=0.87`
+ `bm25tuned_prf` = BM25 (tuned) + PRF, `k1=3.44`, `b=0.87`
