# Anserini Regressions: TREC 2020 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on complete documents using Composite Tokenizer

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2020 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2020.html).
Here we are using Composite Tokenizer which combines **Lucene tokenization** with **WordPiece tokenization** (i.e., from BERT) using the following tokenizer from HuggingFace [`bert-base-uncased`](https://huggingface.co/bert-base-uncased).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-doc-composite-tokenizer.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-doc-composite-tokenizer.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-composite-tokenizer
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc \
  -index indexes/lucene-index.msmarco-doc-composite-tokenizer/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -storePositions -storeDocvectors -storeRaw -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeTokenizer \
  >& logs/log.msmarco-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the document corpus in Anserini's jsonl format.
See [this page](experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-composite-tokenizer/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default.topics.dl20.txt \
  -bm25 -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeTokenizer &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl20.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.3809    |
| **nDCG@10**                                                                                                  | **BM25 (default)**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.5422    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.6074    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.8147    |
