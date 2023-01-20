# Anserini Regressions: NeuCLIR22 &mdash; Russian (Query Translation)

This page presents **query translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Russian, with the following configuration:

+ Queries: Translated from English into Russian
+ Documents: Original Russian corpus
+ Model: SPLADE

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/neuclir22-ru-qt-splade.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/neuclir22-ru-qt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt-splade
```

## Corpus Download

TODO

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-ru \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -impact -pretokenized -storeDocvectors \
  >& logs/log.neuclir22-ru &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-title.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc_title.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-title.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc_title.txt \
  -impact -pretokenized &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt \
  -impact -pretokenized -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt \
  -impact -pretokenized -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.ht-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt \
  -impact -pretokenized -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.splade.mt-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt \
  -impact -pretokenized -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade.topics.neuclir22-ru.splade.mt-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rm3.topics.neuclir22-ru.splade.mt-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.ht-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.splade+rocchio.topics.neuclir22-ru.splade.mt-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **SPLADE**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3212    | 0.3212    | 0.3097    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.2721    | 0.2604    | 0.2891    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.2808    | 0.2808    | 0.2929    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3212    | 0.3212    | 0.3097    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.2721    | 0.2604    | 0.2891    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.2808    | 0.2808    | 0.2929    |
| **nDCG@20**                                                                                                  | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.4452    | 0.4452    | 0.4337    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3922    | 0.3792    | 0.3965    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.4156    | 0.4156    | 0.4075    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.4452    | 0.4452    | 0.4337    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3922    | 0.3792    | 0.3965    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.4156    | 0.4156    | 0.4075    |
| **J@20**                                                                                                     | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.3706    | 0.3706    | 0.3807    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.3684    | 0.3689    | 0.3697    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.3846    | 0.3846    | 0.3947    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.3706    | 0.3706    | 0.3807    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.3684    | 0.3689    | 0.3697    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.3846    | 0.3846    | 0.3947    |
| **Recall@1000**                                                                                              | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (human-translated queries)](https://neuclir.github.io/)                       | 0.7725    | 0.7725    | 0.7918    |
| [NeuCLIR 2022 (Russian): desc (human-translated queries)](https://neuclir.github.io/)                        | 0.7200    | 0.7150    | 0.7090    |
| [NeuCLIR 2022 (Russian): desc+title (human-translated queries)](https://neuclir.github.io/)                  | 0.7669    | 0.7669    | 0.7590    |
| [NeuCLIR 2022 (Russian): title (machine-translated queries)](https://neuclir.github.io/)                     | 0.7725    | 0.7725    | 0.7918    |
| [NeuCLIR 2022 (Russian): desc (machine-translated queries)](https://neuclir.github.io/)                      | 0.7200    | 0.7150    | 0.7090    |
| [NeuCLIR 2022 (Russian): desc+title (machine-translated queries)](https://neuclir.github.io/)                | 0.7669    | 0.7669    | 0.7590    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-ru-qt-splade.template) and run `bin/build.sh` to rebuild the documentation.

