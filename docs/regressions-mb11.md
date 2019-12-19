# Anserini: Regressions for Tweets2011 (MB11 &amp; MB12)

This page describes regressions for the Microblog Tracks from TREC 2011 and 2012 using the Tweets2011 collection.
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mb11.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mb11.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Note that the Tweets2011 collection is distributed as a list of tweet ids that you have to download yourself, so the
effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2011 collection:

```
nohup sh target/appassembler/bin/IndexCollection -collection TweetCollection -input /path/to/mb11 \
 -index lucene-index.mb11.pos+docvectors+rawdocs -generator TweetGenerator -threads 44 \
 -storePositions -storeDocvectors -storeRawDocs -uniqueDocid -tweet.keepUrls -tweet.stemming >& log.mb11.pos+docvectors+rawdocs &
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

+ [`topics.microblog2011.txt`](../src/main/resources/topics-and-qrels/topics.microblog2011.txt): [topics for the TREC 2011 Microblog Track](https://trec.nist.gov/data/microblog/11/topics.MB1-50.txt)
+ [`topics.microblog2012.txt`](../src/main/resources/topics-and-qrels/topics.microblog2012.txt): [topics for the TREC 2012 Microblog Track](https://trec.nist.gov/data/microblog/12/2012.topics.MB51-110.txt)
+ [`qrels.microblog2011.txt`](../src/main/resources/topics-and-qrels/qrels.microblog2011.txt): [qrels for TREC 2011 Microblog Track](https://trec.nist.gov/data/microblog/11/microblog11-qrels)
+ [`qrels.microblog2012.txt`](../src/main/resources/topics-and-qrels/qrels.microblog2012.txt): [qrels for TREC 2012 Microblog Track](https://trec.nist.gov/data/microblog/12/adhoc-qrels)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt \
 -searchtweets -bm25 -output run.mb11.bm25.topics.microblog2011.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt \
 -searchtweets -bm25 -output run.mb11.bm25.topics.microblog2012.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt \
 -searchtweets -bm25 -rm3 -output run.mb11.bm25+rm3.topics.microblog2011.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt \
 -searchtweets -bm25 -rm3 -output run.mb11.bm25+rm3.topics.microblog2012.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt \
 -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb11.bm25+ax.topics.microblog2011.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt \
 -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb11.bm25+ax.topics.microblog2012.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt \
 -searchtweets -ql -output run.mb11.ql.topics.microblog2011.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt \
 -searchtweets -ql -output run.mb11.ql.topics.microblog2012.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt \
 -searchtweets -ql -rm3 -output run.mb11.ql+rm3.topics.microblog2011.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt \
 -searchtweets -ql -rm3 -output run.mb11.ql+rm3.topics.microblog2012.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt \
 -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb11.ql+ax.topics.microblog2011.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.mb11.pos+docvectors+rawdocs \
 -topicreader Microblog -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt \
 -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic -output run.mb11.ql+ax.topics.microblog2012.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25.topics.microblog2011.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.bm25.topics.microblog2012.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25+rm3.topics.microblog2011.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.bm25+rm3.topics.microblog2012.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25+ax.topics.microblog2011.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.bm25+ax.topics.microblog2012.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.ql.topics.microblog2011.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.ql.topics.microblog2012.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.ql+rm3.topics.microblog2011.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.ql+rm3.topics.microblog2012.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.ql+ax.topics.microblog2011.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.ql+ax.topics.microblog2012.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2011 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2011.txt)| 0.3384    | 0.3650    | 0.4008    | 0.3584    | 0.3923    | 0.4201    |
[TREC 2012 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2012.txt)| 0.1948    | 0.2193    | 0.2309    | 0.2102    | 0.2389    | 0.2474    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2011 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2011.txt)| 0.3959    | 0.4170    | 0.4612    | 0.4061    | 0.4435    | 0.4408    |
[TREC 2012 Microblog Track Topics](../src/main/resources/topics-and-qrels/topics.microblog2012.txt)| 0.3316    | 0.3463    | 0.3554    | 0.3333    | 0.3514    | 0.3842    |
