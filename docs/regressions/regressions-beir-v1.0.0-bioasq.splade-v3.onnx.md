# Anserini Regressions: BEIR (v1.0.0) &mdash; BioASQ

**Model**: [SPLADE-v3](https://arxiv.org/abs/2403.06789) (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using [SPLADE-v3](https://arxiv.org/abs/2403.06789) on [BEIR (v1.0.0) &mdash; BioASQ](http://beir.ai/).
The model itself can be download [here](https://huggingface.co/naver/splade-v3).
See the [official SPLADE repo](https://github.com/naver/splade) and the following paper for more details:

> Carlos Lassance, Hervé Déjean, Thibault Formal, and Stéphane Clinchant. [SPLADE-v3: New baselines for SPLADE.](https://arxiv.org/abs/2403.06789) _arXiv:2403.06789_.

In these experiments, we are using ONNX to perform query encoding on the fly.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-bioasq.splade-v3.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-bioasq.splade-v3.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.splade-v3.onnx
```

All the BEIR corpora, encoded by the SPLADE-v3 model, are available for download:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-splade-v3.tar -P collections/
tar xvf collections/beir-v1.0.0-splade-v3.tar -C collections/
```

The tarball is 55 GB and has MD5 checksum `37f294610af763ce48eed03afd9455df`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Sample indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-bioasq.splade-v3 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.beir-v1.0.0-bioasq.splade-v3/ \
  -impact -pretokenized \
  >& logs/log.beir-v1.0.0-bioasq.splade-v3 &
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.beir-v1.0.0-bioasq.splade-v3/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-bioasq.test.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-bioasq.splade-v3.splade-v3-onnx.topics.beir-v1.0.0-bioasq.test.txt \
  -impact -pretokenized -removeQuery -hits 1000 -encoder SpladeV3 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-bioasq.test.txt runs/run.beir-v1.0.0-bioasq.splade-v3.splade-v3-onnx.topics.beir-v1.0.0-bioasq.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-bioasq.test.txt runs/run.beir-v1.0.0-bioasq.splade-v3.splade-v3-onnx.topics.beir-v1.0.0-bioasq.test.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-bioasq.test.txt runs/run.beir-v1.0.0-bioasq.splade-v3.splade-v3-onnx.topics.beir-v1.0.0-bioasq.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **SPLADE-v3**|
|:-------------------------------------------------------------------------------------------------------------|--------------|
| BEIR (v1.0.0): BioASQ                                                                                        | 0.5142       |
| **R@100**                                                                                                    | **SPLADE-v3**|
| BEIR (v1.0.0): BioASQ                                                                                        | 0.7647       |
| **R@1000**                                                                                                   | **SPLADE-v3**|
| BEIR (v1.0.0): BioASQ                                                                                        | 0.9018       |
