# Anserini: Experiments on ClueWeb12-B13

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CW12Collection \
 -input /path/to/ClueWeb12-B13/DiskB/ -generator JsoupGenerator \
 -index lucene-index.cw12b13.pos+docvectors -threads 32 \
 -storePositions -storeDocvectors \
 >& log.cw12b13.pos+docvectors &
```

The directory `/path/to/cw12-b13/` should be the root directory of ClueWeb12-B13 collection, i.e., `/path/to/cw12-b13/` should bring up a bunch of subdirectories, `ClueWeb12_00` to `ClueWeb12_18`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
After indexing has completed, you should be able to perform retrieval as follows:

```
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25.txt -bm25 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.bm25.txt -bm25 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25+rm3.txt -bm25 -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.bm25+rm3.txt -bm25 -rm3 &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.ql.txt -ql &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.ql.txt -ql &

sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.ql+rm3.txt -ql -rm3 &
sh target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12b13.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25+rm3.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql+rm3.txt
```

And to compute NDCG:

```
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.bm25+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.bm25+rm3.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql.txt | grep 'amean'

eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt run.web.201-250.ql+rm3.txt | grep 'amean'
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt run.web.251-300.ql+rm3.txt | grep 'amean'
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

AP                                                                             | BM25   |BM25+RM3| QL     | QL+RM3
:------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.0457 | 0.0438 | 0.0389 | 0.0314
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.0219 | 0.0188 | 0.0228 | 0.0204

P30                                                                            | BM25   |BM25+RM3| QL     | QL+RM3
:------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.2000 | 0.1773 | 0.1720 | 0.1433
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.1293 | 0.1140 | 0.1313 | 0.1160

NDCG@20                                                                        | BM25   |BM25+RM3| QL     | QL+RM3
:------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2013 Web Track: Topics 201-250](http://trec.nist.gov/data/web2013.html)  | 0.1242 | 0.1172 | 0.1158 | 0.0878
[TREC 2014 Web Track: Topics 251-300](http://trec.nist.gov/data/web2014.html)  | 0.1190 | 0.0987 | 0.1133 | 0.0961
