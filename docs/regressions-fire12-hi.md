# Anserini: Regressions for [FIRE 2012 Monolingual Hindi](https://www.isical.ac.in/~fire/2012/adhoc.html)

This page documents regression experiments for [FIRE 2012 ad hoc retrieval (Monolingual Hindi)](https://www.isical.ac.in/~fire/2012/adhoc.html).
The document collection can be found in [FIRE data page](http://fire.irsi.res.in/fire/static/data).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/fire12-hi.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/fire12-hi.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection CleanTrecCollection \
  -input /path/to/fire12-hi \
  -index indexes/lucene-index.fire12-hi/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -storePositions -storeDocvectors -storeRaw -language hi \
  >& logs/log.fire12-hi &
```

The directory `/path/to/fire12-hi/` should be a directory containing the collection, containing `hi_AmarUjala` and `hi_NavbharatTimes` directories.
There should be 331,599 documents in total.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from the [FIRE data page](http://fire.irsi.res.in/fire/static/data):

+ [`topics.fire12hi.176-225.txt`](../src/main/resources/topics-and-qrels/topics.fire12hi.176-225.txt): topics for FIRE 2012 Monolingual Hindi (176 to 225)
+ [`qrels.fire12hi.176-225.txt`](../src/main/resources/topics-and-qrels/qrels.fire12hi.176-225.txt): qrels (version II) for FIRE 2012 Monolingual Hindi (176 to 225)

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.fire12-hi/ \
  -topics src/main/resources/topics-and-qrels/topics.fire12hi.176-225.txt -topicreader Trec \
  -output runs/run.fire12-hi.bm25.topics.fire12hi.176-225.txt \
  -bm25 -language hi &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.20 -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.fire12hi.176-225.txt runs/run.fire12-hi.bm25.topics.fire12hi.176-225.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[FIRE 2012 (Monolingual Hindi)](../src/main/resources/topics-and-qrels/topics.fire12en.176-225.txt)| 0.3867    |


P20                                     | BM25      |
:---------------------------------------|-----------|
[FIRE 2012 (Monolingual Hindi)](../src/main/resources/topics-and-qrels/topics.fire12en.176-225.txt)| 0.4470    |


nDCG@20                                 | BM25      |
:---------------------------------------|-----------|
[FIRE 2012 (Monolingual Hindi)](../src/main/resources/topics-and-qrels/topics.fire12en.176-225.txt)| 0.5310    |
