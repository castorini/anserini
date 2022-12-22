# Anserini Regressions: MS MARCO Passage Ranking

**Model**: DistilSPLADE-max

This page describes regression experiments, integrated into Anserini's regression testing framework, using the DistilSPLADE-max model from SPLADEv2 on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).
The DistilSPLADE-max model is described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, Stéphane Clinchant. [SPLADE v2: Sparse Lexical and Expansion Model for Information Retrieval.](https://arxiv.org/abs/2109.10086) _arXiv:2109.10086_.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-distill-splade-max.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-distill-splade-max.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-distill-splade-max
```

We make available a version of the MS MARCO passage corpus that has already been processed with DistilSPLADE-max, i.e., performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-passage-distill-splade-max
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-distill-splade-max.tar -P collections/
tar xvf collections/msmarco-passage-distill-splade-max.tar -C collections/
```

To confirm, `msmarco-passage-distill-splade-max.tar` is 9.9 GB and has MD5 checksum `b5d126f5d9a8e1b3ef3f5cb0ba651725`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-distill-splade-max \
  --corpus-path collections/msmarco-passage-distill-splade-max
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-distill-splade-max \
  -index indexes/lucene-index.msmarco-passage-distill-splade-max/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-passage-distill-splade-max &
```

The path `/path/to/msmarco-passage-distill-splade-max/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADEv2 tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-distill-splade-max/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.distill-splade-max.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **DistilSPLADE-max**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3746    |
| **RR@10**                                                                                                    | **DistilSPLADE-max**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3686    |
| **R@100**                                                                                                    | **DistilSPLADE-max**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8984    |
| **R@1000**                                                                                                   | **DistilSPLADE-max**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9787    |

The above runs are in TREC output format and evaluated with `trec_eval`.
In order to reproduce results reported in the paper, we need to convert to MS MARCO output format and then evaluate:

```bash
python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.txt \
   --output runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.tsv --quiet

python tools/scripts/msmarco/msmarco_passage_eval.py \
   tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt \
   runs/run.msmarco-passage-distill-splade-max.distill-splade-max.topics.msmarco-passage.dev-subset.distill-splade-max.tsv
```

The results should be as follows:

```
#####################
MRR @10: 0.36852691363078205
QueriesRanked: 6980
#####################
```

This corresponds to the effectiveness reported in the paper.

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/msmarco-passage-distill-splade-max.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@jmmackenzie](https://github.com/jmmackenzie) on 2021-10-15 (commit [`52b76f6`](https://github.com/castorini/anserini/commit/52b76f63b163036e8fad1a6e1b10b431b4ddd06c))
+ Results reproduced by [@justram](https://github.com/justram) on 2022-03-02 (commit [`41b64d9`](https://github.com/castorini/anserini/commit/41b65d9fcb82d787faf4ca937f81faca82ead8c2))
+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-14 (commit [`dc07344`](https://github.com/castorini/anserini/commit/dc073447c8a0c07b53d979c49bf1e2e018200508))
