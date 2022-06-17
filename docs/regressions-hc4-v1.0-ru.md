# Anserini Regressions: HC4 (v1.0) &mdash; Russian

This page documents BM25 regression experiments for [HC4 (v1.0) &mdash; Russian](https://github.com/hltcoe/HC4).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-v1.0-ru.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-v1.0-ru.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-ru
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/hc4-v1.0-rus \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.hc4-v1.0-rus &
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
  -output runs/run.hc4-v1.0-rus.bm25.topics.hc4-v1.0-ru.dev.title.txt \
  -bm25 -hits 100 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.hc4-v1.0-russian/ \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-v1.0-rus.bm25.topics.hc4-v1.0-ru.dev.desc.txt \
  -bm25 -hits 100 -language ru &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-rus.bm25.topics.hc4-v1.0-ru.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-rus.bm25.topics.hc4-v1.0-ru.dev.desc.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| MAP                                                                                                          | BM25      |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [HC4 (Russian): dev-topic title](https://github.com/hltcoe/HC4)                                              | 0.2767    |
| [HC4 (Russian): dev-topic description](https://github.com/hltcoe/HC4)                                        | 0.2321    |
