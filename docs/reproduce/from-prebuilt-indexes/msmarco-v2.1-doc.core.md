# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2.1 Doc

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: MS MARCO V2.1 Doc
+ **Config**: [msmarco-v2.1-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc.core.yaml)

## Summary

The table below summarizes the effectiveness of dev and dev2 in terms of RR@100; DL21, DL22, DL23, and RAG24 in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **dev** = msmarco-v2.1-doc.dev
+ **dev2** = msmarco-v2.1-doc.dev2
+ **DL21** = dl21-doc-msmarco-v2.1
+ **DL22** = dl22-doc-msmarco-v2.1
+ **DL23** = dl23-doc-msmarco-v2.1
+ **RAG24** = rag24.raggy-dev

| # | name | dev | dev2 | DL21 | DL22 | DL23 | RAG24 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.1654 | 0.1732 | 0.5183 | 0.2991 | 0.2914 | 0.3631 |
| [2](#condition-2) | BM25 segmented doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.1973 | 0.2000 | 0.5778 | 0.3576 | 0.3356 | 0.4227 |



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

### 1. BM25 doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

**Config**: [msmarco-v2.1-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc.core.yaml)

#### msmarco-v2-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc \
    -topics msmarco-v2-doc.dev \
    -output runs/run.msmarco-v2.1-doc.core.bm25-doc.msmarco-v2-doc.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev runs/run.msmarco-v2.1-doc.core.bm25-doc.msmarco-v2-doc.dev.txt
```

#### msmarco-v2-doc.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc \
    -topics msmarco-v2-doc.dev2 \
    -output runs/run.msmarco-v2.1-doc.core.bm25-doc.msmarco-v2-doc.dev2.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev2 runs/run.msmarco-v2.1-doc.core.bm25-doc.msmarco-v2-doc.dev2.txt
```

#### dl21-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc \
    -topics dl21-doc \
    -output runs/run.msmarco-v2.1-doc.core.bm25-doc.dl21-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl21-doc.txt
```

#### dl22-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc \
    -topics dl22-doc \
    -output runs/run.msmarco-v2.1-doc.core.bm25-doc.dl22-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl22-doc.txt
```

#### dl23-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc \
    -topics dl23-doc \
    -output runs/run.msmarco-v2.1-doc.core.bm25-doc.dl23-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-doc.dl23-doc.txt
```

#### rag24.raggy-dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc \
    -topics rag24.raggy-dev \
    -output runs/run.msmarco-v2.1-doc.core.bm25-doc.rag24.raggy-dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-doc.rag24.raggy-dev.txt
```

<a id="condition-2"></a>

### 2. BM25 segmented doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

**Config**: [msmarco-v2.1-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2.1-doc.core.yaml)

#### msmarco-v2-doc.dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics msmarco-v2-doc.dev \
    -output runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.msmarco-v2-doc.dev.txt \
    -hits 10000 \
    -bm25 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.msmarco-v2-doc.dev.txt
```

#### msmarco-v2-doc.dev2

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics msmarco-v2-doc.dev2 \
    -output runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.msmarco-v2-doc.dev2.txt \
    -hits 10000 \
    -bm25 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev2 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.msmarco-v2-doc.dev2.txt
```

#### dl21-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics dl21-doc \
    -output runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl21-doc.txt \
    -hits 10000 \
    -bm25 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl21-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl21-doc.txt
```

#### dl22-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics dl22-doc \
    -output runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl22-doc.txt \
    -hits 10000 \
    -bm25 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl22-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl22-doc.txt
```

#### dl23-doc

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics dl23-doc \
    -output runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl23-doc.txt \
    -hits 10000 \
    -bm25 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl23-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc-msmarco-v2.1 runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.dl23-doc.txt
```

#### rag24.raggy-dev

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2.1-doc-segmented \
    -topics rag24.raggy-dev \
    -output runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.rag24.raggy-dev.txt \
    -hits 10000 \
    -bm25 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -m recall.100 rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.rag24.raggy-dev.txt
java -cp $fatjar trec_eval -c -m recall.1000 rag24.raggy-dev runs/run.msmarco-v2.1-doc.core.bm25-segmented-doc.rag24.raggy-dev.txt
```


