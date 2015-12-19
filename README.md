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
 -index lucene-index.gov2 -threads 32 -positions -optimize 2> emptyDocIDs.txt 1> recordCounts.txt &
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`. The command above builds a standard positional index (`-positions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors (e.g., for query expansion), add the `-docvectors` option.

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -collection GOV2 -index lucene-index.gov2.vec \
  -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25.txt -bm25
```

A copy of `trec_eval` is included in `eval/`. Unpack and compile it. Then you can evaluate the runs:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.bm25.txt
```

With the topics and qrels in `src/main/resources/topics-and-qrels/`, you should be able to replicate the following results:

                                                                                        | MAP    |  P30
----------------------------------------------------------------------------------------|--------|-------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.2673 | 0.4850
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.3365 | 0.5520
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.3053 | 0.4913


### Experiments on ClueWeb09 (Category B)

Indexing:

```
sh target/appassembler/bin/IndexWebCollection -collection CW09 -input /path/to/cw09/ClueWeb09_English_1/ \
  -index lucene-index.cw09b.cnt -threads 32 -optimize
```

The directory `/path/to/cw09/ClueWeb09_English_1` should be the root directory of ClueWeb09B collection, i.e., `ls /path/to/cw09/ClueWeb09_English_1` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -collection CW09 -index lucene-index.cw09b.cnt
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.txt -bm25
```

You should then be able to evaluate using `trec_eval`, as with Gov2 above. With the topics and qrels in `src/main/resources/topics-and-qrels/`, you should be able to replicate the following results:

                                                                              | MAP    |  P30
------------------------------------------------------------------------------|--------|-------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1091 | 0.2667
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1095 | 0.2540
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.1072 | 0.2187


### Experiments on ClueWeb09 (Category A)

```
sh target/appassembler/bin/IndexWebCollection -collection CW09 -input /path/to/cw09/ \
  -index lucene-index.cw09a.cnt -threads 32 -optimize 2> emptyDocIDs.txt 1> recordCounts.txt
```

The directory `/path/to/cw09/` should be the root directory of ClueWeb09 collection, i.e., `/path/to/cw09/` should bring up a bunch of subdirectories, `ClueWeb09_English_1` to `ClueWeb09_English_10`.

After indexing is done, you should be able to compare record counts file with the one comes from the dataset.
`emptyDocIDs.txt` file contains the documents that are not indexed. [JSoup](http://jsoup.org) produces empty string, probably they are not valid HTMLs.
If you count it with `wc -l` and add it the number that is reported from the indexer, you should obtain the total number of documents for the dataset.

### Experiments on ClueWeb12-B13

```
sh target/appassembler/bin/IndexWebCollection -collection CW12 -input /path/to/cw12b/ \
  -index lucene-index.cw12b.cnt -threads 32 -optimize 2> emptyDocIDs.txt 1> recordCounts.txt
```

The directory `/path/to/cw12b/` should be the root directory of ClueWeb12-B13 collection, i.e., `/path/to/cw12b/` should bring up a bunch of subdirectories, `ClueWeb12_00` to `ClueWeb12_18`.

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -collection CW12 -index lucene-index.cw12b.cnt
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.txt -bm25
```

You should then be able to evaluate using `trec_eval`, as with Gov2 and ClueWeb09 above. With the topics and qrels in `src/main/resources/topics-and-qrels/`, you should be able to replicate the following results:

                                                                               | MAP    |  P30
-------------------------------------------------------------------------------|--------|-------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.0458 | 0.2000
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.0220 | 0.1307

### Experiments on Tweets2011

Indexing:

```
sh target/appassembler/bin/IndexTweets -collection /path/to/tweets2011-collection/ \
  -index tweets2011-index -optimize -store
```

Running topics from TREC 2011 and 2012:

```
sh target/appassembler/bin/SearchTweets -index tweets2011-index/ \
 -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb2011.ql.txt -ql
```

For evaluation:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.microblog2011.txt run.mb2011.ql.txt
```


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

### DumpDocids:

Output all the document IDs in a Lucene Index.

```sh
sh target/appassembler/bin/DumpDocids -indexPath /path/to/index \
-docIdPath /path/to/save/docIds -docIdName "name of docID field"
```
=======
