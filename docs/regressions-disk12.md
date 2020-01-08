# Anserini: Regressions for [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A)

This page describes regressions for ad hoc topics from the early TRECs, which use [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/disk12.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/disk12.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/disk12 \
 -index lucene-index.disk12.pos+docvectors+rawdocs -generator JsoupGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs >& log.disk12.pos+docvectors+rawdocs &
```

The directory `/path/to/disk12/` should be the root directory of [TIPSTER Disks 1 &amp; 2](https://catalog.ldc.upenn.edu/LDC93T3A), i.e., `ls /path/to/disk12/` should bring up subdirectories like `doe`, `wsj`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.adhoc.51-100.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt): [TREC-1 Ad Hoc Topics 51-100](http://trec.nist.gov/data/topics_eng/topics.51-100.gz)
+ [`topics.adhoc.101-150.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt): [TREC-2 Ad Hoc Topics 101-150](http://trec.nist.gov/data/topics_eng/topics.101-150.gz)
+ [`topics.adhoc.151-200.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt): [TREC-3 Ad Hoc Topics 151-200](http://trec.nist.gov/data/topics_eng/topics.151-200.gz)
+ [`qrels.adhoc.51-100.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt): [qrels for TREC-1 Ad Hoc Topics 51-100](http://trec.nist.gov/data/qrels_eng/qrels.51-100.disk1.disk2.parts1-5.tar.gz)
+ [`qrels.adhoc.101-150.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt): [qrels for TREC-2 Ad Hoc Topics 101-150](http://trec.nist.gov/data/qrels_eng/qrels.101-150.disk1.disk2.parts1-5.tar.gz)
+ [`qrels.adhoc.151-200.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt): [qrels for TREC-3 Ad Hoc Topics 151-200](http://trec.nist.gov/data/qrels_eng/qrels.151-200.201-250.disks1-3.all.tar.gz)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt \
 -bm25 -output run.disk12.bm25.topics.adhoc.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt \
 -bm25 -output run.disk12.bm25.topics.adhoc.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt \
 -bm25 -output run.disk12.bm25.topics.adhoc.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt \
 -bm25 -rm3 -output run.disk12.bm25+rm3.topics.adhoc.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt \
 -bm25 -rm3 -output run.disk12.bm25+rm3.topics.adhoc.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt \
 -bm25 -rm3 -output run.disk12.bm25+rm3.topics.adhoc.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.disk12.bm25+ax.topics.adhoc.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.disk12.bm25+ax.topics.adhoc.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.disk12.bm25+ax.topics.adhoc.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt \
 -ql -output run.disk12.ql.topics.adhoc.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt \
 -ql -output run.disk12.ql.topics.adhoc.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt \
 -ql -output run.disk12.ql.topics.adhoc.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt \
 -ql -rm3 -output run.disk12.ql+rm3.topics.adhoc.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt \
 -ql -rm3 -output run.disk12.ql+rm3.topics.adhoc.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt \
 -ql -rm3 -output run.disk12.ql+rm3.topics.adhoc.151-200.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -output run.disk12.ql+ax.topics.adhoc.51-100.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -output run.disk12.ql+ax.topics.adhoc.101-150.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.disk12.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -output run.disk12.ql+ax.topics.adhoc.151-200.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt run.disk12.bm25.topics.adhoc.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt run.disk12.bm25.topics.adhoc.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt run.disk12.bm25.topics.adhoc.151-200.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt run.disk12.bm25+rm3.topics.adhoc.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt run.disk12.bm25+rm3.topics.adhoc.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt run.disk12.bm25+rm3.topics.adhoc.151-200.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt run.disk12.bm25+ax.topics.adhoc.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt run.disk12.bm25+ax.topics.adhoc.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt run.disk12.bm25+ax.topics.adhoc.151-200.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt run.disk12.ql.topics.adhoc.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt run.disk12.ql.topics.adhoc.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt run.disk12.ql.topics.adhoc.151-200.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt run.disk12.ql+rm3.topics.adhoc.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt run.disk12.ql+rm3.topics.adhoc.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt run.disk12.ql+rm3.topics.adhoc.151-200.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.51-100.txt run.disk12.ql+ax.topics.adhoc.51-100.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.101-150.txt run.disk12.ql+ax.topics.adhoc.101-150.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.151-200.txt run.disk12.ql+ax.topics.adhoc.151-200.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC-1 Ad Hoc Topics 51-100](../src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt)| 0.2273    | 0.2634    | 0.2640    | 0.2189    | 0.2435    | 0.2501    |
[TREC-2 Ad Hoc Topics 101-150](../src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt)| 0.2010    | 0.2587    | 0.2722    | 0.2015    | 0.2442    | 0.2593    |
[TREC-3 Ad Hoc Topics 151-200](../src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt)| 0.2580    | 0.3390    | 0.3318    | 0.2518    | 0.3042    | 0.3103    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC-1 Ad Hoc Topics 51-100](../src/main/resources/topics-and-qrels/topics.adhoc.51-100.txt)| 0.4533    | 0.4800    | 0.5067    | 0.4520    | 0.4627    | 0.4953    |
[TREC-2 Ad Hoc Topics 101-150](../src/main/resources/topics-and-qrels/topics.adhoc.101-150.txt)| 0.4280    | 0.4593    | 0.4753    | 0.4207    | 0.4420    | 0.4740    |
[TREC-3 Ad Hoc Topics 151-200](../src/main/resources/topics-and-qrels/topics.adhoc.151-200.txt)| 0.4740    | 0.5273    | 0.5100    | 0.4580    | 0.4913    | 0.5167    |
