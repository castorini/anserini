# Anserini Regressions: TREC 2019 Deep Learning Track (Passage)

**Models**: BM25 with quantized weights (8 bits)

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2019 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl19-passage-bm25-b8.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl19-passage-bm25-b8.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-bm25-b8
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage \
  -index indexes/lucene-index.msmarco-passage-bm25-b8/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -impact -pretokenized \
  >& logs/log.msmarco-passage &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files containing quantized BM25 vectors for every document

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-bm25-b8/ \
  -topics src/main/resources/topics-and-qrels/topics.dl19-passage.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-b8.topics.dl19-passage.txt \
  -impact &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -l 2 src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-b8.topics.dl19-passage.txt
tools/eval/trec_eval.9.0.4/trec_eval -m ndcg_cut.10 -c src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-b8.topics.dl19-passage.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-b8.topics.dl19-passage.txt
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c -l 2 src/main/resources/topics-and-qrels/qrels.dl19-passage.txt runs/run.msmarco-passage.bm25-b8.topics.dl19-passage.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| AP@1000                                                                                                      | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.3046    |


| nDCG@10                                                                                                      | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.4993    |


| R@100                                                                                                        | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.4949    |


| R@1000                                                                                                       | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html)                                                   | 0.7639    |
