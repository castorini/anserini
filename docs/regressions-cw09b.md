# Anserini: Regressions for [ClueWeb09 (Category B)](http://lemurproject.org/clueweb09.php/)

This page describes regressions for the Web Tracks from TREC 2009 to 2012 using the [ClueWeb09 (Category B) collection](http://lemurproject.org/clueweb09.php/).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/cw09b.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/cw09b.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection ClueWeb09Collection \
  -input /path/to/cw09b \
  -index indexes/lucene-index.cw09b/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 44 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.cw09b &
```

The directory `/path/to/ClueWeb09b` should be the root directory of the [ClueWeb09 (Category B) collection](http://lemurproject.org/clueweb09.php/), i.e., `ls /path/to/ClueWeb09b` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.web.1-50.txt`](../src/main/resources/topics-and-qrels/topics.web.1-50.txt): [topics for the TREC 2009 Web Track (Topics 1-50)](http://trec.nist.gov/data/web/09/wt09.topics.full.xml)
+ [`topics.web.51-100.txt`](../src/main/resources/topics-and-qrels/topics.web.51-100.txt): [topics for the TREC 2010 Web Track (Topics 51-100)](http://trec.nist.gov/data/web/10/wt2010-topics.xml)
+ [`topics.web.101-150.txt`](../src/main/resources/topics-and-qrels/topics.web.101-150.txt): [topics for the TREC 2011 Web Track (Topics 101-150)](http://trec.nist.gov/data/web/11/full-topics.xml)
+ [`topics.web.151-200.txt`](../src/main/resources/topics-and-qrels/topics.web.151-200.txt): [topics for the TREC 2012 Web Track (Topics 151-200)](http://trec.nist.gov/data/web/12/full-topics.xml)
+ [`prels.web.1-50.txt`](../src/main/resources/topics-and-qrels/prels.web.1-50.txt): [prels for the TREC 2009 Web Track (Topics 1-50, category B runs)](http://trec.nist.gov/data/web/09/prels.catB.1-50.gz)
+ [`qrels.web.51-100.txt`](../src/main/resources/topics-and-qrels/qrels.web.51-100.txt): [qrels for the TREC 2010 Web Track (Topics 51-100)](http://trec.nist.gov/data/web/10/10.adhoc-qrels.final)
+ [`qrels.web.101-150.txt`](../src/main/resources/topics-and-qrels/qrels.web.101-150.txt): [qrels for the TREC 2011 Web Track (Topics 101-150)](http://trec.nist.gov/data/web/11/qrels.adhoc)
+ [`qrels.web.151-200.txt`](../src/main/resources/topics-and-qrels/qrels.web.151-200.txt): [qrels for the TREC 2012 Web Track (Topics 151-200)](http://trec.nist.gov/data/web/12/qrels.adhoc)

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25.topics.web.51-100.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25.topics.web.101-150.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25.topics.web.151-200.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.51-100.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.101-150.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.151-200.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.51-100.txt \
  -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.101-150.txt \
  -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -topicreader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.151-200.txt \
  -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -topicreader Webxml \
  -output runs/run.cw09b.ql.topics.web.51-100.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -topicreader Webxml \
  -output runs/run.cw09b.ql.topics.web.101-150.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -topicreader Webxml \
  -output runs/run.cw09b.ql.topics.web.151-200.txt \
  -qld &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -topicreader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.51-100.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -topicreader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.101-150.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -topicreader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.151-200.txt \
  -qld -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -topicreader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.51-100.txt \
  -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -topicreader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.101-150.txt \
  -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -topicreader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.151-200.txt \
  -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25.topics.web.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25.topics.web.51-100.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25.topics.web.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25.topics.web.101-150.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25.topics.web.151-200.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25.topics.web.151-200.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+rm3.topics.web.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+rm3.topics.web.51-100.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+rm3.topics.web.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+rm3.topics.web.101-150.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+rm3.topics.web.151-200.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+rm3.topics.web.151-200.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+ax.topics.web.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+ax.topics.web.51-100.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+ax.topics.web.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+ax.topics.web.101-150.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+ax.topics.web.151-200.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+ax.topics.web.151-200.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql.topics.web.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql.topics.web.51-100.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql.topics.web.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql.topics.web.101-150.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql.topics.web.151-200.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql.topics.web.151-200.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+rm3.topics.web.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+rm3.topics.web.51-100.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+rm3.topics.web.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+rm3.topics.web.101-150.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+rm3.topics.web.151-200.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+rm3.topics.web.151-200.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+ax.topics.web.51-100.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+ax.topics.web.51-100.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+ax.topics.web.101-150.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+ax.topics.web.101-150.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+ax.topics.web.151-200.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+ax.topics.web.151-200.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.1126    | 0.0931    | 0.0961    | 0.1060    | 0.1019    | 0.1088    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.1094    | 0.1085    | 0.0986    | 0.0959    | 0.0839    | 0.0860    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.1106    | 0.1108    | 0.1356    | 0.1070    | 0.1058    | 0.1224    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.2681    | 0.2382    | 0.2535    | 0.2438    | 0.2312    | 0.2625    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.2513    | 0.2487    | 0.2367    | 0.2147    | 0.2053    | 0.2120    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.2167    | 0.1927    | 0.2547    | 0.2080    | 0.1980    | 0.2220    |


nDCG@20                                 | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.1351    | 0.1368    | 0.1767    | 0.1143    | 0.1182    | 0.1495    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.1894    | 0.1915    | 0.1854    | 0.1631    | 0.1449    | 0.1537    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.1015    | 0.0918    | 0.1388    | 0.0875    | 0.0896    | 0.1091    |


ERR@20                                  | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2010 Web Track (Topics 51-100)](../src/main/resources/topics-and-qrels/topics.web.51-100.txt)| 0.0733    | 0.0747    | 0.1019    | 0.0599    | 0.0592    | 0.0751    |
[TREC 2011 Web Track (Topics 101-150)](../src/main/resources/topics-and-qrels/topics.web.101-150.txt)| 0.0959    | 0.0959    | 0.0950    | 0.0850    | 0.0787    | 0.0861    |
[TREC 2012 Web Track (Topics 151-200)](../src/main/resources/topics-and-qrels/topics.web.151-200.txt)| 0.1304    | 0.1494    | 0.2399    | 0.1306    | 0.1333    | 0.1564    |
