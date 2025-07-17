# Anserini Regressions: BRIGHT &mdash; Economics

This page documents BM25 regression experiments for [BRIGHT &mdash; Economics](https://brightbenchmark.github.io/).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/bright-economics.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/bright-economics.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and build Anserini to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression bright-economics
```

All the BRIGHT corpora are available for download:

```bash
wget https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-corpus.tar -P collections/
tar xvf collections/bright-corpus.tar -C collections/
```

The tarball is 297 MB and has MD5 checksum `d8c829f0e4468a8ce62768b6a1162158`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection JsonCollection \
  -input /path/to/bright-economics \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.bright-economics/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.bright-economics &
```

The path `/path/to/bright-economics/` should point to the corpus downloaded above.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-economics/ \
  -topics tools/topics-and-qrels/topics.bright-economics.tsv.gz \
  -topicReader TsvString \
  -output runs/run.bright-economics.bm25.topics.bright-economics.txt \
  -bm25 -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.bright-economics.txt runs/run.bright-economics.bm25.topics.bright-economics.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.bright-economics.txt runs/run.bright-economics.bm25.topics.bright-economics.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.bright-economics.txt runs/run.bright-economics.bm25.topics.bright-economics.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BRIGHT: Economics                                                                                            | 0.1645    |
| **R@100**                                                                                                    | **BM25**  |
| BRIGHT: Economics                                                                                            | 0.4077    |
| **R@1000**                                                                                                   | **BM25**  |
| BRIGHT: Economics                                                                                            | 0.6833    |
