# Anserini: Regressions for MS MARCO Document Ranking

This page documents regression experiments for the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Note that there are four different regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is first segmented into passages, each passage is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc-docTTTTTquery-per-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc-docTTTTTquery-per-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -input /path/to/msmarco-doc-docTTTTTquery-per-passage \
 -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-passage.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-docTTTTTquery-per-passage &
```

The directory `/path/to/msmarco-doc-docTTTTTquery-per-passage/` should be a directory containing the expanded document collection; see [this link](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini) for how to prepare this collection.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-default.topics.msmarco-doc.dev.txt \
 -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-tuned.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 2.56 -bm25.b 0.59 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-default.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery-per-passage.bm25-tuned.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (Default)| BM25 (Tuned)|
:---------------------------------------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.3182    | 0.3211    |


R@100                                   | BM25 (Default)| BM25 (Tuned)|
:---------------------------------------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.8481    | 0.8627    |


R@1000                                  | BM25 (Default)| BM25 (Tuned)|
:---------------------------------------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.9490    | 0.9530    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=2.56`, `b=0.59`.
Note that here we are using `trec_eval` to evaluate the top 1000 hits for each query; beware, an official MS MARCO document ranking task leaderboard submission comprises only 100 hits per query.
See [this page](experiments-msmarco-doc-leaderboard.md) for details on Anserini baseline runs that were submitted to the official leaderboard.

The passage retrieval functionality is only available in `SearchCollection`; we use a simple script to convert back into MS MARCO format.

To generate an MS MARCO submission with the BM25 tuned parameters, corresponding to "BM25 (Tuned)" above:

```bash
$ target/appassembler/bin/SearchCollection -topicreader TsvString \
   -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
   -index indexes/lucene-index.msmarco-doc-per-passage.pos+docvectors+raw \
   -output runs/run.msmarco-doc-per-passage.bm25-tuned.trec \
   -bm25 -bm25.k1 2.56 -bm25.b 0.59 -hits 1000 \
   -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100

$ python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-doc-per-passage.bm25-tuned.trec \
   --output runs/run.msmarco-doc-per-passage.bm25-tuned.txt

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
   --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
   --run runs/run.msmarco-doc-per-passage.bm25-tuned.txt

#####################
MRR @100: 0.32081861579183746
QueriesRanked: 5193
#####################
```

This run corresponds to the MS MARCO document ranking leaderboard entry "Anserini's BM25 + doc2query-T5 expansion (per passage), parameters tuned for recall@100 (k1=2.56, b=0.59)" dated 2020/12/11.
