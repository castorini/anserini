# Anserini: Regressions for [ClueWeb12](http://lemurproject.org/clueweb12.php/)

This page describes regressions for the Web Tracks from TREC 2013 and 2014 using the (full) [ClueWeb12 collection](http://lemurproject.org/clueweb12.php/).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/cw12.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/cw12.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection ClueWeb12Collection -input /path/to/cw12 \
 -index lucene-index.cw12.pos+docvectors+rawdocs -generator JsoupGenerator -threads 44 \
 -storePositions -storeDocvectors -storeRawDocs >& log.cw12.pos+docvectors+rawdocs &
```

The directory `/path/to/cw12/` should be the root directory of the (full) [ClueWeb12 collection](http://lemurproject.org/clueweb12.php/), i.e., `/path/to/cw12/` should contain `Disk1`, `Disk2`, `Disk3`, `Disk4`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.web.201-250.txt`](../src/main/resources/topics-and-qrels/topics.web.201-250.txt): [topics for the TREC 2013 Web Track (Topics 201-250)](http://trec.nist.gov/data/web/2013/trec2013-topics.xml)
+ [`topics.web.251-300.txt`](../src/main/resources/topics-and-qrels/topics.web.251-300.txt): [topics for the TREC 2014 Web Track (Topics 251-300)](http://trec.nist.gov/data/web/2014/trec2014-topics.xml)
+ [`qrels.web.201-250.txt`](../src/main/resources/topics-and-qrels/qrels.web.201-250.txt): [one aspect per topic qrels for the TREC 2013 Web Track (Topics 201-250)](http://trec.nist.gov/data/web/2013/qrels.adhoc.txt)
+ [`qrels.web.251-300.txt`](../src/main/resources/topics-and-qrels/qrels.web.251-300.txt): [one aspect per topic qrels for the TREC 2014 Web Track (Topics 251-300)](http://trec.nist.gov/data/web/2014/qrels.adhoc.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
 -bm25 -output run.cw12.bm25.topics.web.201-250.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
 -bm25 -output run.cw12.bm25.topics.web.251-300.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
 -bm25 -rm3 -output run.cw12.bm25+rm3.topics.web.201-250.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
 -bm25 -rm3 -output run.cw12.bm25+rm3.topics.web.251-300.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
 -ql -output run.cw12.ql.topics.web.201-250.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
 -ql -output run.cw12.ql.topics.web.251-300.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
 -ql -rm3 -output run.cw12.ql+rm3.topics.web.201-250.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
 -ql -rm3 -output run.cw12.ql+rm3.topics.web.251-300.txt &
```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25.topics.web.201-250.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25.topics.web.251-300.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25.topics.web.251-300.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25+rm3.topics.web.201-250.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.bm25+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25+rm3.topics.web.251-300.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.bm25+rm3.topics.web.251-300.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql.topics.web.201-250.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql.topics.web.251-300.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql.topics.web.251-300.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql+rm3.topics.web.201-250.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.cw12.ql+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql+rm3.topics.web.251-300.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.cw12.ql+rm3.topics.web.251-300.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | QL        | +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.1694    | 0.1464    | 0.1494    | 0.1290    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.2469    | 0.2324    | 0.2466    | 0.2177    |


P30                                     | BM25      | +RM3      | QL        | +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.2773    | 0.2393    | 0.2607    | 0.2347    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.4547    | 0.4080    | 0.4380    | 0.3800    |


NDCG20                                  | BM25      | +RM3      | QL        | +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.2088    | 0.2033    | 0.1993    | 0.1725    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.2572    | 0.2530    | 0.2218    | 0.2083    |


ERR20                                   | BM25      | +RM3      | QL        | +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.1284    | 0.1264    | 0.1233    | 0.1008    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.1616    | 0.1655    | 0.1322    | 0.1245    |
