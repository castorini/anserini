# Anserini: Regressions for MS MARCO Document Ranking

This page documents regression experiments for the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Note that there are four different regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is first segmented into passages, each passage is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini), in the context of doc2query-T5.
In the passage (i.e., segment) indexing condition, we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking; this is known as the MaxP technique.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc-segmented-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc-segmented-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-segmented-docTTTTTquery \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-segmented-docTTTTTquery &
```

The directory `/path/to/msmarco-doc-segmented-docTTTTTquery/` should be a directory containing the expanded segmented corpus in Anserini's jsonl format.
See [this page](experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default.topics.msmarco-doc.dev.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 2.56 -bm25.b 0.59 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-default.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-docTTTTTquery.bm25-tuned.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| BM25 (tuned)|
:---------------------------------------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.3184    | 0.3213    |


R@100                                   | BM25 (default)| BM25 (tuned)|
:---------------------------------------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.8479    | 0.8627    |


R@1000                                  | BM25 (default)| BM25 (tuned)|
:---------------------------------------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.9490    | 0.9530    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=2.56`, `b=0.59`, tuned to optimize for recall@100 (i.e., for first-stage retrieval) on 2019/12.

In these runs, we are retrieving the top 1000 hits for each query and using `trec_eval` to evaluate all 1000 hits.
Since we're in the passage condition, we fetch the 10000 passages and select the top 1000 documents using MaxP.
This lets us measure R@100 and R@1000; the latter is particularly important when these runs are used as first-stage retrieval.
Beware, an official MS MARCO document ranking task leaderboard submission comprises only 100 hits per query.
See [this page](experiments-msmarco-doc-leaderboard.md) for details on Anserini baseline runs that were submitted to the official leaderboard.

The MaxP passage retrieval functionality is available in `SearchCollection`.
To generate an MS MARCO submission with the BM25 default parameters, corresponding to "BM25 (default)" above:

```bash
$ target/appassembler/bin/SearchCollection -topicreader TsvString \
   -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
   -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-passage.pos+docvectors+raw \
   -output runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-default.txt -format msmarco \
   -bm25 -bm25.k1 0.9 -bm25.b 0.4 -hits 1000 \
   -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
   --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
   --run runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-default.txt

#####################
MRR @100: 0.31779258157039536
QueriesRanked: 5193
#####################
```

Note that the above command uses `-format msmarco` to directly generate a run in the MS MARCO output format.

To generate an MS MARCO submission with the BM25 tuned parameters, corresponding to "BM25 (tuned)" above:

```bash
$ target/appassembler/bin/SearchCollection -topicreader TsvString \
   -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
   -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-passage.pos+docvectors+raw \
   -output runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-tuned.txt -format msmarco \
   -bm25 -bm25.k1 2.56 -bm25.b 0.59 -hits 1000 \
   -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
   --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
   --run runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-tuned.txt

#####################
MRR @100: 0.32081861579183746
QueriesRanked: 5193
#####################
```

This run corresponds to the MS MARCO document ranking leaderboard entry "Anserini's BM25 + doc2query-T5 expansion (per passage), parameters tuned for recall@100 (k1=2.56, b=0.59)" dated 2020/12/11, and is reported in the Lin et al. (SIGIR 2021) Pyserini paper.
Again, note that the above command uses `-format msmarco` to directly generate a run in the MS MARCO output format.