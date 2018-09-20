# Anserini: Experiments on Tweets2013 (MB13 &amp; MB14)

## Indexing

### Tweets2013 collection

Note that the Tweets2013 collection is distributed as a list of tweet ids that you have to download yourself, so the
effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2013 collection:

```
nohup sh target/appassembler/bin/IndexCollection -collection TweetCollection \
-generator TweetGenerator -threads 44 -input /path/to/mb13 -index \
lucene-index.mb13.pos+docvectors -storePositions -storeDocvectors -storeRawDocs \
-uniqueDocid -optimize -tweet.keepUrls -tweet.stemming >& \
log.mb13.pos+docvectors+rawdocs &
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

+ `topics.microblog2013.txt`: [TREC 2013 Microblog Track](https://trec.nist.gov/data/microblog/2013/topics.MB111-170.txt)
+ `topics.microblog2014.txt`: [TREC 2014 Microblog Track](https://trec.nist.gov/data/microblog/2014/topics.MB171-225.txt)
+ `qrels.microblog2013.txt`: [Qrels for TREC 2013 Microblog Track](https://trec.nist.gov/data/microblog/2013/qrels.txt)
+ `qrels.microblog2014.txt`: [Qrels for TREC 2014 Microblog Track](https://trec.nist.gov/data/microblog/2014/qrels2014.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt -output run.mb13.bm25.topics.microblog2013.txt -searchtweets -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt -output run.mb13.bm25.topics.microblog2014.txt -searchtweets -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt -output run.mb13.bm25+rm3.topics.microblog2013.txt -searchtweets -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt -output run.mb13.bm25+rm3.topics.microblog2014.txt -searchtweets -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt -output run.mb13.bm25+ax.topics.microblog2013.txt -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt -output run.mb13.bm25+ax.topics.microblog2014.txt -searchtweets -bm25 -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt -output run.mb13.ql.topics.microblog2013.txt -searchtweets -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt -output run.mb13.ql.topics.microblog2014.txt -searchtweets -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt -output run.mb13.ql+rm3.topics.microblog2013.txt -searchtweets -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt -output run.mb13.ql+rm3.topics.microblog2014.txt -searchtweets -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt -output run.mb13.ql+ax.topics.microblog2013.txt -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Microblog -index lucene-index.mb13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.microblog2014.txt -output run.mb13.ql+ax.topics.microblog2014.txt -searchtweets -ql -axiom -axiom.beta 1.0 -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.bm25.topics.microblog2013.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.bm25.topics.microblog2014.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.bm25+rm3.topics.microblog2013.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.bm25+rm3.topics.microblog2014.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.bm25+ax.topics.microblog2013.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.bm25+ax.topics.microblog2014.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.ql.topics.microblog2013.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.ql.topics.microblog2014.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.ql+rm3.topics.microblog2013.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.ql+rm3.topics.microblog2014.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2013.txt run.mb13.ql+ax.topics.microblog2013.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.microblog2014.txt run.mb13.ql+ax.topics.microblog2014.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)| 0.2371    | 0.2441    | 0.2855    | 0.2602    | 0.2814    | 0.3152    |
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)| 0.3931    | 0.4079    | 0.4796    | 0.4181    | 0.4641    | 0.4965    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)| 0.4339    | 0.4344    | 0.4728    | 0.4561    | 0.4689    | 0.5078    |
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)| 0.6212    | 0.6067    | 0.6648    | 0.6430    | 0.6424    | 0.6727    |


