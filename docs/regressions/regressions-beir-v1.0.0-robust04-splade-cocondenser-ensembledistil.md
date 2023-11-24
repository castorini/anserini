# Anserini Regressions: BEIR (v1.0.0) &mdash; Robust04

**Model**: [SPLADE++ (CoCondenser-EnsembleDistil)](https://arxiv.org/abs/2205.04733)

This page describes regression experiments, integrated into Anserini's regression testing framework, using [SPLADE++ (CoCondenser-EnsembleDistil)](https://arxiv.org/abs/2205.04733) on [BEIR (v1.0.0) &mdash; Robust04](http://beir.ai/).
See the [official SPLADE repo](https://github.com/naver/splade) for more details; the model itself can be download [here](https://huggingface.co/naver/splade-cocondenser-ensembledistil).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-robust04-splade-cocondenser-ensembledistil.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-robust04-splade-cocondenser-ensembledistil.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search \
  --regression beir-v1.0.0-robust04-splade-cocondenser-ensembledistil
```

## Indexing

Sample indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-robust04-splade-cocondenser-ensembledistil \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.beir-v1.0.0-robust04-splade-cocondenser-ensembledistil/ \
  -threads 16 -impact -pretokenized -optimize \
  >& logs/log.beir-v1.0.0-robust04-splade-cocondenser-ensembledistil &
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-robust04-splade-cocondenser-ensembledistil/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-robust04.test.splade-cocondenser-ensembledistil.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-robust04-splade-cocondenser-ensembledistil.splade-cocondenser-ensembledistil.topics.beir-v1.0.0-robust04.test.splade-cocondenser-ensembledistil.txt \
  -impact -pretokenized -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-robust04.test.txt runs/run.beir-v1.0.0-robust04-splade-cocondenser-ensembledistil.splade-cocondenser-ensembledistil.topics.beir-v1.0.0-robust04.test.splade-cocondenser-ensembledistil.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-robust04.test.txt runs/run.beir-v1.0.0-robust04-splade-cocondenser-ensembledistil.splade-cocondenser-ensembledistil.topics.beir-v1.0.0-robust04.test.splade-cocondenser-ensembledistil.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-robust04.test.txt runs/run.beir-v1.0.0-robust04-splade-cocondenser-ensembledistil.splade-cocondenser-ensembledistil.topics.beir-v1.0.0-robust04.test.splade-cocondenser-ensembledistil.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **SPLADE++ (CoCondenser-EnsembleDistil)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): Robust04                                                                                      | 0.4679    |
| **R@100**                                                                                                    | **SPLADE++ (CoCondenser-EnsembleDistil)**|
| BEIR (v1.0.0): Robust04                                                                                      | 0.3850    |
| **R@1000**                                                                                                   | **SPLADE++ (CoCondenser-EnsembleDistil)**|
| BEIR (v1.0.0): Robust04                                                                                      | 0.6228    |


## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-robust04-splade-cocondenser-ensembledistil.template) and run `bin/build.sh` to rebuild the documentation.
