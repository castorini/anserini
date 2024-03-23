# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Model**: SPLADE++ CoCondenser-EnsembleDistil (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [SPLADE++ CoCondenser-EnsembleDistil](https://huggingface.co/naver/splade-cocondenser-ensembledistil) model on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html), as described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

In these experiments, we are using ONNX to perform query encoding on the fly.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl20-passage-splade-pp-ed-onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl20-passage-splade-pp-ed-onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-splade-pp-ed-onnx
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with SPLADE++ CoCondenser-EnsembleDistil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl20-passage-splade-pp-ed-onnx
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
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-splade-pp-ed-onnx \
  --corpus-path collections/msmarco-passage-splade-pp-ed
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-splade-pp-ed \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-passage-splade-pp-ed/ \
  -threads 16 -impact -pretokenized -storeDocvectors \
  >& logs/log.msmarco-passage-splade-pp-ed &
```

The path `/path/to/msmarco-passage-splade-pp-ed/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADE-distil CoCodenser Medium tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl20.txt \
  -impact -pretokenized -encoder SpladePlusPlusEnsembleDistil &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl20.txt \
  -impact -pretokenized -rm3 -encoder SpladePlusPlusEnsembleDistil &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl20.txt \
  -impact -pretokenized -rocchio -encoder SpladePlusPlusEnsembleDistil &
```

Evaluation can be performed using `trec_eval`:

```bash
target/appassembler/bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl20.txt
target/appassembler/bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl20.txt
target/appassembler/bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl20.txt
target/appassembler/bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl20.txt

target/appassembler/bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl20.txt
target/appassembler/bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl20.txt
target/appassembler/bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl20.txt
target/appassembler/bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl20.txt

target/appassembler/bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl20.txt
target/appassembler/bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl20.txt
target/appassembler/bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl20.txt
target/appassembler/bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.4999    | 0.5098    | 0.5084    |
| **nDCG@10**                                                                                                  | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7197    | 0.7145    | 0.7280    |
| **R@100**                                                                                                    | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7653    | 0.7555    | 0.7704    |
| **R@1000**                                                                                                   | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.8998    | 0.9046    | 0.9069    |

Note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl20-passage-splade-pp-ed-onnx.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@cadurosar](https://github.com/cadurosar) on 2023-06-01 (commit [`70ea75`](https://github.com/castorini/anserini/commit/70ea75314ba570001eb68134f2185b55f6c66044))
+ Results reproduced by [@ArthurChen189](https://github.com/ArthurChen189) on 2023-06-01 (commit [`a403a2a`](https://github.com/castorini/anserini/commit/a403a2a44af9322c7a2dbdb5240180a62398ab06))
