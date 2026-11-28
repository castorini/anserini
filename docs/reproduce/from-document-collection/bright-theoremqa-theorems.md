# <img src="../../anserini-logo.png" height="30" /> BRIGHT &mdash; TheoremQA-T

**Anserini reproductions from document collection**

+ **Models**: BM25 and BM25QS
+ **Config**: [`bright-theoremqa-theorems.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.yaml)
+ **Template**: [`bright-theoremqa-theorems.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/bright-theoremqa-theorems.template)

This page describes reproductions from the raw document collection using BM25 and BM25QS on [BRIGHT &mdash; TheoremQA-T](https://brightbenchmark.github.io/).
BM25QS (query-side BM25) is described in the following paper:

> Yijun Ge, Sahel Sharifymoghaddam, and Jimmy Lin. [Lighting the Way for BRIGHT: Reproducible Baselines with Anserini, Pyserini, and RankLLM.](https://arxiv.org/abs/2509.02558) _arXiv:2509.02558_, 2025.

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/bright-theoremqa-theorems.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config bright-theoremqa-theorems
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
  -input /path/to/bright-theoremqa-theorems \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.bright-theoremqa-theorems/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.bright-theoremqa-theorems &
```

The path `/path/to/bright-theoremqa-theorems/` should point to the corpus downloaded above.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-theoremqa-theorems/ \
  -topics bright-theoremqa-theorems \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25.topics-bright-theoremqa-theorems.txt \
  -bm25 -removeQuery -hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-theoremqa-theorems/ \
  -topics bright-theoremqa-theorems \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25qs.topics-bright-theoremqa-theorems.txt \
  -bm25.querySide -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 bright-theoremqa-theorems runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25.topics-bright-theoremqa-theorems.txt
bin/trec_eval -c -m recall.100 bright-theoremqa-theorems runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25.topics-bright-theoremqa-theorems.txt
bin/trec_eval -c -m recall.1000 bright-theoremqa-theorems runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25.topics-bright-theoremqa-theorems.txt

bin/trec_eval -c -m ndcg_cut.10 bright-theoremqa-theorems runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25qs.topics-bright-theoremqa-theorems.txt
bin/trec_eval -c -m recall.100 bright-theoremqa-theorems runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25qs.topics-bright-theoremqa-theorems.txt
bin/trec_eval -c -m recall.1000 bright-theoremqa-theorems runs/run.lucene-inverted.bright-theoremqa-theorems.model-bm25qs.topics-bright-theoremqa-theorems.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**         | **BM25**   | **BM25QS** |
|:--------------------|:----------:|:----------:|
| BRIGHT: TheoremQA-T | 0.0214     | 0.0492     |
| **R@100**           | **BM25**   | **BM25QS** |
| BRIGHT: TheoremQA-T | 0.1338     | 0.1842     |
| **R@1000**          | **BM25**   | **BM25QS** |
| BRIGHT: TheoremQA-T | 0.3846     | 0.4200     |
