# <img src="../../anserini-logo.png" height="30" /> TREC 2019 Deep Learning Track (Document)

**Anserini reproductions from document collection**

+ **Models**: bag-of-words on segmented documents using WordPiece tokenization (pre-tokenized using [`bert-base-uncased`](https://huggingface.co/bert-base-uncased) from Hugging Face)
+ **Config**: [`dl19-doc-segmented.wp-tok.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.wp-tok.yaml)
+ **Template**: [`dl19-doc-segmented.wp-tok.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/dl19-doc-segmented.wp-tok.template)

This page describes reproductions from the raw document collection on the [TREC 2019 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2019.html).
For additional instructions on working with MS MARCO document collection, refer to [this page](../../../docs/experiments-msmarco-doc.md).

In these experiments, we are using WordPiece tokenization (i.e., from BERT) with the [`bert-base-uncased`](https://huggingface.co/bert-base-uncased) tokenizer from Hugging Face, on a pre-tokenized version of the raw document collection.
At retrieval time, we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking; this is known as the MaxP technique.
In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/dl19-doc-segmented.wp-tok.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config dl19-doc-segmented.wp-tok
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonCollection \
  -input /path/to/msmarco-doc-segmented-wp \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented.wp-tok/ \
  -storePositions -storeDocvectors -storeRaw -pretokenized \
  >& logs/log.msmarco-doc-segmented-wp &
```

The directory `/path/to/msmarco-doc-segmented/` should be a directory containing the segmented corpus in Anserini's jsonl format.
See [this page](../../../docs/experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented.wp-tok/ \
  -topics dl19-doc.wp \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v1-doc-segmented.wp-tok.model-bm25-default.topics-dl19-doc.wp.txt \
  -bm25 -pretokenized -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m map dl19-doc runs/run.lucene-inverted.msmarco-v1-doc-segmented.wp-tok.model-bm25-default.topics-dl19-doc.wp.txt
bin/trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.lucene-inverted.msmarco-v1-doc-segmented.wp-tok.model-bm25-default.topics-dl19-doc.wp.txt
bin/trec_eval -c -m recall.100 dl19-doc runs/run.lucene-inverted.msmarco-v1-doc-segmented.wp-tok.model-bm25-default.topics-dl19-doc.wp.txt
bin/trec_eval -c -m recall.1000 dl19-doc runs/run.lucene-inverted.msmarco-v1-doc-segmented.wp-tok.model-bm25-default.topics-dl19-doc.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                             | **BM25 (default)** |
|:-------------------------------------------------------|:------------------:|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html) | 0.2269             |
| **nDCG@10**                                            | **BM25 (default)** |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html) | 0.5206             |
| **R@100**                                              | **BM25 (default)** |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html) | 0.3708             |
| **R@1000**                                             | **BM25 (default)** |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html) | 0.6476             |
