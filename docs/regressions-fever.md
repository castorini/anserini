# Anserini: Regressions for FEVER Fact Verification

This page documents regression experiments for the [FEVER fact verification task](https://fever.ai/), which is integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/fever.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/fever.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection FeverParagraphCollection \
  -input /path/to/fever \
  -index indexes/lucene-index.fever-paragraph/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.fever &
```

The directory `/path/to/fever` should be a directory containing the expanded document collection; see [this link](../docs/experiments-fever.md) for how to prepare this collection.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 9999 claims as part of the dev set for the original FEVER paper.
The original data can be found [here](https://fever.ai/resources.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.fever-paragraph/ \
  -topics src/main/resources/topics-and-qrels/topics.fever.dev.txt -topicreader TsvInt \
  -output runs/run.fever.bm25-default.topics.fever.dev.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.fever-paragraph/ \
  -topics src/main/resources/topics-and-qrels/topics.fever.dev.txt -topicreader TsvInt \
  -output runs/run.fever.bm25-tuned.topics.fever.dev.txt \
  -bm25 -bm25.k1 0.9 -bm25.b 0.1 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.fever.dev.txt runs/run.fever.bm25-default.topics.fever.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.fever.dev.txt runs/run.fever.bm25-tuned.topics.fever.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

R@100                                   | BM25 (Default)| BM25 (Tuned)|
:---------------------------------------|-----------|-----------|
[FEVER Paper Development Dataset](https://s3-eu-west-1.amazonaws.com/fever.public/paper_dev.jsonl)| 0.8974    | 0.8988    |


R@1000                                  | BM25 (Default)| BM25 (Tuned)|
:---------------------------------------|-----------|-----------|
[FEVER Paper Development Dataset](https://s3-eu-west-1.amazonaws.com/fever.public/paper_dev.jsonl)| 0.9477    | 0.9481    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=0.9`, `b=0.1`.
