# Anserini Regressions: TREC 2024 RAG Track RAGgy Dev Topics

**Model**: [SPLADE-v3](https://arxiv.org/abs/2403.06789) (using ONNX for on-the-fly query encoding)

This page describes experiments, integrated into Anserini's regression testing framework, on the "RAGgy dev topics" on the MS MARCO V2.1 _segmented_ document corpus.
These "RAGgy topics" were manually curated from the TREC 2021, 2022, and 2023 Deep Learning Tracks to be "RAG-worthy" according to the track organizers.

The model itself can be download [here](https://huggingface.co/naver/splade-v3).
See the [official SPLADE repo](https://github.com/naver/splade) and the following paper for more details:

> Carlos Lassance, Hervé Déjean, Thibault Formal, and Stéphane Clinchant. [SPLADE-v3: New baselines for SPLADE.](https://arxiv.org/abs/2403.06789) _arXiv:2403.06789_.

In these experiments, we are using ONNX to perform query encoding on the fly.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/rag24-doc-segmented-raggy-dev.splade-v3.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/rag24-doc-segmented-raggy-dev.splade-v3.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-raggy-dev.splade-v3.onnx
```

We make available a version of the MS MARCO V2.1 segmented document corpus that has already been encoded with SPLADE-v3.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression rag24-doc-segmented-raggy-dev.splade-v3.onnx
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
python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-raggy-dev.splade-v3.onnx \
  --corpus-path collections/msmarco_v2.1_doc_segmented_splade-v3
```

## Indexing

Typical indexing command:

```
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
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
These "RAG-worthy" topics were manually curated from the TREC 2021, 2022, and 2023 Deep Learning Tracks and projected over to the V2.1 version of the corpus.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented.splade-v3/ \
  -topics tools/topics-and-qrels/topics.rag24.raggy-dev.txt \
  -topicReader TsvString \
  -output runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag24.raggy-dev.txt \
  -impact -pretokenized -removeQuery -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 -encoder SpladeV3 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag24.raggy-dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented-splade-v3.splade-v3-onnx.topics.rag24.raggy-dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **SPLADE-v3**|
|:-------------------------------------------------------------------------------------------------------------|--------------|
| RAG 24: RAGgy dev queries                                                                                    | 0.2295       |
| **MRR@100**                                                                                                  | **SPLADE-v3**|
| RAG 24: RAGgy dev queries                                                                                    | 0.8738       |
| **nDCG@10**                                                                                                  | **SPLADE-v3**|
| RAG 24: RAGgy dev queries                                                                                    | 0.5536       |
| **R@100**                                                                                                    | **SPLADE-v3**|
| RAG 24: RAGgy dev queries                                                                                    | 0.3657       |
| **R@1000**                                                                                                   | **SPLADE-v3**|
| RAG 24: RAGgy dev queries                                                                                    | 0.6787       |
