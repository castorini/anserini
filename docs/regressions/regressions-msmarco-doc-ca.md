# Anserini Regressions: MS MARCO Document Ranking

**Models**: various bag-of-words approaches on complete documents using `CompositeAnalyzer`.

This page documents regression experiments on the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Here we are using `CompositeAnalyzer` which combines **Lucene tokenization** with **WordPiece tokenization** (i.e., from BERT) using the following tokenizer from HuggingFace [`bert-base-uncased`](https://huggingface.co/bert-base-uncased).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-doc-ca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-doc-ca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-ca
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc \
  -index indexes/lucene-index.msmarco-doc-ca/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 7 -storePositions -storeDocvectors -storeRaw -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeAnalyzer \
  >& logs/log.msmarco-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the document corpus in Anserini's jsonl format.
See [this page](experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-ca/ \
  -topics tools/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt \
  -bm25 -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2410    |
| **RR@100**                                                                                                   | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2403    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.7441    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.9004    |
