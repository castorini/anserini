# Anserini Regressions: BEIR (v1.0.0) &mdash; TREC-NEWS

**Model**: SPLADE-distil CoCodenser Medium

This page describes regression experiments, integrated into Anserini's regression testing framework, using SPLADE-distil CoCodenser Medium on [BEIR (v1.0.0) &mdash; TREC-NEWS](http://beir.ai/).
SPLADE-distil CoCodenser Medium is an intermediate model version between [SPLADEv2](https://arxiv.org/abs/2109.10086) and [SPLADE++](https://arxiv.org/abs/2205.04733), where the model used distillation (as in SPLADEv2), but started with the CoCondenser pre-trained model.
See the [official SPLADE repo](https://github.com/naver/splade) for more details; the model itself can be download [here](http://download-de.europe.naverlabs.com/Splade_Release_Jan22/splade_distil_CoCodenser_medium.tar.gz).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-trec-news-splade-distil-cocodenser-medium.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-trec-news-splade-distil-cocodenser-medium.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search \
  --regression beir-v1.0.0-trec-news-splade-distil-cocodenser-medium
```

## Corpus

We make available a version of the BEIR-v1.0.0 trec-news corpus that has already been processed with SPLADE-distil CoCodenser Medium, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.
For details on how to train SPLADE-distil CoCodenser Medium and perform inference, please see [guide provided by Naver Labs Europe](https://github.com/naver/splade/tree/main/anserini_evaluation).

Download the corpus and unpack into `collections/`:

```
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/beir-v1.0.0-splade_distil_cocodenser_medium-trec-news.tar -P collections/
tar xvf collections/beir-v1.0.0-splade_distil_cocodenser_medium-trec-news.tar -C collections/
```

To confirm, the tarball is 8.9 MB and has MD5 checksum `9c5a181e03cbc7f13abd0e0e4bf9158e`.

With the corpus downloaded, the following command will perform the complete regression, end to end, on any machine:

```
python src/main/python/run_regression.py --index --verify --search \
  --regression beir-v1.0.0-trec-news-splade-distil-cocodenser-medium \
  --corpus-path collections/beir-v1.0.0-splade_distil_cocodenser_medium-trec-news
```

Alternatively, you can simply copy/paste from the commands below and obtain the same results.

## Indexing

Sample indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-trec-news-splade_distil_cocodenser_medium \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.beir-v1.0.0-trec-news-splade_distil_cocodenser_medium/ \
  -threads 16 -impact -pretokenized \
  >& logs/log.beir-v1.0.0-trec-news-splade_distil_cocodenser_medium &
```

The path `/path/to/beir-v1.0.0-trec-news-splade_distil_cocodenser_medium/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
Upon completion, we should have an index with 8,674 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-trec-news-splade_distil_cocodenser_medium/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-trec-news.test.splade_distil_cocodenser_medium.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-trec-news-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-trec-news.test.splade_distil_cocodenser_medium.txt \
  -impact -pretokenized -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-trec-news.test.txt runs/run.beir-v1.0.0-trec-news-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-trec-news.test.splade_distil_cocodenser_medium.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-trec-news.test.txt runs/run.beir-v1.0.0-trec-news-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-trec-news.test.splade_distil_cocodenser_medium.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-trec-news.test.txt runs/run.beir-v1.0.0-trec-news-splade_distil_cocodenser_medium.splade_distil_cocodenser_medium.topics.beir-v1.0.0-trec-news.test.splade_distil_cocodenser_medium.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **SPLADE-distill CoCodenser Medium**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): TREC-NEWS                                                                                     | 0.3936    |
| **R@100**                                                                                                    | **SPLADE-distill CoCodenser Medium**|
| BEIR (v1.0.0): TREC-NEWS                                                                                     | 0.4323    |
| **R@1000**                                                                                                   | **SPLADE-distill CoCodenser Medium**|
| BEIR (v1.0.0): TREC-NEWS                                                                                     | 0.6977    |


## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-trec-news-splade-distil-cocodenser-medium.template) and run `bin/build.sh` to rebuild the documentation.
