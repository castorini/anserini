# Anserini Regressions: MIRACL (v1.0) &mdash; Hindi

This page documents BM25 regression experiments for [MIRACL (v1.0) &mdash; Hindi](https://github.com/project-miracl/miracl).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/miracl-v1.0-hi.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/miracl-v1.0-hi.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-hi
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/miracl-v1.0-hi \
  -index indexes/lucene-index.miracl-v1.0-hi/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language hi \
  >& logs/log.miracl-v1.0-hi &
```

See [this page](https://github.com/project-miracl/miracl) for more details about the MIRACL corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.miracl-v1.0-hi/ \
  -topics src/main/resources/topics-and-qrels/topics.miracl-v1.0-hi-dev.tsv \
  -topicreader TsvString \
  -output runs/run.miracl-v1.0-hi.bm25.topics.miracl-v1.0-hi-dev.txt \
  -bm25 -hits 100 -language hi &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.miracl-v1.0-hi-dev.tsv runs/run.miracl-v1.0-hi.bm25.topics.miracl-v1.0-hi-dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.miracl-v1.0-hi-dev.tsv runs/run.miracl-v1.0-hi.bm25.topics.miracl-v1.0-hi-dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MIRACL (Hindi): dev](https://github.com/project-miracl/miracl)                                              | 0.4578    |
| **R@100**                                                                                                    | **BM25**  |
| [MIRACL (Hindi): dev](https://github.com/project-miracl/miracl)                                              | 0.8679    |
