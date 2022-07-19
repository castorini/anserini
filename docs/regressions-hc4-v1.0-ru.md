# Anserini Regressions: HC4 (v1.0) &mdash; Russian

This page documents BM25 regression experiments for [HC4 (v1.0) &mdash; Russian](https://github.com/hltcoe/HC4) ([paper](https://arxiv.org/pdf/2201.09992.pdf)).

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
  -index indexes/lucene-index.hc4-v1.0-russian/ \
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
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rm3 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rocchio -language ru &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.dev.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.test.txt runs/run.hc4-v1.0-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Russian): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.2767    | 0.2266    | 0.3897    |
| [HC4 (Russian): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.2321    | 0.0821    | 0.2776    |
| [HC4 (Russian): dev-topic description+title](https://github.com/hltcoe/HC4)                                  | 0.3130    | 0.2037    | 0.3532    |
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.2105    | 0.2293    | 0.2505    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1779    | 0.1787    | 0.2151    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.2178    | 0.2202    | 0.2610    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-v1.0-ru.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
