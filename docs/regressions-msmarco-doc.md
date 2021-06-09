# Anserini: Regressions for MS MARCO Document Ranking

This page documents regression experiments for the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Note that there are four different regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** none

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery#reproducing-ms-marco-document-ranking-results-with-anserini), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

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
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned2.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 4.46 -bm25.b 0.82 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned2+rm3.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.2310    | 0.1632    | 0.2788    | 0.2289    | 0.2775    | 0.2238    |


R@100                                   | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.7279    | 0.6765    | 0.8065    | 0.7872    | 0.8076    | 0.7789    |


R@1000                                  | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.8856    | 0.8785    | 0.9326    | 0.9320    | 0.9357    | 0.9307    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=3.44`, `b=0.87`, tuned on 2019/06 and used for TREC 2019 Deep Learning Track baseline runs.
+ The setting "tuned2" refers to `k1=4.46`, `b=0.82`, tuned to optimize for recall@100 (i.e., for first-stage retrieval) on 2019/12; see [this page](experiments-msmarco-doc.md) additional details.

See [this page](experiments-msmarco-doc.md) for more details on tuning.

In these runs, we are retrieving the top 1000 hits for each query and using `trec_eval` to evaluate all 1000 hits.
This lets us measure R@100 and R@1000; the latter is particularly important when these runs are used as first-stage retrieval.
Beware, an official MS MARCO document ranking task leaderboard submission comprises only 100 hits per query.
See [this page](experiments-msmarco-doc-leaderboard.md) for details on Anserini baseline runs that were submitted to the official leaderboard.

Note that leaderboard runs were generated with `SearchMsmarco` in the MS MARCO format.
Conversion of a run in that format into the TREC format is slightly lossy due to tie-breaking effects.

To generate an MS MARCO submission with the BM25 default parameters, corresponding to "BM25 (default)" above:

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 0.9 -b 0.4 -threads 9 \
   -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
   -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
   -output runs/run.msmarco-doc.bm25-default.txt

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
   --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
   --run runs/run.msmarco-doc.bm25-default.txt

#####################
MRR @100: 0.23005723505603573
QueriesRanked: 5193
#####################
```

This run corresponds to the MS MARCO document ranking leaderboard entry "Anserini's BM25, default parameters (k1=0.9, b=0.4)" dated 2020/08/16, and is reported in the Lin et al. (SIGIR 2021) Pyserini paper.

To generate an MS MARCO submission with the BM25 tuned parameters, corresponding to "BM25 (tuned)" above:

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 4.46 -b 0.82 -threads 9 \
   -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
   -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
   -output runs/run.msmarco-doc.bm25-tuned.txt

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
   --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
   --run runs/run.msmarco-doc.bm25-tuned.txt

#####################
MRR @100: 0.2770296928568702
QueriesRanked: 5193
#####################
```

This run was _not_ submitted to the MS MARCO document ranking leaderboard, but is reported in the Lin et al. (SIGIR 2021) Pyserini paper.
