# Anserini Regressions: TIPSTER Disks 1 &amp; 2

**Models**: various bag-of-words approaches

This page describes regressions for ad hoc topics from TREC 1-3, which use [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A).
The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/disk12.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/disk12.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression disk12
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection TrecCollection \
  -input /path/to/disk12 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.disk12/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.disk12 &
```

The directory `/path/to/disk12/` should be the root directory of [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A), i.e., `ls /path/to/disk12/` should bring up subdirectories like `doe`, `wsj`.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from NIST:

+ [`topics.adhoc.51-100.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.51-100.txt): [TREC-1 Ad Hoc Topics 51-100](http://trec.nist.gov/data/topics_eng/)
+ [`topics.adhoc.101-150.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.101-150.txt): [TREC-2 Ad Hoc Topics 101-150](http://trec.nist.gov/data/topics_eng/)
+ [`topics.adhoc.151-200.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.151-200.txt): [TREC-3 Ad Hoc Topics 151-200](http://trec.nist.gov/data/topics_eng/)
+ [`qrels.adhoc.51-100.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.adhoc.51-100.txt): [qrels for TREC-1 Ad Hoc Topics 51-100](http://trec.nist.gov/data/qrels_eng/)
+ [`qrels.adhoc.101-150.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.adhoc.101-150.txt): [qrels for TREC-2 Ad Hoc Topics 101-150](http://trec.nist.gov/data/qrels_eng/)
+ [`qrels.adhoc.151-200.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.adhoc.151-200.txt): [qrels for TREC-3 Ad Hoc Topics 151-200](http://trec.nist.gov/data/qrels_eng/)

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.51-100.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25.topics.adhoc.51-100.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.101-150.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25.topics.adhoc.101-150.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.151-200.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25.topics.adhoc.151-200.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.51-100.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25+rm3.topics.adhoc.51-100.txt \
  -bm25 -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.101-150.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25+rm3.topics.adhoc.101-150.txt \
  -bm25 -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.151-200.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25+rm3.topics.adhoc.151-200.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.51-100.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25+ax.topics.adhoc.51-100.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.101-150.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25+ax.topics.adhoc.101-150.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.151-200.txt \
  -topicReader Trec \
  -output runs/run.disk12.bm25+ax.topics.adhoc.151-200.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.51-100.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql.topics.adhoc.51-100.txt \
  -qld &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.101-150.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql.topics.adhoc.101-150.txt \
  -qld &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.151-200.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql.topics.adhoc.151-200.txt \
  -qld &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.51-100.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql+rm3.topics.adhoc.51-100.txt \
  -qld -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.101-150.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql+rm3.topics.adhoc.101-150.txt \
  -qld -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.151-200.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql+rm3.topics.adhoc.151-200.txt \
  -qld -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.51-100.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql+ax.topics.adhoc.51-100.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.101-150.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql+ax.topics.adhoc.101-150.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics tools/topics-and-qrels/topics.adhoc.151-200.txt \
  -topicReader Trec \
  -output runs/run.disk12.ql+ax.topics.adhoc.151-200.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.bm25.topics.adhoc.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.bm25.topics.adhoc.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.bm25.topics.adhoc.151-200.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.bm25+rm3.topics.adhoc.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.bm25+rm3.topics.adhoc.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.bm25+rm3.topics.adhoc.151-200.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.bm25+ax.topics.adhoc.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.bm25+ax.topics.adhoc.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.bm25+ax.topics.adhoc.151-200.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.ql.topics.adhoc.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.ql.topics.adhoc.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.ql.topics.adhoc.151-200.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.ql+rm3.topics.adhoc.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.ql+rm3.topics.adhoc.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.ql+rm3.topics.adhoc.151-200.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.ql+ax.topics.adhoc.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.ql+ax.topics.adhoc.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.ql+ax.topics.adhoc.151-200.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [TREC-1 Ad Hoc Topics 51-100](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.51-100.txt)| 0.2277    | 0.2614    | 0.2648    | 0.2188    | 0.2464    | 0.2502    |
| [TREC-2 Ad Hoc Topics 101-150](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.101-150.txt)| 0.2003    | 0.2579    | 0.2698    | 0.2010    | 0.2424    | 0.2596    |
| [TREC-3 Ad Hoc Topics 151-200](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.151-200.txt)| 0.2634    | 0.3345    | 0.3407    | 0.2580    | 0.3029    | 0.3129    |
| **P30**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [TREC-1 Ad Hoc Topics 51-100](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.51-100.txt)| 0.4540    | 0.4927    | 0.5127    | 0.4553    | 0.4673    | 0.4947    |
| [TREC-2 Ad Hoc Topics 101-150](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.101-150.txt)| 0.4253    | 0.4580    | 0.4720    | 0.4193    | 0.4427    | 0.4760    |
| [TREC-3 Ad Hoc Topics 151-200](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.151-200.txt)| 0.4860    | 0.5293    | 0.5273    | 0.4753    | 0.5000    | 0.5187    |
