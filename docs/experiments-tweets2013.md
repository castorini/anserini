# Anserini Experiments on Tweets2013

Indexing the Tweets2013 collection:

```
nohup sh target/appassembler/bin/IndexTweets -collection /path/to/Tweets2013/ \
  -index lucene-index.tweets2013.docvectors -optimize -store > log.tweets2013.txt &
```

Running topics from TREC 2013 (also look in `src/main/resources/topics-and-qrels/` for topics from TREC 2014):

```
sh target/appassembler/bin/SearchTweets -collection Twitter -index lucene-index.tweets2013.docvectors -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.microblog2013.txt -output run.mb13.bm25.txt
```

Retrieval model options are the same as above, as is running `trec_eval`. You should be able to get effectiveness scores along the following lines:

MAP                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.2319 | 0.2409 | 0.2536 | 0.2747
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.3791 | 0.3971 | 0.3976 | 0.4393

P30                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Microblog Track](http://trec.nist.gov/data/microblog2013.html)  | 0.4261 | 0.4211 | 0.4483 | 0.4639
[TREC 2014 Microblog Track](http://trec.nist.gov/data/microblog2014.html)  | 0.6103 | 0.6024 | 0.6345 | 0.6267

