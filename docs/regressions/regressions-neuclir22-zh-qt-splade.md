# Anserini Regressions: NeuCLIR22 &mdash; Chinese (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Chinese, with the following configuration:

+ Queries: Translated from English into Chinese
+ Documents: Original Chinese corpus
+ Model: SPLADE NeuCLIR22

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-zh-qt-splade.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-zh-qt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

We make available a version of the corpus that has already been encoded with SPLADE NeuCLIR22, i.e., we performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is required to reproduce these experiments; see instructions below.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt-splade
```

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/neuclir22-zh-splade.tar -P collections/
tar xvf collections/neuclir22-zh-splade.tar -C collections/
```

To confirm, `neuclir22-zh-splade.tar` is 5.9 GB and has MD5 checksum `f491137ef8a8020bfb4940a3bdeaf886`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt-splade \
  --corpus-path collections/neuclir22-zh-splade
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-zh-splade \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -impact -pretokenized -storeRaw \
  >& logs/log.neuclir22-zh-splade &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc_title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc_title.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-zh.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade.topics.neuclir22-zh.splade.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rm3.topics.neuclir22-zh.splade.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-splade.splade+rocchio.topics.neuclir22-zh.splade.mt-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **SPLADE**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.2107    | 0.2107    | 0.2338    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.1763    | 0.1763    | 0.2042    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.2108    | 0.2108    | 0.2253    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.1850    | 0.1850    | 0.2075    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.1495    | 0.1495    | 0.1810    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.1917    | 0.1917    | 0.2139    |
| **nDCG@20**                                                                                                  | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3110    | 0.3110    | 0.3198    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2935    | 0.2935    | 0.2926    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3143    | 0.3143    | 0.3077    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.2843    | 0.2843    | 0.2920    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2527    | 0.2527    | 0.2562    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.2929    | 0.2929    | 0.3029    |
| **J@20**                                                                                                     | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.2930    | 0.2930    | 0.3101    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2711    | 0.2711    | 0.3092    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3000    | 0.3000    | 0.3057    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.2904    | 0.2904    | 0.3066    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2482    | 0.2482    | 0.2728    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.2794    | 0.2794    | 0.2978    |
| **Recall@1000**                                                                                              | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (human-translated queries)](https://neuclir.github.io/)                       | 0.6803    | 0.6803    | 0.7100    |
| [NeuCLIR 2022 (Chinese): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.6602    | 0.6602    | 0.7205    |
| [NeuCLIR 2022 (Chinese): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.6551    | 0.6551    | 0.7029    |
| [NeuCLIR 2022 (Chinese): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.6424    | 0.6424    | 0.6861    |
| [NeuCLIR 2022 (Chinese): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.5919    | 0.5919    | 0.6096    |
| [NeuCLIR 2022 (Chinese): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.6312    | 0.6312    | 0.6535    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-zh-qt-splade.template) and run `bin/build.sh` to rebuild the documentation.

