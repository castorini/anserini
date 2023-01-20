# Anserini Regressions: NeuCLIR22 &mdash; Persian (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Persian, with the following configuration:

+ Queries: Translated from English into Persian
+ Documents: Original Persian corpus
+ Model: SPLADE

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/neuclir22-fa-qt-splade.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/neuclir22-fa-qt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt-splade
```

## Corpus Download

TODO

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-fa \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -impact -pretokenized -storeDocvectors \
  >& logs/log.neuclir22-fa &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-title.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc_title.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-title.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc_title.txt \
  -impact -pretokenized &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt \
  -impact -pretokenized -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.ht-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-fa.splade.mt-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt \
  -impact -pretokenized -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade.topics.neuclir22-fa.splade.mt-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rm3.topics.neuclir22-fa.splade.mt-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa.splade+rocchio.topics.neuclir22-fa.splade.mt-desc_title.txt
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

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-fa-qt-splade.template) and run `bin/build.sh` to rebuild the documentation.

