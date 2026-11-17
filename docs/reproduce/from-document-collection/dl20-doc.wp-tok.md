# <img src="../../anserini-logo.png" height="30" /> TREC 2020 Deep Learning Track (Document)

**Anserini reproductions from raw documents**

+ **Models**: bag-of-words on complete documents using WordPiece tokenization (pre-tokenized using [`bert-base-uncased`](https://huggingface.co/bert-base-uncased) from Hugging Face)
+ **Config**: [`dl20-doc.wp-tok.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.wp-tok.yaml)
+ **Template**: [`dl20-doc.wp-tok.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/dl20-doc.wp-tok.template)

This page describes reproductions from the raw document collection on the [TREC 2020 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2020.html).
For additional instructions on working with MS MARCO document collection, refer to [this page](../../../docs/experiments-msmarco-doc.md).

In these experiments, we are using WordPiece tokenization (i.e., from BERT) with the [`bert-base-uncased`](https://huggingface.co/bert-base-uncased) tokenizer from Hugging Face, on a pre-tokenized version of the raw document collection.
In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/dl20-doc.wp-tok.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config dl20-doc.wp-tok
```

## Indexing

Typical indexing command:

```bash
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
See [this page](../../../docs/experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc.wp-tok/ \
  -topics dl20.wp \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v1-doc.wp-tok.model-bm25-default.topics-dl20.wp.txt \
  -bm25 -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m map dl20-doc runs/run.lucene-inverted.msmarco-v1-doc.wp-tok.model-bm25-default.topics-dl20.wp.txt
bin/trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.lucene-inverted.msmarco-v1-doc.wp-tok.model-bm25-default.topics-dl20.wp.txt
bin/trec_eval -c -m recall.100 dl20-doc runs/run.lucene-inverted.msmarco-v1-doc.wp-tok.model-bm25-default.topics-dl20.wp.txt
bin/trec_eval -c -m recall.1000 dl20-doc runs/run.lucene-inverted.msmarco-v1-doc.wp-tok.model-bm25-default.topics-dl20.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                             | **BM25 (default)** |
|:-------------------------------------------------------|:------------------:|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html) | 0.3258             |
| **nDCG@10**                                            | **BM25 (default)** |
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html) | 0.5046             |
| **R@100**                                              | **BM25 (default)** |
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html) | 0.5483             |
| **R@1000**                                             | **BM25 (default)** |
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html) | 0.7436             |
