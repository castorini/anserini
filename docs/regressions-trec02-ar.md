# Anserini: Regressions for [TREC 2002 Monolingual Arabic](https://trec.nist.gov/pubs/trec11/t11_proceedings.html)

This page documents regression experiments for monolingual Arabic document retrieval as part of the [TREC 2002 CLIR Track](https://trec.nist.gov/pubs/trec11/t11_proceedings.html).
The description of the document collection can be found on the [TREC data page](https://trec.nist.gov/data/docs_noneng.html).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/trec02-ar.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/trec02-ar.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/trec02-ar \
 -index lucene-index.trec02-ar.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -language ar >& log.trec02-ar.pos+docvectors+rawdocs &
```

The collection comprises Agence France Presse (AFP) Arabic newswire, from [LDC2001T55 (Arabic Newswire Part 1)](https://catalog.ldc.upenn.edu/LDC2001T55).
Inside the LDC2007T38 distribution, there should be a directory named `transcripts`, which contains 2,337 gzipped files in 7 directories, `1994` ... `2000`.
The path above `/path/to/trec02-ar/` should point to this `transcripts/` directory.
The collection contains 383,872 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST's page for [non-English topics](https://trec.nist.gov/data/topics_noneng/index.html) and [non-English relevance judgments](https://trec.nist.gov/data/qrels_noneng/index.html):

+ [`topics.trec02ar-ar.txt`](../src/main/resources/topics-and-qrels/topics.trec02ar-ar.txt): TREC 2002 cross language topics in Arabic
+ [`qrels.trec02ar.txt`](../src/main/resources/topics-and-qrels/qrels.trec02ar.txt): TREC 2002 cross language relevance judgements

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.trec02-ar.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.trec02ar-ar.txt \
 -language ar -bm25 -output run.trec02-ar.bm25.topics.trec02ar-ar.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.20 -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.trec02ar.txt run.trec02-ar.bm25.topics.trec02ar-ar.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[TREC 2002 (Monolingual Arabic)](../src/main/resources/topics-and-qrels/topics.trec02ar-ar.txt)| 0.2932    |


P20                                     | BM25      |
:---------------------------------------|-----------|
[TREC 2002 (Monolingual Arabic)](../src/main/resources/topics-and-qrels/topics.trec02ar-ar.txt)| 0.3610    |


NDCG20                                  | BM25      |
:---------------------------------------|-----------|
[TREC 2002 (Monolingual Arabic)](../src/main/resources/topics-and-qrels/topics.trec02ar-ar.txt)| 0.4056    |
