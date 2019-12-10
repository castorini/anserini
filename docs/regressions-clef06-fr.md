# Anserini: Regressions for [CLEF2006 Monolingual French](http://www.clef-initiative.eu/edition/clef2006)

This page documents regression experiments for [CLEF2006 monolingual French topics)](http://www.clef-initiative.eu/edition/clef2006).
The description of the document collection can be found in the [CLEF corpus page](http://www.clef-initiative.eu/dataset/corpus).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/clef06-fr.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/celf06-fr.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection -input /path/to/clef06-fr \
 -index lucene-index.clef06-fr.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -language fr >& log.clef06-fr.pos+docvectors+rawdocs &
```

The directory `/path/to/clef06-fr/` should be a directory containing the collection (the format is jsonline format).

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 49 questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.clef06-fr.pos+docvectors+rawdocs \
 -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.clef06fr.mono.fr.txt \
 -language fr -bm25 -output run.clef06-fr.bm25.topics.clef06fr.mono.fr.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.clef06fr.txt run.clef06-fr.bm25.topics.clef06fr.mono.fr.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[CLEF2006 (French monolingual)](http://www.clef-initiative.eu/edition/clef2006)| 0.3111    |


P30                                     | BM25      |
:---------------------------------------|-----------|
[CLEF2006 (French monolingual)](http://www.clef-initiative.eu/edition/clef2006)| 0.2735    |
