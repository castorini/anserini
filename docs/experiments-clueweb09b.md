# Anserini Experiments on ClueWeb09 (Category B)

Indexing:

```
nohup sh target/appassembler/bin/IndexCollection -collection CW09Collection \
 -input /path/to/cw09/ClueWeb09_English_1/ -generator JsoupGenerator \
 -index lucene-index.cw09b.pos+docvectors -threads 32 -storePositions -storeDocvectors -optimize \
 > log.cw09b.pos+docvectors &
```

The directory `/path/to/cw09/ClueWeb09_English_1` should be the root directory of ClueWeb09B collection, i.e., 
`ls /path/to/cw09/ClueWeb09_English_1` should bring up a bunch of subdirectories, `en0000` to `enwp03`. The above 
command builds an index that stores term positions (`-storePositions`) as well as doc vectors for relevance feedback 
(`-storeDocvectors`), and `-optimize` force merges all index segment into one.

After indexing is done, you should be able to perform a retrieval as follows:

```
sh target/appassembler/bin/SearchCollection \
  -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`. Use `trec_eval` to compute AP and P30, and use 
`gdeval` to compute NDCG@20:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.bm25.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.bm25.txt
```

You should be able to replicate the following results:

AP                                                                            | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1091 | 0.1065 | 0.1026 | 0.1056
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1095 | 0.1140 | 0.0972 | 0.1021
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.1072 | 0.1336 | 0.1035 | 0.1120


P30                                                                           | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.2667 | 0.2583 | 0.2403 | 0.2528
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.2540 | 0.2627 | 0.2220 | 0.2267
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.2187 | 0.2313 | 0.2027 | 0.2007

NDCG@20                                                                       | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1320 | 0.1443 | 0.1131 | 0.1307
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1915 | 0.1920 | 0.1633 | 0.1670
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.0977 | 0.1309 | 0.0862 | 0.1027
