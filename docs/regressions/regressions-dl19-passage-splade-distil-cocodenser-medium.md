# Anserini Regressions: TREC 2019 Deep Learning Track (Passage)

**Model**: SPLADE-distil CoCodenser Medium

This page describes regression experiments, integrated into Anserini's regression testing framework, using SPLADE-distil CoCodenser Medium on the [TREC 2019 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html).
SPLADE-distil CoCodenser Medium is an intermediate model version between [SPLADEv2](https://arxiv.org/abs/2109.10086) and [SPLADE++](https://arxiv.org/abs/2205.04733), where the model used distillation (as in SPLADEv2), but started with the CoCondenser pre-trained model.
See the [official SPLADE repo](https://github.com/naver/splade) for more details; the model itself can be download [here](http://download-de.europe.naverlabs.com/Splade_Release_Jan22/splade_distil_CoCodenser_medium.tar.gz).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl19-passage-splade-distil-cocodenser-medium.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl19-passage-splade-distil-cocodenser-medium.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-splade-distil-cocodenser-medium
```

We make available a version of the MS MARCO Passage Corpus that has already been processed with SPLADE-distil CoCodenser Medium, i.e., performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl19-passage-splade-distil-cocodenser-medium
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
python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-splade-distil-cocodenser-medium \
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

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADE-distil CoCodenser Medium tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade_distil_cocodenser_medium/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.splade_distil_cocodenser_medium.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl19-passage.splade_distil_cocodenser_medium.txt \
  -impact -pretokenized &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade_distil_cocodenser_medium/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.splade_distil_cocodenser_medium.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade_distil_cocodenser_medium.rm3.topics.dl19-passage.splade_distil_cocodenser_medium.txt \
  -impact -pretokenized -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade_distil_cocodenser_medium/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.splade_distil_cocodenser_medium.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-splade_distil_cocodenser_medium.rocchio.topics.dl19-passage.splade_distil_cocodenser_medium.txt \
  -impact -pretokenized -rocchio &
```

Evaluation can be performed using `trec_eval`:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl19-passage.splade_distil_cocodenser_medium.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rm3.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rm3.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rm3.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rm3.topics.dl19-passage.splade_distil_cocodenser_medium.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rocchio.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rocchio.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rocchio.topics.dl19-passage.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.rocchio.topics.dl19-passage.splade_distil_cocodenser_medium.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **SPLADE-distill CoCodenser Medium**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.4970    | 0.5194    | 0.5224    |
| **nDCG@10**                                                                                                  | **SPLADE-distill CoCodenser Medium**| **+RM3**  | **+Rocchio**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7425    | 0.7261    | 0.7316    |
| **R@100**                                                                                                    | **SPLADE-distill CoCodenser Medium**| **+RM3**  | **+Rocchio**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.6344    | 0.6485    | 0.6533    |
| **R@1000**                                                                                                   | **SPLADE-distill CoCodenser Medium**| **+RM3**  | **+Rocchio**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.8756    | 0.8736    | 0.8774    |

Note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl19-passage-splade-distil-cocodenser-medium.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-14 (commit [`dc07344`](https://github.com/castorini/anserini/commit/dc073447c8a0c07b53d979c49bf1e2e018200508))
