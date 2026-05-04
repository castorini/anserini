# <img src="../../anserini-logo.png" height="30" /> MS MARCO V2 Doc

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: MS MARCO V2 Doc
+ **Config**: [msmarco-v2-doc.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.optional.yaml)

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
| [1](#condition-1) | uniCOIL noexp (cached queries) | 0.2231 | 0.2314 | 0.6495 | 0.4165 | 0.3898 |
| [2](#condition-2) | uniCOIL with doc2query-T5 (cached queries) | 0.2419 | 0.2445 | 0.6783 | 0.4451 | 0.4149 |



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

### 1. uniCOIL noexp (cached queries)

**Config**: [msmarco-v2-doc.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.optional.yaml)

#### msmarco-v2-doc.dev.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-noexp-0shot \
    -topics msmarco-v2-doc.dev.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.msmarco-v2-doc.dev.unicoil-noexp.0shot.txt \
    -parallelism 16 \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.msmarco-v2-doc.dev.unicoil-noexp.0shot.txt
```

#### msmarco-v2-doc.dev2.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-noexp-0shot \
    -topics msmarco-v2-doc.dev2.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.msmarco-v2-doc.dev2.unicoil-noexp.0shot.txt \
    -parallelism 16 \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.msmarco-v2-doc.dev2.unicoil-noexp.0shot.txt
```

#### dl21.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-noexp-0shot \
    -topics dl21.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt \
    -parallelism 16 \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl21.unicoil-noexp.0shot.txt
```

#### dl22.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-noexp-0shot \
    -topics dl22.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt \
    -parallelism 16 \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl22.unicoil-noexp.0shot.txt
```

#### dl23.unicoil-noexp.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-noexp-0shot \
    -topics dl23.unicoil-noexp.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt \
    -parallelism 16 \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc runs/run.msmarco-v2-doc.optional.unicoil-noexp.cached.dl23.unicoil-noexp.0shot.txt
```

<a id="condition-2"></a>

### 2. uniCOIL with doc2query-T5 (cached queries)

**Config**: [msmarco-v2-doc.optional.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v2-doc.optional.yaml)

#### msmarco-v2-doc.dev.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics msmarco-v2-doc.dev.unicoil.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil.cached.msmarco-v2-doc.dev.unicoil.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev runs/run.msmarco-v2-doc.optional.unicoil.cached.msmarco-v2-doc.dev.unicoil.0shot.txt
```

#### msmarco-v2-doc.dev2.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics msmarco-v2-doc.dev2.unicoil.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil.cached.msmarco-v2-doc.dev2.unicoil.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m recip_rank msmarco-v2-doc.dev2 runs/run.msmarco-v2-doc.optional.unicoil.cached.msmarco-v2-doc.dev2.unicoil.0shot.txt
```

#### dl21.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics dl21.unicoil.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil.cached.dl21.unicoil.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl21-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl21-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl21-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 dl21-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl21.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl21-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl21.unicoil.0shot.txt
```

#### dl22.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics dl22.unicoil.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil.cached.dl22.unicoil.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl22-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl22-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl22-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 dl22-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl22.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl22-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl22.unicoil.0shot.txt
```

#### dl23.unicoil.0shot

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index msmarco-v2-doc-segmented.unicoil-0shot \
    -topics dl23.unicoil.0shot \
    -output runs/run.msmarco-v2-doc.optional.unicoil.cached.dl23.unicoil.0shot.txt \
    -impact \
    -pretokenized \
    -hits 10000 \
    -selectMaxPassage \
    -selectMaxPassage.delimiter \# \
    -selectMaxPassage.hits 1000
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -M 100 -m map dl23-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -M 100 -m recip_rank dl23-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m ndcg_cut.10 dl23-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.100 dl23-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl23.unicoil.0shot.txt
java -cp $fatjar trec_eval -c -m recall.1000 dl23-doc runs/run.msmarco-v2-doc.optional.unicoil.cached.dl23.unicoil.0shot.txt
```


