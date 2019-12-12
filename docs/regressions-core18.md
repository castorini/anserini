# Anserini: Regressions for the [Washington Post](https://trec.nist.gov/data/wapost/) ([Core18](https://trec-core.github.io/2018/))

This page describes regressions for the TREC 2018 Common Core Track, which uses the [TREC Washington Post Corpus](https://trec.nist.gov/data/wapost/).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/core18.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/core18.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection WashingtonPostCollection -input /path/to/core18 \
 -index lucene-index.core18.pos+docvectors+rawdocs -generator WapoGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs >& log.core18.pos+docvectors+rawdocs &
```

The directory `/path/to/core18/` should be the root directory of the [TREC Washington Post Corpus](https://trec.nist.gov/data/wapost/), i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.core18.txt`](../src/main/resources/topics-and-qrels/topics.core18.txt): [topics for the TREC 2018 Common Core Track](https://trec.nist.gov/data/core/topics2018.txt)
+ [`qrels.core18.txt`](../src/main/resources/topics-and-qrels/qrels.core18.txt): [qrels for the TREC 2018 Common Core Track](https://trec.nist.gov/data/core/qrels2018.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.core18.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -bm25 -output run.core18.bm25.topics.core18.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.core18.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -bm25 -rm3 -output run.core18.bm25+rm3.topics.core18.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.core18.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.core18.bm25+ax.topics.core18.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.core18.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -ql -output run.core18.ql.topics.core18.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.core18.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -ql -rm3 -output run.core18.ql+rm3.topics.core18.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.core18.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -output run.core18.ql+ax.topics.core18.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.bm25.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.bm25+rm3.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.bm25+ax.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.ql.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.ql+rm3.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.ql+ax.topics.core18.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2018 Common Core Track Topics](../src/main/resources/topics-and-qrels/topics.core18.txt)| 0.2495    | 0.3135    | 0.2925    | 0.2526    | 0.3073    | 0.2966    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2018 Common Core Track Topics](../src/main/resources/topics-and-qrels/topics.core18.txt)| 0.3567    | 0.4200    | 0.4027    | 0.3653    | 0.4000    | 0.4060    |

## Replication Log

* Results replicated by [@andrewyates](https://github.com/andrewyates) on 2018-11-30 (commit [`c1aac5`](https://github.com/castorini/Anserini/commit/c1aac5e353e2ab77db3e7106cb4c017a09ce0fe9))
* Results replicated by [@chriskamphuis](https://github.com/chriskamphuis) on 2019-09-07 (commit [`61f6f20`](https://github.com/castorini/anserini/commit/61f6f20ff6872484966ea1badcdcdcebf1eea852))