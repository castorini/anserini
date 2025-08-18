# Anserini Regressions: BRIGHT &mdash; Robotics

**Model**: [SPLADE-v3](https://arxiv.org/abs/2403.06789) (using ONNX for on-the-fly query encoding)

This page documents regression experiments for [BRIGHT &mdash; Robotics](https://brightbenchmark.github.io/) using using [SPLADE-v3](https://arxiv.org/abs/2403.06789).
The model itself can be download [here](https://huggingface.co/naver/splade-v3).
See the [official SPLADE repo](https://github.com/naver/splade) and the following paper for more details:

> Carlos Lassance, Hervé Déjean, Thibault Formal, and Stéphane Clinchant. [SPLADE-v3: New baselines for SPLADE.](https://arxiv.org/abs/2403.06789) _arXiv:2403.06789_.

In these experiments, we are using ONNX to perform query encoding on the fly.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/bright-robotics.splade-v3.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/bright-robotics.splade-v3.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and build Anserini to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression bright-robotics.splade-v3.onnx
```

All the BRIGHT corpora, encoded byt he SPLADE-v3 mode, are available for download:

```bash
wget https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-splade-v3.tar -P collections/
tar xvf collections/bright-splade-v3.tar -C collections/
```

The tarball is 1.5 GB and has MD5 checksum `434cd776b5c40f8112d2bf888c58a516`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/bright-robotics \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.bright-robotics.splade-v3/ \
  -impact -pretokenized \
  >& logs/log.bright-robotics &
```

The path `/path/to/bright-robotics/` should point to the corpus downloaded above.
The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-robotics.splade-v3/ \
  -topics tools/topics-and-qrels/topics.bright-robotics.tsv.gz \
  -topicReader TsvString \
  -output runs/run.bright-robotics.splade-v3-onnx.topics.bright-robotics.txt \
  -impact -pretokenized -removeQuery -hits 1000 -encoder SpladeV3 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.bright-robotics.txt runs/run.bright-robotics.splade-v3-onnx.topics.bright-robotics.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.bright-robotics.txt runs/run.bright-robotics.splade-v3-onnx.topics.bright-robotics.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.bright-robotics.txt runs/run.bright-robotics.splade-v3-onnx.topics.bright-robotics.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **SPLADE-v3**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BRIGHT: Robotics                                                                                             | 0.1578    |
| **R@100**                                                                                                    | **SPLADE-v3**|
| BRIGHT: Robotics                                                                                             | 0.4470    |
| **R@1000**                                                                                                   | **SPLADE-v3**|
| BRIGHT: Robotics                                                                                             | 0.7557    |
