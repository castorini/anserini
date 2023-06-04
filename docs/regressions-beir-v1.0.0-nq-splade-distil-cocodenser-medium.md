# Anserini Regressions: BEIR (v1.0.0) &mdash; NQ

**Model**: SPLADE-distil CoCodenser Medium

This page describes regression experiments, integrated into Anserini's regression testing framework, using SPLADE-distil CoCodenser Medium on [BEIR (v1.0.0) &mdash; NQ](http://beir.ai/).
The SPLADE-distil CoCodenser Medium model is open-sourced by [Naver Labs Europe](https://europe.naverlabs.com/research/machine-learning-and-optimization/splade-models).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/beir-v1.0.0-nq-splade-distil-cocodenser-medium.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/beir-v1.0.0-nq-splade-distil-cocodenser-medium.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search \
  --regression beir-v1.0.0-nq-splade-distil-cocodenser-medium
```

## Corpus

We make available a version of the BEIR-v1.0.0 nq corpus that has already been processed with SPLADE-distil CoCodenser Medium, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.
For details on how to train SPLADE-distil CoCodenser Medium and perform inference, please see [guide provided by Naver Labs Europe](https://github.com/naver/splade/tree/main/anserini_evaluation).

Download the corpus and unpack into `collections/`:

```
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/beir-v1.0.0-splade_distil_cocodenser_medium-nq.tar -P collections/
tar xvf collections/beir-v1.0.0-splade_distil_cocodenser_medium-nq.tar -C collections/
```

To confirm, the tarball is 1.9 GB and has MD5 checksum `ec9f0d3245c7200209c4dd9ec19055a9`.

With the corpus downloaded, the following command will perform the complete regression, end to end, on any machine:

```
python src/main/python/run_regression.py --index --verify --search \
  --regression beir-v1.0.0-nq-splade-distil-cocodenser-medium \
  --corpus-path collections/beir-v1.0.0-splade_distil_cocodenser_medium-nq
```

Alternatively, you can simply copy/paste from the commands below and obtain the same results.

## Indexing

Sample indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-nq-splade_distil_cocodenser_medium \
  -index indexes/lucene-index.beir-v1.0.0-nq-splade_distil_cocodenser_medium/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.beir-v1.0.0-nq-splade_distil_cocodenser_medium &
```

The path `/path/to/beir-v1.0.0-nq-splade_distil_cocodenser_medium/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
Upon completion, we should have an index with 2,681,468 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-nq-splade_distil_cocodenser_medium/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-nq.test.splade_distil_cocodenser_medium.tsv.gz \
  -topicreader TsvString \
  -output runs/run.beir-v1.0.0-nq-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-nq.test.splade_distil_cocodenser_medium.txt \
  -impact -pretokenized -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-nq.test.txt runs/run.beir-v1.0.0-nq-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-nq.test.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-nq.test.txt runs/run.beir-v1.0.0-nq-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-nq.test.splade_distil_cocodenser_medium.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-nq.test.txt runs/run.beir-v1.0.0-nq-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-nq.test.splade_distil_cocodenser_medium.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **SPLADE-distill CoCodenser Medium**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): NQ                                                                                            | 0.5442    |
| **R@100**                                                                                                    | **SPLADE-distill CoCodenser Medium**|
| BEIR (v1.0.0): NQ                                                                                            | 0.9285    |
| **R@1000**                                                                                                   | **SPLADE-distill CoCodenser Medium**|
| BEIR (v1.0.0): NQ                                                                                            | 0.9812    |


## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/beir-v1.0.0-nq-splade-distil-cocodenser-medium.template) and run `bin/build.sh` to rebuild the documentation.
