# Anserini Regressions: TREC 2019 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on complete documents using `CompositeAnalyzer`

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2019 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2019.html).
Here we are using `CompositeAnalyzer` which combines **Lucene tokenization** with **WordPiece tokenization** (i.e., from BERT) using the following tokenizer from HuggingFace [`bert-base-uncased`](https://huggingface.co/bert-base-uncased).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl19-doc.wp-ca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl19-doc.wp-ca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl19-doc.wp-ca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 9 \
  -collection JsonCollection \
  -input /path/to/msmarco-doc \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-doc.wp-ca/ \
  -storePositions -storeDocvectors -storeRaw -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeAnalyzer \
  >& logs/log.msmarco-doc &
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
  -index indexes/lucene-inverted.msmarco-v1-doc.wp-ca/ \
  -topics tools/topics-and-qrels/topics.dl19-doc.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt \
  -bm25 -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.2476    |
| **nDCG@10**                                                                                                  | **BM25 (default)**|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.5127    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.3993    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.7073    |
