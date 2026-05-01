# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2 Doc

**Anserini reproductions from prebuilt indexes for the MS MARCO V2 Doc collection (core)**

**Config**: [msmarco-v2-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.core.yaml)

## Summary

The table below summarizes the effectiveness of dev and dev2 in terms of RR@100; DL21, DL22, and DL23 in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **dev** = msmarco-v2-doc.dev
+ **dev2** = msmarco-v2-doc.dev2
+ **DL21** = dl21-doc
+ **DL22** = dl22-doc
+ **DL23** = dl23-doc

| # | name | dev | dev2 | DL21 | DL22 | DL23 |
| --- | --- | --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 complete doc (k1=0.9, b=0.4) | 0.1572 | 0.1659 | 0.5116 | 0.2993 | 0.2946 |
| [2](#condition-2) | BM25 completed doc with doc2query-T5 (k1=0.9, b=0.4) | 0.2011 | 0.2012 | 0.5792 | 0.3539 | 0.3511 |
| [3](#condition-3) | BM25 segmented doc (k1=0.9, b=0.4) | 0.1896 | 0.1930 | 0.5776 | 0.3618 | 0.3405 |
| [4](#condition-4) | BM25 segmented doc with doc2query-T5 (k1=0.9, b=0.4) | 0.2226 | 0.2234 | 0.6289 | 0.3975 | 0.3612 |
| [5](#condition-5) | uniCOIL (with doc2query-T5): ONNX | 0.2419 | 0.2445 | 0.6783 | 0.4451 | 0.4150 |



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

### 1. BM25 complete doc (k1=0.9, b=0.4)

**Config**: [msmarco-v2-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.core.yaml)

#### msmarco-v2-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc \
    -topics msmarco-v2-doc.dev \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-default.msmarco-v2-doc.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.msmarco-v2-doc.core.bm25-doc-default.msmarco-v2-doc.dev.txt
```

#### msmarco-v2-doc.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc \
    -topics msmarco-v2-doc.dev2 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-default.msmarco-v2-doc.dev2.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.msmarco-v2-doc.core.bm25-doc-default.msmarco-v2-doc.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc \
    -topics dl21 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-default.dl21.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc \
    -topics dl22 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-default.dl22.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc \
    -topics dl23 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-default.dl23.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-default.dl23.txt
```

<a id="condition-2"></a>

### 2. BM25 completed doc with doc2query-T5 (k1=0.9, b=0.4)

**Config**: [msmarco-v2-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.core.yaml)

#### msmarco-v2-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc.d2q-t5 \
    -topics msmarco-v2-doc.dev \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev.txt
```

#### msmarco-v2-doc.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc.d2q-t5 \
    -topics msmarco-v2-doc.dev2 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev2.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc.d2q-t5 \
    -topics dl21 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl21.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc.d2q-t5 \
    -topics dl22 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl22.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc.d2q-t5 \
    -topics dl23 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl23.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-default.dl23.txt
```

<a id="condition-3"></a>

### 3. BM25 segmented doc (k1=0.9, b=0.4)

**Config**: [msmarco-v2-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.core.yaml)

#### msmarco-v2-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented \
    -topics msmarco-v2-doc.dev \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.msmarco-v2-doc.dev.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.msmarco-v2-doc.dev.txt
```

#### msmarco-v2-doc.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented \
    -topics msmarco-v2-doc.dev2 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.msmarco-v2-doc.dev2.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.msmarco-v2-doc.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented \
    -topics dl21 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl21.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented \
    -topics dl22 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl22.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented \
    -topics dl23 \
    -output runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl23.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc runs/run.msmarco-v2-doc.core.bm25-doc-segmented-default.dl23.txt
```

<a id="condition-4"></a>

### 4. BM25 segmented doc with doc2query-T5 (k1=0.9, b=0.4)

**Config**: [msmarco-v2-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.core.yaml)

#### msmarco-v2-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.d2q-t5 \
    -topics msmarco-v2-doc.dev \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev.txt
```

#### msmarco-v2-doc.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.d2q-t5 \
    -topics msmarco-v2-doc.dev2 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev2.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.d2q-t5 \
    -topics dl21 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl21.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.d2q-t5 \
    -topics dl22 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl22.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.d2q-t5 \
    -topics dl23 \
    -output runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl23.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc runs/run.msmarco-v2-doc.core.bm25-d2q-t5-doc-segmented-default.dl23.txt
```

<a id="condition-5"></a>

### 5. uniCOIL (with doc2query-T5): ONNX

**Config**: [msmarco-v2-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.core.yaml)

#### msmarco-v2-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics msmarco-v2-doc.dev \
    -output runs/run.msmarco-v2-doc.core.unicoil.onnx.msmarco-v2-doc.dev.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000 \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.msmarco-v2-doc.core.unicoil.onnx.msmarco-v2-doc.dev.txt
```

#### msmarco-v2-doc.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics msmarco-v2-doc.dev2 \
    -output runs/run.msmarco-v2-doc.core.unicoil.onnx.msmarco-v2-doc.dev2.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000 \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.msmarco-v2-doc.core.unicoil.onnx.msmarco-v2-doc.dev2.txt
```

#### dl21

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics dl21 \
    -output runs/run.msmarco-v2-doc.core.unicoil.onnx.dl21.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000 \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl21.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl21.txt
```

#### dl22

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics dl22 \
    -output runs/run.msmarco-v2-doc.core.unicoil.onnx.dl22.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000 \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl22.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl22.txt
```

#### dl23

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics dl23 \
    -output runs/run.msmarco-v2-doc.core.unicoil.onnx.dl23.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000 \
    -encoder UniCoil
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl23.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc runs/run.msmarco-v2-doc.core.unicoil.onnx.dl23.txt
```


