# Anserini Regressions: BEIR (v1.0.0) &mdash; ArguAna

**Model**: uniCOIL without any expansions (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using uniCOIL (without any expansions) on [BEIR (v1.0.0) &mdash; ArguAna](http://beir.ai/).
The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

In these experiments, we are using cached queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-arguana.unicoil-noexp.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-arguana.unicoil-noexp.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.unicoil-noexp.cached
```

All the BEIR corpora, encoded by the uniCOIL-noexp model, are available for download:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-unicoil-noexp.tar -P collections/
tar xvf collections/beir-v1.0.0-unicoil-noexp.tar -C collections/
```

The tarball is 30 GB and has MD5 checksum `4fd04d2af816a6637fc12922cccc8a83`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-arguana.unicoil-noexp \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.beir-v1.0.0-arguana.unicoil-noexp/ \
  -impact -pretokenized \
  >& logs/log.beir-v1.0.0-arguana.unicoil-noexp &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.beir-v1.0.0-arguana.unicoil-noexp/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-arguana.test.unicoil-noexp.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-arguana.unicoil-noexp.unicoil-noexp-cached.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt \
  -impact -pretokenized -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-arguana.test.txt runs/run.beir-v1.0.0-arguana.unicoil-noexp.unicoil-noexp-cached.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-arguana.test.txt runs/run.beir-v1.0.0-arguana.unicoil-noexp.unicoil-noexp-cached.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-arguana.test.txt runs/run.beir-v1.0.0-arguana.unicoil-noexp.unicoil-noexp-cached.topics.beir-v1.0.0-arguana.test.unicoil-noexp.txt
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
