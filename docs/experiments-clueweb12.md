# Anserini: Experiments on ClueWeb12

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CW12Collection \
 -input /path/to/ClueWeb12/ -generator JsoupGenerator \
 -index lucene-index.cw12.pos+docvectors -threads 88 \
 -storePositions -storeDocvectors \
 >& log.cw12.pos+docvectors &
```

The directory `/path/to/cw12/` should be the root directory of ClueWeb12 collection, i.e., `/path/to/cw12/` should contain 
`Disk1`, `Disk2`, `Disk3`, `Disk4`.

Note that the above indexing command builds an index over the entire ClueWeb12 collection, which comprises 733 million documents. The result is a 2.4 TB index. On a modern server, the process takes a bit more than a day.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.web.201-250.txt`: [Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/trec2013-topics.xml)
+ `topics.web.251-300.txt`: [Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/trec2014-topics.xml)
+ `qrels.web.201-250.txt`: [one aspect per topic qrels for Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/qrels.adhoc.txt)
+ `qrels.web.251-300.txt`: [one aspect per topic qrels for Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/qrels.adhoc.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25.txt -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.bm25.txt -bm25 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25+rm3.txt -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.bm25+rm3.txt -bm25 -rm3 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.ql.txt -ql &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.ql.txt -ql &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.ql+rm3.txt -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25+rm3.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql+rm3.txt
```

And to compute NDCG:

```
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25+rm3.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql+rm3.txt | grep 'amean'
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

AP                                                                             | BM25   | BM25+RM3 | QL     | QL+RM3 |
:------------------------------------------------------------------------------|--------|----------|--------|--------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.1673 | 0.1483   | 0.1438 | 0.1245 |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.2432 | 0.2460   | 0.2401 | 0.2302 |

P30                                                                            | BM25   | BM25+RM3 | QL     | QL+RM3 |
:------------------------------------------------------------------------------|--------|----------|--------|--------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.2827 | 0.2360   | 0.2507 | 0.2053 |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.4500 | 0.4167   | 0.4367 | 0.3880 |

NDCG@20                                                                        | BM25   | BM25+RM3 | QL     | QL+RM3 |
:------------------------------------------------------------------------------|--------|----------|--------|--------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.2066 | 0.1752   | 0.1905 | 0.1563 |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.2646 | 0.2479   | 0.2327 | 0.2139 |

Note that RM3 effectiveness is low because there are large number of unjudged documents.
