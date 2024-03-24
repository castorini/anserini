# Anserini Regressions: MS MARCO Document Ranking

**Models**: bag-of-words approaches with WordPiece tokenization provided by HuggingFace Tokenizer Integration

This page documents regression experiments on the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Here we are using **WordPiece tokenization** (i.e., from BERT) with the following tokenizer from HuggingFace [`bert-base-uncased`](https://huggingface.co/bert-base-uncased): .

In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-doc-hgf-wp.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-doc-hgf-wp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-hgf-wp
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-doc-hgf-wp/ \
  -threads 7 -storePositions -storeDocvectors -storeRaw -analyzeWithHuggingFaceTokenizer bert-base-uncased \
  >& logs/log.msmarco-doc &
```

The directory `/path/to/msmarco-passage-wp/` should be a directory containing the corpus in Anserini's jsonl format.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-hgf-wp/ \
  -topics tools/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt \
  -bm25 -analyzeWithHuggingFaceTokenizer bert-base-uncased &
```

Evaluation can be performed using `trec_eval`:

```
target/appassembler/bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2234    |
| **RR@100**                                                                                                   | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2227    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.7031    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.8743    |
