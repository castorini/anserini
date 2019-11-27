# Anserini: Regressions for [TREC2002 Monolingual Arabic](https://trec.nist.gov/pubs/trec11/t11_proceedings.html)

This page documents regression experiments for [TREC2002 Arabic monolingual topics)](https://trec.nist.gov/pubs/trec11/t11_proceedings.html).
The description of the document collection can be found in the [TREC data page](https://trec.nist.gov/data/docs_noneng.html): Agence France Presse (AFP) Arabic newswire, from [LDC2001T55 (Arabic Newswire Part 1)](https://catalog.ldc.upenn.edu/LDC2001T55).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/trec02-ar.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/trec02-ar.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/trec02-ar \
 -index lucene-index.trec02-ar.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -language ar >& log.trec02-ar.pos+docvectors+rawdocs &
```

The directory `/path/to/trec02-ar/` should be a directory containing the collection, 2337 gzipped files from LDC2007T38.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 50 questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.trec02-ar.pos+docvectors+rawdocs \
 -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.trec02ar.mono.ar.txt \
 -language ar -bm25 -output run.trec02-ar.bm25.topics.trec02ar.mono.ar.txt &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.trec02ar.txt run.trec02-ar.bm25.topics.trec02ar.mono.ar.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[TREC2002 (Arabic monolingual)](../src/main/resources/topics-and-qrels/topics.trec02ar.momo.ar.txt)| 0.2932    |


P30                                     | BM25      |
:---------------------------------------|-----------|
[TREC2002 (Arabic monolingual)](../src/main/resources/topics-and-qrels/topics.trec02ar.momo.ar.txt)| 0.3313    |


