# Anserini Regressions: MS MARCO Passage Ranking

**Models**: bag-of-words approaches with WordPiece tokenization

This page documents regression experiments on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking), which is integrated into Anserini's regression testing framework.
Here we are using **WordPiece tokenization** (i.e., from BERT).
In general, effectiveness is lower than with "standard" Lucene tokenization for two reasons: (1) we're losing stemming, and (2) some terms are chopped into less meaningful subwords.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-wp.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-wp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-wp
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
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-wp/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.wp.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-wp.bm25-default.topics.msmarco-passage.dev-subset.wp.txt \
  -bm25 -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-wp.bm25-default.topics.msmarco-passage.dev-subset.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-wp.bm25-default.topics.msmarco-passage.dev-subset.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-wp.bm25-default.topics.msmarco-passage.dev-subset.wp.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-wp.bm25-default.topics.msmarco-passage.dev-subset.wp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.1836    |
| **RR@10**                                                                                                    | **BM25 (default)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.1752    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.6231    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8263    |
