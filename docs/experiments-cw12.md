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
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.1673    | 0.1489    | 0.1438    | 0.1235    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.2432    | 0.2468    | 0.2401    | 0.2331    |


P30                                     | BM25      | BM25+RM3  | QL        | QL+RM3    |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.2827    | 0.2347    | 0.2507    | 0.2047    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.4500    | 0.4200    | 0.4367    | 0.4013    |


NDCG20                                  | BM25      | BM25+RM3  | QL        | QL+RM3    |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.2066    | 0.1757    | 0.1905    | 0.1557    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.2646    | 0.2435    | 0.2327    | 0.2168    |


ERR20                                   | BM25      | BM25+RM3  | QL        | QL+RM3    |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.1213    | 0.0915    | 0.1169    | 0.0859    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.1737    | 0.1741    | 0.1451    | 0.1344    |


