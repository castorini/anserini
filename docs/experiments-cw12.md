# Anserini: Experiments on [ClueWeb12](http://lemurproject.org/clueweb12.php/)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection ClueWeb12Collection \
-generator JsoupGenerator -threads 44 -input /path/to/cw12 -index \
lucene-index.cw12.pos+docvectors -storePositions -storeDocvectors -storeRawDocs \
>& log.cw12.pos+docvectors+rawdocs &
```

The directory `/path/to/cw12/` should be the root directory of ClueWeb12 collection, i.e., `/path/to/cw12/` should contain
`Disk1`, `Disk2`, `Disk3`, `Disk4`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.web.201-250.txt`: [Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/trec2013-topics.xml)
+ `topics.web.251-300.txt`: [Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/trec2014-topics.xml)
+ `qrels.web.201-250.txt`: [one aspect per topic qrels for Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/qrels.adhoc.txt)
+ `qrels.web.251-300.txt`: [one aspect per topic qrels for Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/qrels.adhoc.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.bm25.topics.web.201-250.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.bm25.topics.web.251-300.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.bm25+rm3.topics.web.201-250.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.bm25+rm3.topics.web.251-300.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.ql.topics.web.201-250.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.ql.topics.web.251-300.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.ql+rm3.topics.web.201-250.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.ql+rm3.topics.web.251-300.txt -ql -rm3 &

```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25+rm3.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25+rm3.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25+rm3.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql+rm3.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql+rm3.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql+rm3.topics.web.251-300.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | QL        | QL+RM3    |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.1695    | 0.1499    | 0.1493    | 0.1273    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.2469    | 0.2479    | 0.2467    | 0.2391    |


P30                                     | BM25      | BM25+RM3  | QL        | QL+RM3    |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.2767    | 0.2413    | 0.2613    | 0.2213    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.4533    | 0.4067    | 0.4380    | 0.4113    |


NDCG20                                  | BM25      | BM25+RM3  | QL        | QL+RM3    |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.2086    | 0.1858    | 0.1993    | 0.1597    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.2578    | 0.2476    | 0.2228    | 0.2262    |


ERR20                                   | BM25      | BM25+RM3  | QL        | QL+RM3    |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.1284    | 0.0984    | 0.1232    | 0.0927    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.1630    | 0.1786    | 0.1321    | 0.1382    |


