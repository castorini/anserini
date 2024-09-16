# Anserini Regressions: TREC 2019 Deep Learning Track (Passage)

**Models**: bag-of-words approaches using `CompositeAnalyzer`

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2019 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html).
Here we are using `CompositeAnalyzer` which combines **Lucene tokenization** with **WordPiece tokenization** (i.e., from BERT) using the following tokenizer from HuggingFace [`bert-base-uncased`](https://huggingface.co/bert-base-uncased).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl19-passage.wp-ca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl19-passage.wp-ca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.wp-ca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 9 \
  -collection JsonCollection \
  -input /path/to/msmarco-passage \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.wp-ca/ \
  -storePositions -storeDocvectors -storeRaw -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeAnalyzer \
  >& logs/log.msmarco-passage &
```

The directory `/path/to/msmarco-passage-wp/` should be a directory containing the corpus in Anserini's jsonl format.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.wp-ca/ \
  -topics tools/topics-and-qrels/topics.dl19-passage.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt \
  -bm25 -analyzeWithHuggingFaceTokenizer bert-base-uncased -useCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-default.topics.dl19-passage.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.2735    |
| **nDCG@10**                                                                                                  | **BM25 (default)**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.4824    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.4857    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.7527    |

❗ Retrieval metrics here are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
For computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl19-passage.wp-ca.template) and run `bin/build.sh` to rebuild the documentation.
