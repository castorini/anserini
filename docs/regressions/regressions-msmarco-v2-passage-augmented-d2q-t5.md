# Anserini Regressions: MS MARCO (V2) Passage Ranking

**Models**: BM25 with doc2query-T5 expansions on augmented passages

This page describes regression experiments for passage ranking _on the augmented version_ of the MS MARCO V2 Passage Corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we expand the augmented passage corpus with doc2query-T5.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v2-passage-augmented-d2q-t5.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v2-passage-augmented-d2q-t5.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-augmented-d2q-t5
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage-augmented-d2q-t5 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -threads 24 -storeRaw \
  >& logs/log.msmarco-v2-passage-augmented-d2q-t5 &
```

The directory `/path/to/msmarco-v2-passage-augmented-d2q-t5/` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-augmented-d2q-t5/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt \
  -bm25 &
```

Evaluation can be performed using `trec_eval`:

```
java -jar target/trec_eval.jar -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt
java -jar target/trec_eval.jar -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt
java -jar target/trec_eval.jar -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev.txt
java -jar target/trec_eval.jar -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt
java -jar target/trec_eval.jar -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt
java -jar target/trec_eval.jar -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented-d2q-t5.bm25-default.topics.msmarco-v2-passage.dev2.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1160    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1158    |
| **MRR@100**                                                                                                  | **BM25 (default)**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1172    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1170    |
| **R@100**                                                                                                    | **BM25 (default)**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.5039    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.5158    |
| **R@1000**                                                                                                   | **BM25 (default)**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.7647    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.7659    |
