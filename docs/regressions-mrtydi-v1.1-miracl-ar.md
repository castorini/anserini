# Anserini Regressions: Mr. TyDi (v1.1) Topics on MIRACL corpus &mdash; Arabic

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Arabic Topics](https://github.com/castorini/mr.tydi) on the MIRACL.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/mrtydi-v1.1-miracl-ar.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/mrtydi-v1.1-miracl-ar.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-miracl-ar
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/miracl-ar \
  -index indexes/lucene-index.miracl-arabic/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -language ar \
  >& logs/log.miracl-ar &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.miracl-arabic/ \
  -topics src/main/resources/topics-and-qrels/topics.mrtydi-v1.1-ar.test.txt.gz \
  -topicreader TsvInt \
  -output runs/run.miracl-ar.bm25.topics.mrtydi-v1.1-ar.test.txt \
  -bm25 -hits 100 -language ar &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m recip_rank -m recall.100 /store/scratch/x978zhan/MIRACL/qrels/ar/ar.qrels.valid.tsv runs/run.miracl-ar.bm25.topics.mrtydi-v1.1-ar.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Arabic): test](https://github.com/castorini/mr.tydi)                                              | 0.5150    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Arabic): test](https://github.com/castorini/mr.tydi)                                              | 0.8805    |
