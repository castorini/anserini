# Anserini Regressions: NeuCLIR22 &mdash; Russian (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Russian, with the following configuration:

+ Queries: Translated from English into Russian
+ Documents: Original Russian corpus
+ Model: SPLADE NeuCLIR22

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-ru-qt-splade.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-ru-qt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

We make available a version of the corpus that has already been encoded with SPLADE NeuCLIR22, i.e., we performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is required to reproduce these experiments; see instructions below.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt-splade
```

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/neuclir22-ru-splade.tar -P collections/
tar xvf collections/neuclir22-ru-splade.tar -C collections/
```

To confirm, `neuclir22-ru-splade.tar` is 11 GB and has MD5 checksum `453a5be6913b2ab32c18541b2882c821`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt-splade \
  --corpus-path collections/neuclir22-ru-splade
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-ru-splade \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -impact -pretokenized -storeRaw \
  >& logs/log.neuclir22-ru-splade &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc_title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc_title.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade.topics.neuclir22-ru.splade.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-splade.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **SPLADE**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3276    | 0.3276    | 0.3072    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2721    | 0.2721    | 0.2953    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.2848    | 0.2848    | 0.3019    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3212    | 0.3212    | 0.3097    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2604    | 0.2604    | 0.2891    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.2808    | 0.2808    | 0.2929    |
| **nDCG@20**                                                                                                  | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.4594    | 0.4594    | 0.4322    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3922    | 0.3922    | 0.4133    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.4214    | 0.4214    | 0.4316    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.4452    | 0.4452    | 0.4337    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3792    | 0.3792    | 0.3965    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.4156    | 0.4156    | 0.4075    |
| **J@20**                                                                                                     | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3645    | 0.3645    | 0.3754    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3684    | 0.3684    | 0.3689    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3860    | 0.3860    | 0.3947    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3706    | 0.3706    | 0.3807    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3689    | 0.3689    | 0.3697    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3846    | 0.3846    | 0.3947    |
| **Recall@1000**                                                                                              | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.7739    | 0.7739    | 0.7946    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.7200    | 0.7200    | 0.7209    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.7612    | 0.7612    | 0.7776    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.7725    | 0.7725    | 0.7918    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.7150    | 0.7150    | 0.7090    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.7669    | 0.7669    | 0.7590    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-ru-qt-splade.template) and run `bin/build.sh` to rebuild the documentation.

