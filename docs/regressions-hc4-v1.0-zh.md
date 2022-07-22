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
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.test.txt runs/run.hc4-v1.0-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Chinese): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.2914    | 0.3088    | 0.2574    |
| [HC4 (Chinese): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.1983    | 0.2210    | 0.2176    |
| [HC4 (Chinese): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.2408    | 0.2951    | 0.2558    |
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1749    | 0.1562    | 0.1618    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1404    | 0.1014    | 0.1386    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1854    | 0.1442    | 0.1883    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-v1.0-zh.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
