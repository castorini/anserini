# Anserini Regressions: MS MARCO Passage Ranking

**Model**: cosDPR-distil (using pre-encoded queries) with HNSW indexes

This page describes regression experiments, integrated into Anserini's regression testing framework, using the cosDPR-distil model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking), as described in the following paper:

> Xueguang Ma, Tommaso Teofili, and Jimmy Lin. [Anserini Gets Dense Retrieval: Integration of Lucene's HNSW Indexes.](https://arxiv.org/abs/2304.12139) _arXiv:2304.12139_, 2023.

In these experiments, we are using pre-encoded queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-passage-cos-dpr-distil.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-passage-cos-dpr-distil.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with cosDPR-distil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-passage-cos-dpr-distil
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
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil \
  --corpus-path collections/msmarco-passage-cos-dpr-distil
```

## Indexing

Sample indexing command, building HNSW indexes:

```bash
target/appassembler/bin/IndexHnswDenseVectors \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-cos-dpr-distil \
  -index indexes/lucene-hnsw.msmarco-passage-cos-dpr-distil/ \
  -generator LuceneDenseVectorDocumentGenerator \
  -threads 16 -M 16 -efC 100 \
  >& logs/log.msmarco-passage-cos-dpr-distil &
```

The path `/path/to/msmarco-passage-cos-dpr-distil/` should point to the corpus downloaded above.

Upon completion, we should have an index with 8,841,823 documents.

<!-- For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md). -->

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows using HNSW indexes:

```bash
target/appassembler/bin/SearchHnswDenseVectors \
  -index indexes/lucene-hnsw.msmarco-passage-cos-dpr-distil/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.gz \
  -topicreader JsonIntVector \
  -output runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt \
  -querygenerator VectorQueryGenerator -topicfield vector -threads 16 -hits 1000 -efSearch 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil.topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **cosDPR-distil**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.392     |
| **RR@10**                                                                                                    | **cosDPR-distil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.387     |
| **R@100**                                                                                                    | **cosDPR-distil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.900     |
| **R@1000**                                                                                                   | **cosDPR-distil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.970     |

Note that due to the non-deterministic nature of HNSW indexing, results may differ slightly between each experimental run.
Nevertheless, scores are generally stable to the third digit after the decimal point.

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-passage-cos-dpr-distil.template) and run `bin/build.sh` to rebuild the documentation.
