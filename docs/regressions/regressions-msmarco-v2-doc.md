# Anserini Regressions: MS MARCO (V2) Document Ranking

**Models**: various bag-of-words approaches on complete documents

This page describes regression experiments for document ranking on the MS MARCO (V2) document corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover bag-of-words baselines.
For additional instructions on working with the MS MARCO V2 document corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v2-doc.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v2-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2-doc \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -threads 24 -storeRaw \
  >& logs/log.msmarco-v2-doc &
```

The directory `/path/to/msmarco-v2-doc/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-doc/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt \
  -bm25 &
```

Evaluation can be performed using `trec_eval`:

```
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt
target/appassembler/bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt
target/appassembler/bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.bm25-default.topics.msmarco-v2-doc.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.1552    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.1639    |
| **MRR@100**                                                                                                  | **BM25 (default)**|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.1572    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.1659    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.5956    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.5970    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                          | 0.8054    |
| [MS MARCO V2 Doc: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                         | 0.8029    |
