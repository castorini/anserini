# Anserini Regressions: HC4 (v1.0) &mdash; Chinese

This page documents BM25 regression experiments for [HC4 (v1.0) &mdash; Chinese](https://github.com/hltcoe/HC4) ([paper](https://arxiv.org/pdf/2201.09992.pdf)).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-v1.0-zh.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-v1.0-zh.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-zh
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-zh \
  --corpus-path collections/hc4-v1.0-zh
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/hc4-v1.0-zh \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language zh \
  >& logs/log.hc4-v1.0-zh &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.title.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.title.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt \
  -bm25 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.title.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.title.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt \
  -bm25 -rm3 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.title.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.title.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt \
  -bm25 -rocchio -language zh &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Chinese): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.2969    | 0.3126    | 0.2641    |
| [HC4 (Chinese): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.2030    | 0.2239    | 0.2211    |
| [HC4 (Chinese): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.2458    | 0.2971    | 0.2641    |
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1801    | 0.1613    | 0.1671    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1455    | 0.1051    | 0.1442    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1907    | 0.1485    | 0.1929    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Chinese): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.3688    | 0.3451    | 0.3220    |
| [HC4 (Chinese): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.2623    | 0.2660    | 0.2577    |
| [HC4 (Chinese): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.3445    | 0.3704    | 0.3082    |
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.2197    | 0.1953    | 0.1966    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1874    | 0.1429    | 0.1685    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.2374    | 0.2026    | 0.2266    |
| **J@10**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Chinese): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.5600    | 0.5500    | 0.5900    |
| [HC4 (Chinese): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.4700    | 0.4100    | 0.4700    |
| [HC4 (Chinese): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.6100    | 0.5200    | 0.5800    |
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.3500    | 0.3520    | 0.3800    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.3160    | 0.2680    | 0.3420    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.3820    | 0.3260    | 0.4100    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-v1.0-zh.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
