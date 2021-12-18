# Anserini: Regressions for [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)

This page describes experiments, integrated into Anserini's regression testing framework, for the TREC 2019 Deep Learning Track (Document Ranking Task) on the MS MARCO document collection using relevance judgments from NIST.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

Note that there are four different regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is first segmented into passages, each passage is treated as a unit of indexing
+ **Expansion Condition:** none

In the passage indexing condition, we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking; this is known as the MaxP technique.
All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl19-doc-per-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl19-doc-per-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-per-passage \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-per-passage &
```

The directory `/path/to/msmarco-doc-per-passage/` should be a directory containing the segmented paragraph collection; see [this link](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini) for how to prepare this collection.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-default.topics.dl19-doc.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-default+rm3.topics.dl19-doc.txt \
  -bm25 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-default+ax.topics.dl19-doc.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-default+prf.topics.dl19-doc.txt \
  -bm25 -bm25prf -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-tuned.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-tuned+rm3.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -rm3 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-tuned+ax.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -axiom -axiom.deterministic -rerankCutoff 20 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-per-passage \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-per-passage.bm25-tuned+prf.topics.dl19-doc.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -bm25prf -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-default.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-default+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-default+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-default+prf.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-tuned.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-tuned+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-tuned+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-per-passage.bm25-tuned+prf.topics.dl19-doc.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.2441    | 0.2880    | 0.3015    | 0.2821    | 0.2394    | 0.2656    | 0.2934    | 0.2838    |


R@100                                   | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.3840    | 0.4356    | 0.4501    | 0.4477    | 0.3903    | 0.4126    | 0.4437    | 0.4362    |


nDCG@10                                 | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)| 0.5276    | 0.5750    | 0.5590    | 0.5591    | 0.5364    | 0.5379    | 0.5546    | 0.5478    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=2.16`, `b=0.61`, tuned using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval) on 2020/12.

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that retrieval metrics are computed to depth 100 hits per query (as opposed to 1000 hits per query for DL19 passage ranking).
Also, remember that we keep qrels of _all_ relevance grades, unlike the case for DL19 passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.
