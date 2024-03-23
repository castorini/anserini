# Anserini Regressions: MS MARCO Passage Ranking

**Model**: cosDPR-distil with inverted indexes using the "LexLSH" technique (b=600); pre-encoded queries

This page describes regression experiments, integrated into Anserini's regression testing framework, using the cosDPR-distil model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).
In these experiments, we are using pre-encoded queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-passage-cos-dpr-distil-lexlsh.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-passage-cos-dpr-distil-lexlsh.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-lexlsh
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with cosDPR-distil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-passage-cos-dpr-distil-lexlsh
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
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-lexlsh \
  --corpus-path collections/msmarco-passage-cos-dpr-distil
```

## Indexing

Sample indexing command, applying inverted indexes to dense vectors using the "LexLSH" technique:

```bash
target/appassembler/bin/IndexInvertedDenseVectors \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-cos-dpr-distil \
  -generator InvertedDenseVectorDocumentGenerator \
  -index indexes/lucene-index.msmarco-passage-cos-dpr-distil.lexlsh-600/ \
  -threads 16 -encoding lexlsh -lexlsh.b 600 \
  >& logs/log.msmarco-passage-cos-dpr-distil &
```

The path `/path/to/msmarco-passage-cos-dpr-distil/` should point to the corpus downloaded above.

Upon completion, we should have an index with 8,841,823 documents.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows using HNSW indexes:

```bash
target/appassembler/bin/SearchInvertedDenseVectors \
  -index indexes/lucene-index.msmarco-passage-cos-dpr-distil.lexlsh-600/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.gz \
  -topicReader JsonIntVector \
  -output runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-lexlsh-600-cached_q.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt \
  -topicField vector -threads 16 -encoding lexlsh -lexlsh.b 600 -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
target/appassembler/bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-lexlsh-600-cached_q.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
target/appassembler/bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-lexlsh-600-cached_q.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-lexlsh-600-cached_q.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-lexlsh-600-cached_q.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **cosDPR-distill**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3509    |
| **RR@10**                                                                                                    | **cosDPR-distill**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3457    |
| **R@100**                                                                                                    | **cosDPR-distill**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8615    |
| **R@1000**                                                                                                   | **cosDPR-distill**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9596    |

Note that due to the non-deterministic nature of HNSW indexing, results may differ slightly between each experimental run.
Nevertheless, scores are generally within 0.005 of the reference values recorded in [our YAML configuration file](../../src/main/resources/regression/msmarco-passage-cos-dpr-distil-lexlsh.yaml).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-passage-cos-dpr-distil-lexlsh.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@yilinjz](https://github.com/yilinjz) on 2023-09-01 (commit [`4ae518b`](https://github.com/castorini/anserini/commit/4ae518bb284ebcba0b273a473bc8774735cb7d19))