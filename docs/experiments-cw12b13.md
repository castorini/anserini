# Anserini: Experiments on [ClueWeb12-B13](http://lemurproject.org/clueweb12/ClueWeb12-CreateB13.php)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection ClueWeb12Collection \
-generator JsoupGenerator -threads 44 -input /path/to/cw12b13 -index \
lucene-index.cw12b13.pos+docvectors -storePositions -storeDocvectors \
-storeRawDocs >& log.cw12b13.pos+docvectors+rawdocs &
```

The directory `/path/to/cw12-b13/` should be the root directory of ClueWeb12-B13 collection, i.e., `/path/to/cw12-b13/` should bring up a bunch of subdirectories, `ClueWeb12_00` to `ClueWeb12_18`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.web.201-250.txt`: [Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/trec2013-topics.xml)
+ `topics.web.251-300.txt`: [Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/trec2014-topics.xml)
+ `qrels.web.201-250.txt`: [one aspect per topic qrels for Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/qrels.adhoc.txt)
+ `qrels.web.251-300.txt`: [one aspect per topic qrels for Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/qrels.adhoc.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12b13.bm25.topics.web.201-250.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12b13.bm25.topics.web.251-300.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12b13.bm25+rm3.topics.web.201-250.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12b13.bm25+rm3.topics.web.251-300.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12b13.bm25+ax.topics.web.201-250.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12b13.bm25+ax.topics.web.251-300.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12b13.ql.topics.web.201-250.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12b13.ql.topics.web.251-300.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12b13.ql+rm3.topics.web.201-250.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12b13.ql+rm3.topics.web.251-300.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12b13.ql+ax.topics.web.201-250.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12b13.ql+ax.topics.web.251-300.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &

```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.bm25.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.bm25.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.bm25.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.bm25.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.bm25+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.bm25+rm3.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.bm25+rm3.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.bm25+rm3.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.bm25+ax.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.bm25+ax.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.bm25+ax.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.bm25+ax.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.ql.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.ql.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.ql.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.ql.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.ql+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.ql+rm3.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.ql+rm3.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.ql+rm3.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.ql+ax.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12b13.ql+ax.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.ql+ax.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12b13.ql+ax.topics.web.251-300.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.0468    | 0.0457    | 0.0435    | 0.0397    | 0.0323    | 0.0359    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.0224    | 0.0192    | 0.0180    | 0.0235    | 0.0212    | 0.0186    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.2113    | 0.1840    | 0.1840    | 0.1767    | 0.1380    | 0.1513    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.1273    | 0.1113    | 0.1107    | 0.1373    | 0.1180    | 0.1167    |


NDCG20                                  | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.1286    | 0.1242    | 0.1287    | 0.1107    | 0.0888    | 0.1143    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.1185    | 0.1030    | 0.0964    | 0.1177    | 0.1040    | 0.1001    |


ERR20                                   | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)| 0.0838    | 0.0880    | 0.0943    | 0.0769    | 0.0576    | 0.0780    |
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)| 0.1201    | 0.1056    | 0.0929    | 0.1091    | 0.1047    | 0.0896    |


