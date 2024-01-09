# Anserini Regressions: BEIR (v1.0.0) &mdash; FiQA-2018

**Model**: uniCOIL (without any expansions)

This page describes regression experiments, integrated into Anserini's regression testing framework, using uniCOIL (without any expansions) on [BEIR (v1.0.0) &mdash; FiQA-2018](http://beir.ai/).
The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-fiqa-unicoil-noexp.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-fiqa-unicoil-noexp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-unicoil-noexp
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-fiqa-unicoil-noexp \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.beir-v1.0.0-fiqa-unicoil-noexp/ \
  -threads 16 -impact -pretokenized \
  >& logs/log.beir-v1.0.0-fiqa-unicoil-noexp &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-fiqa-unicoil-noexp/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-fiqa.test.unicoil-noexp.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-fiqa-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-fiqa.test.unicoil-noexp.txt \
  -impact -pretokenized -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
java -jar target/trec_eval.jar -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-fiqa.test.txt runs/run.beir-v1.0.0-fiqa-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-fiqa.test.unicoil-noexp.txt
java -jar target/trec_eval.jar -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-fiqa.test.txt runs/run.beir-v1.0.0-fiqa-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-fiqa.test.unicoil-noexp.txt
java -jar target/trec_eval.jar -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-fiqa.test.txt runs/run.beir-v1.0.0-fiqa-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-fiqa.test.unicoil-noexp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **uniCOIL no expansion**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): FiQA-2018                                                                                     | 0.2888    |
| **R@100**                                                                                                    | **uniCOIL no expansion**|
| BEIR (v1.0.0): FiQA-2018                                                                                     | 0.5533    |
| **R@1000**                                                                                                   | **uniCOIL no expansion**|
| BEIR (v1.0.0): FiQA-2018                                                                                     | 0.7465    |
