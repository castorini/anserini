# Anserini Experiments on ClueWeb12-B13

Indexing:

```
nohup sh target/appassembler/bin/IndexCollection -collection CW12Collection \
 -input /path/to/cw12-b13/ -generator JsoupGenerator \
 -index lucene-index.cw12b13.pos+docvectors -threads 32 -storePositions -storeDocvectors -optimize \
 > log.cw12b13.pos+docvectors &
```

The directory `/path/to/cw12-b13/` should be the root directory of ClueWeb12-B13 collection, i.e., `/path/to/cw12-b13/` 
should bring up a bunch of subdirectories, `ClueWeb12_00` to `ClueWeb12_18`.  The above command builds an index that 
stores term positions (`-storePositions`) as well as doc vectors for relevance feedback (`-storeDocvectors`), and 
`-optimize` force merges all index segment into one.

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchCollection \
  -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`. Use `trec_eval` to compute AP and P30, and use 
`gdeval` to compute NDCG@20:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25.txt
```

You should be able to replicate the following results:

AP                                                                             | BM25   |BM25+RM3| QL     | QL+RM3
:------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.0458 | 0.0428 | 0.0390 | 0.0321
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.0220 | 0.0189 | 0.0230 | 0.0202

P30                                                                            | BM25   |BM25+RM3| QL     | QL+RM3
:------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.2000 | 0.1753 | 0.1720 | 0.1447
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.1307 | 0.1140 | 0.1327 | 0.1180

NDCG@20                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
:------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.1242 | 0.1110 | 0.1159 | 0.0876
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.1190 | 0.1016 | 0.1159 | 0.0987
