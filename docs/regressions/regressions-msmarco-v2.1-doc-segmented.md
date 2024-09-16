# Anserini Regressions: MS MARCO V2.1 Document Ranking

**Models**: various bag-of-words approaches on segmented documents

This page describes regression experiments for document ranking _on the segmented version_ of the MS MARCO V2.1 document corpus using the dev queries, which is integrated into Anserini's regression testing framework.
This corpus was derived from the MS MARCO V2 _segmented_ document corpus and prepared for the TREC 2024 RAG Track.

Here, we cover bag-of-words baselines where each document in the MS MARCO V2.1 _segmented_ document corpus is treated as a unit of indexing.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v2.1-doc-segmented.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v2.1-doc-segmented.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2.1-doc-segmented
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2.1-doc-segmented \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -storeRaw \
  >& logs/log.msmarco-v2.1-doc-segmented &
```

The setting of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
These evaluation resources are from the original V2 corpus, but have been "projected" over to the V2.1 corpus.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.msmarco-v2-doc.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.1951    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.1972    |
| **MRR@100**                                                                                                  | **BM25 (default)**|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.1973    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.2000    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.6683    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.6771    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.8528    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.8577    |
