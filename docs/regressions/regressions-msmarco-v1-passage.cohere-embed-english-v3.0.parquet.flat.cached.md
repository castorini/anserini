# Anserini Regressions: MS MARCO Passage Ranking

**Model**: [Cohere embed-english-v3.0](https://docs.cohere.com/reference/embed) with flat indexes (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [Cohere embed-english-v3.0](https://docs.cohere.com/reference/embed) model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).

In these experiments, we are using cached queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with Cohere embed-english-v3.0.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-cohere-embed-english-v3.0.parquet.tar -P collections/
tar xvf collections/msmarco-passage-cohere-embed-english-v3.0.parquet.tar -C collections/
```

To confirm, `msmarco-passage-cohere-embed-english-v3.0.parquet.tar` is 16 GB and has MD5 checksum `760dfb5ba9e2b0cc6f7e527e518fef03`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached \
  --corpus-path collections/msmarco-passage-cohere-embed-english-v3.0.parquet
```

## Indexing

Sample indexing command, building flat indexes:

```bash
bin/run.sh io.anserini.index.IndexFlatDenseVectors \
  -threads 16 \
  -collection ParquetDenseVectorCollection \
  -input /path/to/msmarco-passage-cohere-embed-english-v3.0.parquet \
  -generator ParquetDenseVectorDocumentGenerator \
  -index indexes/lucene-flat.msmarco-v1-passage.cohere-embed-english-v3.0/ \
  >& logs/log.msmarco-passage-cohere-embed-english-v3.0.parquet &
```

The path `/path/to/msmarco-passage-cohere-embed-english-v3.0.parquet/` should point to the corpus downloaded above.
Upon completion, we should have an index with 8,841,823 documents.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows using HNSW indexes:

```bash
bin/run.sh io.anserini.search.SearchFlatDenseVectors \
  -index indexes/lucene-flat.msmarco-v1-passage.cohere-embed-english-v3.0/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.gz \
  -topicReader JsonIntVector \
  -output runs/run.msmarco-passage-cohere-embed-english-v3.0.parquet.cohere-embed-english-v3.0-flat-cached.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt \
  -hits 1000 -threads 16 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.parquet.cohere-embed-english-v3.0-flat-cached.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.parquet.cohere-embed-english-v3.0-flat-cached.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.parquet.cohere-embed-english-v3.0-flat-cached.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cohere-embed-english-v3.0.parquet.cohere-embed-english-v3.0-flat-cached.topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **cohere-embed-english-v3.0**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3716    |
| **RR@10**                                                                                                    | **cohere-embed-english-v3.0**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3658    |
| **R@100**                                                                                                    | **cohere-embed-english-v3.0**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8935    |
| **R@1000**                                                                                                   | **cohere-embed-english-v3.0**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9786    |

Note that since we're running brute-force search with cached queries on non-quantized indexes, the results should be reproducible _exactly_.
