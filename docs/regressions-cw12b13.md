# Anserini: Regressions for [ClueWeb12-B13](http://lemurproject.org/clueweb12/ClueWeb12-CreateB13.php)

This page describes regressions for the Web Tracks from TREC 2013 and 2014 using the [ClueWeb12-B13 collection](http://lemurproject.org/clueweb12/ClueWeb12-CreateB13.php).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/cw12b13.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/cw12b13.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection ClueWeb12Collection \
  -input /path/to/cw12b13 \
  -index indexes/lucene-index.cw12b13/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 44 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.cw12b13 &
```

The directory `/path/to/cw12-b13/` should be the root directory of the [ClueWeb12-B13 collection](http://lemurproject.org/clueweb12/ClueWeb12-CreateB13.php), i.e., `/path/to/cw12-b13/` should bring up a bunch of subdirectories, `ClueWeb12_00` to `ClueWeb12_18`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.web.201-250.txt`](../src/main/resources/topics-and-qrels/topics.web.201-250.txt): [topics for the TREC 2013 Web Track (Topics 201-250)](http://trec.nist.gov/data/web/2013/trec2013-topics.xml)
+ [`topics.web.251-300.txt`](../src/main/resources/topics-and-qrels/topics.web.251-300.txt): [topics for the TREC 2014 Web Track (Topics 251-300)](http://trec.nist.gov/data/web/2014/trec2014-topics.xml)
+ [`qrels.web.201-250.txt`](../src/main/resources/topics-and-qrels/qrels.web.201-250.txt): [one aspect per topic qrels for the TREC 2013 Web Track (Topics 201-250)](http://trec.nist.gov/data/web/2013/qrels.adhoc.txt)
+ [`qrels.web.251-300.txt`](../src/main/resources/topics-and-qrels/qrels.web.251-300.txt): [one aspect per topic qrels for the TREC 2014 Web Track (Topics 251-300)](http://trec.nist.gov/data/web/2014/qrels.adhoc.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -topicreader Webxml \
  -output runs/run.cw12b13.bm25.topics.web.201-250.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -topicreader Webxml \
  -output runs/run.cw12b13.bm25.topics.web.251-300.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -topicreader Webxml \
  -output runs/run.cw12b13.bm25+rm3.topics.web.201-250.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -topicreader Webxml \
  -output runs/run.cw12b13.bm25+rm3.topics.web.251-300.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -topicreader Webxml \
  -output runs/run.cw12b13.bm25+ax.topics.web.201-250.txt \
  -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -topicreader Webxml \
  -output runs/run.cw12b13.bm25+ax.topics.web.251-300.txt \
  -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -topicreader Webxml \
  -output runs/run.cw12b13.ql.topics.web.201-250.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -topicreader Webxml \
  -output runs/run.cw12b13.ql.topics.web.251-300.txt \
  -qld &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -topicreader Webxml \
  -output runs/run.cw12b13.ql+rm3.topics.web.201-250.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -topicreader Webxml \
  -output runs/run.cw12b13.ql+rm3.topics.web.251-300.txt \
  -qld -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -topicreader Webxml \
  -output runs/run.cw12b13.ql+ax.topics.web.201-250.txt \
  -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12b13/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -topicreader Webxml \
  -output runs/run.cw12b13.ql+ax.topics.web.251-300.txt \
  -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.bm25.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.bm25.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.bm25.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.bm25.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.bm25+rm3.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.bm25+rm3.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.bm25+rm3.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.bm25+rm3.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.bm25+ax.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.bm25+ax.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.bm25+ax.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.bm25+ax.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.ql.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.ql.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.ql.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.ql.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.ql+rm3.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.ql+rm3.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.ql+rm3.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.ql+rm3.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.ql+ax.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12b13.ql+ax.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.ql+ax.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12b13.ql+ax.topics.web.251-300.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.0468    | 0.0408    | 0.0432    | 0.0397    | 0.0322    | 0.0356    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.0224    | 0.0210    | 0.0181    | 0.0235    | 0.0203    | 0.0179    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.2107    | 0.1673    | 0.1780    | 0.1773    | 0.1513    | 0.1567    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.1273    | 0.1207    | 0.1107    | 0.1373    | 0.1173    | 0.1147    |


nDCG@20                                 | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.1289    | 0.1114    | 0.1311    | 0.1104    | 0.0921    | 0.1113    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.1183    | 0.1075    | 0.0974    | 0.1176    | 0.1004    | 0.0984    |


ERR@20                                  | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)| 0.0838    | 0.0752    | 0.0949    | 0.0767    | 0.0552    | 0.0720    |
[TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)| 0.1198    | 0.1055    | 0.0925    | 0.1091    | 0.0928    | 0.0879    |

## Reproduction Log[*](reproducibility.md)

* Results reproduced by [@matthew-z](https://github.com/matthew-z) on 2019-04-14 (commit [`abaa4c8`](https://github.com/castorini/Anserini/commit/abaa4c8e7cb50e8e4a3677377716f609b7859538))[<sup>*</sup>](https://github.com/castorini/Anserini/pull/590)[<sup>!</sup>](https://github.com/castorini/Anserini/issues/592)
