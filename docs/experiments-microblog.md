# Anserini Experiments on TREC Microblog Collection


### Tweets2011 collection

Note that the Tweets2011 collection is distributed as a list of tweet ids that you have to download yourself, so the 
effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2011 collection:

```
nohup sh target/appassembler/bin/IndexCollection -collection TwitterCollection  -input \
/path/to/Tweets2011/ -generator JsoupGenerator  -index lucene-index.Tweets2011.pos+docvectors -threads 32 \
-storePositions -storeDocvectors -optimize > log.Tweets2011.txt &
```
__NB:__ The process is backgrounded 

Running topics from TREC 2011 (also look in `src/main/resources/topics-and-qrels/` for topics from TREC 2012):

```
sh target/appassembler/bin/SearchTweets -topicreader Twitter -index lucene-index.Tweets2011.pos+docvectors -bm25 \
-hits 1000 -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.bm25.txt
```

Options for the retrieval model are similar to the web runs: specify `-bm25` to use BM25, `-ql` to use query likelihood,
 and add `-rm3` to invoke the RM3 relevance feedback model (requires docvectors index).

For evaluation:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25.txt
```

You should be able to get effectiveness scores along the following lines:

MAP                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3448 | 0.3719 | 0.3632 | 0.4112
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.2064 | 0.2230 | 0.2203 | 0.2541

P@30                                                                       | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3741 | 0.4034 | 0.3905 | 0.4306
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.3237 | 0.3305 | 0.3249 | 0.3475


Effectiveness scores after retaining the retweets while indexing with `-keepRetweets` option:

MAP                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.2947 | 0.3157 | 0.3054 | 0.3269
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.1786 | 0.1917 | 0.1896 | 0.2133

P@30                                                                       | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3252 | 0.3537 | 0.3327 | 0.3476
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.2938 | 0.3000 | 0.2921 | 0.3062

### Tweets2013 collection
The Tweets 2013 collection can be indexed and searched using the commands same commands as above.

Running topics from TREC 2013 and from TREC 2014 (look in `src/main/resources/topics-and-qrels/` ):

Retrieval model options are the same as above, as is running `trec_eval`. You should be able to get effectiveness scores 
along the following lines:

MAP                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.2438 | 0.2563 |0.2632  | 0.2890
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.3868 | 0.3990 |0.4122  | 0.4706

P@30                                                                       | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.4450 | 0.4328 |0.4533  | 0.4689
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.6085 | 0.5867 |0.6327  | 0.6533


Effectiveness scores after retaining the retweets while indexing with `-keepRetweets` option:

MAP                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.1925 | 0.2027 |0.2027  | 0.2117
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.2961 | 0.3208 |0.3027  | 0.3205

P@30                                                                       | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.3722 | 0.3639 |0.3722  | 0.3722
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.5085 | 0.5097 |0.5055  | 0.4752