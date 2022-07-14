# Anserini Regressions: HC4 (v1.0) &mdash; Persian

This page documents BM25 regression experiments for [HC4 (v1.0) &mdash; Persian](https://github.com/hltcoe/HC4) ([paper](https://arxiv.org/pdf/2201.09992.pdf)).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-v1.0-fa.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-v1.0-fa.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-fa
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-fa \
  --corpus-path collections/hc4-v1.0-fa
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/hc4-v1.0-fa \
  -index indexes/lucene-index.hc4-v1.0-persian/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language fa \
  >& logs/log.hc4-v1.0-fa &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-persian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.dev.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.dev.title.txt \
  -bm25 -hits 100 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-persian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.dev.desc.txt \
  -bm25 -hits 100 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-persian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.test.title.txt \
  -bm25 -hits 100 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-persian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.test.desc.txt \
  -bm25 -hits 100 -language fa &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-fa.dev.txt runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-fa.dev.txt runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.dev.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-fa.test.txt runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-fa.test.txt runs/run.hc4-v1.0-fa.bm25.topics.hc4-v1.0-fa.test.desc.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [HC4 (Persian): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.2919    |
| [HC4 (Persian): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.3188    |
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.2837    |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.2882    |

The above results reproduce the BM25 title queries run in Table 2 of [this paper](https://arxiv.org/pdf/2201.08471.pdf).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-v1.0-fa.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
