# Anserini Regressions: NeuCLIR22 &mdash; Chinese (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Chinese, with the following configuration:

+ Queries: Translated from English into Chinese
+ Documents: Original Chinese corpus
+ Model: BM25

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-zh-qt.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-zh-qt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt \
  --corpus-path collections/neuclir22-zh
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-zh \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-zh \
  -storePositions -storeDocvectors -storeRaw -language zh \
  >& logs/log.neuclir22-zh &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-title.txt \
  -bm25 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc.txt \
  -bm25 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc_title.txt \
  -bm25 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-title.txt \
  -bm25 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc.txt \
  -bm25 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc_title.txt \
  -bm25 -language zh &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-title.txt \
  -bm25 -rm3 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc.txt \
  -bm25 -rm3 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc_title.txt \
  -bm25 -rm3 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-title.txt \
  -bm25 -rm3 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc.txt \
  -bm25 -rm3 -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc_title.txt \
  -bm25 -rm3 -language zh &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-title.txt \
  -bm25 -rocchio -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc.txt \
  -bm25 -rocchio -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc_title.txt \
  -bm25 -rocchio -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-title.txt \
  -bm25 -rocchio -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc.txt \
  -bm25 -rocchio -language zh &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc_title.txt \
  -bm25 -rocchio -language zh &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.mt-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.1505    | 0.1501    | 0.1692    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.1306    | 0.0925    | 0.1251    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.1734    | 0.1367    | 0.1888    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.1009    | 0.0995    | 0.1134    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.0791    | 0.0454    | 0.0800    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.1053    | 0.0731    | 0.1095    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.2478    | 0.2127    | 0.2544    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2068    | 0.1519    | 0.1985    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.2572    | 0.1891    | 0.2734    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.1830    | 0.1571    | 0.1861    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.1498    | 0.0868    | 0.1464    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.1754    | 0.1152    | 0.1785    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3895    | 0.3132    | 0.4004    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3588    | 0.2461    | 0.3689    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.4298    | 0.2925    | 0.4298    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3750    | 0.3303    | 0.3842    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2925    | 0.2031    | 0.3276    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3961    | 0.2522    | 0.3934    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.4759    | 0.4651    | 0.5230    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.4577    | 0.3703    | 0.5113    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.4940    | 0.4373    | 0.5327    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3829    | 0.3621    | 0.4361    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2989    | 0.2462    | 0.3748    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.4028    | 0.2746    | 0.4341    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-zh-qt.template) and run `bin/build.sh` to rebuild the documentation.

