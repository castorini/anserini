# Anserini: Experiments on [Car17](http://trec-car.cs.unh.edu/)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CarCollection \
-generator LuceneDocumentGenerator -threads 40 -input /path/to/car17 -index \
lucene-index.car17.pos+docvectors -storePositions -storeDocvectors -storeRawDocs \
>& log.car17.pos+docvectors+rawdocs &
```

The directory `/path/to/Car17` should be the root directory of Car17 collection, i.e., `ls /path/to/Car17` should bring up a list of `.cbor` files.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.car17.test200.txt`: [Topics for the test200 subset (TREC 2017 Complex Answer Retrieval Track)](http://trec-car.cs.unh.edu/datareleases/v1.5/test200-v1.5.tar.xz)
+ `qrel: qrels.car17.test200.hierarchical.txt`: [adhoc qrels (TREC 2017 Complex Answer Retrieval Track)](http://trec-car.cs.unh.edu/datareleases/v1.5/test200-v1.5.tar.xz)


After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.car17.test200.txt -output run.car17.bm25.topics.car17.test200.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.car17.test200.txt -output run.car17.bm25+rm3.topics.car17.test200.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.car17.test200.txt -output run.car17.bm25+ax.topics.car17.test200.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.car17.test200.txt -output run.car17.ql.topics.car17.test200.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.car17.test200.txt -output run.car17.ql+rm3.topics.car17.test200.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.car17.test200.txt -output run.car17.ql+ax.topics.car17.test200.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17.test200.hierarchical.txt run.car17.bm25.topics.car17.test200.txt

eval/trec_eval.9.0/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17.test200.hierarchical.txt run.car17.bm25+rm3.topics.car17.test200.txt

eval/trec_eval.9.0/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17.test200.hierarchical.txt run.car17.bm25+ax.topics.car17.test200.txt

eval/trec_eval.9.0/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17.test200.hierarchical.txt run.car17.ql.topics.car17.test200.txt

eval/trec_eval.9.0/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17.test200.hierarchical.txt run.car17.ql+rm3.topics.car17.test200.txt

eval/trec_eval.9.0/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17.test200.hierarchical.txt run.car17.ql+ax.topics.car17.test200.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.1689    | 0.1396    | 0.1355    | 0.1516    | 0.1201    | 0.1082    |


RECIP_RANK                              | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.2321    | 0.1922    | 0.1857    | 0.2085    | 0.1661    | 0.1501    |


