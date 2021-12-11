# Anserini: Regressions for [TREC Disks 4 &amp; 5](https://trec.nist.gov/data/cd45/index.html)

This page describes regressions for ad hoc topics from TREC 7-8, which use [TREC Disks 4 &amp; 5](https://trec.nist.gov/data/cd45/index.html).
The exact configurations for these regressions are stored in [this YAML file](${yaml).
Note that this page is automatically generated from [this template](${template}) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /path/to/disk45 \
 -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.disk45 &
```

The directory `/path/to/disk45/` should be the root directory of [TREC Disks 4 &amp; 5](https://trec.nist.gov/data/cd45/index.html); inside each there should be subdirectories like `ft`, `fr94`.
Note that Anserini ignores the `cr` folder when indexing, which is the standard configuration.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.adhoc.351-400.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt): [TREC-7 Ad Hoc Topics 351-400](http://trec.nist.gov/data/topics_eng/)
+ [`topics.adhoc.401-450.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt): [TREC-8 Ad Hoc Topics 401-450](http://trec.nist.gov/data/topics_eng/)
+ [`qrels.adhoc.351-400.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt): [qrels for TREC-7 Ad Hoc Topics 351-400](http://trec.nist.gov/data/qrels_eng/)
+ [`qrels.adhoc.401-450.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt): [qrels for TREC-8 Ad Hoc Topics 401-450](http://trec.nist.gov/data/qrels_eng/)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
 -output runs/run.disk45.bm25.topics.adhoc.351-400.txt \
 -bm25 &
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
 -output runs/run.disk45.bm25.topics.adhoc.401-450.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
 -output runs/run.disk45.bm25+rm3.topics.adhoc.351-400.txt \
 -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
 -output runs/run.disk45.bm25+rm3.topics.adhoc.401-450.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
 -output runs/run.disk45.bm25+ax.topics.adhoc.351-400.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
 -output runs/run.disk45.bm25+ax.topics.adhoc.401-450.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
 -output runs/run.disk45.ql.topics.adhoc.351-400.txt \
 -qld &
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
 -output runs/run.disk45.ql.topics.adhoc.401-450.txt \
 -qld &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
 -output runs/run.disk45.ql+rm3.topics.adhoc.351-400.txt \
 -qld -rm3 &
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
 -output runs/run.disk45.ql+rm3.topics.adhoc.401-450.txt \
 -qld -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
 -output runs/run.disk45.ql+ax.topics.adhoc.351-400.txt \
 -qld -axiom -axiom.deterministic -rerankCutoff 20 &
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.disk45.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
 -output runs/run.disk45.ql+ax.topics.adhoc.401-450.txt \
 -qld -axiom -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25.topics.adhoc.401-450.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25+rm3.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25+rm3.topics.adhoc.401-450.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25+ax.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25+ax.topics.adhoc.401-450.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.ql.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.ql.topics.adhoc.401-450.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.ql+rm3.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.ql+rm3.topics.adhoc.401-450.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.ql+ax.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.ql+ax.topics.adhoc.401-450.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC-7 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt)| 0.1862    | 0.2354    | 0.2431    | 0.1843    | 0.2168    | 0.2298    |
[TREC-8 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt)| 0.2515    | 0.2750    | 0.2812    | 0.2460    | 0.2702    | 0.2647    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC-7 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt)| 0.3093    | 0.3447    | 0.3287    | 0.3073    | 0.3307    | 0.3193    |
[TREC-8 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt)| 0.3560    | 0.3760    | 0.3753    | 0.3480    | 0.3680    | 0.3500    |
