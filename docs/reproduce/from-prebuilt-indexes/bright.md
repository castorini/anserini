# <img src="../../anserini-logo.png" height="30" /> BRIGHT

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: BRIGHT
+ **Config**: [bright.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/bright.yaml)

## Summary

The table below summarizes effectiveness in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **BM25** = BM25, bag-of-words baseline
+ **BM25qs** = BM25, query-side BM25 baseline
+ **SPLADE** = SPLADE-v3 (ONNX)
+ **BGE** = bge-large-en-v1.5 w/ flat indexes (ONNX)

| corpus | [BM25](#condition-1) | [BM25qs](#condition-2) | [SPLADE](#condition-3) | [BGE](#condition-4) |
| --- | --- | --- | --- | --- |
| biology | 0.1824 | 0.1972 | 0.2101 | 0.1242 |
| earth-science | 0.2791 | 0.2789 | 0.2670 | 0.2545 |
| economics | 0.1645 | 0.1518 | 0.1604 | 0.1662 |
| psychology | 0.1342 | 0.1266 | 0.1527 | 0.1805 |
| robotics | 0.1091 | 0.1390 | 0.1578 | 0.1230 |
| stackoverflow | 0.1626 | 0.1855 | 0.1290 | 0.1099 |
| sustainable-living | 0.1613 | 0.1515 | 0.1497 | 0.1440 |
| leetcode | 0.2471 | 0.2497 | 0.2603 | 0.2668 |
| pony | 0.0434 | 0.0789 | 0.1440 | 0.0338 |
| aops | 0.0645 | 0.0627 | 0.0692 | 0.0638 |
| theoremqa-questions | 0.0733 | 0.1036 | 0.1113 | 0.1411 |
| theoremqa-theorems | 0.0214 | 0.0492 | 0.0554 | 0.0532 |



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

### 1. BM25, bag-of-words baseline

**Config**: [bright.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/bright.yaml)

#### biology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-biology \
    -topics bright-biology \
    -output runs/run.bright.bm25.biology.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-biology runs/run.bright.bm25.biology.txt
```

#### earth-science

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-earth-science \
    -topics bright-earth-science \
    -output runs/run.bright.bm25.earth-science.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-earth-science runs/run.bright.bm25.earth-science.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-economics \
    -topics bright-economics \
    -output runs/run.bright.bm25.economics.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-economics runs/run.bright.bm25.economics.txt
```

#### psychology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-psychology \
    -topics bright-psychology \
    -output runs/run.bright.bm25.psychology.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-psychology runs/run.bright.bm25.psychology.txt
```

#### robotics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-robotics \
    -topics bright-robotics \
    -output runs/run.bright.bm25.robotics.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-robotics runs/run.bright.bm25.robotics.txt
```

#### stackoverflow

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-stackoverflow \
    -topics bright-stackoverflow \
    -output runs/run.bright.bm25.stackoverflow.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-stackoverflow runs/run.bright.bm25.stackoverflow.txt
```

#### sustainable-living

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-sustainable-living \
    -topics bright-sustainable-living \
    -output runs/run.bright.bm25.sustainable-living.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-sustainable-living runs/run.bright.bm25.sustainable-living.txt
```

#### leetcode

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-leetcode \
    -topics bright-leetcode \
    -output runs/run.bright.bm25.leetcode.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-leetcode runs/run.bright.bm25.leetcode.txt
```

#### pony

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-pony \
    -topics bright-pony \
    -output runs/run.bright.bm25.pony.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-pony runs/run.bright.bm25.pony.txt
```

#### aops

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-aops \
    -topics bright-aops \
    -output runs/run.bright.bm25.aops.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-aops runs/run.bright.bm25.aops.txt
```

#### theoremqa-questions

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-theoremqa-questions \
    -topics bright-theoremqa-questions \
    -output runs/run.bright.bm25.theoremqa-questions.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-questions runs/run.bright.bm25.theoremqa-questions.txt
```

#### theoremqa-theorems

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-theoremqa-theorems \
    -topics bright-theoremqa-theorems \
    -output runs/run.bright.bm25.theoremqa-theorems.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-theorems runs/run.bright.bm25.theoremqa-theorems.txt
```

<a id="condition-2"></a>

### 2. BM25, query-side BM25 baseline

**Config**: [bright.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/bright.yaml)

#### biology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-biology \
    -topics bright-biology \
    -output runs/run.bright.bm25qs.biology.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-biology runs/run.bright.bm25qs.biology.txt
```

#### earth-science

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-earth-science \
    -topics bright-earth-science \
    -output runs/run.bright.bm25qs.earth-science.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-earth-science runs/run.bright.bm25qs.earth-science.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-economics \
    -topics bright-economics \
    -output runs/run.bright.bm25qs.economics.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-economics runs/run.bright.bm25qs.economics.txt
```

#### psychology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-psychology \
    -topics bright-psychology \
    -output runs/run.bright.bm25qs.psychology.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-psychology runs/run.bright.bm25qs.psychology.txt
```

#### robotics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-robotics \
    -topics bright-robotics \
    -output runs/run.bright.bm25qs.robotics.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-robotics runs/run.bright.bm25qs.robotics.txt
```

#### stackoverflow

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-stackoverflow \
    -topics bright-stackoverflow \
    -output runs/run.bright.bm25qs.stackoverflow.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-stackoverflow runs/run.bright.bm25qs.stackoverflow.txt
```

#### sustainable-living

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-sustainable-living \
    -topics bright-sustainable-living \
    -output runs/run.bright.bm25qs.sustainable-living.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-sustainable-living runs/run.bright.bm25qs.sustainable-living.txt
```

#### leetcode

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-leetcode \
    -topics bright-leetcode \
    -output runs/run.bright.bm25qs.leetcode.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-leetcode runs/run.bright.bm25qs.leetcode.txt
```

#### pony

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-pony \
    -topics bright-pony \
    -output runs/run.bright.bm25qs.pony.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-pony runs/run.bright.bm25qs.pony.txt
```

#### aops

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-aops \
    -topics bright-aops \
    -output runs/run.bright.bm25qs.aops.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-aops runs/run.bright.bm25qs.aops.txt
```

#### theoremqa-questions

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-theoremqa-questions \
    -topics bright-theoremqa-questions \
    -output runs/run.bright.bm25qs.theoremqa-questions.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-questions runs/run.bright.bm25qs.theoremqa-questions.txt
```

#### theoremqa-theorems

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-theoremqa-theorems \
    -topics bright-theoremqa-theorems \
    -output runs/run.bright.bm25qs.theoremqa-theorems.txt \
    -bm25.querySide \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-theorems runs/run.bright.bm25qs.theoremqa-theorems.txt
```

<a id="condition-3"></a>

### 3. SPLADE-v3 (ONNX)

**Config**: [bright.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/bright.yaml)

#### biology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-biology.splade-v3 \
    -topics bright-biology \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.biology.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-biology runs/run.bright.splade-v3.onnx.biology.txt
```

#### earth-science

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-earth-science.splade-v3 \
    -topics bright-earth-science \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.earth-science.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-earth-science runs/run.bright.splade-v3.onnx.earth-science.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-economics.splade-v3 \
    -topics bright-economics \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.economics.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-economics runs/run.bright.splade-v3.onnx.economics.txt
```

#### psychology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-psychology.splade-v3 \
    -topics bright-psychology \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.psychology.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-psychology runs/run.bright.splade-v3.onnx.psychology.txt
```

#### robotics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-robotics.splade-v3 \
    -topics bright-robotics \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.robotics.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-robotics runs/run.bright.splade-v3.onnx.robotics.txt
```

#### stackoverflow

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-stackoverflow.splade-v3 \
    -topics bright-stackoverflow \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.stackoverflow.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-stackoverflow runs/run.bright.splade-v3.onnx.stackoverflow.txt
```

#### sustainable-living

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-sustainable-living.splade-v3 \
    -topics bright-sustainable-living \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.sustainable-living.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-sustainable-living runs/run.bright.splade-v3.onnx.sustainable-living.txt
```

#### pony

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-pony.splade-v3 \
    -topics bright-pony \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.pony.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-pony runs/run.bright.splade-v3.onnx.pony.txt
```

#### leetcode

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-leetcode.splade-v3 \
    -topics bright-leetcode \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.leetcode.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-leetcode runs/run.bright.splade-v3.onnx.leetcode.txt
```

#### aops

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-aops.splade-v3 \
    -topics bright-aops \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.aops.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-aops runs/run.bright.splade-v3.onnx.aops.txt
```

#### theoremqa-questions

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-theoremqa-questions.splade-v3 \
    -topics bright-theoremqa-questions \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.theoremqa-questions.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-questions runs/run.bright.splade-v3.onnx.theoremqa-questions.txt
```

#### theoremqa-theorems

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index bright-theoremqa-theorems.splade-v3 \
    -topics bright-theoremqa-theorems \
    -encoder SpladeV3 \
    -output runs/run.bright.splade-v3.onnx.theoremqa-theorems.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-theorems runs/run.bright.splade-v3.onnx.theoremqa-theorems.txt
```

<a id="condition-4"></a>

### 4. bge-large-en-v1.5 w/ flat indexes (ONNX)

**Config**: [bright.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/bright.yaml)

#### biology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-biology.bge-large-en-v1.5.flat \
    -topics bright-biology \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.biology.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-biology runs/run.bright.bge-large-en-v1.5.flat.onnx.biology.txt
```

#### earth-science

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-earth-science.bge-large-en-v1.5.flat \
    -topics bright-earth-science \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.earth-science.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-earth-science runs/run.bright.bge-large-en-v1.5.flat.onnx.earth-science.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-economics.bge-large-en-v1.5.flat \
    -topics bright-economics \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.economics.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-economics runs/run.bright.bge-large-en-v1.5.flat.onnx.economics.txt
```

#### psychology

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-psychology.bge-large-en-v1.5.flat \
    -topics bright-psychology \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.psychology.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-psychology runs/run.bright.bge-large-en-v1.5.flat.onnx.psychology.txt
```

#### robotics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-robotics.bge-large-en-v1.5.flat \
    -topics bright-robotics \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.robotics.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-robotics runs/run.bright.bge-large-en-v1.5.flat.onnx.robotics.txt
```

#### stackoverflow

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-stackoverflow.bge-large-en-v1.5.flat \
    -topics bright-stackoverflow \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.stackoverflow.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-stackoverflow runs/run.bright.bge-large-en-v1.5.flat.onnx.stackoverflow.txt
```

#### sustainable-living

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-sustainable-living.bge-large-en-v1.5.flat \
    -topics bright-sustainable-living \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.sustainable-living.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-sustainable-living runs/run.bright.bge-large-en-v1.5.flat.onnx.sustainable-living.txt
```

#### pony

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-pony.bge-large-en-v1.5.flat \
    -topics bright-pony \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.pony.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-pony runs/run.bright.bge-large-en-v1.5.flat.onnx.pony.txt
```

#### leetcode

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-leetcode.bge-large-en-v1.5.flat \
    -topics bright-leetcode \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.leetcode.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-leetcode runs/run.bright.bge-large-en-v1.5.flat.onnx.leetcode.txt
```

#### aops

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-aops.bge-large-en-v1.5.flat \
    -topics bright-aops \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.aops.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-aops runs/run.bright.bge-large-en-v1.5.flat.onnx.aops.txt
```

#### theoremqa-questions

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-theoremqa-questions.bge-large-en-v1.5.flat \
    -topics bright-theoremqa-questions \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.theoremqa-questions.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-questions runs/run.bright.bge-large-en-v1.5.flat.onnx.theoremqa-questions.txt
```

#### theoremqa-theorems

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index bright-theoremqa-theorems.bge-large-en-v1.5.flat \
    -topics bright-theoremqa-theorems \
    -encoder BgeLargeEn15 \
    -output runs/run.bright.bge-large-en-v1.5.flat.onnx.theoremqa-theorems.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 bright-theoremqa-theorems runs/run.bright.bge-large-en-v1.5.flat.onnx.theoremqa-theorems.txt
```


