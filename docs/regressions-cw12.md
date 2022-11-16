# Anserini Regressions: ClueWeb12

**Models**: various bag-of-words approaches

This page describes regressions for the Web Tracks from TREC 2013 and 2014 using the (full) [ClueWeb12 collection](http://lemurproject.org/clueweb12.php/).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/cw12.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/cw12.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression cw12
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection ClueWeb12Collection \
  -input /path/to/cw12 \
  -index indexes/lucene-index.cw12/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 44 -storeRaw \
  >& logs/log.cw12 &
```

The directory `/path/to/cw12/` should be the root directory of the (full) [ClueWeb12 collection](http://lemurproject.org/clueweb12.php/), i.e., `/path/to/cw12/` should contain `Disk1`, `Disk2`, `Disk3`, `Disk4`.

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
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
  -topicreader Webxml \
  -output runs/run.cw12.bm25.topics.web.201-250.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
  -topicreader Webxml \
  -output runs/run.cw12.bm25.topics.web.251-300.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
  -topicreader Webxml \
  -output runs/run.cw12.bm25+rm3.topics.web.201-250.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
  -topicreader Webxml \
  -output runs/run.cw12.bm25+rm3.topics.web.251-300.txt \
  -parallelism 16 -bm25 -rm3 -collection ClueWeb09Collection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
  -topicreader Webxml \
  -output runs/run.cw12.ql.topics.web.201-250.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
  -topicreader Webxml \
  -output runs/run.cw12.ql.topics.web.251-300.txt \
  -qld &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt \
  -topicreader Webxml \
  -output runs/run.cw12.ql+rm3.topics.web.201-250.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.cw12/ \
  -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt \
  -topicreader Webxml \
  -output runs/run.cw12.ql+rm3.topics.web.251-300.txt \
  -parallelism 16 -qld -rm3 -collection ClueWeb09Collection &
```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.bm25.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.bm25.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.bm25.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.bm25.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.bm25+rm3.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.bm25+rm3.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.bm25+rm3.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.bm25+rm3.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.ql.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.ql.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.ql.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.ql.topics.web.251-300.txt

tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.ql+rm3.topics.web.201-250.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt runs/run.cw12.ql+rm3.topics.web.201-250.txt
tools/eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.ql+rm3.topics.web.251-300.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt runs/run.cw12.ql+rm3.topics.web.251-300.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **QL**    | **+RM3**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|
| [TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)        | 0.1695    | 0.1477    | 0.1494    | 0.1284    |
| [TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)        | 0.2469    | 0.2342    | 0.2467    | 0.2185    |
| **P30**                                                                                                      | **BM25**  | **+RM3**  | **QL**    | **+RM3**  |
| [TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)        | 0.2767    | 0.2400    | 0.2607    | 0.2373    |
| [TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)        | 0.4547    | 0.4140    | 0.4380    | 0.3800    |
| **nDCG@20**                                                                                                  | **BM25**  | **+RM3**  | **QL**    | **+RM3**  |
| [TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)        | 0.2083    | 0.2058    | 0.1993    | 0.1701    |
| [TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)        | 0.2572    | 0.2548    | 0.2220    | 0.2076    |
| **ERR@20**                                                                                                   | **BM25**  | **+RM3**  | **QL**    | **+RM3**  |
| [TREC 2013 Web Track (Topics 201-250)](../src/main/resources/topics-and-qrels/topics.web.201-250.txt)        | 0.1283    | 0.1304    | 0.1232    | 0.0995    |
| [TREC 2014 Web Track (Topics 251-300)](../src/main/resources/topics-and-qrels/topics.web.251-300.txt)        | 0.1616    | 0.1655    | 0.1323    | 0.1242    |
