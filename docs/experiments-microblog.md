# Anserini Experiments on TREC Microblog Collection


### Tweets2011 collection

Note that the Tweets2011 collection is distributed as a list of tweet ids that you have to download yourself, so the 
effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2011 collection:

```
nohup sh target/appassembler/bin/IndexCollection -collection TweetCollection \
 -input /path/to/Tweets2011/ -generator TweetGenerator \
 -index lucene-index.tweets2011.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize -uniqueDocid -tweet.keepUrls -tweet.stemming \
 >& log.tweets2011 &
```
__NB:__ The process is backgrounded

More available indexing options:
* `-tweet.keepRetweets`: boolean switch to keep retweets while indexing, default `false`
* `-tweet.keepUrls`: boolean switch to keep URLs in the tweet, default `false`
* `-tweet.stemming`: boolean switch to apply Porter stemming while indexing tweets, default `false`
* `-tweet.maxId`: the max tweet Id for indexing. Tweet Ids that are larger (when being parsed to Long type) than this value will NOT be indexed, default `LONG.MAX_VALUE`
* `-tweet.deletedIdsFile`: a file that contains deleted tweetIds, one per line. these tweeets won't be indexed

Running topics from TREC 2011 (also look in `src/main/resources/topics-and-qrels/` for topics from TREC 2012):

```
sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index lucene-index.tweets2011.pos+docvectors+rawdocs -bm25 \
-hits 1000 -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.keepUrls.stemming.bm25.txt
```

Options for the retrieval model are similar to the web runs: specify `-bm25` to use BM25, `-ql` to use query likelihood,
 and add `-rm3` to invoke the RM3 relevance feedback model (requires docvectors index).

For evaluation:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25.txt
```

You should be able to get effectiveness scores along the following lines:

MAP                                                                        | BM25   |BM25+RM3| QL     | QL+RM3 |
---------------------------------------------------------------------------|-------:|-------:|-------:|-------:|
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3351 | 0.3472 | 0.3614 | 0.4100 |
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.1912 | 0.2056 | 0.2100 | 0.2413 |
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.2306 | 0.2356 | 0.2599 | 0.2804 |
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.3836 | 0.3962 | 0.4184 | 0.4660 |

P30                                                                        | BM25   |BM25+RM3| QL     | QL+RM3 |
---------------------------------------------------------------------------|-------:|-------:|-------:|-------:|
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3837 | 0.4027 | 0.4095 | 0.4463 |
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.3328 | 0.3418 | 0.3322 | 0.3531 |
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.4222 | 0.4061 | 0.4517 | 0.4606 |
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.6176 | 0.5873 | 0.6424 | 0.6442 |
