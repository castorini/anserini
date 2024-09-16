# Anserini Regressions: TREC 2019 Deep Learning Track (Passage)

**Model**: OpenAI-ada2 embeddings with quantized HNSW indexes (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using OpenAI-ada2 embeddings on the [TREC 2019 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html), as described in the following paper:

> Jimmy Lin, Ronak Pradeep, Tommaso Teofili, and Jasper Xian. [Vector Search with OpenAI Embeddings: Lucene Is All You Need.](https://arxiv.org/abs/2308.14963) _arXiv:2308.14963_, 2023.

In these experiments, we are using cached queries (i.e., cached results of query encoding).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl19-passage.openai-ada2.hnsw-int8.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl19-passage.openai-ada2.hnsw-int8.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.openai-ada2.hnsw-int8.cached
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with the OpenAI-ada2 embedding model.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl19-passage.openai-ada2.hnsw-int8.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-openai-ada2.tar -P collections/
tar xvf collections/msmarco-passage-openai-ada2.tar -C collections/
```

To confirm, `msmarco-passage-openai-ada2.tar` is 109 GB and has MD5 checksum `a4d843d522ff3a3af7edbee789a63402`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.openai-ada2.hnsw-int8.cached \
  --corpus-path collections/msmarco-passage-openai-ada2
```

## Indexing

Sample indexing command, building quantized HNSW indexes:

```bash
bin/run.sh io.anserini.index.IndexHnswDenseVectors \
  -threads 16 \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-openai-ada2 \
  -generator DenseVectorDocumentGenerator \
  -index indexes/lucene-hnsw-int8.msmarco-v1-passage.openai-ada2/ \
  -M 16 -efC 100 -quantize.int8 \
  >& logs/log.msmarco-passage-openai-ada2 &
```

The path `/path/to/msmarco-passage-openai-ada2/` should point to the corpus downloaded above.
Upon completion, we should have an index with 8,841,823 documents.

Note that here we are explicitly using Lucene's `NoMergePolicy` merge policy, which suppresses any merging of index segments.
This is because merging index segments is a costly operation and not worthwhile given our query set.
Furthermore, we are using Lucene's [Automatic Byte Quantization](https://www.elastic.co/search-labs/blog/articles/scalar-quantization-in-lucene) feature, which increase the on-disk footprint of the indexes since we're storing both the int8 quantized vectors and the float32 vectors, but only the int8 quantized vectors need to be loaded into memory.
See [issue #2292](https://github.com/castorini/anserini/issues/2292) for some experiments reporting the performance impact.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchHnswDenseVectors \
  -index indexes/lucene-hnsw-int8.msmarco-v1-passage.openai-ada2/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.openai-ada2.jsonl.gz \
  -topicReader JsonIntVector \
  -output runs/run.msmarco-passage-openai-ada2.openai-ada2-hnsw-int8-cached.topics.dl19-passage.openai-ada2.jsonl.txt \
  -generator VectorQueryGenerator -topicField vector -threads 16 -hits 1000 -efSearch 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-openai-ada2.openai-ada2-hnsw-int8-cached.topics.dl19-passage.openai-ada2.jsonl.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-openai-ada2.openai-ada2-hnsw-int8-cached.topics.dl19-passage.openai-ada2.jsonl.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-openai-ada2.openai-ada2-hnsw-int8-cached.topics.dl19-passage.openai-ada2.jsonl.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-openai-ada2.openai-ada2-hnsw-int8-cached.topics.dl19-passage.openai-ada2.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **OpenAI-ada2**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.479     |
| **nDCG@10**                                                                                                  | **OpenAI-ada2**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.703     |
| **R@100**                                                                                                    | **OpenAI-ada2**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.623     |
| **R@1000**                                                                                                   | **OpenAI-ada2**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.863     |

The above figures are from running brute-force search with cached queries on non-quantized **flat** indexes.
With cached queries on quantized HNSW indexes, observed results are likely to differ; scores may be lower by up to 0.01, sometimes more.
Note that both HNSW indexing and quantization are non-deterministic (i.e., results may differ slightly between trials).

❗ Retrieval metrics here are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
For computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl19-passage.openai-ada2.hnsw-int8.cached.template) and run `bin/build.sh` to rebuild the documentation.
