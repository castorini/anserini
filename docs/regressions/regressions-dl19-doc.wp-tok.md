# Anserini Regressions: TREC 2019 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on complete documents with WordPiece tokenization

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2019 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2019.html).
Here we are using **WordPiece tokenization** (i.e., from BERT) on the entire document.
In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl19-doc.wp-tok.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl19-doc.wp-tok.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl19-doc.wp-tok
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 7 \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-wp \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-doc.wp-tok/ \
  -storePositions -storeDocvectors -storeRaw -pretokenized \
  >& logs/log.msmarco-doc-wp &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the document corpus in Anserini's jsonl format.
See [this page](../../docs/experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.wp-tok/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.wp.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-wp.bm25-default.topics.dl19-doc.wp.txt \
  -bm25 -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-wp.bm25-default.topics.dl19-doc.wp.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-wp.bm25-default.topics.dl19-doc.wp.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-wp.bm25-default.topics.dl19-doc.wp.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc-wp.bm25-default.topics.dl19-doc.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.1947    |
| **nDCG@10**                                                                                                  | **BM25 (default)**|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.4672    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.3400    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.6421    |
