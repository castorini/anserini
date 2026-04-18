# MS MARCO V1 Documents

**Config**: [msmarco-v1-doc.core.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-doc.core.yaml)

The table below summarizes the effectiveness of dev in terms of RR@100; DL19 and DL20 in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **dev** = msmarco-doc.dev
+ **DL19** = dl19-doc
+ **DL20** = dl19-doc

| # | name | dev | DL19 | DL20 |
| --- | --- | --- | --- | --- |
| [1](#condition-1) | BM25 complete doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.2299 | 0.5176 | 0.5286 |
| [2](#condition-2) | BM25 complete doc with doc2query-T5 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.2880 | 0.5968 | 0.5885 |
| [3](#condition-3) | BM25 segmented doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.2684 | 0.5302 | 0.5281 |
| [4](#condition-4) | BM25 segmented doc with doc2query-T5 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.3179 | 0.6119 | 0.5957 |
| [5](#condition-5) | uniCOIL (with doc2query-T5): ONNX | 0.3531 | 0.6396 | 0.6033 |

<a id="condition-1"></a>

### 1. BM25 complete doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.core.bm25-doc-default.msmarco-doc.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.core.bm25-doc-default.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-doc-default.dl19-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.core.bm25-doc-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.core.bm25-doc-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.core.bm25-doc-default.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-doc-default.dl20-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.core.bm25-doc-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.core.bm25-doc-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.core.bm25-doc-default.dl20-doc.txt
```

<a id="condition-2"></a>

### 2. BM25 complete doc with doc2query-T5 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc.d2q-t5 \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.msmarco-doc.dev.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc.d2q-t5 \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl19-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc.d2q-t5 \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl20-doc.txt \
    -hits 1000 \
    -bm25
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-default.dl20-doc.txt
```

<a id="condition-3"></a>

### 3. BM25 segmented doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.msmarco-doc.dev.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl19-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl20-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.core.bm25-doc-segmented-default.dl20-doc.txt
```

<a id="condition-4"></a>

### 4. BM25 segmented doc with doc2query-T5 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.d2q-t5 \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.msmarco-doc.dev.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.d2q-t5 \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl19-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl19-doc.txt
```

#### dl20-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.d2q-t5 \
    -topics dl20-doc \
    -output runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl20-doc.txt \
    -bm25 \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl20-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.core.bm25-d2q-t5-doc-segmented-default.dl20-doc.txt
```

<a id="condition-5"></a>

### 5. uniCOIL (with doc2query-T5): ONNX

#### msmarco-doc.dev

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil \
    -topics msmarco-doc.dev \
    -output runs/run.msmarco-v1-doc.core.unicoil.onnx.msmarco-doc.dev.txt \
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
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-doc.dev runs/run.msmarco-v1-doc.core.unicoil.onnx.msmarco-doc.dev.txt
```

#### dl19-doc

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil \
    -topics dl19-doc \
    -output runs/run.msmarco-v1-doc.core.unicoil.onnx.dl19-doc.txt \
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
java -cp $fatjar trec_eval -c -M 100 -m map dl19-doc runs/run.msmarco-v1-doc.core.unicoil.onnx.dl19-doc.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl19-doc runs/run.msmarco-v1-doc.core.unicoil.onnx.dl19-doc.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl19-doc runs/run.msmarco-v1-doc.core.unicoil.onnx.dl19-doc.txt
```

#### dl20

Retrieval command:

```bash
java -cp $fatjar -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector \
    io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v1-doc-segmented.unicoil \
    -topics dl20 \
    -output runs/run.msmarco-v1-doc.core.unicoil.onnx.dl20.txt \
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
java -cp $fatjar trec_eval -c -M 100 -m map dl20-doc runs/run.msmarco-v1-doc.core.unicoil.onnx.dl20.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl20-doc runs/run.msmarco-v1-doc.core.unicoil.onnx.dl20.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl20-doc runs/run.msmarco-v1-doc.core.unicoil.onnx.dl20.txt
```

