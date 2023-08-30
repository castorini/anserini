# Anserini Regressions: BEIR (v1.0.0) &mdash; HotpotQA

This page documents BM25 regression experiments for [BEIR (v1.0.0) &mdash; HotpotQA](http://beir.ai/).
These experiments index the corpus in a "flat" manner, by concatenating the "title" and "text" into the "contents" field.
All the documents and queries are pre-tokenized with `bert-base-uncased` tokenizer.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/beir-v1.0.0-hotpotqa-flat-wp.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/beir-v1.0.0-hotpotqa-flat-wp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-flat-wp
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection BeirFlatCollection \
  -input /path/to/beir-v1.0.0-hotpotqa-flat-wp \
  -index indexes/lucene-index.beir-v1.0.0-hotpotqa-flat-wp/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw -pretokenized \
  >& logs/log.beir-v1.0.0-hotpotqa-flat-wp &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-hotpotqa-flat-wp/ \
  -topics tools/topics-and-qrels/topics.beir-v1.0.0-hotpotqa.test.wp.tsv.gz \
  -topicreader TsvString \
  -output runs/run.beir-v1.0.0-hotpotqa-flat-wp.bm25.topics.beir-v1.0.0-hotpotqa.test.wp.txt \
  -bm25 -removeQuery -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.beir-v1.0.0-hotpotqa.test.txt runs/run.beir-v1.0.0-hotpotqa-flat-wp.bm25.topics.beir-v1.0.0-hotpotqa.test.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.beir-v1.0.0-hotpotqa.test.txt runs/run.beir-v1.0.0-hotpotqa-flat-wp.bm25.topics.beir-v1.0.0-hotpotqa.test.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.beir-v1.0.0-hotpotqa.test.txt runs/run.beir-v1.0.0-hotpotqa-flat-wp.bm25.topics.beir-v1.0.0-hotpotqa.test.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| BEIR (v1.0.0): HotpotQA                                                                                      | 0.5926    |
| **R@100**                                                                                                    | **BM25**  |
| BEIR (v1.0.0): HotpotQA                                                                                      | 0.7612    |
| **R@1000**                                                                                                   | **BM25**  |
| BEIR (v1.0.0): HotpotQA                                                                                      | 0.8600    |
