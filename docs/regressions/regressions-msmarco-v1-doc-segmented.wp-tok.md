# Anserini Regressions: MS MARCO Document Ranking

**Models**: various bag-of-words approaches on segmented documents with WordPiece tokenization

This page documents regression experiments on the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Here we are using **WordPiece tokenization** (i.e., from BERT) on passages from the documents.
At retrieval time, we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking; this is known as the MaxP technique.
In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v1-doc-segmented.wp-tok.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v1-doc-segmented.wp-tok.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc-segmented.wp-tok
```

## Indexing

Typical indexing command:

```
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
See [this page](../../docs/experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc-segmented.wp-tok/ \
  -topics tools/topics-and-qrels/topics.msmarco-doc.dev.wp.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented-wp.bm25-default.topics.msmarco-doc.dev.wp.txt \
  -bm25 -pretokenized -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-wp.bm25-default.topics.msmarco-doc.dev.wp.txt
bin/trec_eval -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-wp.bm25-default.topics.msmarco-doc.dev.wp.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-wp.bm25-default.topics.msmarco-doc.dev.wp.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-wp.bm25-default.topics.msmarco-doc.dev.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2674    |
| **RR@100**                                                                                                   | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2668    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.7705    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.9118    |
