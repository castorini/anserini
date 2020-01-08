# Anserini: Regressions for [CLEF2006 Monolingual French](http://www.clef-initiative.eu/edition/clef2006)

This page documents regression experiments for monolingual French document retrieval as part of the [CLEF 2006 Multilingual Document Retrieval (Ad Hoc) Track](http://www.clef-initiative.eu/edition/clef2006).
Associated data can be found on the [CLEF test suites pages](http://www.clef-initiative.eu/dataset/corpus).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/clef06-fr.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/clef06-fr.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection -input /path/to/clef06-fr \
 -index lucene-index.clef06-fr.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -language fr >& log.clef06-fr.pos+docvectors+rawdocs &
```

The collection comprises news articles from ATS (SDA) and Le Monde totaling 177,452 documents.
Since the original distribution is in a format that's slightly different from standard TREC collections, we used a [preprocessing script](../src/main/python/clir/document_preprocess.py) to convert the collection into Anserini's JSON line format (we also applied a bit of light data cleaning using a script that has been lost; if you have problems replicating our results, get in touch directly).
The directory `/path/to/clef06-fr/` should point to the location of the processed collection.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), from the [CLEF test suites pages](http://www.clef-initiative.eu/dataset/corpus):

+ [`topics.clef06fr.mono.fr.txt`](../src/main/resources/topics-and-qrels/topics.clef06fr.mono.fr.txt): CLEF 2006 ad hoc track topics in French
+ [`qrels.clef06fr.txt`](../src/main/resources/topics-and-qrels/qrels.clef06fr.txt): CLEF 2006 ad hoc track French relevance judgements

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.clef06-fr.pos+docvectors+rawdocs \
 -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.clef06fr.mono.fr.txt \
 -language fr -bm25 -output run.clef06-fr.bm25.topics.clef06fr.mono.fr.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.20 -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.clef06fr.txt run.clef06-fr.bm25.topics.clef06fr.mono.fr.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[CLEF 2006 (Monolingual French)](../src/main/resources/topics-and-qrels/topics.clef06fr.mono.fr.txt)| 0.3111    |


P20                                     | BM25      |
:---------------------------------------|-----------|
[CLEF 2006 (Monolingual French)](../src/main/resources/topics-and-qrels/topics.clef06fr.mono.fr.txt)| 0.3184    |


NDCG20                                  | BM25      |
:---------------------------------------|-----------|
[CLEF 2006 (Monolingual French)](../src/main/resources/topics-and-qrels/topics.clef06fr.mono.fr.txt)| 0.4458    |
