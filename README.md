Anserini
========

Build using Maven:

```
mvn clean package appassembler:assemble
```

Anserini is designed to support experiments on various standard TREC collections out of the box:

+ [Gov2](docs/experiments-gov2.md)
+ [ClueWeb09b](docs/experiments-clueweb09b.md)
+ [ClueWeb12-B13](experiments-clueweb12-b13.md)


### Experiments on Tweets2011

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


### Experiments on Tweets2013

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



### Twitter (Near) Real-Time Search

To get access to the Twitter public stream, you need a developer account to obtain OAuth credentials. After creating an account on the Twitter developer site, you can obtain these credentials by [creating an "application"](https://dev.twitter.com/apps/new). After you've created an application, create an access token by clicking on the button "Create my access token".

To to run the Twitter (near) real-time search demo, you must save your Twitter API OAuth credentials in a file named `twitter4j.properties` in your current working directory. See [this page](http://twitter4j.org/en/configuration.html) for more information about Twitter4j configurations. The file should contain the following (replace the `**********` instances with your information):

```
oauth.consumerKey=**********
oauth.consumerSecret=**********
oauth.accessToken=**********
oauth.accessTokenSecret=**********
```

Once you've done that, fire up the demo with:

```
sh target/appassembler/bin/TweetSearcher -index twitter-index
```

The demo starts up an HTTP server on port `8080`, but this can be changed with the `-port` option. Query via a web browser at `http://localhost:8080/search?query=query`. Try `birthday`, as there are always birthdays being celebrated. 

User could change the maximum number of hits returned at 'http://localhost:8080/search?query=birthday&top=15'. The default number of hits is 20. 


###YoGosling

YoGosling is a branch from Anserini[https://github.com/lintool/Anserini] project. It serves as the Twitter Real-Time Search baseline system. For more details, please checkout documentation [here](https://github.com/YoGosling/Anserini/blob/new-rebase-branch/src/main/java/io/anserini/rts/README.md)
