# Anserini Regressions: MS MARCO Passage Ranking

**Model**: SPLADE-v3 (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using [SPLADE-v3](https://huggingface.co/naver/splade-v3) on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking), as described in the following paper:

> Carlos Lassance, Hervé Déjean, Thibault Formal, and Stéphane Clinchant. [SPLADE-v3: New baselines for SPLADE.](https://arxiv.org/abs/2403.06789) _arXiv:2403.06789_.

In these experiments, we are using cached queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v1-passage.splade-v3.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.splade-v3.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-v3.cached
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with SPLADE-v3.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-v1-passage.splade-v3.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-splade-v3.tar -P collections/
tar xvf collections/msmarco-passage-splade-v3.tar -C collections/
```

To confirm, `msmarco-passage-splade-v3.tar` is 7.4 GB and has MD5 checksum `b5fbe7c294bd0b1e18f773337f540670`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-v3.cached \
  --corpus-path collections/msmarco-passage-splade-v3
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-splade-v3 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.splade-v3/ \
  -impact -pretokenized -storeDocvectors \
  >& logs/log.msmarco-passage-splade-v3 &
```

The path `/path/to/msmarco-passage-splade-v3/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.splade-v3/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.splade-v3.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-v3.splade-v3-cached.topics.msmarco-passage.dev-subset.splade-v3.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-v3.splade-v3-cached.topics.msmarco-passage.dev-subset.splade-v3.txt
bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-v3.splade-v3-cached.topics.msmarco-passage.dev-subset.splade-v3.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-v3.splade-v3-cached.topics.msmarco-passage.dev-subset.splade-v3.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-v3.splade-v3-cached.topics.msmarco-passage.dev-subset.splade-v3.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **SPLADEv3**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.4049    |
| **RR@10**                                                                                                    | **SPLADEv3**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3999    |
| **R@100**                                                                                                    | **SPLADEv3**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9228    |
| **R@1000**                                                                                                   | **SPLADEv3**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9868    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.splade-v3.cached.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@clides](https://github.com/clides) on 2025-04-03 (commit [`e59e25a`](https://github.com/castorini/anserini/commit/e59e25a1853f901a513baf7e3ab41ec15fd6640e))
