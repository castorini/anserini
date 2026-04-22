# <img src="../../anserini-logo.png" height="30" /> MS MARCO V1 Doc

**Anserini reproductions from prebuilt indexes for the MS MARCO V1 Doc collection (optional)**

**Config**: [msmarco-v1-doc.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-doc.optional.yaml)

## Summary

The table below summarizes the effectiveness of dev in terms of RR@100; DL19 and DL20 in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **dev** = msmarco-doc.dev
+ **DL19** = dl19-doc
+ **DL20** = dl19-doc

| # | name | dev | DL19 | DL20 |
| --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 complete doc (k1=0.9, b=0.4) | 0.2299 | 0.5176 | 0.5286 |
| [2](#condition-2) | BM25 complete doc (k1=0.9, b=0.4) | 0.2299 | 0.5176 | 0.5286 |
| [3](#condition-3) | BM25 segmented doc (k1=0.9, b=0.4) | 0.2684 | 0.5302 | 0.5281 |
| [4](#condition-4) | BM25 segmented doc (k1=0.9, b=0.4) | 0.2684 | 0.5302 | 0.5281 |
| [5](#condition-5) | uniCOIL (no expansions): cached queries | 0.3409 | 0.6349 | 0.5893 |
| [6](#condition-6) | uniCOIL (with doc2query-T5): cached queries | 0.3531 | 0.6396 | 0.6033 |



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

### 1. BM25 complete doc (k1=0.9, b=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-slim \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.msmarco-doc.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-slim \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl19-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-slim \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl20-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-slim.dl20-doc.txt
```

<a id="condition-2"></a>

### 2. BM25 complete doc (k1=0.9, b=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-full \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.msmarco-doc.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-full \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl19-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-full \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl20-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-default-full.dl20-doc.txt
```

<a id="condition-3"></a>

### 3. BM25 segmented doc (k1=0.9, b=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented-slim \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.msmarco-doc.dev.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented-slim \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl19-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented-slim \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl20-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-slim.dl20-doc.txt
```

<a id="condition-4"></a>

### 4. BM25 segmented doc (k1=0.9, b=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented-full \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.msmarco-doc.dev.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented-full \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl19-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented-full \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl20-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.optional.bm25-doc-segmented-default-full.dl20-doc.txt
```

<a id="condition-5"></a>

### 5. uniCOIL (no expansions): cached queries

#### msmarco-doc.dev.unicoil-noexp

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil-noexp \
    -topics msmarco-doc.dev.unicoil-noexp \
    -output runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.msmarco-doc.dev.unicoil-noexp.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.msmarco-doc.dev.unicoil-noexp.txt
```

#### dl19-doc.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil-noexp \
    -topics dl19-doc.unicoil-noexp.0shot \
    -output runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl19-doc.unicoil-noexp.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl19-doc.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl19-doc.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl19-doc.unicoil-noexp.0shot.txt
```

#### dl20.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil-noexp \
    -topics dl20.unicoil-noexp.0shot \
    -output runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl20.unicoil-noexp.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl20.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl20.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.optional.unicoil-noexp.cached.dl20.unicoil-noexp.0shot.txt
```

<a id="condition-6"></a>

### 6. uniCOIL (with doc2query-T5): cached queries

#### msmarco-doc.dev.unicoil

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil \
    -topics msmarco-doc.dev.unicoil \
    -output runs/run.msmarco-v1-doc.optional.unicoil.cached.msmarco-doc.dev.unicoil.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.optional.unicoil.cached.msmarco-doc.dev.unicoil.txt
```

#### dl19-doc.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil \
    -topics dl19-doc.unicoil.0shot \
    -output runs/run.msmarco-v1-doc.optional.unicoil.cached.dl19-doc.unicoil.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.optional.unicoil.cached.dl19-doc.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.optional.unicoil.cached.dl19-doc.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.optional.unicoil.cached.dl19-doc.unicoil.0shot.txt
```

#### dl20.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil \
    -topics dl20.unicoil.0shot \
    -output runs/run.msmarco-v1-doc.optional.unicoil.cached.dl20.unicoil.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.optional.unicoil.cached.dl20.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.optional.unicoil.cached.dl20.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.optional.unicoil.cached.dl20.unicoil.0shot.txt
```


