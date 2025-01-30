# Anserini: Baselines for MS MARCO V2.1

The MS MARCO V2.1 collections were created for the [TREC RAG Track](https://trec-rag.github.io/).
It was the official corpus used in 2024 and will remain the corpus for 2025.
There are two separate MS MARCO V2.1 "variants", documents and segmented documents:

+ The segmented documents corpus (segments = passages) is the one actually used for the TREC RAG evaluations. It contains 113,520,750 passages.
+ The documents corpus is the source of the segments and useful as a point of reference. It contains 10,960,555 documents.

This guide focuses on the segmented documents corpus.

## Effectiveness Summary

### TREC 2024 RAG

<!-- https://trec-rag.github.io/annoucements/umbrela-qrels/ -->

With Anserini, you can reproduce baseline runs on the TREC 2024 RAG test queries using BM25 and ArcticEmbed-L embeddings.
Using the UMBRELA qrels, these are the results you'd get:

**nDCG@20**

| Dataset              |  BM25  | ArcticEmbed-L |
|:---------------------|:------:|:-------------:|
| RAG24 Test (UMBRELA) | 0.3198 |    0.5603     |

**nDCG@100**

| Dataset              |  BM25  | ArcticEmbed-L |
|:---------------------|:------:|:-------------:|
| RAG24 Test (UMBRELA) | 0.2563 |    0.5603     |

**Recall@100**

| Dataset               |  BM25  | ArcticEmbed-L |
|:----------------------|:------:|:-------------:|
| RAG24 Test  (UMBRELA) | 0.1395 |    0.2547     |

See instructions below on how to reproduce these runs.

### Dev Queries

With Anserini, you can reproduce baseline runs on the TREC 2024 RAG "dev queries".
These capture topics and _document-level_ qrels originally targeted at the V2 documents corpus, but have been "projected" over to the V2.1 corpus.

**nDCG@10**

| Dataset     |  BM25  | ArcticEmbed-L |
|:------------|:------:|:-------------:|
| dev         | 0.2301 |    0.3545     |
| dev2        | 0.2339 |    0.3533     |
| DL21        | 0.5778 |    0.6989     |
| DL22        | 0.3576 |    0.5465     |
| DL23        | 0.3356 |    0.4644     |
| RAG24 RAGGy | 0.4227 |    0.5770     |

**Recall@100**

| Dataset     |  BM25  | ArcticEmbed-L |
|:------------|:------:|:-------------:|
| dev         | 0.6683 |    0.8385     |
| dev2        | 0.6771 |    0.8337     |
| DL21        | 0.3811 |    0.4077     |
| DL22        | 0.2330 |    0.3147     |
| DL23        | 0.3049 |    0.3490     |
| RAG24 RAGGy | 0.2807 |    0.3624     |

See instructions below on how to reproduce these runs.

## BM25 Baselines

For the MS MARCO V2.1 segmented document collection, Anserini has prebuilt indexes

❗ Beware, the `msmarco-v2.1-doc-segmented` prebuilt index is 84 GB uncompressed.
Both indexes will be downloaded automatically.
See [this guide on prebuilt indexes](prebuilt-indexes.md) for more details.

Here's how you reproduce results on the TREC 2024 RAG Track test queries:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index msmarco-v2.1-doc-segmented \
  -topics rag24.test \
  -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt \
  -bm25 -hits 1000
```

And to evaluate:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
```

You should arrive at exactly the effectiveness metrics above.
Note that these are _passage_level_ relevance judgments.

Here's how you reproduce results on the "dev queries" (we iterate over the different query sets).
Note that here we are generating document-level runs via the MaxP technique (i.e., each document is represented by its highest-scoring passage).

```bash
TOPICS=(msmarco-v2-doc.dev msmarco-v2-doc.dev2 dl21-doc dl22-doc dl23-doc rag24.raggy-dev); for t in "${TOPICS[@]}"
do
    bin/run.sh io.anserini.search.SearchCollection -index msmarco-v2.1-doc-segmented -topics $t -output runs/run.msmarco-v2.1.doc-segmented.${t}.txt -threads 16 -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000
done
```

You should arrive at exactly the effectiveness metrics above.
Note that these are _document_level_ relevance judgments.

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt runs/run.msmarco-v2.1.doc-segmented.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt runs/run.msmarco-v2.1.doc-segmented.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1.doc-segmented.dl21-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1.doc-segmented.dl22-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1.doc-segmented.dl23-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1.doc-segmented.rag24.raggy-dev.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt runs/run.msmarco-v2.1.doc-segmented.msmarco-v2-doc.dev.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt runs/run.msmarco-v2.1.doc-segmented.msmarco-v2-doc.dev2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1.doc-segmented.dl21-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl22-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1.doc-segmented.dl22-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.msmarco-v2.1.doc-segmented.dl23-doc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.msmarco-v2.1.doc-segmented.rag24.raggy-dev.txt
```

## ArcticEmbed-L Baselines

For the MS MARCO V2.1 segmented document collection, Anserini has prebuilt indexes with ArcticEmbed-L embeddings.
The embedding vectors were generated by Snowflake and are freely downloadable [on Hugging Face](https://huggingface.co/datasets/Snowflake/msmarco-v2.1-snowflake-arctic-embed-l).
We provide prebuilt HNSW indexes with int8 quantization, divided into 10 shards, `00` to `09`.

❗ Beware, the complete ArcticEmbed-L index for all 10 shards of the MS MARCO V2.1 segmented document collection totals 558 GB!
The commands below will download the indexes automatically, so make sure you have plenty of space.
See [this guide on prebuilt indexes](prebuilt-indexes.md) for general info on prebuilt indexes.
Additional helpful tips are provided below for dealing with space issues.

Here's how you reproduce results on the TREC 2024 RAG Track test queries:

```bash
# RAG24 test
SHARDS=(00 01 02 03 04 05 06 07 08 09); for shard in "${SHARDS[@]}"
do
    bin/run.sh io.anserini.search.SearchHnswDenseVectors -index msmarco-v2.1-doc-segmented-shard${shard}.arctic-embed-l.hnsw-int8 -efSearch 1000 -topics rag24.test.snowflake-arctic-embed-l -output runs/run.rag24.test.arctic-l-msv2.1.shard${shard}.txt -hits 250 -threads 32 > logs/log.run.rag24.test.arctic-l-msv2.1.shard${shard}.txt 2>&1
done
```

Note that here we are generating passage-level runs.
As it turns out, for evaluation purposes, you can just cat all the 10 run files together and evaluate:

```bash
cat runs/run.rag24.test.arctic-l-msv2.1.shard0* > runs/run.rag24.test.arctic-l-msv2.1.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.rag24.test.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.rag24.test.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.test-umbrela-all.txt runs/run.rag24.test.arctic-l-msv2.1.txt
```

You should arrive at exactly the effectiveness metrics above.
Note that these are _passage_level_ relevance judgments.

Here's how you reproduce results on the "dev queries".
Note that here we are generating document-level runs via the MaxP technique (i.e., each document is represented by its highest-scoring passage).

```bash
# dev
SHARDS=(00 01 02 03 04 05 06 07 08 09); for shard in "${SHARDS[@]}"
do
    bin/run.sh io.anserini.search.SearchHnswDenseVectors -index msmarco-v2.1-doc-segmented-shard${shard}.arctic-embed-l.hnsw-int8 -efSearch 1000 -topics msmarco-v2-doc.dev.snowflake-arctic-embed-l -output runs/run.msmarco-v2-doc.dev.arctic-l-msv2.1.shard${shard}.txt -threads 32 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 250 > logs/log.run.msmarco-v2-doc.dev.arctic-l-msv2.1.shard${shard}.txt 2>&1
done

# dev2
SHARDS=(00 01 02 03 04 05 06 07 08 09); for shard in "${SHARDS[@]}"
do
    bin/run.sh io.anserini.search.SearchHnswDenseVectors -index msmarco-v2.1-doc-segmented-shard${shard}.arctic-embed-l.hnsw-int8 -efSearch 1000 -topics msmarco-v2-doc.dev2.snowflake-arctic-embed-l -output runs/run.msmarco-v2-doc.dev2.arctic-l-msv2.1.shard${shard}.txt -threads 32 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 250 > logs/log.run.msmarco-v2-doc.dev2.arctic-l-msv2.1.shard${shard}.txt 2>&1
done

# DL21
SHARDS=(00 01 02 03 04 05 06 07 08 09); for shard in "${SHARDS[@]}"
do
    bin/run.sh io.anserini.search.SearchHnswDenseVectors -index msmarco-v2.1-doc-segmented-shard${shard}.arctic-embed-l.hnsw-int8 -efSearch 1000 -topics dl21.snowflake-arctic-embed-l -output runs/run.dl21.arctic-l-msv2.1.shard${shard}.txt -threads 32 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 250 > logs/log.run.dl21.arctic-l-msv2.1.shard${shard}.txt 2>&1
done

# DL22
SHARDS=(00 01 02 03 04 05 06 07 08 09); for shard in "${SHARDS[@]}"
do
    bin/run.sh io.anserini.search.SearchHnswDenseVectors -index msmarco-v2.1-doc-segmented-shard${shard}.arctic-embed-l.hnsw-int8 -efSearch 1000 -topics dl22.snowflake-arctic-embed-l -output runs/run.dl22.arctic-l-msv2.1.shard${shard}.txt -threads 32 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 250 > logs/log.run.dl22.arctic-l-msv2.1.shard${shard}.txt 2>&1
done

# DL23
SHARDS=(00 01 02 03 04 05 06 07 08 09); for shard in "${SHARDS[@]}"
do
    bin/run.sh io.anserini.search.SearchHnswDenseVectors -index msmarco-v2.1-doc-segmented-shard${shard}.arctic-embed-l.hnsw-int8 -efSearch 1000 -topics dl23.snowflake-arctic-embed-l -output runs/run.dl23.arctic-l-msv2.1.shard${shard}.txt -threads 32 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 250 > logs/log.run.dl23.arctic-l-msv2.1.shard${shard}.txt 2>&1
done

# RAG24 Raggy
SHARDS=(00 01 02 03 04 05 06 07 08 09); for shard in "${SHARDS[@]}"
do
    bin/run.sh io.anserini.search.SearchHnswDenseVectors -index msmarco-v2.1-doc-segmented-shard${shard}.arctic-embed-l.hnsw-int8 -efSearch 1000 -topics rag24.raggy-dev.snowflake-arctic-embed-l -output runs/run.rag24.raggy-dev.arctic-l-msv2.1.shard${shard}.txt -threads 32 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 250 > logs/log.run.rag24.raggy-dev.arctic-l-msv2.1.shard${shard}.txt 2>&1
done
```

As it turns out, for evaluation purposes, you can just cat all the 10 run files together and evaluate:

```
cat runs/run.msmarco-v2-doc.dev.arctic-l-msv2.1.shard0* > runs/run.msmarco-v2-doc.dev.arctic-l-msv2.1.txt
cat runs/run.msmarco-v2-doc.dev2.arctic-l-msv2.1.shard0* > runs/run.msmarco-v2-doc.dev2.arctic-l-msv2.1.txt
cat runs/run.dl21.arctic-l-msv2.1.shard0* > runs/run.dl21.arctic-l-msv2.1.txt
cat runs/run.dl22.arctic-l-msv2.1.shard0* > runs/run.dl22.arctic-l-msv2.1.txt
cat runs/run.dl23.arctic-l-msv2.1.shard0* > runs/run.dl23.arctic-l-msv2.1.txt
cat runs/run.rag24.raggy-dev.arctic-l-msv2.1.shard0* > runs/run.rag24.raggy-dev.arctic-l-msv2.1.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt runs/run.msmarco-v2-doc.dev.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt runs/run.msmarco-v2-doc.dev2.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.dl21.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-doc-msmarco-v2.1.txt runs/run.dl22.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.dl23.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.rag24.raggy-dev.arctic-l-msv2.1.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt runs/run.msmarco-v2-doc.dev.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt runs/run.msmarco-v2-doc.dev2.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt runs/run.dl21.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl22-doc-msmarco-v2.1.txt runs/run.dl22.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt runs/run.dl23.arctic-l-msv2.1.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.rag24.raggy-dev.txt runs/run.rag24.raggy-dev.arctic-l-msv2.1.txt
```

You should arrive at exactly the effectiveness metrics above.
Note that these are _document_level_ relevance judgments.

The indexes for ArcticEmbed-L are big!
Here's specifically their sizes, in GB:

```
56	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.20250114.4884f5.aab3f8e9aa0563bd0f875584784a0845
51	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.20250114.4884f5.34ea30fe72c2bc1795ae83e71b191547
64	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.20250114.4884f5.b6271d6db65119977491675f74f466d5
61	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.20250114.4884f5.a9cd644eb6037f67d2e9c06a8f60928d
58	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.20250114.4884f5.07b7e451e0525d01c1f1f2b1c42b1bd5
56	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.20250114.4884f5.2573dce175788981be2f266ebb33c96d
54	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.20250114.4884f5.a644aea445a8b78cc9e99d2ce111ff11
52	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.20250114.4884f5.402d37deccb44b5fc105049889e8aaea
58	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.20250114.4884f5.89ebcd027f7297b26a1edc8ae5726527
52	lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.20250114.4884f5.5e580bb7eb9ee2bb6bfa492b3430c17d
558	total
```

The list above shows the complete index directory name after each index shard has been downloaded and unpacked into `~/.cache/pyserini/indexes/`.

One helpful tip is to share the indexes among multiple people using symlinks, instead of everyone having their own copy.
Something like:

```
cd ~/.cache/pyserini/indexes/
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.20250114.4884f5.aab3f8e9aa0563bd0f875584784a0845 .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.20250114.4884f5.34ea30fe72c2bc1795ae83e71b191547 .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.20250114.4884f5.b6271d6db65119977491675f74f466d5 .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.20250114.4884f5.a9cd644eb6037f67d2e9c06a8f60928d .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.20250114.4884f5.07b7e451e0525d01c1f1f2b1c42b1bd5 .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.20250114.4884f5.2573dce175788981be2f266ebb33c96d .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.20250114.4884f5.a644aea445a8b78cc9e99d2ce111ff11 .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.20250114.4884f5.402d37deccb44b5fc105049889e8aaea .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.20250114.4884f5.89ebcd027f7297b26a1edc8ae5726527 .
ln -s /path/to/lucene-hnsw-int8.msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.20250114.4884f5.5e580bb7eb9ee2bb6bfa492b3430c17d .
```

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@xxx](https://github.com/xxx) on 2025-xx-xx (commit [`xxxxxx`](https://github.com/castorini/anserini/commit/xxxxxx))
