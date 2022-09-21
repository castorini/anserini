# Anserini Regressions: HC4 (v1.0) &mdash; Russian

This page documents BM25 regression experiments for [HC4 (v1.0) &mdash; Russian](https://github.com/hltcoe/HC4) ([paper](https://arxiv.org/pdf/2201.09992.pdf)).
To be clear, the queries are in Russian (human translations) and the corpus is in Russian.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-v1.0-ru.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-v1.0-ru.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-ru
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-ru \
  --corpus-path collections/hc4-v1.0-ru
```


## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/hc4-v1.0-ru \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.hc4-v1.0-ru &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rm3 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rocchio -language ru &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Russian): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.2937    | 0.2389    | 0.3995    |
| [HC4 (Russian): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.2373    | 0.0842    | 0.2817    |
| [HC4 (Russian): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.3186    | 0.2163    | 0.3564    |
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.2186    | 0.2375    | 0.2641    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1883    | 0.1896    | 0.2250    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.2265    | 0.2317    | 0.2732    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.3942    | 0.3376    | 0.4719    |
| [HC4 (Russian): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.2580    | 0.1838    | 0.3168    |
| [HC4 (Russian): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.3972    | 0.3410    | 0.4400    |
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.2944    | 0.3209    | 0.3163    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.2456    | 0.2461    | 0.2767    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.2989    | 0.3007    | 0.3265    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.4375    | 0.4500    | 0.5125    |
| [HC4 (Russian): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.5125    | 0.3625    | 0.5500    |
| [HC4 (Russian): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.5000    | 0.4625    | 0.5875    |
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.3470    | 0.3630    | 0.3930    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.3180    | 0.2950    | 0.3510    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.3670    | 0.3530    | 0.3990    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.8432    | 0.7598    | 0.8710    |
| [HC4 (Russian): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.5942    | 0.3707    | 0.6171    |
| [HC4 (Russian): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.7619    | 0.6428    | 0.7639    |
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.7182    | 0.7208    | 0.7728    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.7355    | 0.6530    | 0.7680    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.7721    | 0.7335    | 0.8271    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-v1.0-ru.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
