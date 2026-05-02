# <img src="../../anserini-logo.png" height="30" /> MS MARCO V1 Passage

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: MS MARCO V1 Passage
+ **Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

## Summary

The table below summarizes the effectiveness of dev in terms of RR@10; DL19 and DL20 in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **dev** = msmarco-v1-passage.dev
+ **DL19** = dl19-passage
+ **DL20** = dl19-passage

| # | name | dev | DL19 | DL20 |
| --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), slim index | 0.1840 | 0.5058 | 0.4796 |
| [2](#condition-2) | BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), full index | 0.1840 | 0.5058 | 0.4796 |
| [3](#condition-3) | SPLADE++ EnsembleDistil (cached queries) | 0.3830 | 0.7317 | 0.7198 |
| [4](#condition-4) | SPLADE++ EnsembleDistil (ONNX) | 0.3828 | 0.7308 | 0.7197 |
| [5](#condition-5) | SPLADE-v3 (cached queries) | 0.3999 | 0.7264 | 0.7522 |
| [6](#condition-6) | cosDPR-distil with HNSW (cached queries) | 0.3887 | 0.7250 | 0.7025 |
| [7](#condition-7) | cosDPR-distil with HNSW (ONNX) | 0.3887 | 0.7250 | 0.7025 |
| [8](#condition-8) | cosDPR-distil with quantized HNSW (cached queries) | 0.3897 | 0.7240 | 0.7004 |
| [9](#condition-9) | cosDPR-distil with quantized HNSW (ONNX) | 0.3899 | 0.7247 | 0.6996 |
| [10](#condition-10) | bge-base-en-v1.5 with HNSW (cached queries) | 0.3574 | 0.7065 | 0.6780 |
| [11](#condition-11) | bge-base-en-v1.5 with quantized HNSW (cached queries) | 0.3572 | 0.7016 | 0.6738 |
| [12](#condition-12) | cohere-embed-english-v3.0 with HNSW (cached queries) | 0.3647 | 0.6956 | 0.7245 |
| [13](#condition-13) | cohere-embed-english-v3.0 with quantized HNSW (cached queries) | 0.3656 | 0.6955 | 0.7262 |



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

### 1. BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), slim index

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage-slim \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.optional.bm25-slim.msmarco-v1-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bm25-slim.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bm25-slim.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage-slim \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.optional.bm25-slim.dl19-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.bm25-slim.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.bm25-slim.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.bm25-slim.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage-slim \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.optional.bm25-slim.dl20-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.bm25-slim.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.bm25-slim.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.bm25-slim.dl20-passage.txt
```

<a id="condition-2"></a>

### 2. BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4), full index

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage-full \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.optional.bm25-full.msmarco-v1-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bm25-full.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bm25-full.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage-full \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.optional.bm25-full.dl19-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.bm25-full.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.bm25-full.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.bm25-full.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage-full \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.optional.bm25-full.dl20-passage.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.bm25-full.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.bm25-full.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.bm25-full.dl20-passage.txt
```

<a id="condition-3"></a>

### 3. SPLADE++ EnsembleDistil (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.splade-pp-ed

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-pp-ed \
    -topics msmarco-v1-passage.dev.splade-pp-ed \
    -output runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.msmarco-v1-passage.dev.splade-pp-ed.txt \
    -impact \
    -pretokenized \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.msmarco-v1-passage.dev.splade-pp-ed.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.msmarco-v1-passage.dev.splade-pp-ed.txt
```

#### dl19-passage.splade-pp-ed

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-pp-ed \
    -topics dl19-passage.splade-pp-ed \
    -output runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl19-passage.splade-pp-ed.txt \
    -impact \
    -pretokenized \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl19-passage.splade-pp-ed.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl19-passage.splade-pp-ed.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl19-passage.splade-pp-ed.txt
```

#### dl20-passage.splade-pp-ed

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-pp-ed \
    -topics dl20-passage.splade-pp-ed \
    -output runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl20-passage.splade-pp-ed.txt \
    -impact \
    -pretokenized \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl20-passage.splade-pp-ed.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl20-passage.splade-pp-ed.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.cached.dl20-passage.splade-pp-ed.txt
```

<a id="condition-4"></a>

### 4. SPLADE++ EnsembleDistil (ONNX)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-pp-ed \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.msmarco-v1-passage.dev.txt \
    -impact \
    -pretokenized \
    -hits 1000 \
    -encoder SpladePlusPlusEnsembleDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-pp-ed \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl19-passage.txt \
    -impact \
    -pretokenized \
    -hits 1000 \
    -encoder SpladePlusPlusEnsembleDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-pp-ed \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl20-passage.txt \
    -impact \
    -pretokenized \
    -hits 1000 \
    -encoder SpladePlusPlusEnsembleDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.splade-pp-ed.onnx.dl20-passage.txt
```

<a id="condition-5"></a>

### 5. SPLADE-v3 (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.splade-v3

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-v3 \
    -topics msmarco-v1-passage.dev.splade-v3 \
    -output runs/run.msmarco-v1-passage.optional.splade-v3.cached.msmarco-v1-passage.dev.splade-v3.txt \
    -impact \
    -pretokenized \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.splade-v3.cached.msmarco-v1-passage.dev.splade-v3.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.splade-v3.cached.msmarco-v1-passage.dev.splade-v3.txt
```

#### dl19-passage.splade-v3

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-v3 \
    -topics dl19-passage.splade-v3 \
    -output runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl19-passage.splade-v3.txt \
    -impact \
    -pretokenized \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl19-passage.splade-v3.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl19-passage.splade-v3.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl19-passage.splade-v3.txt
```

#### dl20-passage.splade-v3

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-passage.splade-v3 \
    -topics dl20-passage.splade-v3 \
    -output runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl20-passage.splade-v3.txt \
    -impact \
    -pretokenized \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl20-passage.splade-v3.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl20-passage.splade-v3.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.splade-v3.cached.dl20-passage.splade-v3.txt
```

<a id="condition-6"></a>

### 6. cosDPR-distil with HNSW (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.cosdpr-distil

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw \
    -topics msmarco-v1-passage.dev.cosdpr-distil \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.msmarco-v1-passage.dev.cosdpr-distil.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.msmarco-v1-passage.dev.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.msmarco-v1-passage.dev.cosdpr-distil.txt
```

#### dl19-passage.cosdpr-distil

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw \
    -topics dl19-passage.cosdpr-distil \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl19-passage.cosdpr-distil.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl19-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl19-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl19-passage.cosdpr-distil.txt
```

#### dl20-passage.cosdpr-distil

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw \
    -topics dl20-passage.cosdpr-distil \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl20-passage.cosdpr-distil.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl20-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl20-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.cached.dl20-passage.cosdpr-distil.txt
```

<a id="condition-7"></a>

### 7. cosDPR-distil with HNSW (ONNX)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.msmarco-v1-passage.dev.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder CosDprDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl19-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder CosDprDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl20-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder CosDprDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw.onnx.dl20-passage.txt
```

<a id="condition-8"></a>

### 8. cosDPR-distil with quantized HNSW (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.cosdpr-distil

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw-int8 \
    -topics msmarco-v1-passage.dev.cosdpr-distil \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.msmarco-v1-passage.dev.cosdpr-distil.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.msmarco-v1-passage.dev.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.msmarco-v1-passage.dev.cosdpr-distil.txt
```

#### dl19-passage.cosdpr-distil

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw-int8 \
    -topics dl19-passage.cosdpr-distil \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl19-passage.cosdpr-distil.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl19-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl19-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl19-passage.cosdpr-distil.txt
```

#### dl20-passage.cosdpr-distil

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw-int8 \
    -topics dl20-passage.cosdpr-distil \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl20-passage.cosdpr-distil.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl20-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl20-passage.cosdpr-distil.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.cached.dl20-passage.cosdpr-distil.txt
```

<a id="condition-9"></a>

### 9. cosDPR-distil with quantized HNSW (ONNX)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw-int8 \
    -topics msmarco-v1-passage.dev \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.msmarco-v1-passage.dev.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder CosDprDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.msmarco-v1-passage.dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.msmarco-v1-passage.dev.txt
```

#### dl19-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw-int8 \
    -topics dl19-passage \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl19-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder CosDprDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl19-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl19-passage.txt
```

#### dl20-passage

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cosdpr-distil.hnsw-int8 \
    -topics dl20-passage \
    -output runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl20-passage.txt \
    -efSearch 1000 \
    -hits 1000 \
    -encoder CosDprDistil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl20-passage.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.cosdpr-distil.hnsw-int8.onnx.dl20-passage.txt
```

<a id="condition-10"></a>

### 10. bge-base-en-v1.5 with HNSW (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.bge-base-en-v1.5

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
    -topics msmarco-v1-passage.dev.bge-base-en-v1.5 \
    -output runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.msmarco-v1-passage.dev.bge-base-en-v1.5.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.msmarco-v1-passage.dev.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.msmarco-v1-passage.dev.bge-base-en-v1.5.txt
```

#### dl19-passage.bge-base-en-v1.5

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
    -topics dl19-passage.bge-base-en-v1.5 \
    -output runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl19-passage.bge-base-en-v1.5.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl19-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl19-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl19-passage.bge-base-en-v1.5.txt
```

#### dl20-passage.bge-base-en-v1.5

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
    -topics dl20-passage.bge-base-en-v1.5 \
    -output runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl20-passage.bge-base-en-v1.5.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl20-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl20-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw.cached.dl20-passage.bge-base-en-v1.5.txt
```

<a id="condition-11"></a>

### 11. bge-base-en-v1.5 with quantized HNSW (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.bge-base-en-v1.5

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8 \
    -topics msmarco-v1-passage.dev.bge-base-en-v1.5 \
    -output runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.msmarco-v1-passage.dev.bge-base-en-v1.5.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.msmarco-v1-passage.dev.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.msmarco-v1-passage.dev.bge-base-en-v1.5.txt
```

#### dl19-passage.bge-base-en-v1.5

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8 \
    -topics dl19-passage.bge-base-en-v1.5 \
    -output runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl19-passage.bge-base-en-v1.5.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl19-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl19-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl19-passage.bge-base-en-v1.5.txt
```

#### dl20-passage.bge-base-en-v1.5

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8 \
    -topics dl20-passage.bge-base-en-v1.5 \
    -output runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl20-passage.bge-base-en-v1.5.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl20-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl20-passage.bge-base-en-v1.5.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.bge-base-en-v1.5.hnsw-int8.cached.dl20-passage.bge-base-en-v1.5.txt
```

<a id="condition-12"></a>

### 12. cohere-embed-english-v3.0 with HNSW (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.cohere-embed-english-v3.0

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cohere-embed-english-v3.0.hnsw \
    -topics msmarco-v1-passage.dev.cohere-embed-english-v3.0 \
    -output runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt
```

#### dl19-passage.cohere-embed-english-v3.0

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cohere-embed-english-v3.0.hnsw \
    -topics dl19-passage.cohere-embed-english-v3.0 \
    -output runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl19-passage.cohere-embed-english-v3.0.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl19-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl19-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl19-passage.cohere-embed-english-v3.0.txt
```

#### dl20-passage.cohere-embed-english-v3.0

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cohere-embed-english-v3.0.hnsw \
    -topics dl20-passage.cohere-embed-english-v3.0 \
    -output runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl20-passage.cohere-embed-english-v3.0.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl20-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl20-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw.cached.dl20-passage.cohere-embed-english-v3.0.txt
```

<a id="condition-13"></a>

### 13. cohere-embed-english-v3.0 with quantized HNSW (cached queries)

**Config**: [msmarco-v1-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.optional.yaml)

#### msmarco-v1-passage.dev.cohere-embed-english-v3.0

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cohere-embed-english-v3.0.hnsw-int8 \
    -topics msmarco-v1-passage.dev.cohere-embed-english-v3.0 \
    -output runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 10 -m recip_rank msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -m recall.1000 msmarco-passage.dev runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt
```

#### dl19-passage.cohere-embed-english-v3.0

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cohere-embed-english-v3.0.hnsw-int8 \
    -topics dl19-passage.cohere-embed-english-v3.0 \
    -output runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl19-passage.cohere-embed-english-v3.0.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl19-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl19-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl19-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl19-passage.cohere-embed-english-v3.0.txt
```

#### dl20-passage.cohere-embed-english-v3.0

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchHnswDenseVectors \
    -threads 16 \
    -index msmarco-v1-passage.cohere-embed-english-v3.0.hnsw-int8 \
    -topics dl20-passage.cohere-embed-english-v3.0 \
    -output runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl20-passage.cohere-embed-english-v3.0.txt \
    -efSearch 1000 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -l 2 -m map dl20-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl20-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl20-passage.cohere-embed-english-v3.0.txt
java -cp $fatjar trec_eval -c -l 2 -m recall.1000 dl20-passage runs/run.msmarco-v1-passage.optional.cohere-embed-english-v3.0.hnsw-int8.cached.dl20-passage.cohere-embed-english-v3.0.txt
```


