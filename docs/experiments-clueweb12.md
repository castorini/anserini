# Anserini Experiments on ClueWeb12

Anserini is able to build a single (monolithic) index on all of ClueWeb12:

```
nohup sh target/appassembler/bin/IndexCollection -collection CW12Collection \
 -input /path/to/cw12/ -generator JsoupGenerator \
 -index lucene-index.cw12.cnt -threads 48 -optimize > log.cw12.cnt &

```

The directory `/path/to/cw12/` should be the root directory of ClueWeb12 collection, i.e., `/path/to/cw12/` should contain 
`Disk1`, `Disk2`, `Disk3`, `Disk4`. The above command builds an index that stores term frequencies only and `-optimize` 
force merges all index segment into one.

On our streeling machine, which is 2 x Intel Xeon E5-2680 v3 2.5GHz (12 cores) with 768 GB RAM, indexing takes around 26 
hours, 7 of which is consumed by the index merge. The index size is 356 GB. Note there's nothing to prevent building 
positional indexes and storing document vectors (for relevance feedback), other than space and time.

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchCollection \
  -topicreader Webxml -index lucene-index.cw12.cnt -bm25 \
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

AP                                                                             | BM25   | QL     |
:------------------------------------------------------------------------------|--------|--------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.1674 | 0.1439 |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.2434 | 0.2408 |

P30                                                                            | BM25   | QL     |
:------------------------------------------------------------------------------|--------|--------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.2833 | 0.2533 |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.4500 | 0.4387 |

NDCG@20                                                                        | BM25   | QL     |
:------------------------------------------------------------------------------|--------|--------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.2067 | 0.1893 |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.2644 | 0.2345 |
