# Anserini: Regressions for [FIRE 2012 Monolingual Hindi](http://isical.ac.in/~fire/2012/adhoc.html)

This page documents regression experiments for [FIRE 2012 Ad-hoc retrieval (Monolingual Hindi topic)](http://isical.ac.in/~fire/2012/adhoc.html).
The document collection can be found in [FIRE 2012 data page](http://fire.irsi.res.in/fire/static/data).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/fire-hi.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/fire12-hi.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/fire12-hi \
 -index lucene-index.fire12-hi.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -language hi >& log.fire12-hi.pos+docvectors+rawdocs &
```

The directory `/path/to/fire12-hi/` should be a directory containing the collection, containing `hi_AmarUjala` and `hi_NavbharatTimes` directories.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 50 questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.fire12-hi.pos+docvectors+rawdocs \
 -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.fire12hi.176-225.txt \
 -language hi -bm25 -output run.fire12-hi.bm25.topics.fire12hi.176-225.txt &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.fire12hi.176-225.txt run.fire12-hi.bm25.topics.fire12hi.176-225.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[FIRE2012 (Hindi monolingual)](http://isical.ac.in/~fire/2012/adhoc.html)| 0.3867    |


P30                                     | BM25      |
:---------------------------------------|-----------|
[FIRE2012 (Hindi monolingual)](http://isical.ac.in/~fire/2012/adhoc.html)| 0.3920    |


