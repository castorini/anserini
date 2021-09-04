# Anserini: Regressions for [TREC 2020 Background Linking](http://trec-news.org/)

This page describes regressions for the background linking task in the [TREC 2020 News Track](http://trec-news.org/).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/backgroundlinking20.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/backgroundlinking20.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection WashingtonPostCollection \
 -input /path/to/backgroundlinking20 \
 -index indexes/lucene-index.core18-v3.pos+docvectors+raw \
 -generator WashingtonPostGenerator \
 -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.backgroundlinking20 &
```

The directory `/path/to/core18/` should be the root directory of the [TREC Washington Post Corpus *v3*](https://trec.nist.gov/data/wapost/), i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.backgroundlinking20.txt`](../src/main/resources/topics-and-qrels/topics.backgroundlinking20.txt): topics for the background linking task of the TREC 2020 News Track
+ [`qrels.backgroundlinking20.txt`](../src/main/resources/topics-and-qrels/qrels.backgroundlinking20.txt): qrels for the background linking task of the TREC 2020 News Track

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core18-v3.pos+docvectors+raw \
 -topicreader BackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking20.txt \
 -output runs/run.backgroundlinking20.bm25.topics.backgroundlinking20.txt \
 -backgroundlinking -backgroundlinking.k 100 -bm25 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core18-v3.pos+docvectors+raw \
 -topicreader BackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking20.txt \
 -output runs/run.backgroundlinking20.bm25+rm3.topics.backgroundlinking20.txt \
 -backgroundlinking -backgroundlinking.k 100 -bm25 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core18-v3.pos+docvectors+raw \
 -topicreader BackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking20.txt \
 -output runs/run.backgroundlinking20.bm25+rm3+df.topics.backgroundlinking20.txt \
 -backgroundlinking -backgroundlinking.datefilter -backgroundlinking.k 100 -bm25 -rm3 -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M1000 -m ndcg_cut.5 -c -M1000 -m map src/main/resources/topics-and-qrels/qrels.backgroundlinking20.txt runs/run.backgroundlinking20.bm25.topics.backgroundlinking20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M1000 -m ndcg_cut.5 -c -M1000 -m map src/main/resources/topics-and-qrels/qrels.backgroundlinking20.txt runs/run.backgroundlinking20.bm25+rm3.topics.backgroundlinking20.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M1000 -m ndcg_cut.5 -c -M1000 -m map src/main/resources/topics-and-qrels/qrels.backgroundlinking20.txt runs/run.backgroundlinking20.bm25+rm3+df.topics.backgroundlinking20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

NCDG@5                                  | BM25      | +RM3      | +RM3+DF   |
:---------------------------------------|-----------|-----------|-----------|
[TREC 2020 Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking20.txt)| 0.5231    | 0.5673    | 0.5316    |


AP                                      | BM25      | +RM3      | +RM3+DF   |
:---------------------------------------|-----------|-----------|-----------|
[TREC 2020 Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking20.txt)| 0.3286    | 0.4519    | 0.3438    |

