# Anserini Experiments on ClueWeb12-B13

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
