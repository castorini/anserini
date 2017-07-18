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

Running topics from TREC 2011 (also look in `src/main/resources/topics-and-qrels/` for topics from TREC 2012):

```
sh target/appassembler/bin/SearchTweets -collection Twitter -index lucene-index.Tweets2011.pos+docvectors -bm25 \
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
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3446 | 0.3743 | 0.3631 | 0.4134
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.2063 | 0.2227 | 0.2203 | 0.2540

P30                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
---------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2011 Microblog Track](http://trec.nist.gov/data/microblog2011.html)  | 0.3741 | 0.4027 | 0.3905 | 0.4340
[TREC 2012 Microblog Track](http://trec.nist.gov/data/microblog2012.html)  | 0.3237 | 0.3294 | 0.3249 | 0.3480

