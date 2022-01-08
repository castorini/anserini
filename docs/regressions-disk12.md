# Anserini: Regressions for [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A)

This page describes regressions for ad hoc topics from TREC 1-3, which use [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/disk12.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/disk12.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection TrecCollection \
  -input /path/to/disk12 \
  -index indexes/lucene-index.disk12/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.disk12 &
```

The directory `/path/to/disk12/` should be the root directory of [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A), i.e., `ls /path/to/disk12/` should bring up subdirectories like `doe`, `wsj`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.adhoc.51-100.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt): [TREC-1 Ad Hoc Topics 51-100](http://trec.nist.gov/data/topics_eng/)
+ [`topics.adhoc.101-150.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt): [TREC-2 Ad Hoc Topics 101-150](http://trec.nist.gov/data/topics_eng/)
+ [`topics.adhoc.151-200.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt): [TREC-3 Ad Hoc Topics 151-200](http://trec.nist.gov/data/topics_eng/)
+ [`qrels.adhoc.51-100.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt): [qrels for TREC-1 Ad Hoc Topics 51-100](http://trec.nist.gov/data/qrels_eng/)
+ [`qrels.adhoc.101-150.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt): [qrels for TREC-2 Ad Hoc Topics 101-150](http://trec.nist.gov/data/qrels_eng/)
+ [`qrels.adhoc.151-200.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt): [qrels for TREC-3 Ad Hoc Topics 151-200](http://trec.nist.gov/data/qrels_eng/)

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt -topicreader Trec \
  -output runs/run.disk12.bm25.topics.adhoc.51-100.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt -topicreader Trec \
  -output runs/run.disk12.bm25.topics.adhoc.101-150.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt -topicreader Trec \
  -output runs/run.disk12.bm25.topics.adhoc.151-200.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt -topicreader Trec \
  -output runs/run.disk12.bm25+rm3.topics.adhoc.51-100.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt -topicreader Trec \
  -output runs/run.disk12.bm25+rm3.topics.adhoc.101-150.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt -topicreader Trec \
  -output runs/run.disk12.bm25+rm3.topics.adhoc.151-200.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt -topicreader Trec \
  -output runs/run.disk12.bm25+ax.topics.adhoc.51-100.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt -topicreader Trec \
  -output runs/run.disk12.bm25+ax.topics.adhoc.101-150.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt -topicreader Trec \
  -output runs/run.disk12.bm25+ax.topics.adhoc.151-200.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt -topicreader Trec \
  -output runs/run.disk12.ql.topics.adhoc.51-100.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt -topicreader Trec \
  -output runs/run.disk12.ql.topics.adhoc.101-150.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt -topicreader Trec \
  -output runs/run.disk12.ql.topics.adhoc.151-200.txt \
  -qld &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt -topicreader Trec \
  -output runs/run.disk12.ql+rm3.topics.adhoc.51-100.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt -topicreader Trec \
  -output runs/run.disk12.ql+rm3.topics.adhoc.101-150.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt -topicreader Trec \
  -output runs/run.disk12.ql+rm3.topics.adhoc.151-200.txt \
  -qld -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt -topicreader Trec \
  -output runs/run.disk12.ql+ax.topics.adhoc.51-100.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt -topicreader Trec \
  -output runs/run.disk12.ql+ax.topics.adhoc.101-150.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk12/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt -topicreader Trec \
  -output runs/run.disk12.ql+ax.topics.adhoc.151-200.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.bm25.topics.adhoc.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.bm25.topics.adhoc.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.bm25.topics.adhoc.151-200.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.bm25+rm3.topics.adhoc.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.bm25+rm3.topics.adhoc.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.bm25+rm3.topics.adhoc.151-200.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.bm25+ax.topics.adhoc.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.bm25+ax.topics.adhoc.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.bm25+ax.topics.adhoc.151-200.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.ql.topics.adhoc.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.ql.topics.adhoc.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.ql.topics.adhoc.151-200.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.ql+rm3.topics.adhoc.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.ql+rm3.topics.adhoc.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.ql+rm3.topics.adhoc.151-200.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt runs/run.disk12.ql+ax.topics.adhoc.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt runs/run.disk12.ql+ax.topics.adhoc.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt runs/run.disk12.ql+ax.topics.adhoc.151-200.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC-1 Ad Hoc Topics 51-100](../src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt)| 0.2277    | 0.2628    | 0.2648    | 0.2188    | 0.2465    | 0.2502    |
[TREC-2 Ad Hoc Topics 101-150](../src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt)| 0.2003    | 0.2578    | 0.2698    | 0.2010    | 0.2429    | 0.2596    |
[TREC-3 Ad Hoc Topics 151-200](../src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt)| 0.2634    | 0.3345    | 0.3407    | 0.2580    | 0.3037    | 0.3129    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC-1 Ad Hoc Topics 51-100](../src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt)| 0.4540    | 0.4860    | 0.5127    | 0.4553    | 0.4680    | 0.4947    |
[TREC-2 Ad Hoc Topics 101-150](../src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt)| 0.4253    | 0.4580    | 0.4720    | 0.4193    | 0.4400    | 0.4760    |
[TREC-3 Ad Hoc Topics 151-200](../src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt)| 0.4860    | 0.5260    | 0.5273    | 0.4753    | 0.4967    | 0.5187    |
