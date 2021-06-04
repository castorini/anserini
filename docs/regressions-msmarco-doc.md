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
 -output runs/run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt \
 -bm25 -bm25prf &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 4.46 -bm25.b 0.82 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 4.46 -bm25.b 0.82 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt \
 -bm25 -bm25.k1 4.46 -bm25.b 0.82 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.100 -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.2310    | 0.1632    | 0.1147    | 0.1357    | 0.2775    | 0.2238    | 0.1885    | 0.1531    |


R@100                                   | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.7279    | 0.6765    | 0.5750    | 0.6362    | 0.8076    | 0.7789    | 0.7510    | 0.6819    |


R@1000                                  | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.8856    | 0.8785    | 0.8369    | 0.8471    | 0.9357    | 0.9307    | 0.9249    | 0.8752    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=4.46`, `b=0.82`.
See [this page](experiments-msmarco-doc.md) for more details.
Note that here we are using `trec_eval` to evaluate the top 1000 hits for each query; beware, an official MS MARCO document ranking task leaderboard submission comprises only 100 hits per query.
See [this page](experiments-msmarco-doc-leaderboard.md) for details on Anserini baseline runs that were submitted to the official leaderboard.

Note that leaderboard runs were generated with `SearchMsmarco` in the MS MARCO format.
Conversion of a run in that format into the TREC format is slightly lossy due to tie-breaking effects.

To generate an MS MARCO submission with the BM25 default parameters, corresponding to "BM25 (Default)" above:

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

This run corresponds to the MS MARCO document ranking leaderboard entry "Anserini's BM25, default parameters (k1=0.9, b=0.4)" dated 2020/08/16.

To generate an MS MARCO submission with the BM25 tuned parameters, corresponding to "BM25 (Tuned)" above:

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

This run was _not_ submitted to the MS MARCO document ranking leaderboard.
