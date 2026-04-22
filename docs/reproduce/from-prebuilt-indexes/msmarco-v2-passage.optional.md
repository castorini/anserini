# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2 Passage

**Anserini reproductions from prebuilt indexes for the MS MARCO V2 Passage collection (optional)**

**Config**: [msmarco-v2-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.optional.yaml)

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
| [1](#condition-1) | BM25 (k1=0.9, b=0.4) | 0.0802 |  | 0.4458 | 0.2692 | 0.2627 |
| [2](#condition-2) | BM25 (k1=0.9, b=0.4) | 0.0802 |  | 0.4458 | 0.2692 | 0.2627 |
| [3](#condition-3) | uniCOIL (no expansion): cached queries | 0.1385 |  | 0.5756 | 0.4077 | 0.3262 |
| [4](#condition-4) | uniCOIL (with doc2query-T5): cached queries | 0.1577 |  | 0.6159 | 0.4614 | 0.3855 |



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

### 1. BM25 (k1=0.9, b=0.4)

**Config**: [msmarco-v2-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.optional.yaml)

#### msmarco-v2-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-slim \
    -topics msmarco-v2-passage.dev \
    -output runs/run.msmarco-v2-passage.optional.bm25-slim.msmarco-v2-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev runs/run.msmarco-v2-passage.optional.bm25-slim.msmarco-v2-passage.dev.txt
```

#### msmarco-v2-passage.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-slim \
    -topics msmarco-v2-passage.dev2 \
    -output runs/run.msmarco-v2-passage.optional.bm25-slim.msmarco-v2-passage.dev2.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev2 runs/run.msmarco-v2-passage.optional.bm25-slim.msmarco-v2-passage.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-slim \
    -topics dl21 \
    -output runs/run.msmarco-v2-passage.optional.bm25-slim.dl21.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-slim \
    -topics dl22 \
    -output runs/run.msmarco-v2-passage.optional.bm25-slim.dl22.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-slim \
    -topics dl23 \
    -output runs/run.msmarco-v2-passage.optional.bm25-slim.dl23.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-slim.dl23.txt
```

<a id="condition-2"></a>

### 2. BM25 (k1=0.9, b=0.4)

**Config**: [msmarco-v2-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.optional.yaml)

#### msmarco-v2-passage.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-full \
    -topics msmarco-v2-passage.dev \
    -output runs/run.msmarco-v2-passage.optional.bm25-full.msmarco-v2-passage.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev runs/run.msmarco-v2-passage.optional.bm25-full.msmarco-v2-passage.dev.txt
```

#### msmarco-v2-passage.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-full \
    -topics msmarco-v2-passage.dev2 \
    -output runs/run.msmarco-v2-passage.optional.bm25-full.msmarco-v2-passage.dev2.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev2 runs/run.msmarco-v2-passage.optional.bm25-full.msmarco-v2-passage.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-full \
    -topics dl21 \
    -output runs/run.msmarco-v2-passage.optional.bm25-full.dl21.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-full \
    -topics dl22 \
    -output runs/run.msmarco-v2-passage.optional.bm25-full.dl22.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage-full \
    -topics dl23 \
    -output runs/run.msmarco-v2-passage.optional.bm25-full.dl23.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.bm25-full.dl23.txt
```

<a id="condition-3"></a>

### 3. uniCOIL (no expansion): cached queries

**Config**: [msmarco-v2-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.optional.yaml)

#### msmarco-v2-passage.dev.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-noexp-0shot \
    -topics msmarco-v2-passage.dev.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
```

#### msmarco-v2-passage.dev2.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-noexp-0shot \
    -topics msmarco-v2-passage.dev2.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev2 runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
```

#### dl21.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-noexp-0shot \
    -topics dl21.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
```

#### dl22.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-noexp-0shot \
    -topics dl22.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
```

#### dl23.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-noexp-0shot \
    -topics dl23.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
```

<a id="condition-4"></a>

### 4. uniCOIL (with doc2query-T5): cached queries

**Config**: [msmarco-v2-passage.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-passage.optional.yaml)

#### msmarco-v2-passage.dev.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics msmarco-v2-passage.dev.unicoil.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil.cached.msmarco-v2-passage.dev.unicoil.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev runs/run.msmarco-v2-passage.optional.unicoil.cached.msmarco-v2-passage.dev.unicoil.0shot.txt
```

#### msmarco-v2-passage.dev2.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics msmarco-v2-passage.dev2.unicoil.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil.cached.msmarco-v2-passage.dev2.unicoil.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-passage.dev2 runs/run.msmarco-v2-passage.optional.unicoil.cached.msmarco-v2-passage.dev2.unicoil.0shot.txt
```

#### dl21.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics dl21.unicoil.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil.cached.dl21.unicoil.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl21-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl21.unicoil.0shot.txt
```

#### dl22.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics dl22.unicoil.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil.cached.dl22.unicoil.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl22-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl22.unicoil.0shot.txt
```

#### dl23.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-passage.unicoil-0shot \
    -topics dl23.unicoil.0shot \
    -output runs/run.msmarco-v2-passage.optional.unicoil.cached.dl23.unicoil.0shot.txt \
    -hits 1000 \
    -impact \
    -pretokenized
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 -l 2 dl23-passage runs/run.msmarco-v2-passage.optional.unicoil.cached.dl23.unicoil.0shot.txt
```


