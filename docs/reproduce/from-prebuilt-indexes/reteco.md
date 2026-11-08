# <img src="../../anserini-logo.png" height="30" /> RETECO v1.1 Track 1a

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: RETECO v1.1 Track 1a
+ **Config**: [reteco.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/reteco.yaml)

## Summary

The table below summarizes effectiveness in terms of nDCG@10.

Key:

+ **BM25 (train)** = BM25, bag-of-words baseline on train
+ **BM25 (dev)** = BM25, bag-of-words baseline on dev
+ **BM25QS (train)** = query-side BM25 on train
+ **BM25QS (dev)** = query-side BM25 on dev

Both BM25 and BM25QS use `k1=0.9`, `b=0.4`, and retrieve the top 1000 documents from the full domain index.

| corpus    | [BM25 (train)](#condition-1) | [BM25 (dev)](#condition-2) | [BM25QS (train)](#condition-3) | [BM25QS (dev)](#condition-4) |
| --------- | :--------------------------: | :------------------------: | :----------------------------: | :--------------------------: |
| bitcoin   | 0.0468                       | 0.0300                     | 0.0845                         | 0.0364                       |
| cardano   | 0.1753                       | 0.1144                     | 0.1801                         | 0.1027                       |
| economics | 0.0361                       | 0.0269                     | 0.0355                         | 0.0466                       |
| genealogy | 0.0667                       | 0.1476                     | 0.1188                         | 0.1854                       |
| history   | 0.0336                       | 0.0428                     | 0.0640                         | 0.0852                       |
| hsm       | 0.1703                       | 0.2366                     | 0.1767                         | 0.2437                       |
| iota      | 0.0352                       | 0.3852                     | 0.1228                         | 0.3243                       |
| law       | 0.0399                       | 0.0294                     | 0.0905                         | 0.0574                       |
| monero    | 0.0288                       | 0.0249                     | 0.0378                         | 0.0320                       |
| politics  | 0.2664                       | 0.2129                     | 0.2802                         | 0.2607                       |
| quant     | 0.0128                       | 0.0000                     | 0.0255                         | 0.0204                       |
| travel    | 0.0412                       | 0.0322                     | 0.0504                         | 0.0315                       |
| workplace | 0.0622                       | 0.0000                     | 0.0798                         | 0.0398                       |

## Commands

From an Anserini checkout, run the complete reproduction with:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromPrebuiltIndexes --config reteco
```

In the commands below:

+ Set `$fatjar` to the actual Anserini fatjar.
+ Set JVM args to `-Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector`.

Something like:

```bash
export fatjar=$(find . -maxdepth 2 -name 'anserini-*-fatjar.jar' -print -quit)
export jvm_args="-Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector"
```

<a id="condition-1"></a>

### 1. BM25, bag-of-words baseline (train)

**Config**: [reteco.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/reteco.yaml#L2)

#### bitcoin

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-bitcoin \
    -topics reteco-v1.1-track1a-bitcoin.train \
    -output runs/run.reteco.bm25-train.bitcoin.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-bitcoin.train runs/run.reteco.bm25-train.bitcoin.txt
```

#### cardano

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-cardano \
    -topics reteco-v1.1-track1a-cardano.train \
    -output runs/run.reteco.bm25-train.cardano.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-cardano.train runs/run.reteco.bm25-train.cardano.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-economics \
    -topics reteco-v1.1-track1a-economics.train \
    -output runs/run.reteco.bm25-train.economics.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-economics.train runs/run.reteco.bm25-train.economics.txt
```

#### genealogy

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-genealogy \
    -topics reteco-v1.1-track1a-genealogy.train \
    -output runs/run.reteco.bm25-train.genealogy.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-genealogy.train runs/run.reteco.bm25-train.genealogy.txt
```

#### history

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-history \
    -topics reteco-v1.1-track1a-history.train \
    -output runs/run.reteco.bm25-train.history.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-history.train runs/run.reteco.bm25-train.history.txt
```

#### hsm

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-hsm \
    -topics reteco-v1.1-track1a-hsm.train \
    -output runs/run.reteco.bm25-train.hsm.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-hsm.train runs/run.reteco.bm25-train.hsm.txt
```

#### iota

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-iota \
    -topics reteco-v1.1-track1a-iota.train \
    -output runs/run.reteco.bm25-train.iota.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-iota.train runs/run.reteco.bm25-train.iota.txt
```

#### law

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-law \
    -topics reteco-v1.1-track1a-law.train \
    -output runs/run.reteco.bm25-train.law.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-law.train runs/run.reteco.bm25-train.law.txt
```

#### monero

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-monero \
    -topics reteco-v1.1-track1a-monero.train \
    -output runs/run.reteco.bm25-train.monero.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-monero.train runs/run.reteco.bm25-train.monero.txt
```

#### politics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-politics \
    -topics reteco-v1.1-track1a-politics.train \
    -output runs/run.reteco.bm25-train.politics.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-politics.train runs/run.reteco.bm25-train.politics.txt
```

#### quant

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-quant \
    -topics reteco-v1.1-track1a-quant.train \
    -output runs/run.reteco.bm25-train.quant.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-quant.train runs/run.reteco.bm25-train.quant.txt
```

#### travel

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-travel \
    -topics reteco-v1.1-track1a-travel.train \
    -output runs/run.reteco.bm25-train.travel.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-travel.train runs/run.reteco.bm25-train.travel.txt
```

#### workplace

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-workplace \
    -topics reteco-v1.1-track1a-workplace.train \
    -output runs/run.reteco.bm25-train.workplace.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-workplace.train runs/run.reteco.bm25-train.workplace.txt
```

<a id="condition-2"></a>

### 2. BM25, bag-of-words baseline (dev)

**Config**: [reteco.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/reteco.yaml#L85)

#### bitcoin

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-bitcoin \
    -topics reteco-v1.1-track1a-bitcoin.dev \
    -output runs/run.reteco.bm25-dev.bitcoin.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-bitcoin.dev runs/run.reteco.bm25-dev.bitcoin.txt
```

#### cardano

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-cardano \
    -topics reteco-v1.1-track1a-cardano.dev \
    -output runs/run.reteco.bm25-dev.cardano.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-cardano.dev runs/run.reteco.bm25-dev.cardano.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-economics \
    -topics reteco-v1.1-track1a-economics.dev \
    -output runs/run.reteco.bm25-dev.economics.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-economics.dev runs/run.reteco.bm25-dev.economics.txt
```

#### genealogy

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-genealogy \
    -topics reteco-v1.1-track1a-genealogy.dev \
    -output runs/run.reteco.bm25-dev.genealogy.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-genealogy.dev runs/run.reteco.bm25-dev.genealogy.txt
```

#### history

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-history \
    -topics reteco-v1.1-track1a-history.dev \
    -output runs/run.reteco.bm25-dev.history.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-history.dev runs/run.reteco.bm25-dev.history.txt
```

#### hsm

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-hsm \
    -topics reteco-v1.1-track1a-hsm.dev \
    -output runs/run.reteco.bm25-dev.hsm.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-hsm.dev runs/run.reteco.bm25-dev.hsm.txt
```

#### iota

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-iota \
    -topics reteco-v1.1-track1a-iota.dev \
    -output runs/run.reteco.bm25-dev.iota.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-iota.dev runs/run.reteco.bm25-dev.iota.txt
```

#### law

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-law \
    -topics reteco-v1.1-track1a-law.dev \
    -output runs/run.reteco.bm25-dev.law.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-law.dev runs/run.reteco.bm25-dev.law.txt
```

#### monero

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-monero \
    -topics reteco-v1.1-track1a-monero.dev \
    -output runs/run.reteco.bm25-dev.monero.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-monero.dev runs/run.reteco.bm25-dev.monero.txt
```

#### politics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-politics \
    -topics reteco-v1.1-track1a-politics.dev \
    -output runs/run.reteco.bm25-dev.politics.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-politics.dev runs/run.reteco.bm25-dev.politics.txt
```

#### quant

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-quant \
    -topics reteco-v1.1-track1a-quant.dev \
    -output runs/run.reteco.bm25-dev.quant.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-quant.dev runs/run.reteco.bm25-dev.quant.txt
```

#### travel

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-travel \
    -topics reteco-v1.1-track1a-travel.dev \
    -output runs/run.reteco.bm25-dev.travel.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-travel.dev runs/run.reteco.bm25-dev.travel.txt
```

#### workplace

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-workplace \
    -topics reteco-v1.1-track1a-workplace.dev \
    -output runs/run.reteco.bm25-dev.workplace.txt \
    -bm25 \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-workplace.dev runs/run.reteco.bm25-dev.workplace.txt
```

<a id="condition-3"></a>

### 3. Query-side BM25 (train)

**Config**: [reteco.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/reteco.yaml#L168)

#### bitcoin

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-bitcoin \
    -topics reteco-v1.1-track1a-bitcoin.train \
    -output runs/run.reteco.bm25qs-train.bitcoin.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-bitcoin.train runs/run.reteco.bm25qs-train.bitcoin.txt
```

#### cardano

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-cardano \
    -topics reteco-v1.1-track1a-cardano.train \
    -output runs/run.reteco.bm25qs-train.cardano.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-cardano.train runs/run.reteco.bm25qs-train.cardano.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-economics \
    -topics reteco-v1.1-track1a-economics.train \
    -output runs/run.reteco.bm25qs-train.economics.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-economics.train runs/run.reteco.bm25qs-train.economics.txt
```

#### genealogy

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-genealogy \
    -topics reteco-v1.1-track1a-genealogy.train \
    -output runs/run.reteco.bm25qs-train.genealogy.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-genealogy.train runs/run.reteco.bm25qs-train.genealogy.txt
```

#### history

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-history \
    -topics reteco-v1.1-track1a-history.train \
    -output runs/run.reteco.bm25qs-train.history.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-history.train runs/run.reteco.bm25qs-train.history.txt
```

#### hsm

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-hsm \
    -topics reteco-v1.1-track1a-hsm.train \
    -output runs/run.reteco.bm25qs-train.hsm.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-hsm.train runs/run.reteco.bm25qs-train.hsm.txt
```

#### iota

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-iota \
    -topics reteco-v1.1-track1a-iota.train \
    -output runs/run.reteco.bm25qs-train.iota.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-iota.train runs/run.reteco.bm25qs-train.iota.txt
```

#### law

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-law \
    -topics reteco-v1.1-track1a-law.train \
    -output runs/run.reteco.bm25qs-train.law.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-law.train runs/run.reteco.bm25qs-train.law.txt
```

#### monero

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-monero \
    -topics reteco-v1.1-track1a-monero.train \
    -output runs/run.reteco.bm25qs-train.monero.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-monero.train runs/run.reteco.bm25qs-train.monero.txt
```

#### politics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-politics \
    -topics reteco-v1.1-track1a-politics.train \
    -output runs/run.reteco.bm25qs-train.politics.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-politics.train runs/run.reteco.bm25qs-train.politics.txt
```

#### quant

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-quant \
    -topics reteco-v1.1-track1a-quant.train \
    -output runs/run.reteco.bm25qs-train.quant.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-quant.train runs/run.reteco.bm25qs-train.quant.txt
```

#### travel

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-travel \
    -topics reteco-v1.1-track1a-travel.train \
    -output runs/run.reteco.bm25qs-train.travel.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-travel.train runs/run.reteco.bm25qs-train.travel.txt
```

#### workplace

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-workplace \
    -topics reteco-v1.1-track1a-workplace.train \
    -output runs/run.reteco.bm25qs-train.workplace.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-workplace.train runs/run.reteco.bm25qs-train.workplace.txt
```

<a id="condition-4"></a>

### 4. Query-side BM25 (dev)

**Config**: [reteco.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/reteco.yaml#L251)

#### bitcoin

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-bitcoin \
    -topics reteco-v1.1-track1a-bitcoin.dev \
    -output runs/run.reteco.bm25qs-dev.bitcoin.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-bitcoin.dev runs/run.reteco.bm25qs-dev.bitcoin.txt
```

#### cardano

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-cardano \
    -topics reteco-v1.1-track1a-cardano.dev \
    -output runs/run.reteco.bm25qs-dev.cardano.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-cardano.dev runs/run.reteco.bm25qs-dev.cardano.txt
```

#### economics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-economics \
    -topics reteco-v1.1-track1a-economics.dev \
    -output runs/run.reteco.bm25qs-dev.economics.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-economics.dev runs/run.reteco.bm25qs-dev.economics.txt
```

#### genealogy

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-genealogy \
    -topics reteco-v1.1-track1a-genealogy.dev \
    -output runs/run.reteco.bm25qs-dev.genealogy.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-genealogy.dev runs/run.reteco.bm25qs-dev.genealogy.txt
```

#### history

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-history \
    -topics reteco-v1.1-track1a-history.dev \
    -output runs/run.reteco.bm25qs-dev.history.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-history.dev runs/run.reteco.bm25qs-dev.history.txt
```

#### hsm

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-hsm \
    -topics reteco-v1.1-track1a-hsm.dev \
    -output runs/run.reteco.bm25qs-dev.hsm.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-hsm.dev runs/run.reteco.bm25qs-dev.hsm.txt
```

#### iota

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-iota \
    -topics reteco-v1.1-track1a-iota.dev \
    -output runs/run.reteco.bm25qs-dev.iota.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-iota.dev runs/run.reteco.bm25qs-dev.iota.txt
```

#### law

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-law \
    -topics reteco-v1.1-track1a-law.dev \
    -output runs/run.reteco.bm25qs-dev.law.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-law.dev runs/run.reteco.bm25qs-dev.law.txt
```

#### monero

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-monero \
    -topics reteco-v1.1-track1a-monero.dev \
    -output runs/run.reteco.bm25qs-dev.monero.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-monero.dev runs/run.reteco.bm25qs-dev.monero.txt
```

#### politics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-politics \
    -topics reteco-v1.1-track1a-politics.dev \
    -output runs/run.reteco.bm25qs-dev.politics.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-politics.dev runs/run.reteco.bm25qs-dev.politics.txt
```

#### quant

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-quant \
    -topics reteco-v1.1-track1a-quant.dev \
    -output runs/run.reteco.bm25qs-dev.quant.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-quant.dev runs/run.reteco.bm25qs-dev.quant.txt
```

#### travel

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-travel \
    -topics reteco-v1.1-track1a-travel.dev \
    -output runs/run.reteco.bm25qs-dev.travel.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-travel.dev runs/run.reteco.bm25qs-dev.travel.txt
```

#### workplace

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 2 \
    -index reteco-v1.1-track1-workplace \
    -topics reteco-v1.1-track1a-workplace.dev \
    -output runs/run.reteco.bm25qs-dev.workplace.txt \
    -bm25.querySide \
    -bm25.k1 0.9 \
    -bm25.b 0.4 \
    -hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-workplace.dev runs/run.reteco.bm25qs-dev.workplace.txt
```
