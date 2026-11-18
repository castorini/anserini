# <img src="../../anserini-logo.png" height="30" /> BRIGHT &mdash; Sustainable Living

**Anserini reproductions from document collection**

+ **Models**: BM25 and BM25QS
+ **Config**: [`bright-sustainable-living.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.yaml)
+ **Template**: [`bright-sustainable-living.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/bright-sustainable-living.template)

This page documents BM25 and BM25QS regression experiments for [BRIGHT &mdash; Sustainable Living](https://brightbenchmark.github.io/).

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/bright-sustainable-living.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config bright-sustainable-living
```

All the BRIGHT corpora are available for download:

```bash
wget https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-corpus.tar -P collections/
tar xvf collections/bright-corpus.tar -C collections/
```

The tarball is 284 MB and has MD5 checksum `568b594709a9977369033117bfb6889c`.
After download and unpacking the corpora, the `ReproduceFromDocumentCollection` command above should work without any issue.

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection JsonCollection \
  -input /path/to/bright-sustainable-living \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.bright-sustainable-living/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.bright-sustainable-living &
```

The path `/path/to/bright-sustainable-living/` should point to the corpus downloaded above.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-sustainable-living/ \
  -topics bright-sustainable-living \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.bright-sustainable-living.model-bm25.topics-bright-sustainable-living.txt \
  -bm25 -removeQuery -hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-sustainable-living/ \
  -topics bright-sustainable-living \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.bright-sustainable-living.model-bm25qs.topics-bright-sustainable-living.txt \
  -bm25.querySide -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 bright-sustainable-living runs/run.lucene-inverted.bright-sustainable-living.model-bm25.topics-bright-sustainable-living.txt
bin/trec_eval -c -m recall.100 bright-sustainable-living runs/run.lucene-inverted.bright-sustainable-living.model-bm25.topics-bright-sustainable-living.txt
bin/trec_eval -c -m recall.1000 bright-sustainable-living runs/run.lucene-inverted.bright-sustainable-living.model-bm25.topics-bright-sustainable-living.txt

bin/trec_eval -c -m ndcg_cut.10 bright-sustainable-living runs/run.lucene-inverted.bright-sustainable-living.model-bm25qs.topics-bright-sustainable-living.txt
bin/trec_eval -c -m recall.100 bright-sustainable-living runs/run.lucene-inverted.bright-sustainable-living.model-bm25qs.topics-bright-sustainable-living.txt
bin/trec_eval -c -m recall.1000 bright-sustainable-living runs/run.lucene-inverted.bright-sustainable-living.model-bm25qs.topics-bright-sustainable-living.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                | **BM25**   | **BM25QS** |
|:---------------------------|:----------:|:----------:|
| BRIGHT: Sustainable Living | 0.1613     | 0.1515     |
| **R@100**                  | **BM25**   | **BM25QS** |
| BRIGHT: Sustainable Living | 0.4200     | 0.4653     |
| **R@1000**                 | **BM25**   | **BM25QS** |
| BRIGHT: Sustainable Living | 0.7424     | 0.7294     |
