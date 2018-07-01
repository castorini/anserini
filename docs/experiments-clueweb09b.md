# Anserini: Experiments on ClueWeb09 (Category B)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CW09Collection \
 -input /path/to/ClueWeb09b/ -generator JsoupGenerator \
 -index lucene-index.cw09b.pos+docvectors -threads 32 \
 -storePositions -storeDocvectors \
 >& log.cw09b.pos+docvectors &
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
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.bm25.txt -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.bm25.txt -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.bm25.txt -bm25 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.bm25+rm3.txt -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.bm25+rm3.txt -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.bm25+rm3.txt -bm25 -rm3 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.ql.txt -ql &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.ql.txt -ql &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.ql.txt -ql &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.ql+rm3.txt -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.ql+rm3.txt -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.bm25.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.bm25+rm3.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.ql.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.ql+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.ql+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.ql+rm3.txt
```

And to compute NDCG:

```
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.bm25.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.bm25.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.bm25.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.bm25+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.bm25+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.bm25+rm3.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.ql.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.ql.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.ql.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt run.web.51-100.ql+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.ql+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.ql+rm3.txt | grep 'amean'
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

AP                                                                            | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1094 | 0.1077 | 0.1027 | 0.1061
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1095 | 0.1147 | 0.0971 | 0.0963
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.1072 | 0.1319 | 0.1035 | 0.1140


P30                                                                           | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.2653 | 0.2556 | 0.2417 | 0.2535
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.2540 | 0.2687 | 0.2220 | 0.2187
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.2180 | 0.2400 | 0.2013 | 0.2053

NDCG@20                                                                       | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1328 | 0.1430 | 0.1132 | 0.1328
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1914 | 0.1885 | 0.1635 | 0.1608
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.0976 | 0.1254 | 0.0862 | 0.1084
