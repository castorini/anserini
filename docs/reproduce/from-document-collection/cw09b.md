# Anserini Regressions: ClueWeb09 (Category B)

**Models**: various bag-of-words approaches

This page describes regressions for the Web Tracks from TREC 2009 to 2012 using the [ClueWeb09 (Category B) collection](http://lemurproject.org/clueweb09.php/).
The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/cw09b.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/cw09b.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config cw09b
```

## Indexing

Typical indexing command:

```bash
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

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).
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

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.51-100 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25.topics.web.51-100.txt \
  -parallelism 16 -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.101-150 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25.topics.web.101-150.txt \
  -parallelism 16 -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.151-200 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25.topics.web.151-200.txt \
  -parallelism 16 -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.51-100 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.51-100.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.101-150 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.101-150.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.151-200 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+rm3.topics.web.151-200.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.51-100 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.51-100.txt \
  -parallelism 16 -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.101-150 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.101-150.txt \
  -parallelism 16 -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.151-200 \
  -topicReader Webxml \
  -output runs/run.cw09b.bm25+ax.topics.web.151-200.txt \
  -parallelism 16 -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.51-100 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql.topics.web.51-100.txt \
  -parallelism 16 -qld &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.101-150 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql.topics.web.101-150.txt \
  -parallelism 16 -qld &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.151-200 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql.topics.web.151-200.txt \
  -parallelism 16 -qld &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.51-100 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.51-100.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.101-150 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.101-150.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.151-200 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+rm3.topics.web.151-200.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.51-100 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.51-100.txt \
  -parallelism 16 -qld -axiom -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.101-150 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.101-150.txt \
  -parallelism 16 -qld -axiom -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.cw09b/ \
  -topics web.151-200 \
  -topicReader Webxml \
  -output runs/run.cw09b.ql+ax.topics.web.151-200.txt \
  -parallelism 16 -qld -axiom -axiom.beta 0.1 -rerankCutoff 20 -collection ClueWeb09Collection &
```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```bash
tools/eval/gdeval.pl web.51-100 runs/run.cw09b.bm25.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 web.51-100 runs/run.cw09b.bm25.topics.web.51-100.txt
tools/eval/gdeval.pl web.101-150 runs/run.cw09b.bm25.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 web.101-150 runs/run.cw09b.bm25.topics.web.101-150.txt
tools/eval/gdeval.pl web.151-200 runs/run.cw09b.bm25.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 web.151-200 runs/run.cw09b.bm25.topics.web.151-200.txt

tools/eval/gdeval.pl web.51-100 runs/run.cw09b.bm25+rm3.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 web.51-100 runs/run.cw09b.bm25+rm3.topics.web.51-100.txt
tools/eval/gdeval.pl web.101-150 runs/run.cw09b.bm25+rm3.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 web.101-150 runs/run.cw09b.bm25+rm3.topics.web.101-150.txt
tools/eval/gdeval.pl web.151-200 runs/run.cw09b.bm25+rm3.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 web.151-200 runs/run.cw09b.bm25+rm3.topics.web.151-200.txt

tools/eval/gdeval.pl web.51-100 runs/run.cw09b.bm25+ax.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 web.51-100 runs/run.cw09b.bm25+ax.topics.web.51-100.txt
tools/eval/gdeval.pl web.101-150 runs/run.cw09b.bm25+ax.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 web.101-150 runs/run.cw09b.bm25+ax.topics.web.101-150.txt
tools/eval/gdeval.pl web.151-200 runs/run.cw09b.bm25+ax.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 web.151-200 runs/run.cw09b.bm25+ax.topics.web.151-200.txt

tools/eval/gdeval.pl web.51-100 runs/run.cw09b.ql.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 web.51-100 runs/run.cw09b.ql.topics.web.51-100.txt
tools/eval/gdeval.pl web.101-150 runs/run.cw09b.ql.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 web.101-150 runs/run.cw09b.ql.topics.web.101-150.txt
tools/eval/gdeval.pl web.151-200 runs/run.cw09b.ql.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 web.151-200 runs/run.cw09b.ql.topics.web.151-200.txt

tools/eval/gdeval.pl web.51-100 runs/run.cw09b.ql+rm3.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 web.51-100 runs/run.cw09b.ql+rm3.topics.web.51-100.txt
tools/eval/gdeval.pl web.101-150 runs/run.cw09b.ql+rm3.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 web.101-150 runs/run.cw09b.ql+rm3.topics.web.101-150.txt
tools/eval/gdeval.pl web.151-200 runs/run.cw09b.ql+rm3.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 web.151-200 runs/run.cw09b.ql+rm3.topics.web.151-200.txt

tools/eval/gdeval.pl web.51-100 runs/run.cw09b.ql+ax.topics.web.51-100.txt
bin/trec_eval -m map -m P.30 web.51-100 runs/run.cw09b.ql+ax.topics.web.51-100.txt
tools/eval/gdeval.pl web.101-150 runs/run.cw09b.ql+ax.topics.web.101-150.txt
bin/trec_eval -m map -m P.30 web.101-150 runs/run.cw09b.ql+ax.topics.web.101-150.txt
tools/eval/gdeval.pl web.151-200 runs/run.cw09b.ql+ax.topics.web.151-200.txt
bin/trec_eval -m map -m P.30 web.151-200 runs/run.cw09b.ql+ax.topics.web.151-200.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                                                 | **BM25**   | **+RM3**   | **+Ax**    | **QL**     | **+RM3**   | **+Ax**    |
|:----------------------------------------------------------------------------------------------------------------------------------------|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)   | 0.1126     | 0.0932     | 0.0953     | 0.1056     | 0.1021     | 0.1084     |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt) | 0.1096     | 0.1081     | 0.0942     | 0.0960     | 0.0835     | 0.0856     |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt) | 0.1103     | 0.1110     | 0.1346     | 0.1069     | 0.1055     | 0.1246     |
| **P30**                                                                                                                                 | **BM25**   | **+RM3**   | **+Ax**    | **QL**     | **+RM3**   | **+Ax**    |
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)   | 0.2660     | 0.2375     | 0.2465     | 0.2424     | 0.2299     | 0.2611     |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt) | 0.2513     | 0.2447     | 0.2220     | 0.2153     | 0.2060     | 0.2127     |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt) | 0.2153     | 0.1967     | 0.2527     | 0.2067     | 0.2007     | 0.2287     |
| **nDCG@20**                                                                                                                             | **BM25**   | **+RM3**   | **+Ax**    | **QL**     | **+RM3**   | **+Ax**    |
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)   | 0.1365     | 0.1403     | 0.1735     | 0.1137     | 0.1186     | 0.1453     |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt) | 0.1895     | 0.1935     | 0.1657     | 0.1631     | 0.1432     | 0.1521     |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt) | 0.1016     | 0.0923     | 0.1403     | 0.0880     | 0.0896     | 0.1075     |
| **ERR@20**                                                                                                                              | **BM25**   | **+RM3**   | **+Ax**    | **QL**     | **+RM3**   | **+Ax**    |
| [TREC 2010 Web Track (Topics 51-100)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.51-100.txt)   | 0.0733     | 0.0773     | 0.1015     | 0.0597     | 0.0591     | 0.0707     |
| [TREC 2011 Web Track (Topics 101-150)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.101-150.txt) | 0.0960     | 0.0976     | 0.1041     | 0.0850     | 0.0779     | 0.0809     |
| [TREC 2012 Web Track (Topics 151-200)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.web.151-200.txt) | 0.1324     | 0.1508     | 0.2393     | 0.1337     | 0.1330     | 0.1537     |
