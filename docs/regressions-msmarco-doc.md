# Anserini Regressions: MS MARCO Document Ranking

**Models**: various bag-of-words approaches on complete documents

This page documents regression experiments on the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking), which is integrated into Anserini's regression testing framework.
Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** none

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-doc \
  -index indexes/lucene-index.msmarco-doc/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 7 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the document corpus in Anserini's jsonl format.
See [this page](experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+rocchio.topics.msmarco-doc.dev.txt \
  -bm25 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.msmarco-doc.dev.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt \
  -bm25 -bm25prf &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -bm25prf &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+rm3.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+ax.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+prf.topics.msmarco-doc.dev.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.msmarco-doc.dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.msmarco-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2305    | 0.1627    | 0.1632    | 0.1630    | 0.1146    | 0.1357    | 0.2784    | 0.2271    | 0.2280    | 0.2271    | 0.1888    | 0.1559    | 0.2773    | 0.2234    | 0.2248    | 0.2231    | 0.1886    | 0.1530    |
| **RR@100**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.2299    | 0.1618    | 0.1624    | 0.1622    | 0.1135    | 0.1347    | 0.2778    | 0.2264    | 0.2274    | 0.2265    | 0.1880    | 0.1550    | 0.2767    | 0.2227    | 0.2242    | 0.2224    | 0.1877    | 0.1521    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.7281    | 0.6771    | 0.6765    | 0.6794    | 0.5754    | 0.6374    | 0.8069    | 0.7870    | 0.7901    | 0.7924    | 0.7560    | 0.6852    | 0.8070    | 0.7785    | 0.7878    | 0.7863    | 0.7526    | 0.6825    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.8856    | 0.8783    | 0.8789    | 0.8808    | 0.8373    | 0.8471    | 0.9324    | 0.9322    | 0.9334    | 0.9324    | 0.9268    | 0.8760    | 0.9357    | 0.9303    | 0.9314    | 0.9316    | 0.9249    | 0.8766    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=3.44`, `b=0.87`, tuned in 2019/06 using the MS MARCO document sparse judgments to optimize for MAP and used for TREC 2019 Deep Learning Track baseline runs.
+ The setting "tuned2" refers to `k1=4.46`, `b=0.82`, tuned in 2020/12 using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval); see [this page](experiments-msmarco-doc.md) additional details.

See [this page](experiments-msmarco-doc.md) for more details on tuning.

In these runs, we are retrieving the top 1000 hits for each query and using `trec_eval` to evaluate all 1000 hits.
This lets us measure R@100 and R@1000; the latter is particularly important when these runs are used as first-stage retrieval.
Beware, an official MS MARCO document ranking task leaderboard submission comprises only 100 hits per query.
See [this page](experiments-msmarco-doc-leaderboard.md) for details on Anserini baseline runs that were submitted to the official leaderboard.

## Additional Implementation Details

Note that prior to December 2021, runs generated with `SearchCollection` in the TREC format and then converted into the MS MARCO format give slightly different results from runs generated by `SearchMsmarco` directly in the MS MARCO format, due to tie-breaking effects.
This was fixed with [#1458](https://github.com/castorini/anserini/issues/1458), which also introduced (intra-configuration) multi-threading.
As a result, `SearchMsmarco` has been deprecated and replaced by `SearchCollection`; both have been verified to generate _identical_ output.

The commands below have been retained for historical reasons only, since in some cases they correspond to official MS MARCO leaderboard submissions.

To generate an MS MARCO submission with the BM25 default parameters, corresponding to "BM25 (default)" above:

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 0.9 -b 0.4 -threads 9 \
    -index indexes/lucene-index.msmarco-doc/ \
    -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -output runs/run.msmarco-doc.bm25-default.txt

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc.bm25-default.txt

#####################
MRR @100: 0.23005723505603573
QueriesRanked: 5193
#####################
```

This run corresponds to the MS MARCO document ranking leaderboard entry "Anserini's BM25, default parameters (k1=0.9, b=0.4)" dated 2020/08/16, and is reported in the Lin et al. (SIGIR 2021) Pyserini paper.

To generate an MS MARCO submission with the BM25 tuned parameters, corresponding to "BM25 (tuned)" above:

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 4.46 -b 0.82 -threads 9 \
   -index indexes/lucene-index.msmarco-doc/ \
   -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
   -output runs/run.msmarco-doc.bm25-tuned.txt

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
   --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
   --run runs/run.msmarco-doc.bm25-tuned.txt

#####################
MRR @100: 0.2770296928568702
QueriesRanked: 5193
#####################
```

This run was _not_ submitted to the MS MARCO document ranking leaderboard, but is reported in the Lin et al. (SIGIR 2021) Pyserini paper.

As of February 2022, following resolution of [#1721](https://github.com/castorini/anserini/issues/1721), BM25 runs for the MS MARCO leaderboard can be generated with the commands below.
For default parameters (`k1=0.9`, `b=0.4`):

```
$ sh target/appassembler/bin/SearchCollection \
    -index indexes/lucene-index.msmarco-doc/ \
    -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -topicreader TsvInt \
    -output runs/run.msmarco-doc.bm25-default.txt \
    -format msmarco \
    -bm25 -hits 100

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc.bm25-default.txt

#####################
MRR @100: 0.22994387925437856
QueriesRanked: 5193
#####################
```

For tuned parameters (`k1=4.46`, `b=0.82`):

```
$ sh target/appassembler/bin/SearchCollection \
    -index indexes/lucene-index.msmarco-doc/ \
    -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -topicreader TsvInt \
    -output runs/run.msmarco-doc.bm25-tuned.txt \
    -format msmarco \
    -bm25 -bm25.k1 4.46 -bm25.b 0.82 -hits 100

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc.bm25-tuned.txt

#####################
MRR @100: 0.2766351807440808
QueriesRanked: 5193
#####################
```

Note that the resolution of [#1721](https://github.com/castorini/anserini/issues/1721) _did_ slightly change the results, since we corrected underlying issues with data preparation.
