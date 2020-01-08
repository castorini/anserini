# Anserini: Regressions for [FIRE 2012 Monolingual Hindi](https://www.isical.ac.in/~fire/2012/adhoc.html)

This page documents regression experiments for [FIRE 2012 ad hoc retrieval (Monolingual Hindi)](https://www.isical.ac.in/~fire/2012/adhoc.html).
The document collection can be found in [FIRE data page](http://fire.irsi.res.in/fire/static/data).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/fire12-hi.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/fire12-hi.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/fire12-hi \
 -index lucene-index.fire12-hi.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -language hi >& log.fire12-hi.pos+docvectors+rawdocs &
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
nohup target/appassembler/bin/SearchCollection -index lucene-index.fire12-hi.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.fire12hi.176-225.txt \
 -language hi -bm25 -output run.fire12-hi.bm25.topics.fire12hi.176-225.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.20 -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.fire12hi.176-225.txt run.fire12-hi.bm25.topics.fire12hi.176-225.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[FIRE 2012 (Monolingual Hindi)](../src/main/resources/topics-and-qrels/topics.fire12en.176-225.txt)| 0.3867    |


P20                                     | BM25      |
:---------------------------------------|-----------|
[FIRE 2012 (Monolingual Hindi)](../src/main/resources/topics-and-qrels/topics.fire12en.176-225.txt)| 0.4470    |


NDCG20                                  | BM25      |
:---------------------------------------|-----------|
[FIRE 2012 (Monolingual Hindi)](../src/main/resources/topics-and-qrels/topics.fire12en.176-225.txt)| 0.5310    |
