# Anserini: Experiments on ClueWeb09 (Category B)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CW09Collection \
 -input /path/to/ClueWeb09b/ClueWeb09_English_1/ -generator JsoupGenerator \
 -index lucene-index.cw09b.pos+docvectors -threads 32 \
 -storePositions -storeDocvectors \
 >& log.cw09b.pos+docvectors &
```

The directory `/path/to/cw09/ClueWeb09_English_1` should be the root directory of ClueWeb09B collection, i.e., `ls /path/to/cw09/ClueWeb09_English_1` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
After indexing has completed, you should be able to perform retrieval as follows:

```
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt  -output run.web.51-100.bm25.txt  -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.bm25.txt -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.bm25.txt -bm25 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt  -output run.web.51-100.bm25+rm3.txt  -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.bm25+rm3.txt -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.bm25+rm3.txt -bm25 -rm3 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt  -output run.web.51-100.ql.txt  -ql &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.ql.txt -ql &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.ql.txt -ql &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt  -output run.web.51-100.ql+rm3.txt  -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.ql+rm3.txt -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw09b.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt  run.web.51-100.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.bm25.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt  run.web.51-100.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.bm25+rm3.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt  run.web.51-100.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt run.web.101-150.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.ql.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt  run.web.51-100.ql+rm3.txt
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
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1091 | 0.1065 | 0.1026 | 0.1056
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1095 | 0.1140 | 0.0972 | 0.1021
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.1072 | 0.1336 | 0.1035 | 0.1120


P30                                                                           | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.2667 | 0.2583 | 0.2403 | 0.2528
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.2540 | 0.2627 | 0.2220 | 0.2267
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.2187 | 0.2313 | 0.2027 | 0.2007

NDCG@20                                                                       | BM25   |BM25+RM3| QL     | QL+RM3
:-----------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2010 Web Track: Topics 51-100](http://trec.nist.gov/data/web10.html)    | 0.1320 | 0.1443 | 0.1131 | 0.1307
[TREC 2011 Web Track: Topics 101-150](http://trec.nist.gov/data/web2011.html) | 0.1915 | 0.1920 | 0.1633 | 0.1670
[TREC 2012 Web Track: Topics 151-200](http://trec.nist.gov/data/web2012.html) | 0.0977 | 0.1309 | 0.0862 | 0.1027
