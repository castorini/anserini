# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2 Passage

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: MS MARCO V2 Passage
+ **Config**: [msmarco-v2-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.core.yaml)

## Summary

The table below summarizes the effectiveness of dev and dev2 in terms of RR@100; DL21, DL22, and DL23 in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **dev** = msmarco-v2-passage.dev
+ **dev2** = msmarco-v2-passage.dev2
+ **DL21** = dl21-passage
+ **DL22** = dl22-passage
+ **DL23** = dl23-passage

| # | name | dev | dev2 | DL21 | DL22 | DL23 |
| --- | --- | --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.0719 | 0.0802 | 0.4458 | 0.2692 | 0.2627 |
| [2](#condition-2) | BM25 with doc2query-T5 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.1072 | 0.1123 | 0.4816 | 0.3599 | 0.3156 |
| [3](#condition-3) | uniCOIL with doc2query-T5 (ONNX) | 0.1499 | 0.1577 | 0.6159 | 0.4614 | 0.3855 |



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

### 1. BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

**Config**: [msmarco-v2-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.core.yaml)

#### msmarco-v2-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage \
    -topics msmarco-v2-passage.dev \
    -output runs/run.msmarco-v2-passage.core.bm25.msmarco-v2-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev runs/run.msmarco-v2-passage.core.bm25.msmarco-v2-passage.dev.txt
```

#### msmarco-v2-passage.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage \
    -topics msmarco-v2-passage.dev2 \
    -output runs/run.msmarco-v2-passage.core.bm25.msmarco-v2-passage.dev2.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev2 runs/run.msmarco-v2-passage.core.bm25.msmarco-v2-passage.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage \
    -topics dl21 \
    -output runs/run.msmarco-v2-passage.core.bm25.dl21.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-passage runs/run.msmarco-v2-passage.core.bm25.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage \
    -topics dl22 \
    -output runs/run.msmarco-v2-passage.core.bm25.dl22.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-passage runs/run.msmarco-v2-passage.core.bm25.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage \
    -topics dl23 \
    -output runs/run.msmarco-v2-passage.core.bm25.dl23.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-passage runs/run.msmarco-v2-passage.core.bm25.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25.dl23.txt
```

<a id="condition-2"></a>

### 2. BM25 with doc2query-T5 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

**Config**: [msmarco-v2-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.core.yaml)

#### msmarco-v2-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.d2q-t5 \
    -topics msmarco-v2-passage.dev \
    -output runs/run.msmarco-v2-passage.core.bm25-d2q-t5.msmarco-v2-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev runs/run.msmarco-v2-passage.core.bm25-d2q-t5.msmarco-v2-passage.dev.txt
```

#### msmarco-v2-passage.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.d2q-t5 \
    -topics msmarco-v2-passage.dev2 \
    -output runs/run.msmarco-v2-passage.core.bm25-d2q-t5.msmarco-v2-passage.dev2.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev2 runs/run.msmarco-v2-passage.core.bm25-d2q-t5.msmarco-v2-passage.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.d2q-t5 \
    -topics dl21 \
    -output runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl21.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl21-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.d2q-t5 \
    -topics dl22 \
    -output runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl22.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl22-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.d2q-t5 \
    -topics dl23 \
    -output runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl23.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl23-passage runs/run.msmarco-v2-passage.core.bm25-d2q-t5.dl23.txt
```

<a id="condition-3"></a>

### 3. uniCOIL with doc2query-T5 (ONNX)

**Config**: [msmarco-v2-passage.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.core.yaml)

#### msmarco-v2-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics msmarco-v2-passage.dev \
    -output runs/run.msmarco-v2-passage.core.unicoil.onnx.msmarco-v2-passage.dev.txt \
    -hits 1000 \
    -impact \
    -pretokenized \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev runs/run.msmarco-v2-passage.core.unicoil.onnx.msmarco-v2-passage.dev.txt
```

#### msmarco-v2-passage.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics msmarco-v2-passage.dev2 \
    -output runs/run.msmarco-v2-passage.core.unicoil.onnx.msmarco-v2-passage.dev2.txt \
    -hits 1000 \
    -impact \
    -pretokenized \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev2 runs/run.msmarco-v2-passage.core.unicoil.onnx.msmarco-v2-passage.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics dl21 \
    -output runs/run.msmarco-v2-passage.core.unicoil.onnx.dl21.txt \
    -hits 1000 \
    -impact \
    -pretokenized \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl21-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl21-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl21-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl21-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics dl22 \
    -output runs/run.msmarco-v2-passage.core.unicoil.onnx.dl22.txt \
    -hits 1000 \
    -impact \
    -pretokenized \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl22-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl22-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl22-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl22-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics dl23 \
    -output runs/run.msmarco-v2-passage.core.unicoil.onnx.dl23.txt \
    -hits 1000 \
    -impact \
    -pretokenized \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl23-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl23-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl23-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl23-passage runs/run.msmarco-v2-passage.core.unicoil.onnx.dl23.txt
```


