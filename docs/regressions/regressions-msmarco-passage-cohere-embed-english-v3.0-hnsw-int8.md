# Anserini Regressions: MS MARCO Passage Ranking

**Model**: [Cohere embed-english-v3.0](https://docs.cohere.com/reference/embed) with HNSW quantized indexes (using pre-encoded queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [Cohere embed-english-v3.0](https://docs.cohere.com/reference/embed) model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).

In these experiments, we are using pre-encoded queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-passage-cohere-embed-english-v3.0-hnsw-int8.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-passage-cohere-embed-english-v3.0-hnsw-int8.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cohere-embed-english-v3.0-hnsw-int8
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with Cohere embed-english-v3.0.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-passage-cohere-embed-english-v3.0-hnsw-int8
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-cohere-embed-english-v3.0.tar -P collections/
tar xvf collections/msmarco-passage-cohere-embed-english-v3.0.tar -C collections/
```

To confirm, `msmarco-passage-cohere-embed-english-v3.0.tar` is 38 GB and has MD5 checksum `06a6e38a0522850c6aa504db7b2617f5`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cohere-embed-english-v3.0-hnsw-int8 \
  --corpus-path collections/msmarco-passage-cohere-embed-english-v3.0
```

## Indexing

Sample indexing command, building HNSW indexes:

```bash
target/appassembler/bin/IndexHnswDenseVectors \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-cohere-embed-english-v3.0 \
  -generator HnswDenseVectorDocumentGenerator \
  -index indexes/lucene-hnsw.msmarco-passage-cohere-embed-english-v3.0-int8/ \
  -threads 16 -M 16 -efC 100 -noMerge -quantize.int8 \
  >& logs/log.msmarco-passage-cohere-embed-english-v3.0 &
```

The path `/path/to/msmarco-passage-cohere-embed-english-v3.0/` should point to the corpus downloaded above.
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
target/appassembler/bin/SearchHnswDenseVectors \
  -index indexes/lucene-hnsw.msmarco-passage-cohere-embed-english-v3.0-int8/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.gz \
  -topicReader JsonIntVector \
  -output runs/run.msmarco-passage-cohere-embed-english-v3.0.cohere-embed-english-v3.0-cached_q.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt \
  -generator VectorQueryGenerator -topicField vector -threads 16 -hits 1000 -efSearch 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.cohere-embed-english-v3.0-cached_q.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
target/appassembler/bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.cohere-embed-english-v3.0-cached_q.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
target/appassembler/bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.cohere-embed-english-v3.0-cached_q.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.cohere-embed-english-v3.0-cached_q.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **cohere-embed-english-v3.0**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.427     |
| **AP@1000**                                                                                                  | **cohere-embed-english-v3.0**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.371     |
| **RR@10**                                                                                                    | **cohere-embed-english-v3.0**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.365     |
| **R@1000**                                                                                                   | **cohere-embed-english-v3.0**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.974     |

Note that due to the non-deterministic nature of HNSW indexing, results may differ slightly between each experimental run.
Nevertheless, scores are generally within 0.005 of the reference values recorded in [our YAML configuration file](../../src/main/resources/regression/msmarco-passage-cohere-embed-english-v3.0-hnsw-int8.yaml).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-passage-cohere-embed-english-v3.0-hnsw-int8.template) and run `bin/build.sh` to rebuild the documentation.
