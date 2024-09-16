# Anserini Regressions: MS MARCO Passage Ranking

**Model**: cosDPR-distil with quantized HNSW indexes (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the cosDPR-distil model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking), as described in the following paper:

> Xueguang Ma, Tommaso Teofili, and Jimmy Lin. [Anserini Gets Dense Retrieval: Integration of Lucene's HNSW Indexes.](https://dl.acm.org/doi/10.1145/3583780.3615112) _Proceedings of the 32nd International Conference on Information and Knowledge Management (CIKM 2023)_, October 2023, pages 5366–5370, Birmingham, the United Kingdom.

In these experiments, we are performing query inference "on-the-fly" with ONNX.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v1-passage.cos-dpr-distil.hnsw-int8.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.cos-dpr-distil.hnsw-int8.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.hnsw-int8.onnx
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with cosDPR-distil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.hnsw-int8.onnx
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-cos-dpr-distil.tar -P collections/
tar xvf collections/msmarco-passage-cos-dpr-distil.tar -C collections/
```

To confirm, `msmarco-passage-cos-dpr-distil.tar` is 57 GB and has MD5 checksum `e20ffbc8b5e7f760af31298aefeaebbd`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.hnsw-int8.onnx \
  --corpus-path collections/msmarco-passage-cos-dpr-distil
```

## Indexing

Sample indexing command, building quantized HNSW indexes:

```bash
bin/run.sh io.anserini.index.IndexHnswDenseVectors \
  -threads 16 \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-cos-dpr-distil \
  -generator DenseVectorDocumentGenerator \
  -index indexes/lucene-hnsw-int8.msmarco-v1-passage.cos-dpr-distil/ \
  -M 16 -efC 100 -quantize.int8 \
  >& logs/log.msmarco-passage-cos-dpr-distil &
```

The path `/path/to/msmarco-passage-cos-dpr-distil/` should point to the corpus downloaded above.
Upon completion, we should have an index with 8,841,823 documents.

Note that here we are explicitly using Lucene's `NoMergePolicy` merge policy, which suppresses any merging of index segments.
This is because merging index segments is a costly operation and not worthwhile given our query set.
Furthermore, we are using Lucene's [Automatic Byte Quantization](https://www.elastic.co/search-labs/blog/articles/scalar-quantization-in-lucene) feature, which increase the on-disk footprint of the indexes since we're storing both the int8 quantized vectors and the float32 vectors, but only the int8 quantized vectors need to be loaded into memory.
See [issue #2292](https://github.com/castorini/anserini/issues/2292) for some experiments reporting the performance impact.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows using HNSW indexes:

```bash
bin/run.sh io.anserini.search.SearchHnswDenseVectors \
  -index indexes/lucene-hnsw-int8.msmarco-v1-passage.cos-dpr-distil/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-hnsw-int8-onnx.topics.msmarco-passage.dev-subset.txt \
  -generator VectorQueryGenerator -topicField title -threads 16 -hits 1000 -efSearch 1000 -encoder CosDprDistil &
```

Note that we are performing query inference "on-the-fly" with ONNX in these experiments.

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-hnsw-int8-onnx.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-hnsw-int8-onnx.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-hnsw-int8-onnx.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-hnsw-int8-onnx.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **cosDPR-distil**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.394     |
| **RR@10**                                                                                                    | **cosDPR-distil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.390     |
| **R@100**                                                                                                    | **cosDPR-distil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.908     |
| **R@1000**                                                                                                   | **cosDPR-distil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.980     |

The above figures are from running brute-force search with cached queries on non-quantized **flat** indexes.
With ONNX query encoding on quantized HNSW indexes, observed results are likely to differ; scores may be lower by up to 0.01, sometimes more.
Note that both HNSW indexing and quantization are non-deterministic (i.e., results may differ slightly between trials).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.cos-dpr-distil.hnsw-int8.onnx.template) and run `bin/build.sh` to rebuild the documentation.
