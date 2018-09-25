# Anserini: Experiments on [ClueWeb09 (Category B)](http://lemurproject.org/clueweb09.php/)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection ClueWeb09Collection \
-generator JsoupGenerator -threads 44 -input /path/to/cw09b -index \
lucene-index.cw09b.pos+docvectors -storePositions -storeDocvectors -storeRawDocs \
>& log.cw09b.pos+docvectors+rawdocs &
```

The directory `/path/to/ClueWeb09b` should be the root directory of ClueWeb09B collection, i.e., `ls /path/to/ClueWeb09b` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.web.1-50.txt`: [Topics 1-50 (TREC 2009 Web Track)](http://trec.nist.gov/data/web/09/wt09.topics.full.xml)
+ `topics.web.51-100.txt`: [Topics 51-100 (TREC 2010 Web Track)](http://trec.nist.gov/data/web/10/wt2010-topics.xml)
+ `topics.web.101-150.txt`: [Topics 101-150 (TREC 2011 Web Track)](http://trec.nist.gov/data/web/11/full-topics.xml)
+ `topics.web.151-200.txt`: [Topics 151-200 (TREC 2012 Web Track)](http://trec.nist.gov/data/web/12/full-topics.xml)
+ `qrels.web.1-50.txt`: [adhoc prels for category B runs for Topics 1-50 (TREC 2009 Web Track)](http://trec.nist.gov/data/web/09/prels.catB.1-50.gz)
+ `qrels.web.51-100.txt`: [adhoc qrels for Topics 51-100 (TREC 2010 Web Track)](http://trec.nist.gov/data/web/10/10.adhoc-qrels.final)
+ `qrels.web.101-150.txt`: [adhoc qrels for Topics 101-150 (TREC 2011 Web Track)](http://trec.nist.gov/data/web/11/qrels.adhoc)
+ `qrels.web.151-200.txt`: [adhoc qrels for Topics 151-200 (TREC 2012 Web Track)](http://trec.nist.gov/data/web/12/qrels.adhoc)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.cw09b.bm25.topics.web.51-100.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.cw09b.bm25.topics.web.101-150.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.cw09b.bm25.topics.web.151-200.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.cw09b.bm25+rm3.topics.web.51-100.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.cw09b.bm25+rm3.topics.web.101-150.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.cw09b.bm25+rm3.topics.web.151-200.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.cw09b.bm25+ax.topics.web.51-100.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.cw09b.bm25+ax.topics.web.101-150.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.cw09b.bm25+ax.topics.web.151-200.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.cw09b.ql.topics.web.51-100.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.cw09b.ql.topics.web.101-150.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.cw09b.ql.topics.web.151-200.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.cw09b.ql+rm3.topics.web.51-100.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.cw09b.ql+rm3.topics.web.101-150.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.cw09b.ql+rm3.topics.web.151-200.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.cw09b.ql+ax.topics.web.51-100.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.cw09b.ql+ax.topics.web.101-150.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.cw09b.ql+ax.topics.web.151-200.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &

```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25.topics.web.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25.topics.web.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25.topics.web.151-200.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25.topics.web.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+rm3.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+rm3.topics.web.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+rm3.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+rm3.topics.web.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+rm3.topics.web.151-200.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+rm3.topics.web.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+ax.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.bm25+ax.topics.web.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+ax.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.bm25+ax.topics.web.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+ax.topics.web.151-200.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.bm25+ax.topics.web.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql.topics.web.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql.topics.web.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql.topics.web.151-200.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql.topics.web.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+rm3.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+rm3.topics.web.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+rm3.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+rm3.topics.web.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+rm3.topics.web.151-200.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+rm3.topics.web.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+ax.topics.web.51-100.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.cw09b.ql+ax.topics.web.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+ax.topics.web.101-150.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.cw09b.ql+ax.topics.web.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+ax.topics.web.151-200.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.cw09b.ql+ax.topics.web.151-200.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC 2010 Web Track: Topics 51-100      | 0.1126    | 0.1156    | 0.0928    | 0.1060    | 0.1123    | 0.1086    |
TREC 2011 Web Track: Topics 101-150     | 0.1094    | 0.1135    | 0.0974    | 0.0958    | 0.0969    | 0.0879    |
TREC 2012 Web Track: Topics 151-200     | 0.1106    | 0.1374    | 0.1315    | 0.1069    | 0.1168    | 0.1212    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC 2010 Web Track: Topics 51-100      | 0.2681    | 0.2847    | 0.2354    | 0.2431    | 0.2646    | 0.2618    |
TREC 2011 Web Track: Topics 101-150     | 0.2513    | 0.2627    | 0.2393    | 0.2147    | 0.2160    | 0.2167    |
TREC 2012 Web Track: Topics 151-200     | 0.2167    | 0.2467    | 0.2553    | 0.2080    | 0.2053    | 0.2140    |


NDCG20                                  | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC 2010 Web Track: Topics 51-100      | 0.1354    | 0.1505    | 0.1637    | 0.1143    | 0.1379    | 0.1454    |
TREC 2011 Web Track: Topics 101-150     | 0.1890    | 0.1798    | 0.1833    | 0.1619    | 0.1527    | 0.1509    |
TREC 2012 Web Track: Topics 151-200     | 0.1014    | 0.1319    | 0.1441    | 0.0868    | 0.1055    | 0.1030    |


ERR20                                   | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC 2010 Web Track: Topics 51-100      | 0.0733    | 0.0840    | 0.0981    | 0.0599    | 0.0653    | 0.0742    |
TREC 2011 Web Track: Topics 101-150     | 0.0959    | 0.1035    | 0.1091    | 0.0849    | 0.0868    | 0.0820    |
TREC 2012 Web Track: Topics 151-200     | 0.1304    | 0.2221    | 0.2355    | 0.1305    | 0.1479    | 0.1558    |


