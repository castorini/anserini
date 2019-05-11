# Anserini: Experiments on [TREC 2017 CAR](http://trec-car.cs.unh.edu/) (v2.0)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CarCollection \
-generator LuceneDocumentGenerator -threads 40 -input /path/to/car17v2.0 -index \
lucene-index.car17v2.0.pos+docvectors+rawdocs -storePositions -storeDocvectors \
-storeRawDocs >& log.car17v2.0.pos+docvectors+rawdocs &
```

The directory `/path/to/Car17v2.0` should be the root directory of Complex Answer Retrieval (CAR) paragraph corpus (v2.0), which can be downloaded [here](http://trec-car.cs.unh.edu/datareleases/).

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

The "benchmarkY1-test" topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from [the CAR website](http://trec-car.cs.unh.edu/datareleases/):

+ `topics.car17v2.0.test.pages.cbor-hierarchical.txt`
+ `qrels.car17v2.0.test.pages.cbor-hierarchical.txt`


After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0.bm25.topics.car17v2.0.benchmarkY1test.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0.bm25+rm3.topics.car17v2.0.benchmarkY1test.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0.bm25+ax.topics.car17v2.0.benchmarkY1test.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0.ql.topics.car17v2.0.benchmarkY1test.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0.ql+rm3.topics.car17v2.0.benchmarkY1test.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0.ql+ax.topics.car17v2.0.benchmarkY1test.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0.bm25.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0.bm25+rm3.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0.bm25+ax.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0.ql.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0.ql+rm3.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0.ql+ax.topics.car17v2.0.benchmarkY1test.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
benchmarkY1test                         | 0.1528    | 0.1270    | 0.1342    | 0.1353    | 0.1065    | 0.1054    |


RECIP_RANK                              | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
benchmarkY1test                         | 0.2294    | 0.1903    | 0.1943    | 0.1989    | 0.1577    | 0.1554    |


