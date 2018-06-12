# Anserini: Experiments on Gov2

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection Gov2Collection \
 -input /tuna1/collections/web/gov2/gov2-corpus/ -generator JsoupGenerator \
 -index lucene-index.gov2.pos+docvectors -threads 16 \
 -storePositions -storeDocvectors -optimize >& log.gov2.pos+docvectors &
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
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
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.2673 | 0.2953 | 0.2635 | 0.2800
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.3365 | 0.3837 | 0.3263 | 0.3627
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.3053 | 0.3411 | 0.2955 | 0.3199


P30                                                                                     | BM25   |BM25+RM3|  QL    | QL+RM3
:---------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.4850 | 0.5306 | 0.4673 | 0.4850
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.5520 | 0.5913 | 0.5167 | 0.5660
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.4913 | 0.5260 | 0.4760 | 0.4873

There is a small amount of non-determinism, particularly with RM3, so tiny differences in the fourth decimal place might be observed.
