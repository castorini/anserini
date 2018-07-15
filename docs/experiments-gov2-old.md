# Anserini: Experiments on Gov2

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection Gov2Collection \
 -input /path/to/gov2/ -generator JsoupGenerator \
 -index lucene-index.gov2.pos+docvectors -threads 16 \
 -storePositions -storeDocvectors \
 >& log.gov2.pos+docvectors &
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.701-750.txt`: [Topics 701-750 (TREC 2004 Terabyte Track)](http://trec.nist.gov/data/terabyte/04/04topics.701-750.txt)
+ `topics.751-800.txt`: [Topics 751-800 (TREC 2005 Terabyte Track)](http://trec.nist.gov/data/terabyte/05/05.topics.751-800.txt)
+ `topics.801-850.txt`: [Topics 801-850 (TREC 2006 Terabyte Track)](http://trec.nist.gov/data/terabyte/06/06.topics.801-850.txt)
+ `qrels.701-750.txt`: [qrels for Topics 701-750 (TREC 2004 Terabyte Track)](http://trec.nist.gov/data/terabyte/04/04.qrels.12-Nov-04)
+ `qrels.751-800.txt`: [qrels for Topics 751-800 (TREC 2005 Terabyte Track)](http://trec.nist.gov/data/terabyte/05/05.adhoc_qrels)
+ `qrels.801-850.txt`: [qrels for Topics 801-850 (TREC 2006 Terabyte Track)](http://trec.nist.gov/data/terabyte/06/qrels.tb06.top50)
 
After indexing has completed, you should be able to perform retrieval as follows:

```
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25.txt -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.bm25.txt -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.bm25.txt -bm25 &

sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25+rm3.txt -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.bm25+rm3.txt -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.bm25+rm3.txt -bm25 -rm3 &

sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.ql.txt -ql &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.ql.txt -ql &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.ql.txt -ql &

sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.ql+rm3.txt -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.ql+rm3.txt -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.ql+rm3.txt -ql -rm3 &
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).

Use `trec_eval` to compute AP and P30:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.751-800.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.801-850.bm25.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.751-800.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.801-850.bm25+rm3.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.751-800.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.801-850.ql.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.ql+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.751-800.ql+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.801-850.ql+rm3.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                                                                     | BM25   |BM25+RM3| QL     | QL+RM3
:---------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.2673 | 0.2981 | 0.2636 | 0.2777
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.3366 | 0.3843 | 0.3264 | 0.3620
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.3055 | 0.3439 | 0.2957 | 0.3168


P30                                                                                     | BM25   |BM25+RM3|  QL    | QL+RM3
:---------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.4837 | 0.5333 | 0.4667 | 0.4912
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.5520 | 0.5933 | 0.5160 | 0.5700
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.4900 | 0.5240 | 0.4753 | 0.4840
