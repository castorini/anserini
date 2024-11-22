# Anserini Regressions: CAR17 (v2.0)

**Models**: various bag-of-words approaches

This page documents regression experiments for the [TREC 2017 Complex Answer Retrieval (CAR)](http://trec-car.cs.unh.edu/) section-level passage retrieval task (v2.0).
The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/car17v2.0.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/car17v2.0.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression car17v2.0
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection CarCollection \
  -input /path/to/car-paragraphCorpus.v2.0 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.car-paragraphCorpus.v2.0/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.car-paragraphCorpus.v2.0 &
```

The directory `/path/to/car17v2.0` should be the root directory of Complex Answer Retrieval (CAR) paragraph corpus (v2.0), which can be downloaded [here](http://trec-car.cs.unh.edu/datareleases/).

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

The "benchmarkY1-test" topics and qrels (v2.0) are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from [the CAR website](http://trec-car.cs.unh.edu/datareleases/):

+ [`topics.car17v2.0.benchmarkY1test.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt)
+ [`qrels.car17v2.0.benchmarkY1test.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt)

Specifically, this is the section-level passage retrieval task with automatic ground truth.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.car-paragraphCorpus.v2.0/ \
  -topics tools/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt \
  -topicReader Car \
  -output runs/run.car-paragraphCorpus.v2.0.bm25.topics.car17v2.0.benchmarkY1test.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.car-paragraphCorpus.v2.0/ \
  -topics tools/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt \
  -topicReader Car \
  -output runs/run.car-paragraphCorpus.v2.0.bm25+rm3.topics.car17v2.0.benchmarkY1test.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.car-paragraphCorpus.v2.0/ \
  -topics tools/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt \
  -topicReader Car \
  -output runs/run.car-paragraphCorpus.v2.0.bm25+ax.topics.car17v2.0.benchmarkY1test.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.car-paragraphCorpus.v2.0/ \
  -topics tools/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt \
  -topicReader Car \
  -output runs/run.car-paragraphCorpus.v2.0.ql.topics.car17v2.0.benchmarkY1test.txt \
  -qld &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.car-paragraphCorpus.v2.0/ \
  -topics tools/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt \
  -topicReader Car \
  -output runs/run.car-paragraphCorpus.v2.0.ql+rm3.topics.car17v2.0.benchmarkY1test.txt \
  -qld -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.car-paragraphCorpus.v2.0/ \
  -topics tools/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt \
  -topicReader Car \
  -output runs/run.car-paragraphCorpus.v2.0.ql+ax.topics.car17v2.0.benchmarkY1test.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m map -c -m recip_rank tools/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt runs/run.car-paragraphCorpus.v2.0.bm25.topics.car17v2.0.benchmarkY1test.txt

bin/trec_eval -c -m map -c -m recip_rank tools/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt runs/run.car-paragraphCorpus.v2.0.bm25+rm3.topics.car17v2.0.benchmarkY1test.txt

bin/trec_eval -c -m map -c -m recip_rank tools/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt runs/run.car-paragraphCorpus.v2.0.bm25+ax.topics.car17v2.0.benchmarkY1test.txt

bin/trec_eval -c -m map -c -m recip_rank tools/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt runs/run.car-paragraphCorpus.v2.0.ql.topics.car17v2.0.benchmarkY1test.txt

bin/trec_eval -c -m map -c -m recip_rank tools/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt runs/run.car-paragraphCorpus.v2.0.ql+rm3.topics.car17v2.0.benchmarkY1test.txt

bin/trec_eval -c -m map -c -m recip_rank tools/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt runs/run.car-paragraphCorpus.v2.0.ql+ax.topics.car17v2.0.benchmarkY1test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [TREC 2017 CAR: benchmarkY1test (v2.0)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt)| 0.1545    | 0.1308    | 0.1364    | 0.1371    | 0.1083    | 0.1077    |
| **MRR**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [TREC 2017 CAR: benchmarkY1test (v2.0)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt)| 0.2321    | 0.1940    | 0.1978    | 0.2013    | 0.1608    | 0.1588    |
