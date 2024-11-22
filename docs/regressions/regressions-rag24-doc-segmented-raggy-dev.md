# Anserini Regressions: TREC 2024 RAG Track RAGgy Dev Topics

**Models**: various bag-of-words approaches on segmented documents

This page describes experiments, integrated into Anserini's regression testing framework, on the "RAGgy dev topics" on the MS MARCO V2.1 _segmented_ document corpus.
These "RAGgy topics" were manually curated from the TREC 2021, 2022, and 2023 Deep Learning Tracks to be "RAG-worthy" according to the track organizers.

Here, we cover bag-of-words baselines where each _segment_ in the MS MARCO V2.1 segmented document corpus is treated as a unit of indexing.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/rag24-doc-segmented-raggy-dev.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/rag24-doc-segmented-raggy-dev.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-raggy-dev
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2DocCollection \
  -input /path/to/msmarco-v2.1-doc-segmented \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -storeRaw \
  >& logs/log.msmarco-v2.1-doc-segmented &
```

The setting of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
These "RAG-worthy" topics were manually curated from the TREC 2021, 2022, and 2023 Deep Learning Tracks and projected over to the V2.1 version of the corpus.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag24.raggy-dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.raggy-dev.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag24.raggy-dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.raggy-dev.txt \
  -bm25 -rm3 -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2.1-doc-segmented/ \
  -topics tools/topics-and-qrels/topics.rag24.raggy-dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.raggy-dev.txt \
  -bm25 -rocchio -collection MsMarcoV2DocCollection -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.raggy-dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default.topics.rag24.raggy-dev.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.raggy-dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rm3.topics.rag24.raggy-dev.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.raggy-dev.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.raggy-dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1-doc-segmented.bm25-default+rocchio.topics.rag24.raggy-dev.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| RAG 24: RAGgy dev queries                                                                                    | 0.1561    | 0.1791    | 0.1818    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: RAGgy dev queries                                                                                    | 0.7465    | 0.7418    | 0.7448    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: RAGgy dev queries                                                                                    | 0.4227    | 0.4093    | 0.4188    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: RAGgy dev queries                                                                                    | 0.2807    | 0.3088    | 0.3141    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| RAG 24: RAGgy dev queries                                                                                    | 0.5745    | 0.6145    | 0.6233    |
