# Anserini Experiments on Tweets2011

Note that the Tweets2011 collection is distributed as a list of tweet ids that you have to download yourself, so the effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2011 collection:

```
nohup sh target/appassembler/bin/IndexTweets -collection /path/to/Tweets2011/ \
  -index lucene-index.tweets2011.docvectors -optimize -store > log.tweets2011.txt &
```

Running topics from TREC 2011 (also look in `src/main/resources/topics-and-qrels/` for topics from TREC 2012):

```
sh target/appassembler/bin/SearchTweets -collection Twitter -index lucene-index.tweets2011.docvectors -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.bm25.txt
```

Options for the retrieval model are similar to the web runs: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 relevance feedback model (requires docvectors index).

For evaluation:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb11.bm25.txt
```

You should be able to get effectiveness scores along the following lines:

MAP                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3388 | 0.3514 | 0.3631 | 0.4024
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.1923 | 0.2093 | 0.2084 | 0.2377

P30                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3871 | 0.4048 | 0.4109 | 0.4408
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.3345 | 0.3492 | 0.3316 | 0.3492

