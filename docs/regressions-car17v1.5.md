# Anserini: Regressions for [TREC 2017 CAR](http://trec-car.cs.unh.edu/) (v1.5)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection CarCollection \
-generator LuceneDocumentGenerator -threads 40 -input /path/to/car17v1.5 -index \
lucene-index.car17v1.5.pos+docvectors+rawdocs -storePositions -storeDocvectors \
-storeRawDocs >& log.car17v1.5.pos+docvectors+rawdocs &
```

The directory `/path/to/Car17v1.5` should be the root directory of Complex Answer Retrieval (CAR) paragraph corpus (v1.5), which can be downloaded [here](http://trec-car.cs.unh.edu/datareleases/).

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

The "benchmarkY1-test" topics and qrels (v1.5) are stored in `src/main/resources/topics-and-qrels/`, downloaded from [the CAR website](http://trec-car.cs.unh.edu/datareleases/):

+ `topics.car17v1.5.benchmarkY1test.txt`
+ `qrels.car17v1.5.benchmarkY1test.txt`

Specifically, this is the section-level passage retrieval task with automatic ground truth.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v1.5.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v1.5.benchmarkY1test.txt -output run.car17v1.5.bm25.topics.car17v1.5.benchmarkY1test.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v1.5.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v1.5.benchmarkY1test.txt -output run.car17v1.5.bm25+rm3.topics.car17v1.5.benchmarkY1test.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v1.5.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v1.5.benchmarkY1test.txt -output run.car17v1.5.bm25+ax.topics.car17v1.5.benchmarkY1test.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v1.5.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v1.5.benchmarkY1test.txt -output run.car17v1.5.ql.topics.car17v1.5.benchmarkY1test.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v1.5.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v1.5.benchmarkY1test.txt -output run.car17v1.5.ql+rm3.topics.car17v1.5.benchmarkY1test.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v1.5.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v1.5.benchmarkY1test.txt -output run.car17v1.5.ql+ax.topics.car17v1.5.benchmarkY1test.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt run.car17v1.5.bm25.topics.car17v1.5.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt run.car17v1.5.bm25+rm3.topics.car17v1.5.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt run.car17v1.5.bm25+ax.topics.car17v1.5.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt run.car17v1.5.ql.topics.car17v1.5.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt run.car17v1.5.ql+rm3.topics.car17v1.5.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt run.car17v1.5.ql+ax.topics.car17v1.5.benchmarkY1test.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2017 CAR: benchmarkY1test (v1.5)](http://trec-car.cs.unh.edu/datareleases/)| 0.1562    | 0.1295    | 0.1358    | 0.1386    | 0.1080    | 0.1048    |


RECIP_RANK                              | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2017 CAR: benchmarkY1test (v1.5)](http://trec-car.cs.unh.edu/datareleases/)| 0.2331    | 0.1923    | 0.1949    | 0.2037    | 0.1599    | 0.1524    |


