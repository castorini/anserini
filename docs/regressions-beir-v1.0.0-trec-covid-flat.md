# Anserini Regressions: BEIR (v1.0.0) &mdash; TREC-COVID

This page documents BM25 regression experiments for [BEIR (v1.0.0) &mdash; TREC-COVID](http://beir.ai/).
These experiments index the corpus in a "flat" manner, by concatenating the "title" and "text" into the "contents" field.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/beir-v1.0.0-trec-covid-flat.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/beir-v1.0.0-trec-covid-flat.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-flat
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection BeirFlatCollection \
  -input /path/to/beir-v1.0.0-trec-covid-flat \
  -index indexes/lucene-index.beir-v1.0.0-trec-covid-flat/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.beir-v1.0.0-trec-covid-flat &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-trec-covid-flat/ \
  -topics src/main/resources/topics-and-qrels/topics.beir-v1.0.0-trec-covid.test.tsv.gz \
  -topicreader TsvString \
  -output runs/run.beir-v1.0.0-trec-covid-flat.bm25.topics.beir-v1.0.0-trec-covid.test.txt \
  -bm25 -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-trec-covid.test.txt runs/run.beir-v1.0.0-trec-covid-flat.bm25.topics.beir-v1.0.0-trec-covid.test.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-trec-covid.test.txt runs/run.beir-v1.0.0-trec-covid-flat.bm25.topics.beir-v1.0.0-trec-covid.test.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-trec-covid.test.txt runs/run.beir-v1.0.0-trec-covid-flat.bm25.topics.beir-v1.0.0-trec-covid.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): TREC-COVID                                                                                    | 0.5947    |
| **R@100**                                                                                                    | **BM25**  |
| BEIR (v1.0.0): TREC-COVID                                                                                    | 0.1091    |
| **R@1000**                                                                                                   | **BM25**  |
| BEIR (v1.0.0): TREC-COVID                                                                                    | 0.3955    |
