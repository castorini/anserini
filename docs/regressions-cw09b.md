# Anserini: Regressions for [ClueWeb09 (Category B)](http://lemurproject.org/clueweb09.php/)

This page describes regressions for the Web Tracks from TREC 2009 to 2012 using the [ClueWeb09 (Category B) collection](http://lemurproject.org/clueweb09.php/).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/cw09b.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/cw09b.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection ClueWeb09Collection -input /path/to/cw09b \
 -index lucene-index.cw09b.pos+docvectors+rawdocs -generator JsoupGenerator -threads 44 \
 -storePositions -storeDocvectors -storeRawDocs >& log.cw09b.pos+docvectors+rawdocs &
```

The directory `/path/to/ClueWeb09b` should be the root directory of the [ClueWeb09 (Category B) collection](http://lemurproject.org/clueweb09.php/), i.e., `ls /path/to/ClueWeb09b` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.web.1-50.txt`](../src/main/resources/topics-and-qrels/topics.web.1-50.txt): [topics for the TREC 2009 Web Track (Topics 1-50)](http://trec.nist.gov/data/web/09/wt09.topics.full.xml)
+ [`topics.web.51-100.txt`](../src/main/resources/topics-and-qrels/topics.web.51-100.txt): [topics for the TREC 2010 Web Track (Topics 51-100)](http://trec.nist.gov/data/web/10/wt2010-topics.xml)
+ [`topics.web.101-150.txt`](../src/main/resources/topics-and-qrels/topics.web.101-150.txt): [topics for the TREC 2011 Web Track (Topics 101-150)](http://trec.nist.gov/data/web/11/full-topics.xml)
+ [`topics.web.151-200.txt`](../src/main/resources/topics-and-qrels/topics.web.151-200.txt): [topics for the TREC 2012 Web Track (Topics 151-200)](http://trec.nist.gov/data/web/12/full-topics.xml)
+ [`prels.web.1-50.txt`](../src/main/resources/topics-and-qrels/prels.web.1-50.txt): [prels for the TREC 2009 Web Track (Topics 1-50, category B runs)](http://trec.nist.gov/data/web/09/prels.catB.1-50.gz)
+ [`qrels.web.51-100.txt`](../src/main/resources/topics-and-qrels/qrels.web.51-100.txt): [qrels for the TREC 2010 Web Track (Topics 51-100)](http://trec.nist.gov/data/web/10/10.adhoc-qrels.final)
+ [`qrels.web.101-150.txt`](../src/main/resources/topics-and-qrels/qrels.web.101-150.txt): [qrels for the TREC 2011 Web Track (Topics 101-150)](http://trec.nist.gov/data/web/11/qrels.adhoc)
+ [`qrels.web.151-200.txt`](../src/main/resources/topics-and-qrels/qrels.web.151-200.txt): [qrels for the TREC 2012 Web Track (Topics 151-200)](http://trec.nist.gov/data/web/12/qrels.adhoc)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt \
 -bm25 -output run.cw09b.bm25.topics.web.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt \
 -bm25 -output run.cw09b.bm25.topics.web.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt \
 -bm25 -output run.cw09b.bm25.topics.web.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt \
 -bm25 -rm3 -output run.cw09b.bm25+rm3.topics.web.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt \
 -bm25 -rm3 -output run.cw09b.bm25+rm3.topics.web.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt \
 -bm25 -rm3 -output run.cw09b.bm25+rm3.topics.web.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 -output run.cw09b.bm25+ax.topics.web.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 -output run.cw09b.bm25+ax.topics.web.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 -output run.cw09b.bm25+ax.topics.web.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt \
 -ql -output run.cw09b.ql.topics.web.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt \
 -ql -output run.cw09b.ql.topics.web.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt \
 -ql -output run.cw09b.ql.topics.web.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt \
 -ql -rm3 -output run.cw09b.ql+rm3.topics.web.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt \
 -ql -rm3 -output run.cw09b.ql+rm3.topics.web.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt \
 -ql -rm3 -output run.cw09b.ql+rm3.topics.web.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 -output run.cw09b.ql+ax.topics.web.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 -output run.cw09b.ql+ax.topics.web.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.cw09b.pos+docvectors+rawdocs \
 -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 -output run.cw09b.ql+ax.topics.web.151-200.txt &
```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25.topics.web.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25.topics.web.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25.topics.web.151-200.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25.topics.web.151-200.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+rm3.topics.web.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+rm3.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+rm3.topics.web.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+rm3.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+rm3.topics.web.151-200.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+rm3.topics.web.151-200.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+ax.topics.web.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+ax.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+ax.topics.web.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+ax.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+ax.topics.web.151-200.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+ax.topics.web.151-200.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql.topics.web.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql.topics.web.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql.topics.web.151-200.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql.topics.web.151-200.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+rm3.topics.web.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+rm3.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+rm3.topics.web.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+rm3.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+rm3.topics.web.151-200.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+rm3.topics.web.151-200.txt

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+ax.topics.web.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+ax.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+ax.topics.web.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+ax.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+ax.topics.web.151-200.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+ax.topics.web.151-200.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.1126    | 0.0933    | 0.0929    | 0.1060    | 0.1019    | 0.1086    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.1094    | 0.1085    | 0.0975    | 0.0958    | 0.0839    | 0.0879    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.1105    | 0.1107    | 0.1315    | 0.1069    | 0.1058    | 0.1212    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.2694    | 0.2389    | 0.2354    | 0.2431    | 0.2312    | 0.2618    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.2513    | 0.2480    | 0.2387    | 0.2147    | 0.2047    | 0.2173    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.2167    | 0.1920    | 0.2553    | 0.2080    | 0.1980    | 0.2147    |


NDCG20                                  | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.1354    | 0.1369    | 0.1632    | 0.1143    | 0.1182    | 0.1454    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.1890    | 0.1916    | 0.1835    | 0.1619    | 0.1449    | 0.1517    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.1014    | 0.0918    | 0.1441    | 0.0868    | 0.0896    | 0.1037    |


ERR20                                   | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.0733    | 0.0747    | 0.0977    | 0.0599    | 0.0592    | 0.0742    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.0959    | 0.0960    | 0.1091    | 0.0849    | 0.0787    | 0.0821    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.1303    | 0.1494    | 0.2355    | 0.1305    | 0.1334    | 0.1558    |
