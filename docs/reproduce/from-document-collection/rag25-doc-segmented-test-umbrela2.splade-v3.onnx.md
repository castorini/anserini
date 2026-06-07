# Anserini Regressions: TREC 2025 RAG Track Test Topics

**Model**: [SPLADE-v3](https://arxiv.org/abs/2403.06789) (using ONNX for on-the-fly query encoding)

This page describes regression experiments for ranking _on the segmented version_ of the MS MARCO V2.1 document corpus using the test topics (= queries in TREC parlance), which is integrated into Anserini's regression testing framework.
This corpus was derived from the MS MARCO V2 _segmented_ document corpus and prepared for the TREC 2024 RAG Track.

The model itself can be download [here](https://huggingface.co/naver/splade-v3).
See the [official SPLADE repo](https://github.com/naver/splade) and the following paper for more details:

> Carlos Lassance, Hervé Déjean, Thibault Formal, and Stéphane Clinchant. [SPLADE-v3: New baselines for SPLADE.](https://arxiv.org/abs/2403.06789) _arXiv:2403.06789_.

In these experiments, we are using ONNX to perform query encoding on the fly.

Evaluation uses qrels over 22 topics from the TREC 2025 RAG Track test set.
More details can be found in the following paper:

> Shivani Upadhyay, Nandan Thakur, Ronak Pradeep, Nick Craswell, Daniel Campos and Jimmy Lin. [Overview of the TREC 2025 Retrieval Augmented Generation (RAG) Track.](https://arxiv.org/abs/2603.09891) _arXiv:2603.09891_, March 2026.

The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.splade-v3.onnx.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/rag25-doc-segmented-test-umbrela2.splade-v3.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config rag25-doc-segmented-test-umbrela2.splade-v3.onnx
```

We make available a version of the MS MARCO V2.1 segmented document corpus that has already been encoded with SPLADE-v3.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --download --index --verify --search --config rag25-doc-segmented-test-umbrela2.splade-v3.onnx
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2.1_doc_segmented_splade-v3.tar -P collections/
tar xvf collections/msmarco_v2.1_doc_segmented_splade-v3.tar -C collections/
```

To confirm, `msmarco-v2.1-doc-segmented-splade-v3.tar` is 125 GB and has MD5 checksum `c62490569364a1eb0101da1ca4a894d9`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config rag25-doc-segmented-test-umbrela2.splade-v3.onnx \
  --corpus-path collections/msmarco_v2.1_doc_segmented_splade-v3
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2.1-doc-segmented-splade-v3 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented.splade-v3/ \
  -impact -pretokenized \
  >& logs/log.msmarco-v2.1-doc-segmented-splade-v3 &
```

The setting of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Here, we are using 22 test topics from the TREC 2025 RAG Track with manual relevance judgments from NIST assessors.
Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented.splade-v3/ \
  -topics tools/topics-and-qrels/topics.rag25.test.jsonl \
  -topicReader JsonString \
  -output runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag25.test.jsonl.txt \
  -impact -pretokenized -removeQuery -hits 1000 -encoder SpladeV3 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.30 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag25.test.jsonl.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag25.test-umbrela2.txt runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag25.test.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@30**          | **SPLADE-v3** |
|:---------------------|:-------------:|
| RAG 25: Test queries | 0.5838        |
| **nDCG@100**         | **SPLADE-v3** |
| RAG 25: Test queries | 0.5372        |
| **R@100**            | **SPLADE-v3** |
| RAG 25: Test queries | 0.1992        |
