# Anserini Regressions: BRIGHT &mdash; AoPS

**Model**: [BGE-large-en-v1.5](https://huggingface.co/BAAI/bge-large-en-v1.5) with flat indexes (using ONNX for on-the-fly query encoding)

This page documents regression experiments, integrated into Anserini's regression testing framework, for [BRIGHT &mdash; AoPS](https://brightbenchmark.github.io/) using [BGE-large-en-v1.5](https://huggingface.co/BAAI/bge-large-en-v1.5).
The model itself can be download [here](https://huggingface.co/BAAI/bge-large-en-v1.5).
See the following paper for more details:

> Shitao Xiao, Zheng Liu, Peitian Zhang, and Niklas Muennighoff. [C-Pack: Packaged Resources To Advance General Chinese Embedding.](https://arxiv.org/abs/2309.07597) _arXiv:2309.07597_, 2023.

In these experiments, we are using ONNX to perform query encoding on the fly.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/bright-aops.bge-large-en-v1.5.flat.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/bright-aops.bge-large-en-v1.5.flat.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and build Anserini to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression bright-aops.bge-large-en-v1.5.flat.onnx
```

All the BRIGHT corpora, encoded by the BGE-large-en-v1.5 model, are available for download:

```bash
wget https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-bge-large-en-v1.5.tar -P collections/
tar xvf collections/bright-bge-large-en-v1.5.tar -C collections/
```

The tarball is 13 GB and has MD5 checksum `0ce2634d34d3d467cd1afd74f2f63c7b`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command, building flat indexes:

```
bin/run.sh io.anserini.index.IndexFlatDenseVectors \
  -threads 16 \
  -collection JsonDenseVectorCollection \
  -input /path/to/bright-aops.bge-large-en-v1.5 \
  -generator DenseVectorDocumentGenerator \
  -index indexes/lucene-flat.bright-aops.bge-large-en-v1.5/ \
  >& logs/log.bright-aops.bge-large-en-v1.5 &
```

The path `/path/to/bright-aops.bge-large-en-v1.5/` should point to the corpus downloaded above.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchFlatDenseVectors \
  -index indexes/lucene-flat.bright-aops.bge-large-en-v1.5/ \
  -topics tools/topics-and-qrels/topics.bright-aops.tsv.gz \
  -topicReader TsvString \
  -output runs/run.bright-aops.bge-large-en-v1.5.bge-flat-onnx.topics.bright-aops.txt \
  -encoder BgeLargeEn15 -hits 1000 -removeQuery -threads 16 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.bright-aops.txt runs/run.bright-aops.bge-large-en-v1.5.bge-flat-onnx.topics.bright-aops.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.bright-aops.txt runs/run.bright-aops.bge-large-en-v1.5.bge-flat-onnx.topics.bright-aops.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.bright-aops.txt runs/run.bright-aops.bge-large-en-v1.5.bge-flat-onnx.topics.bright-aops.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BGE-large-en-v1.5**|
|:-------------------------------------------------------------------------------------------------------------|----------------------|
| BRIGHT: AoPS                                                                                                 | 0.0638               |
| **R@100**                                                                                                    | **BGE-large-en-v1.5**|
| BRIGHT: AoPS                                                                                                 | 0.2405               |
| **R@1000**                                                                                                   | **BGE-large-en-v1.5**|
| BRIGHT: AoPS                                                                                                 | 0.4398               |

With ONNX query encoding on non-quantized flat indexes, observed results may differ slightly (typically, lower), but scores should generally be within 0.001 of the results reported above (with some outliers).
