# Anserini Regressions: BEIR (v1.0.0) &mdash; ArguAna

**Model**: uniCOIL (without any expansions)

This page describes regression experiments, integrated into Anserini's regression testing framework, using uniCOIL (without any expansions) on [BEIR (v1.0.0) &mdash; ArguAna](http://beir.ai/).
The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/beir-v1.0.0-arguana-unicoil-noexp.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/beir-v1.0.0-arguana-unicoil-noexp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-unicoil-noexp
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-arguana-unicoil-noexp \
  -index indexes/lucene-index.beir-v1.0.0-arguana-unicoil-noexp/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.beir-v1.0.0-arguana-unicoil-noexp &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-arguana-unicoil-noexp/ \
  -topics src/main/resources/topics-and-qrels/topics.beir-v1.0.0-arguana.test.unicoil-noexp.tsv.gz \
  -topicreader TsvString \
  -output runs/run.beir-v1.0.0-arguana-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt \
  -impact -pretokenized -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-arguana.test.txt runs/run.beir-v1.0.0-arguana-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-arguana.test.txt runs/run.beir-v1.0.0-arguana-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-arguana.test.txt runs/run.beir-v1.0.0-arguana-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **uniCOIL no expansion**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): ArguAna                                                                                       | 0.3959    |
| **R@100**                                                                                                    | **uniCOIL no expansion**|
| BEIR (v1.0.0): ArguAna                                                                                       | 0.9225    |
| **R@1000**                                                                                                   | **uniCOIL no expansion**|
| BEIR (v1.0.0): ArguAna                                                                                       | 0.9794    |
