# Anserini Regressions: FEVER Fact Verification

This page documents BM25 regression experiments for the [FEVER fact verification task](https://fever.ai/), which is integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/fever.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/fever.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression fever
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection FeverParagraphCollection \
  -input /path/to/fever \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.fever-paragraph/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.fever &
```

The directory `/path/to/fever` should be a directory containing the expanded document collection; see [this page](../../docs/experiments-fever.md) for how to prepare this collection.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 9999 claims as part of the dev set for the original FEVER paper.
The original data can be found [here](https://fever.ai/resources.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.fever-paragraph/ \
  -topics tools/topics-and-qrels/topics.fever.dev.txt \
  -topicReader TsvInt \
  -output runs/run.fever.bm25-default.topics.fever.dev.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.fever-paragraph/ \
  -topics tools/topics-and-qrels/topics.fever.dev.txt \
  -topicReader TsvInt \
  -output runs/run.fever.bm25-tuned.topics.fever.dev.txt \
  -bm25 -bm25.k1 0.9 -bm25.b 0.1 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m recall.100 -c -m recall.1000 tools/topics-and-qrels/qrels.fever.dev.txt runs/run.fever.bm25-default.topics.fever.dev.txt

bin/trec_eval -c -m recall.100 -c -m recall.1000 tools/topics-and-qrels/qrels.fever.dev.txt runs/run.fever.bm25-tuned.topics.fever.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **R@100**                                                                                                    | **BM25 (Default)**| **BM25 (Tuned)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|
| [FEVER Paper Development Dataset](https://s3-eu-west-1.amazonaws.com/fever.public/paper_dev.jsonl)           | 0.8974    | 0.8988    |
| **R@1000**                                                                                                   | **BM25 (Default)**| **BM25 (Tuned)**|
| [FEVER Paper Development Dataset](https://s3-eu-west-1.amazonaws.com/fever.public/paper_dev.jsonl)           | 0.9477    | 0.9481    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=0.9`, `b=0.1`.
