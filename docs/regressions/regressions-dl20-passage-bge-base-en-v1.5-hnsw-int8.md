# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Model**: [BGE-base-en-v1.5](https://huggingface.co/BAAI/bge-base-en-v1.5) with HNSW quantized indexes (using pre-encoded queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [BGE-base-en-v1.5](https://huggingface.co/BAAI/bge-base-en-v1.5) model on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html), as described in the following paper:

> Shitao Xiao, Zheng Liu, Peitian Zhang, and Niklas Muennighoff. [C-Pack: Packaged Resources To Advance General Chinese Embedding.](https://arxiv.org/abs/2309.07597) _arXiv:2309.07597_, 2023.

In these experiments, we are using pre-encoded queries (i.e., cached results of query encoding).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl20-passage-bge-base-en-v1.5-hnsw-int8.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl20-passage-bge-base-en-v1.5-hnsw-int8.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-bge-base-en-v1.5-hnsw-int8
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with cosDPR-distil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl20-passage-bge-base-en-v1.5-hnsw-int8
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-bge-base-en-v1.5.tar -P collections/
tar xvf collections/msmarco-passage-bge-base-en-v1.5.tar -C collections/
```

To confirm, `msmarco-passage-bge-base-en-v1.5.tar` is 59 GB and has MD5 checksum `353d2c9e72e858897ad479cca4ea0db1`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-bge-base-en-v1.5-hnsw-int8 \
  --corpus-path collections/msmarco-passage-bge-base-en-v1.5
```

## Indexing

Sample indexing command, building HNSW indexes:

```bash
target/appassembler/bin/IndexHnswDenseVectors \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-bge-base-en-v1.5 \
  -generator HnswDenseVectorDocumentGenerator \
  -index indexes/lucene-hnsw.msmarco-passage-bge-base-en-v1.5-int8/ \
  -threads 16 -M 16 -efC 100 -memoryBuffer 65536 -noMerge -quantize.int8 \
  >& logs/log.msmarco-passage-bge-base-en-v1.5 &
```

The path `/path/to/msmarco-passage-bge-base-en-v1.5/` should point to the corpus downloaded above.
Upon completion, we should have an index with 8,841,823 documents.

Note that here we are explicitly using Lucene's `NoMergePolicy` merge policy, which suppresses any merging of index segments.
This is because merging index segments is a costly operation and not worthwhile given our query set.
Furthermore, we are using Lucene's [Automatic Byte Quantization](https://www.elastic.co/search-labs/blog/articles/scalar-quantization-in-lucene) feature, which increase the on-disk footprint of the indexes since we're storing both the int8 quantized vectors and the float32 vectors, but only the int8 quantized vectors need to be loaded into memory.
See [issue #2292](https://github.com/castorini/anserini/issues/2292) for some experiments reporting the performance impact.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchHnswDenseVectors \
  -index indexes/lucene-hnsw.msmarco-passage-bge-base-en-v1.5-int8/ \
  -topics tools/topics-and-qrels/topics.dl20.bge-base-en-v1.5.jsonl.gz \
  -topicReader JsonIntVector \
  -output runs/run.msmarco-passage-bge-base-en-v1.5.bge-hnsw-cached_q.topics.dl20.bge-base-en-v1.5.jsonl.txt \
  -generator VectorQueryGenerator -topicField vector -threads 16 -hits 1000 -efSearch 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
target/appassembler/bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-bge-base-en-v1.5.bge-hnsw-cached_q.topics.dl20.bge-base-en-v1.5.jsonl.txt
target/appassembler/bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-bge-base-en-v1.5.bge-hnsw-cached_q.topics.dl20.bge-base-en-v1.5.jsonl.txt
target/appassembler/bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-bge-base-en-v1.5.bge-hnsw-cached_q.topics.dl20.bge-base-en-v1.5.jsonl.txt
target/appassembler/bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-bge-base-en-v1.5.bge-hnsw-cached_q.topics.dl20.bge-base-en-v1.5.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BGE-base-en-v1.5**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.463     |
| **nDCG@10**                                                                                                  | **BGE-base-en-v1.5**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.674     |
| **R@100**                                                                                                    | **BGE-base-en-v1.5**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.712     |
| **R@1000**                                                                                                   | **BGE-base-en-v1.5**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.840     |

Note that due to the non-deterministic nature of HNSW indexing, results may differ slightly between each experimental run.
Nevertheless, scores are generally within 0.005 of the reference values recorded in [our YAML configuration file](../../src/main/resources/regression/dl20-passage-bge-base-en-v1.5-hnsw-int8.yaml).

Also note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl20-passage-bge-base-en-v1.5-hnsw-int8.template) and run `bin/build.sh` to rebuild the documentation.
