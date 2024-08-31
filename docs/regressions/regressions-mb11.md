# Anserini Regressions: Tweets2011 (MB11 &amp; MB12)

**Models**: various bag-of-words approaches

This page describes regressions for the Microblog Tracks from TREC 2011 and 2012 using the Tweets2011 collection.
The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mb11.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mb11.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mb11
```

## Indexing

Note that the Tweets2011 collection is distributed as a list of tweet ids that you have to download yourself, so the
effectiveness results you'll get should be similar, but will likely not be identical to the scores reported here.

Indexing the Tweets2011 collection:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 44 \
  -collection TweetCollection \
  -input /path/to/mb11 \
  -generator TweetGenerator \
  -index indexes/lucene-index.mb11/ \
  -storePositions -storeDocvectors -storeRaw -tweet.keepUrls -tweet.stemming \
  >& logs/log.mb11 &
```

More available indexing options:
* `-tweet.keepRetweets`: boolean switch to keep retweets while indexing, default `false`
* `-tweet.keepUrls`: boolean switch to keep URLs in the tweet, default `false`
* `-tweet.stemming`: boolean switch to apply Porter stemming while indexing tweets, default `false`
* `-tweet.maxId`: the max tweet Id for indexing. Tweet Ids that are larger (when being parsed to Long type) than this value will NOT be indexed, default `LONG.MAX_VALUE`
* `-tweet.deletedIdsFile`: a file that contains deleted tweetIds, one per line. these tweeets won't be indexed

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from NIST:

+ [`topics.microblog2011.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.microblog2011.txt): [topics for the TREC 2011 Microblog Track](https://trec.nist.gov/data/microblog/11/topics.MB1-50.txt)
+ [`topics.microblog2012.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.microblog2012.txt): [topics for the TREC 2012 Microblog Track](https://trec.nist.gov/data/microblog/12/2012.topics.MB51-110.txt)
+ [`qrels.microblog2011.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.microblog2011.txt): [qrels for TREC 2011 Microblog Track](https://trec.nist.gov/data/microblog/11/microblog11-qrels)
+ [`qrels.microblog2012.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.microblog2012.txt): [qrels for TREC 2012 Microblog Track](https://trec.nist.gov/data/microblog/12/adhoc-qrels)

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2011.txt \
  -topicReader Microblog \
  -output runs/run.mb11.bm25.topics.microblog2011.txt \
  -searchTweets -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2012.txt \
  -topicReader Microblog \
  -output runs/run.mb11.bm25.topics.microblog2012.txt \
  -searchTweets -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2011.txt \
  -topicReader Microblog \
  -output runs/run.mb11.bm25+rm3.topics.microblog2011.txt \
  -searchTweets -bm25 -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2012.txt \
  -topicReader Microblog \
  -output runs/run.mb11.bm25+rm3.topics.microblog2012.txt \
  -searchTweets -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2011.txt \
  -topicReader Microblog \
  -output runs/run.mb11.bm25+ax.topics.microblog2011.txt \
  -searchTweets -bm25 -axiom -axiom.beta 1.0 -axiom.deterministic -rerankCutoff 20 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2012.txt \
  -topicReader Microblog \
  -output runs/run.mb11.bm25+ax.topics.microblog2012.txt \
  -searchTweets -bm25 -axiom -axiom.beta 1.0 -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2011.txt \
  -topicReader Microblog \
  -output runs/run.mb11.ql.topics.microblog2011.txt \
  -searchTweets -qld &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2012.txt \
  -topicReader Microblog \
  -output runs/run.mb11.ql.topics.microblog2012.txt \
  -searchTweets -qld &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2011.txt \
  -topicReader Microblog \
  -output runs/run.mb11.ql+rm3.topics.microblog2011.txt \
  -searchTweets -qld -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2012.txt \
  -topicReader Microblog \
  -output runs/run.mb11.ql+rm3.topics.microblog2012.txt \
  -searchTweets -qld -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2011.txt \
  -topicReader Microblog \
  -output runs/run.mb11.ql+ax.topics.microblog2011.txt \
  -searchTweets -qld -axiom -axiom.beta 1.0 -axiom.deterministic -rerankCutoff 20 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mb11/ \
  -topics tools/topics-and-qrels/topics.microblog2012.txt \
  -topicReader Microblog \
  -output runs/run.mb11.ql+ax.topics.microblog2012.txt \
  -searchTweets -qld -axiom -axiom.beta 1.0 -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2011.txt runs/run.mb11.bm25.topics.microblog2011.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2012.txt runs/run.mb11.bm25.topics.microblog2012.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2011.txt runs/run.mb11.bm25+rm3.topics.microblog2011.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2012.txt runs/run.mb11.bm25+rm3.topics.microblog2012.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2011.txt runs/run.mb11.bm25+ax.topics.microblog2011.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2012.txt runs/run.mb11.bm25+ax.topics.microblog2012.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2011.txt runs/run.mb11.ql.topics.microblog2011.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2012.txt runs/run.mb11.ql.topics.microblog2012.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2011.txt runs/run.mb11.ql+rm3.topics.microblog2011.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2012.txt runs/run.mb11.ql+rm3.topics.microblog2012.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2011.txt runs/run.mb11.ql+ax.topics.microblog2011.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.microblog2012.txt runs/run.mb11.ql+ax.topics.microblog2012.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [TREC 2011 Microblog Track Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.microblog2011.txt)| 0.3384    | 0.3658    | 0.4008    | 0.3584    | 0.3961    | 0.4201    |
| [TREC 2012 Microblog Track Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.microblog2012.txt)| 0.1948    | 0.2209    | 0.2309    | 0.2102    | 0.2400    | 0.2474    |
| **P30**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [TREC 2011 Microblog Track Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.microblog2011.txt)| 0.3959    | 0.4177    | 0.4612    | 0.4061    | 0.4408    | 0.4408    |
| [TREC 2012 Microblog Track Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.microblog2012.txt)| 0.3316    | 0.3463    | 0.3554    | 0.3333    | 0.3520    | 0.3842    |
