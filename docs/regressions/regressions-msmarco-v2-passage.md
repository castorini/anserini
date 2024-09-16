# Anserini Regressions: MS MARCO (V2) Passage Ranking

**Models**: various bag-of-words approaches on original passages

This page describes regression experiments for passage ranking on the MS MARCO V2 passage corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover bag-of-words baselines.
For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v2-passage.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v2-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -storeRaw \
  >& logs/log.msmarco-v2-passage &
```

The directory `/path/to/msmarco-v2-passage/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt \
  -bm25 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.bm25-default.topics.msmarco-v2-passage.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.0709    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.0794    |
| **MRR@100**                                                                                                  | **BM25 (default)**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.0719    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.0802    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.3397    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.3459    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.5733    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.5839    |
