# Anserini: Regressions for [CAR17](http://trec-car.cs.unh.edu/) (v2.0) + Doc2query

This page documents regression experiments for the [TREC 2017 Complex Answer Retrieval (CAR)](http://trec-car.cs.unh.edu/) section-level passage retrieval task (v2.0), with Doc2query expansions, as proposed in the following paper:

+ Rodrigo Nogueira, Wei Yang, Jimmy Lin, Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) _arxiv:1904.08375_

These experiments are integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-doc2query.md).

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
-generator LuceneDocumentGenerator -threads 30 -input \
/path/to/car17v2.0-doc2query -index \
lucene-index.car17v2.0-doc2query.pos+docvectors+rawdocs -storePositions \
-storeDocvectors -storeRawDocs >& log.car17v2.0-doc2query.pos+docvectors+rawdocs \
&
```

The directory `/path/to/car17v2.0-doc2query` should be the root directory of Complex Answer Retrieval (CAR) paragraph corpus (v2.0) that has been augmented with the Doc2query expansions, i.e., `collection_jsonl_expanded_topk10/` as described in [this page](experiments-doc2query.md).

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

The "benchmarkY1-test" topics and qrels (v2.0) are stored in `src/main/resources/topics-and-qrels/`, downloaded from [the CAR website](http://trec-car.cs.unh.edu/datareleases/):

+ `topics.car17v2.0.benchmarkY1test.txt`
+ `qrels.car17v2.0.benchmarkY1test.txt`

Specifically, this is the section-level passage retrieval task with automatic ground truth.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0-doc2query.bm25.topics.car17v2.0.benchmarkY1test.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0-doc2query.bm25+rm3.topics.car17v2.0.benchmarkY1test.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0-doc2query.bm25+ax.topics.car17v2.0.benchmarkY1test.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0-doc2query.ql.topics.car17v2.0.benchmarkY1test.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0-doc2query.ql+rm3.topics.car17v2.0.benchmarkY1test.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index.car17v2.0-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt -output run.car17v2.0-doc2query.ql+ax.topics.car17v2.0.benchmarkY1test.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0-doc2query.bm25.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0-doc2query.bm25+rm3.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0-doc2query.bm25+ax.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0-doc2query.ql.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0-doc2query.ql+rm3.topics.car17v2.0.benchmarkY1test.txt

eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt run.car17v2.0-doc2query.ql+ax.topics.car17v2.0.benchmarkY1test.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2017 CAR: benchmarkY1test (v2.0)](http://trec-car.cs.unh.edu/datareleases/)| 0.1807    | 0.1521    | 0.1470    | 0.1752    | 0.1453    | 0.1339    |


RECIP_RANK                              | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2017 CAR: benchmarkY1test (v2.0)](http://trec-car.cs.unh.edu/datareleases/)| 0.2750    | 0.2275    | 0.2186    | 0.2653    | 0.2156    | 0.1981    |


