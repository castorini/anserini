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
TREC 2010 Web Track: Topics 51-100      | 0.1094    | 0.1075    | 0.0966    | 0.1027    | 0.1060    | 0.1088    |
TREC 2011 Web Track: Topics 101-150     | 0.1095    | 0.1146    | 0.0996    | 0.0971    | 0.0961    | 0.0914    |
TREC 2012 Web Track: Topics 151-200     | 0.1072    | 0.1318    | 0.1242    | 0.1035    | 0.1132    | 0.1215    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC 2010 Web Track: Topics 51-100      | 0.2653    | 0.2604    | 0.2521    | 0.2417    | 0.2507    | 0.2618    |
TREC 2011 Web Track: Topics 101-150     | 0.2540    | 0.2713    | 0.2420    | 0.2220    | 0.2207    | 0.2267    |
TREC 2012 Web Track: Topics 151-200     | 0.2180    | 0.2387    | 0.2313    | 0.2013    | 0.2040    | 0.2100    |


NDCG20                                  | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC 2010 Web Track: Topics 51-100      | 0.1328    | 0.1423    | 0.1715    | 0.1132    | 0.1314    | 0.1470    |
TREC 2011 Web Track: Topics 101-150     | 0.1914    | 0.1861    | 0.1877    | 0.1635    | 0.1608    | 0.1592    |
TREC 2012 Web Track: Topics 151-200     | 0.0976    | 0.1308    | 0.1187    | 0.0862    | 0.1075    | 0.1055    |


ERR20                                   | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC 2010 Web Track: Topics 51-100      | 0.0717    | 0.0784    | 0.1007    | 0.0586    | 0.0649    | 0.0802    |
TREC 2011 Web Track: Topics 101-150     | 0.0947    | 0.1081    | 0.1064    | 0.0842    | 0.0921    | 0.0879    |
TREC 2012 Web Track: Topics 151-200     | 0.1382    | 0.2179    | 0.1921    | 0.1315    | 0.1574    | 0.1583    |


