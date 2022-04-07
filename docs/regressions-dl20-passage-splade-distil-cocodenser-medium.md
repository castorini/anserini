# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Model**: SPLADE-distil CoCodenser Medium

This page describes regression experiments, integrated into Anserini's regression testing framework, using SPLADE-distil CoCodenser Medium on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html).
The SPLADE-distil CoCodenser Medium model is open-sourced by [Naver Labs Europe](https://europe.naverlabs.com/research/machine-learning-and-optimization/splade-models).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-passage-splade-distil-cocodenser-medium.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-passage-splade-distil-cocodenser-medium.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-splade-distil-cocodenser-medium
```

## Corpus

We make available a version of the MS MARCO passage corpus that has already been processed with the model (i.e., with infrerence applied to generate the lexical representations).
Thus, no neural inference is involved.
For details on how to train SPLADE-distil CoCodenser Medium and perform inference, please see [guide provided by Naver Labs Europe](https://github.com/naver/splade/tree/main/anserini_evaluation).

Download the corpus and unpack into `collections/`:

```
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-splade_distil_cocodenser_medium.tar -P collections/

tar xvf collections/msmarco-passage-splade_distil_cocodenser_medium.tar -C collections/
```

To confirm, `msmarco-passage-splade_distil_cocodenser_medium.tar` is 4.9 GB and has MD5 checksum `54a81e855a7678bc83ecb3ecf1ac5c1c`.

With the corpus downloaded, the following command will perform the complete regression, end to end, on any machine:

```
python src/main/python/run_regression.py --index --verify --search \
  --regression dl20-passage-splade-distil-cocodenser-medium \
  --corpus-path collections/msmarco-passage-splade_distil_cocodenser_medium
```

Alternatively, you can simply copy/paste from the commands below and obtain the same results.

## Indexing

Sample indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-splade_distil_cocodenser_medium \
  -index indexes/lucene-index.msmarco-passage-splade_distil_cocodenser_medium/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-passage-splade_distil_cocodenser_medium &
```

The path `/path/to/msmarco-passage-splade_distil_cocodenser_medium/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADE-distil CoCodenser Medium tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-splade_distil_cocodenser_medium/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.splade_distil_cocodenser_medium.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl20.splade_distil_cocodenser_medium.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl20.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl20.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl20.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.dl20.splade_distil_cocodenser_medium.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| AP@1000                                                                                                      | SPLADE-distill CoCodenser Medium|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.5019    |


| nDCG@10                                                                                                      | SPLADE-distill CoCodenser Medium|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7179    |


| R@100                                                                                                        | SPLADE-distill CoCodenser Medium|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7619    |


| R@1000                                                                                                       | SPLADE-distill CoCodenser Medium|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.8901    |

Note that retrieval metrics are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
Also, for computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/dl20-passage-splade-distil-cocodenser-medium.template) and run `bin/build.sh` to rebuild the documentation.
