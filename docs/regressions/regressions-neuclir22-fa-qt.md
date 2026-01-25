# Anserini Regressions: NeuCLIR22 &mdash; Persian (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Persian, with the following configuration:

+ Queries: Translated from English into Persian
+ Documents: Original Persian corpus
+ Model: BM25

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-fa-qt.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-fa-qt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt \
  --corpus-path collections/neuclir22-fa
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-fa \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-fa \
  -storePositions -storeDocvectors -storeRaw -language fa \
  >& logs/log.neuclir22-fa &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-title.txt \
  -bm25 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc.txt \
  -bm25 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc_title.txt \
  -bm25 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-title.txt \
  -bm25 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc.txt \
  -bm25 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc_title.txt \
  -bm25 -language fa &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-title.txt \
  -bm25 -rm3 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc.txt \
  -bm25 -rm3 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc_title.txt \
  -bm25 -rm3 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-title.txt \
  -bm25 -rm3 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc.txt \
  -bm25 -rm3 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc_title.txt \
  -bm25 -rm3 -language fa &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-title.txt \
  -bm25 -rocchio -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc.txt \
  -bm25 -rocchio -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.ht-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc_title.txt \
  -bm25 -rocchio -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-title.txt \
  -bm25 -rocchio -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc.txt \
  -bm25 -rocchio -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.mt-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc_title.txt \
  -bm25 -rocchio -language fa &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default.topics.neuclir22-fa.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.neuclir22-fa.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.neuclir22-fa.mt-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-------------------|-----------|-------------|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.2492            | 0.1791    | 0.2289      |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.1970            | 0.1182    | 0.2156      |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.2484            | 0.1511    | 0.2339      |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.2638            | 0.1876    | 0.2545      |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.1956            | 0.1196    | 0.2234      |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.2648            | 0.1715    | 0.2590      |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3595            | 0.2792    | 0.3284      |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2966            | 0.2137    | 0.2938      |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3488            | 0.2457    | 0.3237      |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3678            | 0.2876    | 0.3619      |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3011            | 0.2168    | 0.3043      |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3758            | 0.2793    | 0.3496      |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3746            | 0.3197    | 0.3912      |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3496            | 0.2654    | 0.3820      |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3873            | 0.3057    | 0.3991      |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3789            | 0.3355    | 0.3908      |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3487            | 0.2662    | 0.3754      |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3873            | 0.3175    | 0.3956      |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.7324            | 0.6940    | 0.7928      |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.7214            | 0.5598    | 0.7728      |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.7494            | 0.6383    | 0.8294      |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.7631            | 0.7070    | 0.8137      |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.6847            | 0.5597    | 0.7685      |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.7714            | 0.6501    | 0.8227      |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-fa-qt.template) and run `bin/build.sh` to rebuild the documentation.

