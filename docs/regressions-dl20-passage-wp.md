# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Models**: bag-of-words approaches with WordPiece tokenization

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2020.html).
Here we are using **WordPiece tokenization** (i.e., from BERT).
In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-passage-wp.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-passage-wp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-wp
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-passage-wp \
  -index indexes/lucene-index.msmarco-passage-wp/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -storePositions -storeDocvectors -storeRaw -pretokenized \
  >& logs/log.msmarco-passage-wp &
```

The directory `/path/to/msmarco-passage-wp/` should be a directory containing the corpus in Anserini's jsonl format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-wp/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.wp.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-wp.bm25-default.topics.dl20.wp.txt \
  -bm25 -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-wp.bm25-default.topics.dl20.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-wp.bm25-default.topics.dl20.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-wp.bm25-default.topics.dl20.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-wp.bm25-default.topics.dl20.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.2606    |
| **nDCG@10**                                                                                                  | **BM25 (default)**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.4680    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.5222    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7633    |
