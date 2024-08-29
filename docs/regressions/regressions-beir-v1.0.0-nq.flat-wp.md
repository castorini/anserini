# Anserini Regressions: BEIR (v1.0.0) &mdash; NQ

This page documents BM25 regression experiments for [BEIR (v1.0.0) &mdash; NQ](http://beir.ai/).
These experiments index the corpus in a "flat" manner, by concatenating the "title" and "text" into the "contents" field.
All the documents and queries are pre-tokenized with `bert-base-uncased` tokenizer.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-nq.flat-wp.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-nq.flat-wp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.flat-wp
```

All the BEIR corpora, pre-tokenized with the `bert-base-uncased` tokenizer, are available for download:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-corpus-wp.tar -P collections/
tar xvf collections/beir-v1.0.0-corpus-wp.tar -C collections/
```

The tarball is 13 GB and has MD5 checksum `3cf8f3dcdcadd49362965dd4466e6ff2`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection BeirFlatCollection \
  -input /path/to/beir-v1.0.0-nq.flat-wp \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.beir-v1.0.0-nq.flat-wp/ \
  -storePositions -storeDocvectors -storeRaw -pretokenized \
  >& logs/log.beir-v1.0.0-nq.flat-wp &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.beir-v1.0.0-nq.flat-wp/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-nq.test.wp.tsv.gz \
  -topicReader TsvString \
  -output runs/run.beir-v1.0.0-nq.flat-wp.bm25.topics.beir-v1.0.0-nq.test.wp.txt \
  -bm25 -removeQuery -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-nq.test.txt runs/run.beir-v1.0.0-nq.flat-wp.bm25.topics.beir-v1.0.0-nq.test.wp.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-nq.test.txt runs/run.beir-v1.0.0-nq.flat-wp.bm25.topics.beir-v1.0.0-nq.test.wp.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-nq.test.txt runs/run.beir-v1.0.0-nq.flat-wp.bm25.topics.beir-v1.0.0-nq.test.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): NQ                                                                                            | 0.3046    |
| **R@100**                                                                                                    | **BM25**  |
| BEIR (v1.0.0): NQ                                                                                            | 0.7390    |
| **R@1000**                                                                                                   | **BM25**  |
| BEIR (v1.0.0): NQ                                                                                            | 0.8917    |
