# Anserini: Regressions for Tweets2013 (MB13 &amp; MB14)

This page describes regressions for the Microblog Tracks from TREC 2013 and 2014 using the Tweets2013 collection.
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mb13.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mb13.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Note that the Tweets2013 collection is distributed as a list of tweet ids that you have to download yourself, so the
effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2013 collection:

```
nohup sh target/appassembler/bin/IndexCollection -collection TweetCollection -input /path/to/mb13 \
 -index lucene-index.mb13.pos+docvectors+rawdocs -generator TweetGenerator -threads 44 \
 -storePositions -storeDocvectors -storeRawDocs -uniqueDocid -optimize -tweet.keepUrls -tweet.stemming >& log.mb13.pos+docvectors+rawdocs &
```

More available indexing options:
* `-tweet.keepRetweets`: boolean switch to keep retweets while indexing, default `false`
* `-tweet.keepUrls`: boolean switch to keep URLs in the tweet, default `false`
* `-tweet.stemming`: boolean switch to apply Porter stemming while indexing tweets, default `false`
* `-tweet.maxId`: the max tweet Id for indexing. Tweet Ids that are larger (when being parsed to Long type) than this value will NOT be indexed, default `LONG.MAX_VALUE`
* `-tweet.deletedIdsFile`: a file that contains deleted tweetIds, one per line. these tweeets won't be indexed

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.microblog2013.txt`](../src/main/resources/topics-and-qrels/topics.microblog2013.txt): [topics for the TREC 2013 Microblog Track](https://trec.nist.gov/data/microblog/2013/topics.MB111-170.txt)
+ [`topics.microblog2014.txt`](../src/main/resources/topics-and-qrels/topics.microblog2014.txt): [topics for the TREC 2014 Microblog Track](https://trec.nist.gov/data/microblog/2014/topics.MB171-225.txt)
+ [`qrels.microblog2013.txt`](../src/main/resources/topics-and-qrels/qrels.microblog2013.txt): [qrels for the TREC 2013 Microblog Track](https://trec.nist.gov/data/microblog/2013/qrels.txt)
+ [`qrels.microblog2014.txt`](../src/main/resources/topics-and-qrels/qrels.microblog2014.txt): [qrels for the TREC 2014 Microblog Track](https://trec.nist.gov/data/microblog/2014/qrels2014.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt \
 -searchtweets -bm25 -output run.mb13.bm25.topics.microblog2013.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt \
 -searchtweets -bm25 -output run.mb13.bm25.topics.microblog2014.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt \
 -searchtweets -bm25 -rm3 -output run.mb13.bm25+rm3.topics.microblog2013.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt \
 -searchtweets -bm25 -rm3 -output run.mb13.bm25+rm3.topics.microblog2014.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt \
 -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb13.bm25+ax.topics.microblog2013.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt \
 -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb13.bm25+ax.topics.microblog2014.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt \
 -searchtweets -ql -output run.mb13.ql.topics.microblog2013.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt \
 -searchtweets -ql -output run.mb13.ql.topics.microblog2014.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt \
 -searchtweets -ql -rm3 -output run.mb13.ql+rm3.topics.microblog2013.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt \
 -searchtweets -ql -rm3 -output run.mb13.ql+rm3.topics.microblog2014.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt \
 -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb13.ql+ax.topics.microblog2013.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb13.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt \
 -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb13.ql+ax.topics.microblog2014.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.bm25.topics.microblog2013.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.bm25.topics.microblog2014.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.bm25+rm3.topics.microblog2013.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.bm25+rm3.topics.microblog2014.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.bm25+ax.topics.microblog2013.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.bm25+ax.topics.microblog2014.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.ql.topics.microblog2013.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.ql.topics.microblog2014.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.ql+rm3.topics.microblog2013.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.ql+rm3.topics.microblog2014.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.ql+ax.topics.microblog2013.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.ql+ax.topics.microblog2014.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2013.txt)| 0.2371    | 0.2513    | 0.2855    | 0.2602    | 0.2911    | 0.3152    |
[TREC 2014 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2014.txt)| 0.3931    | 0.4374    | 0.4796    | 0.4181    | 0.4676    | 0.4965    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2013.txt)| 0.4339    | 0.4411    | 0.4728    | 0.4561    | 0.4906    | 0.5078    |
[TREC 2014 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2014.txt)| 0.6212    | 0.6442    | 0.6648    | 0.6430    | 0.6533    | 0.6727    |
