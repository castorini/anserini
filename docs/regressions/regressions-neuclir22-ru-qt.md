# Anserini Regressions: NeuCLIR22 &mdash; Russian (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Russian, with the following configuration:

+ Queries: Translated from English into Russian
+ Documents: Original Russian corpus
+ Model: BM25

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-ru-qt.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-ru-qt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt \
  --corpus-path collections/neuclir22-ru
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-ru \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-ru \
  -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.neuclir22-ru &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-title.txt \
  -bm25 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc.txt \
  -bm25 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc_title.txt \
  -bm25 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-title.txt \
  -bm25 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc.txt \
  -bm25 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc_title.txt \
  -bm25 -language ru &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-title.txt \
  -bm25 -rm3 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc.txt \
  -bm25 -rm3 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc_title.txt \
  -bm25 -rm3 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-title.txt \
  -bm25 -rm3 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc.txt \
  -bm25 -rm3 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc_title.txt \
  -bm25 -rm3 -language ru &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-title.txt \
  -bm25 -rocchio -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc.txt \
  -bm25 -rocchio -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc_title.txt \
  -bm25 -rocchio -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-title.txt \
  -bm25 -rocchio -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc.txt \
  -bm25 -rocchio -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc_title.txt \
  -bm25 -rocchio -language ru &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.mt-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.2821    | 0.2049    | 0.2649    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2107    | 0.1287    | 0.2458    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.2662    | 0.1653    | 0.2734    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.2639    | 0.2035    | 0.2540    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.1968    | 0.1344    | 0.2408    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.2561    | 0.1741    | 0.2889    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3668    | 0.2984    | 0.3572    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3138    | 0.2155    | 0.3366    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3665    | 0.2638    | 0.3630    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3564    | 0.2968    | 0.3426    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2972    | 0.2166    | 0.3257    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3605    | 0.2766    | 0.3764    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3732    | 0.3246    | 0.3873    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3522    | 0.2592    | 0.3803    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3943    | 0.2917    | 0.3947    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3838    | 0.3399    | 0.3864    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3404    | 0.2719    | 0.3759    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3873    | 0.3035    | 0.3917    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.7125    | 0.6471    | 0.7381    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.6655    | 0.5406    | 0.7276    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.7421    | 0.6320    | 0.7770    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.7332    | 0.6779    | 0.7439    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.6210    | 0.5536    | 0.7136    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.7373    | 0.6271    | 0.7959    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-ru-qt.template) and run `bin/build.sh` to rebuild the documentation.

