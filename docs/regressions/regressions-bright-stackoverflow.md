# Anserini Regressions: BRIGHT &mdash; Stack Overflow

This page documents BM25 regression experiments for [BRIGHT &mdash; Stack Overflow](https://brightbenchmark.github.io/).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/bright-stackoverflow.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/bright-stackoverflow.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and build Anserini to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression bright-stackoverflow
```

All the BRIGHT corpora are available for download:

```bash
wget https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-corpus.tar -P collections/
tar xvf collections/bright-corpus.tar -C collections/
```

The tarball is 284 MB and has MD5 checksum `568b594709a9977369033117bfb6889c`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection JsonCollection \
  -input /path/to/bright-stackoverflow \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.bright-stackoverflow/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.bright-stackoverflow &
```

The path `/path/to/bright-stackoverflow/` should point to the corpus downloaded above.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-stackoverflow/ \
  -topics tools/topics-and-qrels/topics.bright-stackoverflow.tsv.gz \
  -topicReader TsvString \
  -output runs/run.bright-stackoverflow.bm25.topics.bright-stackoverflow.txt \
  -bm25 -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.bright-stackoverflow.txt runs/run.bright-stackoverflow.bm25.topics.bright-stackoverflow.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.bright-stackoverflow.txt runs/run.bright-stackoverflow.bm25.topics.bright-stackoverflow.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.bright-stackoverflow.txt runs/run.bright-stackoverflow.bm25.topics.bright-stackoverflow.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BRIGHT: Stack Overflow                                                                                       | 0.1626    |
| **R@100**                                                                                                    | **BM25**  |
| BRIGHT: Stack Overflow                                                                                       | 0.4093    |
| **R@1000**                                                                                                   | **BM25**  |
| BRIGHT: Stack Overflow                                                                                       | 0.6735    |
