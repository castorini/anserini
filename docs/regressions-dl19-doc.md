# Anserini Regressions: TREC 2019 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on complete documents

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2019 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2019.html).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** none

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl19-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl19-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl19-doc
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
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+rm3.topics.dl19-doc.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+rocchio.topics.dl19-doc.txt \
  -bm25 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.dl19-doc.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+ax.topics.dl19-doc.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-default+prf.topics.dl19-doc.txt \
  -bm25 -bm25prf &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned.topics.dl19-doc.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+rm3.topics.dl19-doc.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+rocchio.topics.dl19-doc.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.dl19-doc.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+ax.topics.dl19-doc.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned+prf.topics.dl19-doc.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -bm25prf &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2.topics.dl19-doc.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+rm3.topics.dl19-doc.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.dl19-doc.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.dl19-doc.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+ax.topics.dl19-doc.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-doc.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc.bm25-tuned2+prf.topics.dl19-doc.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+rocchio-neg.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-default+prf.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+rocchio-neg.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned+prf.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rm3.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+rocchio-neg.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+ax.topics.dl19-doc.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.dl19-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.dl19-doc.txt runs/run.msmarco-doc.bm25-tuned2+prf.topics.dl19-doc.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.2434    | 0.2773    | 0.2811    | 0.2813    | 0.2454    | 0.2541    | 0.2311    | 0.2682    | 0.2683    | 0.2670    | 0.2792    | 0.2774    | 0.2336    | 0.2638    | 0.2657    | 0.2670    | 0.2724    | 0.2815    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.5176    | 0.5174    | 0.5256    | 0.5279    | 0.4732    | 0.5107    | 0.5139    | 0.5453    | 0.5445    | 0.5419    | 0.5203    | 0.5294    | 0.5233    | 0.5526    | 0.5584    | 0.5567    | 0.5093    | 0.5360    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.3949    | 0.4191    | 0.4261    | 0.4259    | 0.3946    | 0.4003    | 0.3853    | 0.4192    | 0.4254    | 0.4224    | 0.4378    | 0.4295    | 0.3849    | 0.4125    | 0.4164    | 0.4172    | 0.4332    | 0.4310    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL19 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.6966    | 0.7507    | 0.7546    | 0.7530    | 0.7323    | 0.7357    | 0.6804    | 0.7276    | 0.7371    | 0.7376    | 0.7532    | 0.7559    | 0.6757    | 0.7188    | 0.7299    | 0.7312    | 0.7474    | 0.7577    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=3.44`, `b=0.87`, tuned in 2019/06 using the MS MARCO document sparse judgments to optimize for MAP and used for TREC 2019 Deep Learning Track baseline runs.
+ The setting "tuned2" refers to `k1=4.46`, `b=0.82`, tuned in 2020/12 using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval); see [this page](experiments-msmarco-doc.md) additional details.

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that in the official evaluation for document ranking, all runs were truncated to top-100 hits per query (whereas all top-1000 hits per query were retained for passage ranking).
Thus, average precision is computed to depth 100 (i.e., AP@100); nDCG@10 remains unaffected.
Remember that we keep qrels of _all_ relevance grades, unlike the case for passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.
Here, we retrieve 1000 hits per query, but measure AP at cutoff 100 (e.g., AP@100).
Thus, the experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

These regressions correspond to official TREC 2019 Deep Learning Track submissions by `BASELINE` group:

+ `bm25base` = BM25 (default), `k1=0.9`, `b=0.4`
+ `bm25base_rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`
+ `bm25base_ax` = BM25 (default) + Ax, `k1=0.9`, `b=0.4`
+ `bm25base_prf` = BM25 (default) + PRF, `k1=0.9`, `b=0.4`
+ `bm25tuned_p` = BM25 (tuned), `k1=3.44`, `b=0.87`
+ `bm25tuned_rm3` = BM25 (tuned) + RM3, `k1=3.44`, `b=0.87`
+ `bm25tuned_ax` = BM25 (tuned) + Ax, `k1=3.44`, `b=0.87`
+ `bm25tuned_prf` = BM25 (tuned) + PRF, `k1=3.44`, `b=0.87`

Note that [#1721](https://github.com/castorini/anserini/issues/1721) slightly change the results, since we corrected underlying issues with data preparation.
