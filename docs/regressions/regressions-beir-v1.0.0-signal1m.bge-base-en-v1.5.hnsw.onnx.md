# Anserini Regressions: BEIR (v1.0.0) &mdash; Signal-1M

**Model**: [BGE-base-en-v1.5](https://huggingface.co/BAAI/bge-base-en-v1.5) with HNSW indexes (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [BGE-base-en-v1.5](https://huggingface.co/BAAI/bge-base-en-v1.5) model on [BEIR (v1.0.0) &mdash; Signal-1M](http://beir.ai/), as described in the following paper:

> Shitao Xiao, Zheng Liu, Peitian Zhang, and Niklas Muennighoff. [C-Pack: Packaged Resources To Advance General Chinese Embedding.](https://arxiv.org/abs/2309.07597) _arXiv:2309.07597_, 2023.

In these experiments, we are using ONNX to perform query encoding on the fly.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-signal1m.bge-base-en-v1.5.hnsw.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-signal1m.bge-base-en-v1.5.hnsw.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.hnsw.onnx
```

All the BEIR corpora, encoded by the BGE-base-en-v1.5 model, are available for download:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-bge-base-en-v1.5.tar -P collections/
tar xvf collections/beir-v1.0.0-bge-base-en-v1.5.tar -C collections/
```

The tarball is 294 GB and has MD5 checksum `e4e8324ba3da3b46e715297407a24f00`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Sample indexing command, building HNSW indexes:

```
bin/run.sh io.anserini.index.IndexHnswDenseVectors \
  -threads 16 \
  -collection JsonDenseVectorCollection \
  -input /path/to/beir-v1.0.0-signal1m.bge-base-en-v1.5 \
  -generator DenseVectorDocumentGenerator \
  -index indexes/lucene-hnsw.beir-v1.0.0-signal1m.bge-base-en-v1.5/ \
  -M 16 -efC 100 -memoryBuffer 65536 -noMerge \
  >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5 &
```

The path `/path/to/beir-v1.0.0-signal1m.bge-base-en-v1.5/` should point to the corpus downloaded above.
Note that here we are explicitly using Lucene's `NoMergePolicy` merge policy, which suppresses any merging of index segments.
This is because merging index segments is a costly operation and not worthwhile given our query set.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchHnswDenseVectors \
  -index indexes/lucene-hnsw.beir-v1.0.0-signal1m.bge-base-en-v1.5/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-signal1m.test.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-signal1m.bge-base-en-v1.5.bge-hnsw-onnx.topics.beir-v1.0.0-signal1m.test.txt \
  -encoder BgeBaseEn15 -hits 1000 -efSearch 1000 -removeQuery -threads 16 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-signal1m.test.txt runs/run.beir-v1.0.0-signal1m.bge-base-en-v1.5.bge-hnsw-onnx.topics.beir-v1.0.0-signal1m.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-signal1m.test.txt runs/run.beir-v1.0.0-signal1m.bge-base-en-v1.5.bge-hnsw-onnx.topics.beir-v1.0.0-signal1m.test.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-signal1m.test.txt runs/run.beir-v1.0.0-signal1m.bge-base-en-v1.5.bge-hnsw-onnx.topics.beir-v1.0.0-signal1m.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BGE-base-en-v1.5**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): Signal-1M                                                                                     | 0.289     |
| **R@100**                                                                                                    | **BGE-base-en-v1.5**|
| BEIR (v1.0.0): Signal-1M                                                                                     | 0.311     |
| **R@1000**                                                                                                   | **BGE-base-en-v1.5**|
| BEIR (v1.0.0): Signal-1M                                                                                     | 0.533     |

The above figures are from running brute-force search with cached queries on non-quantized **flat** indexes.
With ONNX query encoding on non-quantized HNSW indexes, observed results may differ slightly (typically, lower), but scores should generally be within 0.003 of the results reported above (with some outliers).
Note that HNSW indexing is non-deterministic (i.e., results may differ slightly between trials).
