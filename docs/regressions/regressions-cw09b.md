# Anserini Regressions: ClueWeb09 (Category B)

**Models**: various bag-of-words approaches

This page describes regressions for the Web Tracks from TREC 2009 to 2012 using the [ClueWeb09 (Category B) collection](http://lemurproject.org/clueweb09.php/).
The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/cw09b.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/cw09b.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression cw09b
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 44 \
  -collection ClueWeb09Collection \
  -input /path/to/cw09b \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.cw09b/ \
  -storeRaw \
  >& logs/log.cw09b &
```

The directory `/path/to/ClueWeb09b` should be the root directory of the [ClueWeb09 (Category B) collection](http://lemurproject.org/clueweb09.php/), i.e., `ls /path/to/ClueWeb09b` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from NIST:

+ [`topics.web.1-50.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.1-50.txt): [topics for the TREC 2009 Web Track (Topics 1-50)](http://trec.nist.gov/data/web/09/wt09.topics.full.xml)
+ [`topics.web.51-100.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt): [topics for the TREC 2010 Web Track (Topics 51-100)](http://trec.nist.gov/data/web/10/wt2010-topics.xml)
+ [`topics.web.101-150.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt): [topics for the TREC 2011 Web Track (Topics 101-150)](http://trec.nist.gov/data/web/11/full-topics.xml)
+ [`topics.web.151-200.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt): [topics for the TREC 2012 Web Track (Topics 151-200)](http://trec.nist.gov/data/web/12/full-topics.xml)
+ [`prels.web.1-50.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/prels.web.1-50.txt): [prels for the TREC 2009 Web Track (Topics 1-50, category B runs)](http://trec.nist.gov/data/web/09/prels.catB.1-50.gz)
+ [`qrels.web.51-100.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.web.51-100.txt): [qrels for the TREC 2010 Web Track (Topics 51-100)](http://trec.nist.gov/data/web/10/10.adhoc-qrels.final)
+ [`qrels.web.101-150.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.web.101-150.txt): [qrels for the TREC 2011 Web Track (Topics 101-150)](http://trec.nist.gov/data/web/11/qrels.adhoc)
+ [`qrels.web.151-200.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.web.151-200.txt): [qrels for the TREC 2012 Web Track (Topics 151-200)](http://trec.nist.gov/data/web/12/qrels.adhoc)

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.51-100.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25.topics.web.51-100.txt \
  -parallelism 16 -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.101-150.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25.topics.web.101-150.txt \
  -parallelism 16 -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.151-200.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25.topics.web.151-200.txt \
  -parallelism 16 -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.51-100.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.51-100.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.101-150.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.101-150.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.151-200.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.151-200.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.51-100.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.51-100.txt \
  -parallelism 16 -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.101-150.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.101-150.txt \
  -parallelism 16 -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.151-200.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.151-200.txt \
  -parallelism 16 -bm25 -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.51-100.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql.topics.web.51-100.txt \
  -parallelism 16 -qld &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.101-150.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql.topics.web.101-150.txt \
  -parallelism 16 -qld &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.151-200.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql.topics.web.151-200.txt \
  -parallelism 16 -qld &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.51-100.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.51-100.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.101-150.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.101-150.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.151-200.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.151-200.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.51-100.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.51-100.txt \
  -parallelism 16 -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.101-150.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.101-150.txt \
  -parallelism 16 -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics tools/topics-and-qrels/topics.web.151-200.txt \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.151-200.txt \
  -parallelism 16 -qld -axiom -axiom.deterministic -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25.topics.web.51-100.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25.topics.web.101-150.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25.topics.web.151-200.txt

tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+rm3.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+rm3.topics.web.51-100.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+rm3.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+rm3.topics.web.101-150.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+rm3.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+rm3.topics.web.151-200.txt

tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+ax.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.bm25+ax.topics.web.51-100.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+ax.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.bm25+ax.topics.web.101-150.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+ax.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.bm25+ax.topics.web.151-200.txt

tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql.topics.web.51-100.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql.topics.web.101-150.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql.topics.web.151-200.txt

tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+rm3.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+rm3.topics.web.51-100.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+rm3.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+rm3.topics.web.101-150.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+rm3.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+rm3.topics.web.151-200.txt

tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+ax.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.51-100.txt runs/run.cw09b.ql+ax.topics.web.51-100.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+ax.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.101-150.txt runs/run.cw09b.ql+ax.topics.web.101-150.txt
tools/eval/gdeval.pl tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+ax.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.web.151-200.txt runs/run.cw09b.ql+ax.topics.web.151-200.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)| 0.1126    | 0.0929    | 0.0961    | 0.1060    | 0.1018    | 0.1088    |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt)| 0.1094    | 0.1080    | 0.0986    | 0.0959    | 0.0835    | 0.0860    |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt)| 0.1105    | 0.1108    | 0.1355    | 0.1070    | 0.1057    | 0.1224    |
| **P30**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)| 0.2688    | 0.2375    | 0.2528    | 0.2438    | 0.2306    | 0.2625    |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt)| 0.2513    | 0.2433    | 0.2367    | 0.2147    | 0.2047    | 0.2120    |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt)| 0.2167    | 0.1920    | 0.2547    | 0.2080    | 0.1973    | 0.2220    |
| **nDCG@20**                                                                                                  | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)| 0.1351    | 0.1371    | 0.1767    | 0.1143    | 0.1181    | 0.1495    |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt)| 0.1894    | 0.1922    | 0.1854    | 0.1631    | 0.1444    | 0.1537    |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt)| 0.1014    | 0.0918    | 0.1388    | 0.0876    | 0.0896    | 0.1091    |
| **ERR@20**                                                                                                   | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)| 0.0733    | 0.0756    | 0.1019    | 0.0599    | 0.0591    | 0.0751    |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt)| 0.0959    | 0.0970    | 0.0950    | 0.0850    | 0.0781    | 0.0861    |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt)| 0.1304    | 0.1491    | 0.2399    | 0.1306    | 0.1333    | 0.1564    |
