# <img src="../../anserini-logo.png" height="30" /> BEIR (v1.0.0) &mdash; Quora

**Anserini reproductions from document collection**

+ **Models**: [SPLADE-v3](https://arxiv.org/abs/2403.06789) (using ONNX for on-the-fly query encoding)
+ **Config**: [`beir-v1.0.0-quora.splade-v3.onnx.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-v3.onnx.yaml)
+ **Template**: [`beir-v1.0.0-quora.splade-v3.onnx.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/beir-v1.0.0-quora.splade-v3.onnx.template)

This page describes reproductions from the raw document collection using [SPLADE-v3](https://huggingface.co/naver/splade-v3) on [BEIR (v1.0.0) &mdash; Quora](http://beir.ai/).
See the [official SPLADE repo](https://github.com/naver/splade) and the following paper for more details:

> Carlos Lassance, Hervé Déjean, Thibault Formal, and Stéphane Clinchant. [SPLADE-v3: New baselines for SPLADE.](https://arxiv.org/abs/2403.06789) _arXiv:2403.06789_.

In these experiments, we are using ONNX to perform query encoding on the fly.

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/beir-v1.0.0-quora.splade-v3.onnx.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config beir-v1.0.0-quora.splade-v3.onnx
```

All the BEIR corpora, encoded by the SPLADE-v3 model, are available for download:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-splade-v3.tar -P collections/
tar xvf collections/beir-v1.0.0-splade-v3.tar -C collections/
```

The tarball is 55 GB and has MD5 checksum `37f294610af763ce48eed03afd9455df`.
After download and unpacking the corpora, the `ReproduceFromDocumentCollection` command above should work without any issue.

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-quora.splade-v3 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.beir-v1.0.0-quora.splade-v3/ \
  -impact -pretokenized \
  >& logs/log.beir-v1.0.0-quora.splade-v3 &
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.beir-v1.0.0-quora.splade-v3/ \
  -topics beir-v1.0.0-quora.test \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.beir-v1.0.0-quora.splade-v3.model-splade-v3-onnx.topics-beir-v1.0.0-quora.test.txt \
  -impact -pretokenized -removeQuery -hits 1000 -encoder SpladeV3 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 beir-v1.0.0-quora.test runs/run.lucene-inverted.beir-v1.0.0-quora.splade-v3.model-splade-v3-onnx.topics-beir-v1.0.0-quora.test.txt
bin/trec_eval -c -m recall.100 beir-v1.0.0-quora.test runs/run.lucene-inverted.beir-v1.0.0-quora.splade-v3.model-splade-v3-onnx.topics-beir-v1.0.0-quora.test.txt
bin/trec_eval -c -m recall.1000 beir-v1.0.0-quora.test runs/run.lucene-inverted.beir-v1.0.0-quora.splade-v3.model-splade-v3-onnx.topics-beir-v1.0.0-quora.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**          | **SPLADE-v3** |
|:---------------------|:-------------:|
| BEIR (v1.0.0): Quora | 0.8141        |
| **R@100**            | **SPLADE-v3** |
| BEIR (v1.0.0): Quora | 0.9858        |
| **R@1000**           | **SPLADE-v3** |
| BEIR (v1.0.0): Quora | 0.9988        |
