# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2.1 Segmented Doc

**Anserini reproductions from prebuilt indexes for the MS MARCO V2.1 Segmented Doc collection (core)**

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

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
| [1](#condition-1) | BM25 segmented doc (k1=0.9, b=0.4) | 0.3198 | 0.2809 | 0.3250 | 0.3468 |
| [2](#condition-2) | SPLADE-v3: ONNX | 0.5167 | 0.4642 | 0.5838 | 0.5957 |
| [3](#condition-3) | ArcticEmbed-L (shard00): quantized (int8) HNSW, ONNX | 0.3003 | 0.2449 | 0.2916 | 0.2793 |
| [4](#condition-4) | ArcticEmbed-L (shard01): quantized (int8) HNSW, ONNX | 0.2599 | 0.2184 | 0.2581 | 0.2604 |
| [5](#condition-5) | ArcticEmbed-L (shard02): quantized (int8) HNSW, ONNX | 0.2661 | 0.2211 | 0.2486 | 0.2429 |
| [6](#condition-6) | ArcticEmbed-L (shard03): quantized (int8) HNSW, ONNX | 0.2705 | 0.2388 | 0.2609 | 0.2874 |
| [7](#condition-7) | ArcticEmbed-L (shard04): quantized (int8) HNSW, ONNX | 0.2937 | 0.2253 | 0.2737 | 0.2687 |
| [8](#condition-8) | ArcticEmbed-L (shard05): quantized (int8) HNSW, ONNX | 0.2590 | 0.2383 | 0.2190 | 0.2499 |
| [9](#condition-9) | ArcticEmbed-L (shard06): quantized (int8) HNSW, ONNX | 0.2444 | 0.2336 | 0.1751 | 0.1783 |
| [10](#condition-10) | ArcticEmbed-L (shard07): quantized (int8) HNSW, ONNX | 0.2417 | 0.2255 | 0.2178 | 0.2230 |
| [11](#condition-11) | ArcticEmbed-L (shard08: quantized (int8) HNSW, ONNX | 0.2847 | 0.2765 | 0.2390 | 0.2312 |
| [12](#condition-12) | ArcticEmbed-L (shard09): quantized (int8) HNSW, ONNX | 0.2432 | 0.2457 | 0.2170 | 0.2182 |



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
export jvm_argsS="-Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector"
```

<a id="condition-1"></a>

### 1. BM25 segmented doc (k1=0.9, b=0.4)

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.bm25.rag25.test.txt
```

<a id="condition-2"></a>

### 2. SPLADE-v3: ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag24.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented.splade-v3 \
    -topics rag25.test \
    -output runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt \
    -impact \
    -pretokenized \
    -removeQuery \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.splade-v3.onnx.rag25.test.txt
```

<a id="condition-3"></a>

### 3. ArcticEmbed-L (shard00): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard00.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-4"></a>

### 4. ArcticEmbed-L (shard01): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard01.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-5"></a>

### 5. ArcticEmbed-L (shard02): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard02.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-6"></a>

### 6. ArcticEmbed-L (shard03): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard03.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-7"></a>

### 7. ArcticEmbed-L (shard04): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard04.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-8"></a>

### 8. ArcticEmbed-L (shard05): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard05.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-9"></a>

### 9. ArcticEmbed-L (shard06): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard06.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-10"></a>

### 10. ArcticEmbed-L (shard07): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard07.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-11"></a>

### 11. ArcticEmbed-L (shard08: quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard08.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

<a id="condition-12"></a>

### 12. ArcticEmbed-L (shard09): quantized (int8) HNSW, ONNX

**Config**: [msmarco-v2.1-doc-segmented.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc-segmented.core.yaml)

#### rag24.test / rag24.test-umbrela-all

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test-umbrela-all runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag24.test / rag24.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag24.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.20 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.test runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag24.test.txt
```

#### rag25.test / rag25.test-umbrela2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test-umbrela2 runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
```

#### rag25.test / rag25.test

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8 \
    -topics rag25.test \
    -encoder ArcticEmbedL \
    -output runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt \
    -hits 250 \
    -efSearch 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.30 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
java -cp $fatjar trec_eval -c -m recall.100 rag25.test runs/run.msmarco-v2.1-doc-segmented.core.shard09.arctic-l.hnsw-int8.onnx.rag25.test.txt
```


