# Anserini Regressions: BEIR (v1.0.0) &mdash; Climate-FEVER

**Model**: [SPLADE++ CoCondenser-EnsembleDistil](https://arxiv.org/abs/2205.04733) (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using [SPLADE++ CoCondenser-EnsembleDistil](https://arxiv.org/abs/2205.04733) on [BEIR (v1.0.0) &mdash; Climate-FEVER](http://beir.ai/).
The model itself can be download [here](https://huggingface.co/naver/splade-cocondenser-ensembledistil).
See the [official SPLADE repo](https://github.com/naver/splade) and the following paper for more details:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

In these experiments, we are using cached queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-climate-fever.splade-pp-ed.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-climate-fever.splade-pp-ed.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.splade-pp-ed.cached
```

All the BEIR corpora, encoded by the SPLADE++ CoCondenser-EnsembleDistil model, are available for download:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-splade-pp-ed.tar -P collections/
tar xvf collections/beir-v1.0.0-splade-pp-ed.tar -C collections/
```

The tarball is 42 GB and has MD5 checksum `9c7de5b444a788c9e74c340bf833173b`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Sample indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-climate-fever.splade-pp-ed \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.beir-v1.0.0-climate-fever.splade-pp-ed/ \
  -impact -pretokenized \
  >& logs/log.beir-v1.0.0-climate-fever.splade-pp-ed &
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.beir-v1.0.0-climate-fever.splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-climate-fever.test.splade-pp-ed.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-climate-fever.splade-pp-ed.splade-pp-ed-cached.topics.beir-v1.0.0-climate-fever.test.splade-pp-ed.txt \
  -impact -pretokenized -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-climate-fever.test.txt runs/run.beir-v1.0.0-climate-fever.splade-pp-ed.splade-pp-ed-cached.topics.beir-v1.0.0-climate-fever.test.splade-pp-ed.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-climate-fever.test.txt runs/run.beir-v1.0.0-climate-fever.splade-pp-ed.splade-pp-ed-cached.topics.beir-v1.0.0-climate-fever.test.splade-pp-ed.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-climate-fever.test.txt runs/run.beir-v1.0.0-climate-fever.splade-pp-ed.splade-pp-ed-cached.topics.beir-v1.0.0-climate-fever.test.splade-pp-ed.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **SPLADE++ (CoCondenser-EnsembleDistil)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): Climate-FEVER                                                                                 | 0.2297    |
| **R@100**                                                                                                    | **SPLADE++ (CoCondenser-EnsembleDistil)**|
| BEIR (v1.0.0): Climate-FEVER                                                                                 | 0.5211    |
| **R@1000**                                                                                                   | **SPLADE++ (CoCondenser-EnsembleDistil)**|
| BEIR (v1.0.0): Climate-FEVER                                                                                 | 0.7183    |
