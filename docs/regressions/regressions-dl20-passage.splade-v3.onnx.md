# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Model**: SPLADE-v3 (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [SPLADE-v3](https://huggingface.co/naver/splade-v3) model on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html), as described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

In these experiments, we are using ONNX to perform query encoding on the fly.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl20-passage.splade-v3.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl20-passage.splade-v3.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.splade-v3.onnx
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with SPLADE++ CoCondenser-SelfDistil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl20-passage.splade-v3.onnx
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-splade-v3.tar -P collections/
tar xvf collections/msmarco-passage-splade-v3.tar -C collections/
```

To confirm, `msmarco-passage-splade-v3.tar` is 4.8 GB and has MD5 checksum `b5fbe7c294bd0b1e18f773337f540670`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.splade-v3.onnx \
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

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADE-v3 tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.splade-v3/ \
  -topics tools/topics-and-qrels/topics.dl20.splade-v3.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-v3.splade-v3-onnx.topics.dl20.splade-v3.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.splade-v3/ \
  -topics tools/topics-and-qrels/topics.dl20.splade-v3.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rm3.topics.dl20.splade-v3.txt \
  -impact -pretokenized -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.splade-v3/ \
  -topics tools/topics-and-qrels/topics.dl20.splade-v3.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rocchio.topics.dl20.splade-v3.txt \
  -impact -pretokenized -rocchio &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx.topics.dl20.splade-v3.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx.topics.dl20.splade-v3.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx.topics.dl20.splade-v3.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx.topics.dl20.splade-v3.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rm3.topics.dl20.splade-v3.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rm3.topics.dl20.splade-v3.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rm3.topics.dl20.splade-v3.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rm3.topics.dl20.splade-v3.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rocchio.topics.dl20.splade-v3.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rocchio.topics.dl20.splade-v3.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rocchio.topics.dl20.splade-v3.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-v3.splade-v3-onnx+rocchio.topics.dl20.splade-v3.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **SPLADEv3**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.5402    | 0.5493    | 0.5456    |
| **nDCG@10**                                                                                                  | **SPLADEv3**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7522    | 0.7280    | 0.7487    |
| **R@100**                                                                                                    | **SPLADEv3**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7671    | 0.7685    | 0.7764    |
| **R@1000**                                                                                                   | **SPLADEv3**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.9039    | 0.9316    | 0.9258    |

❗ Retrieval metrics here are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
For computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2102.07662).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl20-passage.splade-v3.onnx.template) and run `bin/build.sh` to rebuild the documentation.
