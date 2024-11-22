# Anserini Regressions: TREC 2002 Monolingual Arabic

This page documents BM25 regression experiments for monolingual Arabic document retrieval as part of the [TREC 2002 CLIR Track](https://trec.nist.gov/pubs/trec11/t11_proceedings.html).
The description of the document collection can be found on the [TREC data page](https://trec.nist.gov/data/docs_noneng.html).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/trec02-ar.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/trec02-ar.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression trec02-ar
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection CleanTrecCollection \
  -input /path/to/trec02-ar \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.trec02-ar/ \
  -storePositions -storeDocvectors -storeRaw -language ar \
  >& logs/log.trec02-ar &
```

The collection comprises Agence France Presse (AFP) Arabic newswire, from [LDC2001T55 (Arabic Newswire Part 1)](https://catalog.ldc.upenn.edu/LDC2001T55).
Inside the LDC2007T38 distribution, there should be a directory named `transcripts`, which contains 2,337 gzipped files in 7 directories, `1994` ... `2000`.
The path above `/path/to/trec02-ar/` should point to this `transcripts/` directory.
The collection contains 383,872 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from NIST's page for [non-English topics](https://trec.nist.gov/data/topics_noneng/index.html) and [non-English relevance judgments](https://trec.nist.gov/data/qrels_noneng/index.html):

+ [`topics.trec02ar-ar.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.trec02ar-ar.txt): TREC 2002 cross language topics in Arabic
+ [`qrels.trec02ar.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.trec02ar.txt): TREC 2002 cross language relevance judgements

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.trec02-ar/ \
  -topics tools/topics-and-qrels/topics.trec02ar-ar.txt \
  -topicReader Trec \
  -output runs/run.trec02-ar.bm25.topics.trec02ar-ar.txt \
  -bm25 -language ar &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -m P.20 -m ndcg_cut.20 tools/topics-and-qrels/qrels.trec02ar.txt runs/run.trec02-ar.bm25.topics.trec02ar-ar.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [TREC 2002 (Monolingual Arabic)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.trec02ar-ar.txt)| 0.2932    |
| **P20**                                                                                                      | **BM25**  |
| [TREC 2002 (Monolingual Arabic)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.trec02ar-ar.txt)| 0.3610    |
| **nDCG@20**                                                                                                  | **BM25**  |
| [TREC 2002 (Monolingual Arabic)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.trec02ar-ar.txt)| 0.4056    |
