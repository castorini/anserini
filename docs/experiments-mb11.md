# Anserini: Experiments on Tweets2011 (MB11 &amp; MB12)

## Indexing

### Tweets2011 collection

Note that the Tweets2011 collection is distributed as a list of tweet ids that you have to download yourself, so the
effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2011 collection:

```
nohup sh target/appassembler/bin/IndexCollection -collection TweetCollection \
-generator TweetGenerator -threads 44 -input /path/to/mb11 -index \
lucene-index.mb11.pos+docvectors -storePositions -storeDocvectors -storeRawDocs \
-uniqueDocid -tweet.keepUrls -tweet.stemming >& log.mb11.pos+docvectors+rawdocs \
&
```
__NB:__ The process is backgrounded

More available indexing options:
* `-tweet.keepRetweets`: boolean switch to keep retweets while indexing, default `false`
* `-tweet.keepUrls`: boolean switch to keep URLs in the tweet, default `false`
* `-tweet.stemming`: boolean switch to apply Porter stemming while indexing tweets, default `false`
* `-tweet.maxId`: the max tweet Id for indexing. Tweet Ids that are larger (when being parsed to Long type) than this value will NOT be indexed, default `LONG.MAX_VALUE`
* `-tweet.deletedIdsFile`: a file that contains deleted tweetIds, one per line. these tweeets won't be indexed

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.microblog2011.txt`: [TREC 2011 Microblog Track](https://trec.nist.gov/data/microblog/11/topics.MB1-50.txt)
+ `topics.microblog2012.txt`: [TREC 2012 Microblog Track](https://trec.nist.gov/data/microblog/12/2012.topics.MB51-110.txt)
+ `qrels.microblog2011.txt`: [Qrels for TREC 2011 Microblog Track](https://trec.nist.gov/data/microblog/11/microblog11-qrels)
+ `qrels.microblog2012.txt`: [Qrels for TREC 2012 Microblog Track](https://trec.nist.gov/data/microblog/12/adhoc-qrels)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.bm25.topics.microblog2011.txt -searchtweets -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb11.bm25.topics.microblog2012.txt -searchtweets -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.bm25+rm3.topics.microblog2011.txt -searchtweets -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb11.bm25+rm3.topics.microblog2012.txt -searchtweets -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.bm25+ax.topics.microblog2011.txt -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb11.bm25+ax.topics.microblog2012.txt -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.ql.topics.microblog2011.txt -searchtweets -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb11.ql.topics.microblog2012.txt -searchtweets -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.ql+rm3.topics.microblog2011.txt -searchtweets -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb11.ql+rm3.topics.microblog2012.txt -searchtweets -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.ql+ax.topics.microblog2011.txt -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb11.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb11.ql+ax.topics.microblog2012.txt -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25.topics.microblog2011.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.bm25.topics.microblog2012.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25+rm3.topics.microblog2011.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.bm25+rm3.topics.microblog2012.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25+ax.topics.microblog2011.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.bm25+ax.topics.microblog2012.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.ql.topics.microblog2011.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.ql.topics.microblog2012.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.ql+rm3.topics.microblog2011.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.ql+rm3.topics.microblog2012.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.ql+ax.topics.microblog2011.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2012.txt run.mb11.ql+ax.topics.microblog2012.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)| 0.3384    | 0.3622    | 0.4008    | 0.3584    | 0.4090    | 0.4201    |
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)| 0.1948    | 0.2126    | 0.2309    | 0.2102    | 0.2399    | 0.2474    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)| 0.3959    | 0.4075    | 0.4612    | 0.4061    | 0.4463    | 0.4408    |
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)| 0.3316    | 0.3463    | 0.3554    | 0.3333    | 0.3565    | 0.3842    |


