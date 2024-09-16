# Anserini Regressions: BEIR (v1.0.0) &mdash; Webis-Touche2020

This page documents BM25 regression experiments for [BEIR (v1.0.0) &mdash; Webis-Touche2020](http://beir.ai/).
These experiments index the corpus in a "flat" manner, by concatenating the "title" and "text" into the "contents" field.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-webis-touche2020.flat.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-webis-touche2020.flat.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.flat
```

All the BEIR corpora are available for download:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-corpus.tar -P collections/
tar xvf collections/beir-v1.0.0-corpus.tar -C collections/
```

The tarball is 14 GB and has MD5 checksum `faefd5281b662c72ce03d22021e4ff6b`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection BeirFlatCollection \
  -input /path/to/beir-v1.0.0-webis-touche2020.flat \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.beir-v1.0.0-webis-touche2020.flat/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.beir-v1.0.0-webis-touche2020.flat &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.beir-v1.0.0-webis-touche2020.flat/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-webis-touche2020.test.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-webis-touche2020.flat.bm25.topics.beir-v1.0.0-webis-touche2020.test.txt \
  -bm25 -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-webis-touche2020.test.txt runs/run.beir-v1.0.0-webis-touche2020.flat.bm25.topics.beir-v1.0.0-webis-touche2020.test.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-webis-touche2020.test.txt runs/run.beir-v1.0.0-webis-touche2020.flat.bm25.topics.beir-v1.0.0-webis-touche2020.test.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-webis-touche2020.test.txt runs/run.beir-v1.0.0-webis-touche2020.flat.bm25.topics.beir-v1.0.0-webis-touche2020.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): Webis-Touche2020                                                                              | 0.4422    |
| **R@100**                                                                                                    | **BM25**  |
| BEIR (v1.0.0): Webis-Touche2020                                                                              | 0.5822    |
| **R@1000**                                                                                                   | **BM25**  |
| BEIR (v1.0.0): Webis-Touche2020                                                                              | 0.8621    |
