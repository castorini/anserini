# Anserini Regressions: MS MARCO Document Ranking

**Models**: BM25 on complete documents with doc2query-T5 expansions

This page documents regression experiments on the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** doc2query-T5

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-docTTTTTquery
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-docTTTTTquery \
  -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 7 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-docTTTTTquery &
```

The directory `/path/to/msmarco-doc-docTTTTTquery/` should be a directory containing the expanded document corpus in Anserini's jsonl format.
See [this page](experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.msmarco-doc.dev.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.msmarco-doc.dev.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.msmarco-doc.dev.txt \
  -bm25 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.68 -bm25.b 0.87 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.68 -bm25.b 0.87 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.68 -bm25.b 0.87 -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-default+rocchio.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2886    | 0.1841    | 0.1841    | 0.3273    | 0.2628    | 0.2648    |
| **RR@100**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2880    | 0.1834    | 0.1833    | 0.3269    | 0.2623    | 0.2643    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.7993    | 0.7422    | 0.7441    | 0.8612    | 0.8390    | 0.8448    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **BM25 (tuned)**| **+RM3**  | **+Rocchio**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.9259    | 0.9126    | 0.9128    | 0.9553    | 0.9522    | 0.9534    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=4.68`, `b=0.87`, tuned in 2020/12 using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval).

In these runs, we are retrieving the top 1000 hits for each query and using `trec_eval` to evaluate all 1000 hits.
This lets us measure R@100 and R@1000; the latter is particularly important when these runs are used as first-stage retrieval.
Beware, an official MS MARCO document ranking task leaderboard submission comprises only 100 hits per query.
See [this page](experiments-msmarco-doc-leaderboard.md) for details on Anserini baseline runs that were submitted to the official leaderboard.

## Additional Implementation Details

Note that prior to December 2021, runs generated with `SearchCollection` in the TREC format and then converted into the MS MARCO format give slightly different results from runs generated by `SearchMsmarco` directly in the MS MARCO format, due to tie-breaking effects.
This was fixed with [#1458](https://github.com/castorini/anserini/issues/1458), which also introduced (intra-configuration) multi-threading.
As a result, `SearchMsmarco` has been deprecated and replaced by `SearchCollection`; both have been verified to generate _identical_ output.

The commands below have been retained for historical reasons only, since they correspond to an official MS MARCO leaderboard submission.

To generate an MS MARCO submission with the BM25 tuned parameters, corresponding to "BM25 (tuned)" above:

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 4.68 -b 0.87 -threads 9 \
    -index indexes/lucene-index.msmarco-doc-docTTTTTquery \
    -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.txt

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.txt

#####################
MRR @100: 0.3265190296491929
QueriesRanked: 5193
#####################
```

This run corresponds to the MS MARCO document ranking leaderboard entry "Anserini's BM25 + doc2query-T5 expansion (per document), parameters tuned for recall@100 (k1=4.68, b=0.87)" dated 2020/12/11, and is reported in the Lin et al. (SIGIR 2021) Pyserini paper.

As of February 2022, following resolution of [#1721](https://github.com/castorini/anserini/issues/1721), BM25 runs for the MS MARCO leaderboard can be generated with the commands below.
For default parameters (`k1=0.9`, `b=0.4`):

```
$ sh target/appassembler/bin/SearchCollection \
    -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
    -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -topicreader TsvInt \
    -output runs/run.msmarco-doc-docTTTTTquery.bm25-default.txt \
    -format msmarco \
    -bm25 -hits 100

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc-docTTTTTquery.bm25-default.txt

#####################
MRR @100: 0.2880115937357742
QueriesRanked: 5193
#####################
```

For tuned parameters (`k1=4.68`, `b=0.87`):

```
$ sh target/appassembler/bin/SearchCollection \
    -index indexes/lucene-index.msmarco-doc-docTTTTTquery/ \
    -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -topicreader TsvInt \
    -output runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.txt \
    -format msmarco \
    -bm25 -bm25.k1 4.68 -bm25.b 0.87 -hits 100

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc-docTTTTTquery.bm25-tuned.txt

#####################
MRR @100: 0.3268656233100833
QueriesRanked: 5193
#####################
```

Note that the resolution of [#1721](https://github.com/castorini/anserini/issues/1721) _did_ slightly change the results, since we corrected underlying issues with data preparation.
