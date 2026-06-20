# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2.1 Segmented Doc

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: MS MARCO V2.1 Segmented Doc
+ **Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

## Summary

The table below summarizes the effectiveness of RAG24 Umbrela-all and RAG24 test in terms of nDCG@20, and RAG25 Umbrela2 and RAG25 test in terms of nDCG@30.
For more metrics, refer to the config directly.

Key:

+ **RAG24 ☂️** = rag24.test-umbrela-all
+ **RAG24 NIST** = rag24.test
+ **RAG25 ☂️** = rag25.test-umbrela2
+ **RAG25 NIST** = rag25.test

| # | name | RAG24 ☂️ | RAG24 NIST | RAG25 ☂️ | RAG25 NIST |
| --- | --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 doc segmented (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), slim index | 0.3198 | 0.2809 | 0.3250 | 0.3468 |
| [2](#condition-2) | BM25 doc segmented (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), regular index | 0.3198 | 0.2809 | 0.3250 | 0.3468 |
| [3](#condition-3) | SPLADE-v3 (ONNX) | 0.5167 | 0.4642 | 0.5838 | 0.5957 |
| [4](#condition-4) | ArcticEmbed-L (shard00) with quantized HNSW (ONNX) | 0.3003 | 0.2449 | 0.2916 | 0.2793 |
| [5](#condition-5) | ArcticEmbed-L (shard01) with quantized HNSW (ONNX) | 0.2599 | 0.2184 | 0.2581 | 0.2604 |
| [6](#condition-6) | ArcticEmbed-L (shard02) with quantized HNSW (ONNX) | 0.2661 | 0.2211 | 0.2486 | 0.2429 |
| [7](#condition-7) | ArcticEmbed-L (shard03) with quantized HNSW (ONNX) | 0.2705 | 0.2388 | 0.2609 | 0.2874 |
| [8](#condition-8) | ArcticEmbed-L (shard04) with quantized HNSW (ONNX) | 0.2937 | 0.2253 | 0.2737 | 0.2687 |
| [9](#condition-9) | ArcticEmbed-L (shard05) with quantized HNSW (ONNX) | 0.2590 | 0.2383 | 0.2190 | 0.2499 |
| [10](#condition-10) | ArcticEmbed-L (shard06) with quantized HNSW (ONNX) | 0.2444 | 0.2336 | 0.1751 | 0.1783 |
| [11](#condition-11) | ArcticEmbed-L (shard07) with quantized HNSW (ONNX) | 0.2417 | 0.2255 | 0.2178 | 0.2230 |
| [12](#condition-12) | ArcticEmbed-L (shard08) with quantized HNSW (ONNX) | 0.2847 | 0.2765 | 0.2390 | 0.2312 |
| [13](#condition-13) | ArcticEmbed-L (shard09) with quantized HNSW (ONNX) | 0.2432 | 0.2457 | 0.2170 | 0.2182 |



The BM25 "slim index" does not contain the document texts, whereas the BM25 "regular index" does contain document texts (and hence supports pseudo-relevance feedback with on-the-fly document parsing).

## Commands

In the commands below:

+ Set `$fatjar` to the actual Anserini fatjar.
+ Set JVM args to `-Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector`.

Something like:

```bash
export fatjar=`ls -d {.,target}/anserini-*-fatjar.jar(N)`

# for zsh
export jvm_args=(-Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector)

# for bash
export jvm_args="-Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector"
```

<a id="condition-1"></a>

### 1. BM25 doc segmented (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), slim index

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-slim \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-slim \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-slim \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-slim \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.bm25-slim.rag25.test.txt
```

<a id="condition-2"></a>

### 2. BM25 doc segmented (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), regular index

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.bm25.rag25.test.txt
```

<a id="condition-3"></a>

### 3. SPLADE-v3 (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.splade-v3.onnx.rag25.test.txt
```

<a id="condition-4"></a>

### 4. ArcticEmbed-L (shard00) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-5"></a>

### 5. ArcticEmbed-L (shard01) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-6"></a>

### 6. ArcticEmbed-L (shard02) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-7"></a>

### 7. ArcticEmbed-L (shard03) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-8"></a>

### 8. ArcticEmbed-L (shard04) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-9"></a>

### 9. ArcticEmbed-L (shard05) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-10"></a>

### 10. ArcticEmbed-L (shard06) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-11"></a>

### 11. ArcticEmbed-L (shard07) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-12"></a>

### 12. ArcticEmbed-L (shard08) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-13"></a>

### 13. ArcticEmbed-L (shard09) with quantized HNSW (ONNX)

**Config**: [msmarco-v2.1-doc-segmented.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
```


