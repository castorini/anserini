# Anserini Regressions: TREC 2020 Deep Learning Track (Document)

**Models**: various bag-of-words approaches on complete documents

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2020 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2020.html).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](../../docs/experiments-msmarco-doc.md).

Note that there are four different bag-of-words regression conditions for this task, and this page describes the following:

+ **Indexing Condition:** each MS MARCO document is treated as a unit of indexing
+ **Expansion Condition:** none

All four conditions are described in detail [here](https://github.com/castorini/docTTTTTquery), in the context of doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl20-doc.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl20-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

Note that in November 2021 we discovered issues in our regression tests, documented [here](../../docs/experiments-msmarco-doc-doc2query-details.md).
As a result, we have had to rebuild all our regressions from the raw corpus.
These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match the numbers here, this may be the reason.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl20-doc
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 7 \
  -collection JsonCollection \
  -input /path/to/msmacro-doc \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmacro-doc &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the document corpus in Anserini's jsonl format.
See [this page](../../docs/experiments-msmarco-doc-doc2query-details.md) for how to prepare the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-default.topics.dl20.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-default+rm3.topics.dl20.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-default+rocchio.topics.dl20.txt \
  -bm25 -rocchio &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-default+rocchio-neg.topics.dl20.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-default+ax.topics.dl20.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-default+prf.topics.dl20.txt \
  -bm25 -bm25prf &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned+rocchio.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rocchio &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned+rocchio-neg.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned+ax.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -axiom -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned+prf.topics.dl20.txt \
  -bm25 -bm25.k1 3.44 -bm25.b 0.87 -bm25prf &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned2.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned2+rm3.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned2+rocchio.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rocchio &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned2+rocchio-neg.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned2+ax.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -axiom -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-doc/ \
  -topics tools/topics-and-qrels/topics.dl20.txt \
  -topicReader TsvInt \
  -output runs/run.msmacro-doc.bm25-tuned2+prf.topics.dl20.txt \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rm3.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rm3.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+rocchio-neg.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+ax.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+ax.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+ax.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+ax.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+prf.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+prf.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+prf.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-default+prf.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rm3.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rm3.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+rocchio-neg.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+ax.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+ax.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+ax.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+ax.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+prf.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+prf.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+prf.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned+prf.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rm3.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rm3.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rm3.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio-neg.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+rocchio-neg.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+ax.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+ax.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+ax.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+ax.topics.dl20.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+prf.topics.dl20.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+prf.topics.dl20.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+prf.topics.dl20.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmacro-doc.bm25-tuned2+prf.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@100**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.3793    | 0.4015    | 0.4089    | 0.4096    | 0.3133    | 0.3515    | 0.3631    | 0.3607    | 0.3634    | 0.3628    | 0.3462    | 0.3561    | 0.3581    | 0.3610    | 0.3628    | 0.3627    | 0.3498    | 0.3530    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.5286    | 0.5254    | 0.5192    | 0.5212    | 0.4275    | 0.4680    | 0.5070    | 0.5130    | 0.5070    | 0.5127    | 0.4941    | 0.4785    | 0.5061    | 0.5195    | 0.5199    | 0.5189    | 0.5106    | 0.4775    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.6110    | 0.6408    | 0.6425    | 0.6431    | 0.5714    | 0.6104    | 0.5935    | 0.5985    | 0.6057    | 0.6045    | 0.6078    | 0.6198    | 0.5860    | 0.5994    | 0.6017    | 0.6013    | 0.5988    | 0.6171    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  | **BM25 (tuned2)**| **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **+PRF**  |
| [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html)                                                       | 0.8085    | 0.8259    | 0.8273    | 0.8270    | 0.8063    | 0.8084    | 0.7876    | 0.8134    | 0.8199    | 0.8238    | 0.8455    | 0.8163    | 0.7776    | 0.8180    | 0.8217    | 0.8267    | 0.8435    | 0.8210    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=3.44`, `b=0.87`, tuned in 2019/06 using the MS MARCO document sparse judgments to optimize for MAP and used for TREC 2019 Deep Learning Track baseline runs.
+ The setting "tuned2" refers to `k1=4.46`, `b=0.82`, tuned in 2020/12 using the MS MARCO document sparse judgments to optimize for recall@100 (i.e., for first-stage retrieval); see [this page](../../docs/experiments-msmarco-doc.md) additional details.

Settings tuned on the MS MARCO document sparse judgments _may not_ work well on the TREC dense judgments.

Note that in the official evaluation for document ranking, all runs were truncated to top-100 hits per query (whereas all top-1000 hits per query were retained for passage ranking).
Thus, average precision is computed to depth 100 (i.e., AP@100); nDCG@10 remains unaffected.
Remember that we keep qrels of _all_ relevance grades, unlike the case for passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.
Here, we retrieve 1000 hits per query, but measure AP at cutoff 100 (e.g., AP@100).
Thus, the experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2102.07662).

Some of these regressions correspond to official TREC 2020 Deep Learning Track submissions by team `anserini`:

+ `d_bm25` = BM25 (default), `k1=0.9`, `b=0.4`
+ `d_bm25rm3` = BM25 (default) + RM3, `k1=0.9`, `b=0.4`

Note that [#1721](https://github.com/castorini/anserini/issues/1721) slightly change the results, since we corrected underlying issues with data preparation.
