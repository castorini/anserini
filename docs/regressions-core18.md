# Anserini: Regressions for the [Washington Post](https://trec.nist.gov/data/wapost/) ([Core18](https://trec-core.github.io/2018/))

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection \
WashingtonPostCollection -generator WapoGenerator -threads 16 -input \
/path/to/core18 -index lucene-index.core18.pos+docvectors+rawdocs \
-storePositions -storeDocvectors -storeRawDocs >& \
log.core18.pos+docvectors+rawdocs &
```

The directory `/path/to/core18/` should be the root directory of TREC Core2018 collection, i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.core18.txt`: [50 test topics](https://trec.nist.gov/data/core/topics2018.txt)
+ `qrels.core18.txt`: [Judgments produced by NIST assessors](https://trec.nist.gov/data/core/qrels2018.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.bm25.topics.core18.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.bm25+rm3.topics.core18.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.bm25+ax.topics.core18.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.ql.topics.core18.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.ql+rm3.topics.core18.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.ql+ax.topics.core18.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

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
[TREC 2018 Common Core Track Topics](https://trec.nist.gov/data/core/topics2018.txt)| 0.2495    | 0.3135    | 0.2925    | 0.2526    | 0.3073    | 0.2966    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2018 Common Core Track Topics](https://trec.nist.gov/data/core/topics2018.txt)| 0.3567    | 0.4200    | 0.4027    | 0.3653    | 0.4000    | 0.4060    |



## Replication Log

* Results replicated by [@andrewyates](https://github.com/andrewyates) on 2018-11-30 (commit [`c1aac5`](https://github.com/castorini/Anserini/commit/c1aac5e353e2ab77db3e7106cb4c017a09ce0fe9))
