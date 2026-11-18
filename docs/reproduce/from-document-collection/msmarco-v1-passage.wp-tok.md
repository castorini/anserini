# <img src="../../anserini-logo.png" height="30" /> MS MARCO Passage Ranking

**Anserini reproductions from document collection**

+ **Models**: bag-of-words using WordPiece tokenization (pre-tokenized using [`bert-base-uncased`](https://huggingface.co/bert-base-uncased) from Hugging Face)
+ **Config**: [`msmarco-v1-passage.wp-tok.yaml`](../../../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.wp-tok.yaml)
+ **Template**: [`msmarco-v1-passage.wp-tok.template`](../../../src/main/resources/reproduce/from-document-collection/docgen/msmarco-v1-passage.wp-tok.template)

This page describes reproductions from the raw document collection on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).

In these experiments, we are using WordPiece tokenization (i.e., from BERT) with the [`bert-base-uncased`](https://huggingface.co/bert-base-uncased) tokenizer from Hugging Face, on a pre-tokenized version of the raw document collection.
In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

This page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/msmarco-v1-passage.wp-tok.template) by the `GenerateReproductionDocsFromDocumentCollectionTest` JUnit test case, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete reproduction, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config msmarco-v1-passage.wp-tok
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 9 \
  -collection JsonCollection \
  -input /path/to/msmarco-passage-wp \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.wp-tok/ \
  -storePositions -storeDocvectors -storeRaw -pretokenized \
  >& logs/log.msmarco-passage-wp &
```

The directory `/path/to/msmarco-passage-wp/` should be a directory containing the corpus in Anserini's jsonl format.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.wp-tok/ \
  -topics msmarco-passage.dev-subset.wp \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v1-passage.wp-tok.model-bm25-default.topics-msmarco-passage.dev-subset.wp.txt \
  -bm25 -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m map msmarco-passage.dev-subset runs/run.lucene-inverted.msmarco-v1-passage.wp-tok.model-bm25-default.topics-msmarco-passage.dev-subset.wp.txt
bin/trec_eval -c -M 10 -m recip_rank msmarco-passage.dev-subset runs/run.lucene-inverted.msmarco-v1-passage.wp-tok.model-bm25-default.topics-msmarco-passage.dev-subset.wp.txt
bin/trec_eval -c -m recall.100 msmarco-passage.dev-subset runs/run.lucene-inverted.msmarco-v1-passage.wp-tok.model-bm25-default.topics-msmarco-passage.dev-subset.wp.txt
bin/trec_eval -c -m recall.1000 msmarco-passage.dev-subset runs/run.lucene-inverted.msmarco-v1-passage.wp-tok.model-bm25-default.topics-msmarco-passage.dev-subset.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                   | **BM25 (default)** |
|:------------------------------------------------------------------------------|:------------------:|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking) | 0.1836             |
| **RR@10**                                                                     | **BM25 (default)** |
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking) | 0.1752             |
| **R@100**                                                                     | **BM25 (default)** |
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking) | 0.6231             |
| **R@1000**                                                                    | **BM25 (default)** |
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking) | 0.8263             |
