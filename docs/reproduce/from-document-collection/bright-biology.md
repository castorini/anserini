# <img src="../../anserini-logo.png" height="30" /> BRIGHT &mdash; Biology

**Anserini reproductions from document collection**

+ **Models**: BM25 and BM25QS
+ **Config**: [`bright-biology.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/bright-biology.yaml)
+ **Template**: [`bright-biology.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/bright-biology.template)

This page documents BM25 and BM25QS regression experiments for [BRIGHT &mdash; Biology](https://brightbenchmark.github.io/).

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/bright-biology.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config bright-biology
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
  -input /path/to/bright-biology \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.bright-biology/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.bright-biology &
```

The path `/path/to/bright-biology/` should point to the corpus downloaded above.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-biology/ \
  -topics bright-biology \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.bright-biology.model-bm25.topics-bright-biology.txt \
  -bm25 -removeQuery -hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-biology/ \
  -topics bright-biology \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.bright-biology.model-bm25qs.topics-bright-biology.txt \
  -bm25.querySide -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 bright-biology runs/run.lucene-inverted.bright-biology.model-bm25.topics-bright-biology.txt
bin/trec_eval -c -m recall.100 bright-biology runs/run.lucene-inverted.bright-biology.model-bm25.topics-bright-biology.txt
bin/trec_eval -c -m recall.1000 bright-biology runs/run.lucene-inverted.bright-biology.model-bm25.topics-bright-biology.txt

bin/trec_eval -c -m ndcg_cut.10 bright-biology runs/run.lucene-inverted.bright-biology.model-bm25qs.topics-bright-biology.txt
bin/trec_eval -c -m recall.100 bright-biology runs/run.lucene-inverted.bright-biology.model-bm25qs.topics-bright-biology.txt
bin/trec_eval -c -m recall.1000 bright-biology runs/run.lucene-inverted.bright-biology.model-bm25qs.topics-bright-biology.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**     | **BM25**   | **BM25QS** |
|:----------------|:----------:|:----------:|
| BRIGHT: Biology | 0.1824     | 0.1972     |
| **R@100**       | **BM25**   | **BM25QS** |
| BRIGHT: Biology | 0.4205     | 0.4583     |
| **R@1000**      | **BM25**   | **BM25QS** |
| BRIGHT: Biology | 0.7644     | 0.7638     |
