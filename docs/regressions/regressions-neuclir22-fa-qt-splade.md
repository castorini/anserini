# Anserini Regressions: NeuCLIR22 &mdash; Persian (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Persian, with the following configuration:

+ Queries: Translated from English into Persian
+ Documents: Original Persian corpus
+ Model: SPLADE NeuCLIR22

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-fa-qt-splade.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-fa-qt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

We make available a version of the corpus that has already been encoded with SPLADE NeuCLIR22, i.e., we performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is required to reproduce these experiments; see instructions below.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt-splade
```

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/neuclir22-fa-splade.tar -P collections/
tar xvf collections/neuclir22-fa-splade.tar -C collections/
```

To confirm, `neuclir22-fa-splade.tar` is 4.0 GB and has MD5 checksum `10fddf0b2a132b9514767bed87ca2693`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt-splade \
  --corpus-path collections/neuclir22-fa-splade
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-fa-splade \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -impact -pretokenized -storeRaw \
  >& logs/log.neuclir22-fa-splade &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc_title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc_title.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade.topics.neuclir22-fa.splade.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-splade.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **SPLADE**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.2896    | 0.2896    | 0.2984    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2959    | 0.2959    | 0.2931    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3376    | 0.3376    | 0.2905    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.2957    | 0.2957    | 0.2836    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2890    | 0.2890    | 0.2824    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3313    | 0.3313    | 0.3136    |
| **nDCG@20**                                                                                                  | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.4301    | 0.4301    | 0.4348    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.4413    | 0.4413    | 0.4232    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.4788    | 0.4788    | 0.4146    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.4437    | 0.4437    | 0.4193    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.4300    | 0.4300    | 0.4121    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.4728    | 0.4728    | 0.4444    |
| **J@20**                                                                                                     | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3684    | 0.3684    | 0.3759    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3741    | 0.3741    | 0.3952    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3952    | 0.3952    | 0.4035    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3632    | 0.3632    | 0.3680    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3737    | 0.3737    | 0.3921    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3895    | 0.3895    | 0.4035    |
| **Recall@1000**                                                                                              | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.7652    | 0.7652    | 0.7897    |
| [NeuCLIR 2022 (Persian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.8173    | 0.8173    | 0.8175    |
| [NeuCLIR 2022 (Persian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.8329    | 0.8329    | 0.8245    |
| [NeuCLIR 2022 (Persian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.8045    | 0.8045    | 0.8099    |
| [NeuCLIR 2022 (Persian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.8172    | 0.8172    | 0.8117    |
| [NeuCLIR 2022 (Persian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.8437    | 0.8437    | 0.8350    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-fa-qt-splade.template) and run `bin/build.sh` to rebuild the documentation.

