# Anserini Regressions: CIRAL (v1.0) &mdash; Yoruba

This page documents BM25 monolingual regression experiments on the dev set of [CIRAL (v1.0) &mdash; Yoruba](https://github.com/ciralproject/ciral).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/ciral-v1.0-yo.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/ciral-v1.0-yo.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-yo
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/ciral-yoruba \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.ciral-v1.0-yo/ \
  -threads 16 -storePositions -storeDocvectors -storeRaw -language yo \
  >& logs/log.ciral-yoruba &
```

See [this page](https://github.com/ciralproject/ciral) for more details about the CIRAL corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.ciral-v1.0-yo/ \
  -topics tools/topics-and-qrels/topics.ciral-v1.0-yo-dev-native.tsv \
  -topicReader TsvInt \
  -output runs/run.ciral-yoruba.bm25-default.topics.ciral-v1.0-yo-dev-native.txt \
  -bm25 -hits 1000 -language yo &
```

Evaluation can be performed using `trec_eval`:

```
target/appassembler/bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.ciral-v1.0-yo-dev.tsv runs/run.ciral-yoruba.bm25-default.topics.ciral-v1.0-yo-dev-native.txt
target/appassembler/bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.ciral-v1.0-yo-dev.tsv runs/run.ciral-yoruba.bm25-default.topics.ciral-v1.0-yo-dev-native.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.ciral-v1.0-yo-dev.tsv runs/run.ciral-yoruba.bm25-default.topics.ciral-v1.0-yo-dev-native.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@20**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [CIRAL Yoruba: Dev](https://huggingface.co/datasets/CIRAL/ciral)                                             | 0.2797    |
| **MRR@10**                                                                                                   | **BM25 (default)**|
| [CIRAL Yoruba: Dev](https://huggingface.co/datasets/CIRAL/ciral)                                             | 0.3833    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [CIRAL Yoruba: Dev](https://huggingface.co/datasets/CIRAL/ciral)                                             | 0.5114    |
