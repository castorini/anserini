# Anserini Regressions: MS MARCO Passage Ranking

**Model**: SPLADE-distil CoCodenser Medium

This page describes regression experiments, integrated into Anserini's regression testing framework, using the SPLADE-distil CoCodenser Medium model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).
SPLADE-distil CoCodenser Medium is an intermediate model version between [SPLADEv2](https://arxiv.org/abs/2109.10086) and [SPLADE++](https://arxiv.org/abs/2205.04733), where the model used distillation (as in SPLADEv2), but started with the CoCondenser pre-trained model.
See the [official SPLADE repo](https://github.com/naver/splade) for more details; the model itself can be download [here](http://download-de.europe.naverlabs.com/Splade_Release_Jan22/splade_distil_CoCodenser_medium.tar.gz).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-passage-splade-distil-cocodenser-medium.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-passage-splade-distil-cocodenser-medium.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-splade-distil-cocodenser-medium
```

We make available a version of the MS MARCO Passage Corpus that has already been processed with SPLADE-distil CoCodenser Medium, i.e., performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-passage-splade-distil-cocodenser-medium
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-splade_distil_cocodenser_medium.tar -P collections/
tar xvf collections/msmarco-passage-splade_distil_cocodenser_medium.tar -C collections/
```

To confirm, `msmarco-passage-splade_distil_cocodenser_medium.tar` is 4.9 GB and has MD5 checksum `f77239a26d08856e6491a34062893b0c`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-splade-distil-cocodenser-medium \
  --corpus-path collections/msmarco-passage-splade_distil_cocodenser_medium
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-splade_distil_cocodenser_medium \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-passage-splade_distil_cocodenser_medium/ \
  -threads 16 -impact -pretokenized -storeDocvectors \
  >& logs/log.msmarco-passage-splade_distil_cocodenser_medium &
```

The path `/path/to/msmarco-passage-splade_distil_cocodenser_medium/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade_distil_cocodenser_medium/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.splade_distil_cocodenser_medium.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.msmarco-passage.dev-subset.splade_distil_cocodenser_medium.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.msmarco-passage.dev-subset.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.msmarco-passage.dev-subset.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.msmarco-passage.dev-subset.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.msmarco-passage.dev-subset.splade_distil_cocodenser_medium.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **SPLADE-distill CoCodenser Medium**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3943    |
| **RR@10**                                                                                                    | **SPLADE-distill CoCodenser Medium**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3892    |
| **R@100**                                                                                                    | **SPLADE-distill CoCodenser Medium**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9111    |
| **R@1000**                                                                                                   | **SPLADE-distill CoCodenser Medium**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9817    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-passage-splade-distil-cocodenser-medium.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-14 (commit [`dc07344`](https://github.com/castorini/anserini/commit/dc073447c8a0c07b53d979c49bf1e2e018200508))
