# <img src="../../anserini-logo.png" height="30" /> MS MARCO V1 Passage

**Anserini reproductions from prebuilt indexes for the MS MARCO V1 Passage collection (core)**

**Config**: [msmarco-v1-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.core.yaml)

## Summary

The table below summarizes the effectiveness of dev in terms of RR@10; DL19 and DL20 in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **dev** = msmarco-v1-passage.dev
+ **DL19** = dl19-passage
+ **DL20** = dl19-passage

| # | name | dev | DL19 | DL20 |
| --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.1840 | 0.5058 | 0.4796 |
| [2](#condition-2) | BM25 with doc2query-T5  (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.2723 | 0.6417 | 0.6187 |
| [3](#condition-3) | SPLADE-v3: ONNX | 0.4000 | 0.7264 | 0.7522 |
| [4](#condition-4) | bge-base-en-v1.5: HNSW, ONNX | 0.3575 | 0.7016 | 0.6768 |
| [5](#condition-5) | bge-base-en-v1.5: quantized (int8) HNSW, ONNX | 0.3575 | 0.7017 | 0.6767 |



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

### 1. BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

**Config**: [msmarco-v1-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.core.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.core.bm25.msmarco-v1-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.core.bm25.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.core.bm25.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.core.bm25.dl19-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.core.bm25.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.core.bm25.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.core.bm25.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.core.bm25.dl20-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.core.bm25.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.core.bm25.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.core.bm25.dl20-passage.txt
```

<a id="condition-2"></a>

### 2. BM25 with doc2query-T5  (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

**Config**: [msmarco-v1-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.core.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.d2q-t5 \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.core.bm25-d2q-t5.msmarco-v1-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.core.bm25-d2q-t5.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.core.bm25-d2q-t5.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.d2q-t5 \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl19-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.d2q-t5 \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl20-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.core.bm25-d2q-t5.dl20-passage.txt
```

<a id="condition-3"></a>

### 3. SPLADE-v3: ONNX

**Config**: [msmarco-v1-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.core.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-v3 \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.core.splade-v3.onnx.msmarco-v1-passage.dev.txt \
    -impact \
    -pretokenized \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.core.splade-v3.onnx.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.core.splade-v3.onnx.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-v3 \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl19-passage.txt \
    -impact \
    -pretokenized \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-v3 \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl20-passage.txt \
    -impact \
    -pretokenized \
    -hits 1000 \
    -encoder SpladeV3
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.core.splade-v3.onnx.dl20-passage.txt
```

<a id="condition-4"></a>

### 4. bge-base-en-v1.5: HNSW, ONNX

**Config**: [msmarco-v1-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.core.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.msmarco-v1-passage.dev.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder BgeBaseEn15
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl19-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder BgeBaseEn15
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl20-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder BgeBaseEn15
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw.onnx.dl20-passage.txt
```

<a id="condition-5"></a>

### 5. bge-base-en-v1.5: quantized (int8) HNSW, ONNX

**Config**: [msmarco-v1-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.core.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8 \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.msmarco-v1-passage.dev.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder BgeBaseEn15
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8 \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl19-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder BgeBaseEn15
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8 \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl20-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder BgeBaseEn15
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.core.bge-base-en-v1.5.hnsw-int8.onnx.dl20-passage.txt
```


