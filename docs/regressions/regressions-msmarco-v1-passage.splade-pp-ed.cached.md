# Anserini Regressions: MS MARCO Passage Ranking

**Model**: SPLADE++ CoCondenser-EnsembleDistil (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [SPLADE++ CoCondenser-EnsembleDistil](https://huggingface.co/naver/splade-cocondenser-ensembledistil) model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking), as described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

In these experiments, we are using cached queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v1-passage.splade-pp-ed.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.splade-pp-ed.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-pp-ed.cached
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with SPLADE++ CoCondenser-EnsembleDistil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-v1-passage.splade-pp-ed.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-splade-pp-ed.tar -P collections/
tar xvf collections/msmarco-passage-splade-pp-ed.tar -C collections/
```

To confirm, `msmarco-passage-splade-pp-ed.tar` is 4.2 GB and has MD5 checksum `e489133bdc54ee1e7c62a32aa582bc77`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-pp-ed.cached \
  --corpus-path collections/msmarco-passage-splade-pp-ed
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-splade-pp-ed \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.splade-pp-ed/ \
  -impact -pretokenized -storeDocvectors \
  >& logs/log.msmarco-passage-splade-pp-ed &
```

The path `/path/to/msmarco-passage-splade-pp-ed/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.splade-pp-ed.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-cached.topics.msmarco-passage.dev-subset.splade-pp-ed.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-cached.topics.msmarco-passage.dev-subset.splade-pp-ed.txt
bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-cached.topics.msmarco-passage.dev-subset.splade-pp-ed.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-cached.topics.msmarco-passage.dev-subset.splade-pp-ed.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-cached.topics.msmarco-passage.dev-subset.splade-pp-ed.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **SPLADE++ CoCondenser-EnsembleDistil**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3884    |
| **RR@10**                                                                                                    | **SPLADE++ CoCondenser-EnsembleDistil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3830    |
| **R@100**                                                                                                    | **SPLADE++ CoCondenser-EnsembleDistil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9095    |
| **R@1000**                                                                                                   | **SPLADE++ CoCondenser-EnsembleDistil**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9831    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.splade-pp-ed.cached.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@justram](https://github.com/justram) on 2023-03-08 (commit [`03f95a8`](https://github.com/castorini/anserini/commit/03f95a8e1ae09ab09efe046bfcbd3a4cdda691b4))
+ Results reproduced by [@ArthurChen189](https://github.com/ArthurChen189) on 2023-06-01 (commit [`a403a2a`](https://github.com/castorini/anserini/commit/a403a2a44af9322c7a2dbdb5240180a62398ab06))