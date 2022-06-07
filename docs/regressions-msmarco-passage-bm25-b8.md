# Anserini Regressions: MS MARCO Passage Ranking

**Models**: BM25 with quantized weights (8 bits)

This page documents regression experiments on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking), which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-bm25-b8.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-bm25-b8.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-bm25-b8
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
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-bm25-b8/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-b8.topics.msmarco-passage.dev-subset.txt \
  -impact &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-b8.topics.msmarco-passage.dev-subset.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-b8.topics.msmarco-passage.dev-subset.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-b8.topics.msmarco-passage.dev-subset.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-b8.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| AP@1000                                                                                                      | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.1925    |


| RR@10                                                                                                        | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.1839    |


| R@100                                                                                                        | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.6604    |


| R@1000                                                                                                       | BM25 (default parameters, quantized 8 bits)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8562    |
