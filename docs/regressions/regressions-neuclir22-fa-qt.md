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
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.2151    | 0.1640    | 0.2304    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.1875    | 0.1070    | 0.2077    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.2271    | 0.1408    | 0.2358    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.2169    | 0.1767    | 0.2321    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.1925    | 0.1227    | 0.2266    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.2526    | 0.1681    | 0.2597    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3428    | 0.2775    | 0.3356    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2843    | 0.2010    | 0.2885    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3429    | 0.2463    | 0.3408    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3331    | 0.2917    | 0.3374    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2974    | 0.2146    | 0.3300    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3700    | 0.2963    | 0.3612    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3759    | 0.3250    | 0.3882    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3592    | 0.2684    | 0.3781    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.4035    | 0.3110    | 0.4035    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3684    | 0.3307    | 0.3899    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3412    | 0.2592    | 0.3618    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3820    | 0.3110    | 0.3908    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.7128    | 0.6870    | 0.7691    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.7027    | 0.5572    | 0.7520    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.7373    | 0.6414    | 0.8092    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.7254    | 0.6877    | 0.7672    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.6815    | 0.5606    | 0.7033    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.7424    | 0.6264    | 0.7829    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-fa-qt.template) and run `bin/build.sh` to rebuild the documentation.

