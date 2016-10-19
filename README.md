Anserini
========

Build using Maven:

```
mvn clean package appassembler:assemble
```

### Experiments on Gov2

Indexing:

```
nohup sh target/appassembler/bin/IndexWebCollection -collection GOV2 -input /path/to/gov2/ \
 -index lucene-index.gov2.pos -threads 32 -positions -optimize \
 2> log.gov2.pos.emptyDocids.txt 1> log.gov2.pos.recordCounts.txt &
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`. The command above builds a standard positional index (`-positions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors (e.g., for query expansion), add the `-docvectors` option.

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -collection GOV2 -index lucene-index.gov2.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 relevance feedback model (requires docvectors index).

A copy of `trec_eval` is included in `eval/`. Unpack and compile it. Then you can evaluate the runs:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.bm25.txt
```

With the topics and qrels in `src/main/resources/topics-and-qrels/`, you should be able to replicate the following results:


MAP                                                                                     | BM25   |BM25+RM3| QL     | QL+RM3
----------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.2673 | 0.2952 | 0.2636 | 0.2800
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.3364 | 0.3839 | 0.3263 | 0.3628
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.3053 | 0.3408 | 0.2956 | 0.3198


P30                                                                                     | BM25   |BM25+RM3|  QL    | QL+RM3
----------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.4850 | 0.5306 | 0.4673 | 0.4850
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.5520 | 0.5927 | 0.5167 | 0.5673
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.4913 | 0.5253 | 0.4760 | 0.4873



### Experiments on ClueWeb09 (Category B)

Indexing:

```
nohup sh target/appassembler/bin/IndexWebCollection -collection CW09 -input /path/to/cw09/ClueWeb09_English_1/ \
 -index lucene-index.cw09b.pos -threads 32 -positions -optimize \
 2> log.cw09b.pos.emptyDocids.txt 1> log.cw09b.pos.recordCounts.txt &
```

The directory `/path/to/cw09/ClueWeb09_English_1` should be the root directory of ClueWeb09B collection, i.e., `ls /path/to/cw09/ClueWeb09_English_1` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

After indexing is done, you should be able to perform a retrieval run (the options are exactly the same as with Gov2 above):

```
sh target/appassembler/bin/SearchWebCollection -collection CW09 -index lucene-index.cw09b.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.bm25.txt
```

You should then be able to evaluate using `trec_eval`, as with Gov2 above. With the topics and qrels in `src/main/resources/topics-and-qrels/`, you should be able to replicate the following results:

MAP                                                                           | BM25   |BM25+RM3| QL     | QL+RM3
------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1091 | 0.1065 | 0.1026 | 0.1055
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1095 | 0.1140 | 0.0972 | 0.1021
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.1072 | 0.1336 | 0.1035 | 0.1120


P30                                                                           | BM25   |BM25+RM3| QL     | QL+RM3
------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.2667 | 0.2583 | 0.2403 | 0.2521
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.2540 | 0.2627 | 0.2220 | 0.2267
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.2187 | 0.2313 | 0.2027 | 0.2007


### Experiments on ClueWeb12-B13

```
nohup sh target/appassembler/bin/IndexWebCollection -collection CW12 -input /path/to/cw12-b13/ \
 -index lucene-index.cw12b13.pos -threads 32 -positions -optimize \
 2> log.cw12b13.pos.emptyDocids.txt 1> log.cw12b13.pos.recordCounts.txt &
```

The directory `/path/to/cw12-b13/` should be the root directory of ClueWeb12-B13 collection, i.e., `/path/to/cw12-b13/` should bring up a bunch of subdirectories, `ClueWeb12_00` to `ClueWeb12_18`.

After indexing is done, you should be able to perform a retrieval run (the options are exactly the same as with Gov2 above):

```
sh target/appassembler/bin/SearchWebCollection -collection CW12 -index lucene-index.cw12b13.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25.txt
```

You should then be able to evaluate using `trec_eval`, as with Gov2 and ClueWeb09 above. With the topics and qrels in `src/main/resources/topics-and-qrels/`, you should be able to replicate the following results:

MAP                                                                            | BM25   |BM25+RM3| QL     | QL+RM3
-------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.0458 | 0.0428 | 0.0390 | 0.0321
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.0220 | 0.0189 | 0.0230 | 0.0202

P30                                                                            | BM25   |BM25+RM3| QL     | QL+RM3
-------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.2000 | 0.1760 | 0.1720 | 0.1447
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.1307 | 0.1140 | 0.1327 | 0.1180


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

YoGosling is a branch from Anserini[https://github.com/lintool/Anserini] project. It serves as the Twitter Real-Time Search baseline system. For more details, please checkout the README file under the rts/ directory. 

