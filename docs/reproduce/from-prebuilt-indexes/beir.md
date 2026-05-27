# <img src="../../anserini-logo.png" height="30" /> BEIR

**Anserini reproductions from prebuilt indexes**

+ **Corpus**: BEIR
+ **Config**: [beir.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/beir.yaml)

## Summary

The table below summarizes effectiveness in terms of nDCG@10.
For more metrics, refer to the config directly.

Key:

+ **BM25f** = BM25, flat bag-of-words baseline
+ **BM25mf** = BM25, multifield bag-of-words baseline
+ **SPLADE** = SPLADE-v3 (ONNX)
+ **BGE** = bge-base-en-v1.5 w/ flat indexes (ONNX)
+ **RRF** = Fusion: RRF (BM25 + BGE)
+ **Avg** = Fusion: Average (BM25 + BGE) with normalization

| corpus | [BM25f](#condition-1) | [BM25mf](#condition-2) | [SPLADE](#condition-3) | [BGE](#condition-4) | [RRF](#condition-5) | [Avg](#condition-6) |
| --- | --- | --- | --- | --- | --- | --- |
| trec-covid | 0.5947 | 0.6559 | 0.7299 | 0.7815 | 0.8041 | 0.7956 |
| bioasq | 0.5225 | 0.4646 | 0.5142 | 0.4148 | 0.5278 | 0.5428 |
| nfcorpus | 0.3218 | 0.3254 | 0.3629 | 0.3738 | 0.3725 | 0.3785 |
| nq | 0.3055 | 0.3285 | 0.5842 | 0.5415 | 0.4831 | 0.5183 |
| hotpotqa | 0.6330 | 0.6027 | 0.6884 | 0.7259 | 0.7389 | 0.7658 |
| fiqa | 0.2361 | 0.2361 | 0.3798 | 0.4065 | 0.3671 | 0.3942 |
| signal1m | 0.3304 | 0.3304 | 0.2465 | 0.2886 | 0.3533 | 0.3626 |
| trec-news | 0.3952 | 0.3977 | 0.4365 | 0.4424 | 0.4855 | 0.5008 |
| robust04 | 0.4070 | 0.4070 | 0.4952 | 0.4435 | 0.5070 | 0.5127 |
| arguana | 0.3970 | 0.4142 | 0.4862 | 0.6375 | 0.5626 | 0.5738 |
| webis-touche2020 | 0.4422 | 0.3673 | 0.3086 | 0.2571 | 0.3771 | 0.3755 |
| cqadupstack-android | 0.3801 | 0.3709 | 0.4109 | 0.5076 | 0.4652 | 0.4868 |
| cqadupstack-english | 0.3453 | 0.3321 | 0.4255 | 0.4857 | 0.4461 | 0.4678 |
| cqadupstack-gaming | 0.4822 | 0.4418 | 0.5193 | 0.5967 | 0.5615 | 0.5818 |
| cqadupstack-gis | 0.2901 | 0.2904 | 0.3236 | 0.4131 | 0.3679 | 0.3937 |
| cqadupstack-mathematica | 0.2015 | 0.2046 | 0.2445 | 0.3163 | 0.2751 | 0.2951 |
| cqadupstack-physics | 0.3214 | 0.3248 | 0.3753 | 0.4724 | 0.4143 | 0.4375 |
| cqadupstack-programmers | 0.2802 | 0.2963 | 0.3387 | 0.4238 | 0.3715 | 0.4005 |
| cqadupstack-stats | 0.2711 | 0.2790 | 0.3137 | 0.3728 | 0.3414 | 0.3534 |
| cqadupstack-tex | 0.2244 | 0.2086 | 0.2493 | 0.3115 | 0.2931 | 0.3090 |
| cqadupstack-unix | 0.2749 | 0.2788 | 0.3196 | 0.4220 | 0.3597 | 0.3853 |
| cqadupstack-webmasters | 0.3059 | 0.3008 | 0.3250 | 0.4072 | 0.3711 | 0.3857 |
| cqadupstack-wordpress | 0.2483 | 0.2562 | 0.2807 | 0.3547 | 0.3353 | 0.3546 |
| quora | 0.7886 | 0.7886 | 0.8141 | 0.8876 | 0.8682 | 0.8858 |
| dbpedia-entity | 0.3180 | 0.3128 | 0.4476 | 0.4073 | 0.4190 | 0.4374 |
| scidocs | 0.1490 | 0.1581 | 0.1567 | 0.2172 | 0.1948 | 0.2019 |
| fever | 0.6513 | 0.7530 | 0.8015 | 0.8629 | 0.8108 | 0.8584 |
| climate-fever | 0.1651 | 0.2129 | 0.2625 | 0.3117 | 0.2812 | 0.2946 |
| scifact | 0.6789 | 0.6647 | 0.7140 | 0.7408 | 0.7420 | 0.7472 |



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

### 1. BM25, flat bag-of-words baseline

**Config**: [beir.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/beir.yaml)

#### trec-covid

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-trec-covid.flat \
    -topics beir-trec-covid \
    -output runs/run.beir.flat.trec-covid.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-covid.test runs/run.beir.flat.trec-covid.txt
```

#### bioasq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-bioasq.flat \
    -topics beir-bioasq \
    -output runs/run.beir.flat.bioasq.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-bioasq.test runs/run.beir.flat.bioasq.txt
```

#### nfcorpus

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-nfcorpus.flat \
    -topics beir-nfcorpus \
    -output runs/run.beir.flat.nfcorpus.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nfcorpus.test runs/run.beir.flat.nfcorpus.txt
```

#### nq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-nq.flat \
    -topics beir-nq \
    -output runs/run.beir.flat.nq.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nq.test runs/run.beir.flat.nq.txt
```

#### hotpotqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-hotpotqa.flat \
    -topics beir-hotpotqa \
    -output runs/run.beir.flat.hotpotqa.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-hotpotqa.test runs/run.beir.flat.hotpotqa.txt
```

#### fiqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-fiqa.flat \
    -topics beir-fiqa \
    -output runs/run.beir.flat.fiqa.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fiqa.test runs/run.beir.flat.fiqa.txt
```

#### signal1m

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-signal1m.flat \
    -topics beir-signal1m \
    -output runs/run.beir.flat.signal1m.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-signal1m.test runs/run.beir.flat.signal1m.txt
```

#### trec-news

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-trec-news.flat \
    -topics beir-trec-news \
    -output runs/run.beir.flat.trec-news.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-news.test runs/run.beir.flat.trec-news.txt
```

#### robust04

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-robust04.flat \
    -topics beir-robust04 \
    -output runs/run.beir.flat.robust04.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-robust04.test runs/run.beir.flat.robust04.txt
```

#### arguana

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-arguana.flat \
    -topics beir-arguana \
    -output runs/run.beir.flat.arguana.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-arguana.test runs/run.beir.flat.arguana.txt
```

#### webis-touche2020

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-webis-touche2020.flat \
    -topics beir-webis-touche2020 \
    -output runs/run.beir.flat.webis-touche2020.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-webis-touche2020.test runs/run.beir.flat.webis-touche2020.txt
```

#### cqadupstack-android

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-android.flat \
    -topics beir-cqadupstack-android \
    -output runs/run.beir.flat.cqadupstack-android.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-android.test runs/run.beir.flat.cqadupstack-android.txt
```

#### cqadupstack-english

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-english.flat \
    -topics beir-cqadupstack-english \
    -output runs/run.beir.flat.cqadupstack-english.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-english.test runs/run.beir.flat.cqadupstack-english.txt
```

#### cqadupstack-gaming

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gaming.flat \
    -topics beir-cqadupstack-gaming \
    -output runs/run.beir.flat.cqadupstack-gaming.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gaming.test runs/run.beir.flat.cqadupstack-gaming.txt
```

#### cqadupstack-gis

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gis.flat \
    -topics beir-cqadupstack-gis \
    -output runs/run.beir.flat.cqadupstack-gis.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gis.test runs/run.beir.flat.cqadupstack-gis.txt
```

#### cqadupstack-mathematica

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-mathematica.flat \
    -topics beir-cqadupstack-mathematica \
    -output runs/run.beir.flat.cqadupstack-mathematica.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-mathematica.test runs/run.beir.flat.cqadupstack-mathematica.txt
```

#### cqadupstack-physics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-physics.flat \
    -topics beir-cqadupstack-physics \
    -output runs/run.beir.flat.cqadupstack-physics.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-physics.test runs/run.beir.flat.cqadupstack-physics.txt
```

#### cqadupstack-programmers

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-programmers.flat \
    -topics beir-cqadupstack-programmers \
    -output runs/run.beir.flat.cqadupstack-programmers.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-programmers.test runs/run.beir.flat.cqadupstack-programmers.txt
```

#### cqadupstack-stats

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-stats.flat \
    -topics beir-cqadupstack-stats \
    -output runs/run.beir.flat.cqadupstack-stats.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-stats.test runs/run.beir.flat.cqadupstack-stats.txt
```

#### cqadupstack-tex

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-tex.flat \
    -topics beir-cqadupstack-tex \
    -output runs/run.beir.flat.cqadupstack-tex.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-tex.test runs/run.beir.flat.cqadupstack-tex.txt
```

#### cqadupstack-unix

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-unix.flat \
    -topics beir-cqadupstack-unix \
    -output runs/run.beir.flat.cqadupstack-unix.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-unix.test runs/run.beir.flat.cqadupstack-unix.txt
```

#### cqadupstack-webmasters

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-webmasters.flat \
    -topics beir-cqadupstack-webmasters \
    -output runs/run.beir.flat.cqadupstack-webmasters.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-webmasters.test runs/run.beir.flat.cqadupstack-webmasters.txt
```

#### cqadupstack-wordpress

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-wordpress.flat \
    -topics beir-cqadupstack-wordpress \
    -output runs/run.beir.flat.cqadupstack-wordpress.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-wordpress.test runs/run.beir.flat.cqadupstack-wordpress.txt
```

#### quora

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-quora.flat \
    -topics beir-quora \
    -output runs/run.beir.flat.quora.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-quora.test runs/run.beir.flat.quora.txt
```

#### dbpedia-entity

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-dbpedia-entity.flat \
    -topics beir-dbpedia-entity \
    -output runs/run.beir.flat.dbpedia-entity.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-dbpedia-entity.test runs/run.beir.flat.dbpedia-entity.txt
```

#### scidocs

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-scidocs.flat \
    -topics beir-scidocs \
    -output runs/run.beir.flat.scidocs.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scidocs.test runs/run.beir.flat.scidocs.txt
```

#### fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-fever.flat \
    -topics beir-fever \
    -output runs/run.beir.flat.fever.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fever.test runs/run.beir.flat.fever.txt
```

#### climate-fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-climate-fever.flat \
    -topics beir-climate-fever \
    -output runs/run.beir.flat.climate-fever.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-climate-fever.test runs/run.beir.flat.climate-fever.txt
```

#### scifact

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-scifact.flat \
    -topics beir-scifact \
    -output runs/run.beir.flat.scifact.txt \
    -bm25 \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scifact.test runs/run.beir.flat.scifact.txt
```

<a id="condition-2"></a>

### 2. BM25, multifield bag-of-words baseline

**Config**: [beir.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/beir.yaml)

#### trec-covid

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-trec-covid.multifield \
    -topics beir-trec-covid \
    -output runs/run.beir.multifield.trec-covid.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-covid.test runs/run.beir.multifield.trec-covid.txt
```

#### bioasq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-bioasq.multifield \
    -topics beir-bioasq \
    -output runs/run.beir.multifield.bioasq.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-bioasq.test runs/run.beir.multifield.bioasq.txt
```

#### nfcorpus

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-nfcorpus.multifield \
    -topics beir-nfcorpus \
    -output runs/run.beir.multifield.nfcorpus.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nfcorpus.test runs/run.beir.multifield.nfcorpus.txt
```

#### nq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-nq.multifield \
    -topics beir-nq \
    -output runs/run.beir.multifield.nq.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nq.test runs/run.beir.multifield.nq.txt
```

#### hotpotqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-hotpotqa.multifield \
    -topics beir-hotpotqa \
    -output runs/run.beir.multifield.hotpotqa.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-hotpotqa.test runs/run.beir.multifield.hotpotqa.txt
```

#### fiqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-fiqa.multifield \
    -topics beir-fiqa \
    -output runs/run.beir.multifield.fiqa.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fiqa.test runs/run.beir.multifield.fiqa.txt
```

#### signal1m

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-signal1m.multifield \
    -topics beir-signal1m \
    -output runs/run.beir.multifield.signal1m.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-signal1m.test runs/run.beir.multifield.signal1m.txt
```

#### trec-news

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-trec-news.multifield \
    -topics beir-trec-news \
    -output runs/run.beir.multifield.trec-news.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-news.test runs/run.beir.multifield.trec-news.txt
```

#### robust04

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-robust04.multifield \
    -topics beir-robust04 \
    -output runs/run.beir.multifield.robust04.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-robust04.test runs/run.beir.multifield.robust04.txt
```

#### arguana

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-arguana.multifield \
    -topics beir-arguana \
    -output runs/run.beir.multifield.arguana.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-arguana.test runs/run.beir.multifield.arguana.txt
```

#### webis-touche2020

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-webis-touche2020.multifield \
    -topics beir-webis-touche2020 \
    -output runs/run.beir.multifield.webis-touche2020.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-webis-touche2020.test runs/run.beir.multifield.webis-touche2020.txt
```

#### cqadupstack-android

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-android.multifield \
    -topics beir-cqadupstack-android \
    -output runs/run.beir.multifield.cqadupstack-android.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-android.test runs/run.beir.multifield.cqadupstack-android.txt
```

#### cqadupstack-english

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-english.multifield \
    -topics beir-cqadupstack-english \
    -output runs/run.beir.multifield.cqadupstack-english.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-english.test runs/run.beir.multifield.cqadupstack-english.txt
```

#### cqadupstack-gaming

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gaming.multifield \
    -topics beir-cqadupstack-gaming \
    -output runs/run.beir.multifield.cqadupstack-gaming.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gaming.test runs/run.beir.multifield.cqadupstack-gaming.txt
```

#### cqadupstack-gis

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gis.multifield \
    -topics beir-cqadupstack-gis \
    -output runs/run.beir.multifield.cqadupstack-gis.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gis.test runs/run.beir.multifield.cqadupstack-gis.txt
```

#### cqadupstack-mathematica

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-mathematica.multifield \
    -topics beir-cqadupstack-mathematica \
    -output runs/run.beir.multifield.cqadupstack-mathematica.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-mathematica.test runs/run.beir.multifield.cqadupstack-mathematica.txt
```

#### cqadupstack-physics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-physics.multifield \
    -topics beir-cqadupstack-physics \
    -output runs/run.beir.multifield.cqadupstack-physics.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-physics.test runs/run.beir.multifield.cqadupstack-physics.txt
```

#### cqadupstack-programmers

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-programmers.multifield \
    -topics beir-cqadupstack-programmers \
    -output runs/run.beir.multifield.cqadupstack-programmers.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-programmers.test runs/run.beir.multifield.cqadupstack-programmers.txt
```

#### cqadupstack-stats

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-stats.multifield \
    -topics beir-cqadupstack-stats \
    -output runs/run.beir.multifield.cqadupstack-stats.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-stats.test runs/run.beir.multifield.cqadupstack-stats.txt
```

#### cqadupstack-tex

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-tex.multifield \
    -topics beir-cqadupstack-tex \
    -output runs/run.beir.multifield.cqadupstack-tex.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-tex.test runs/run.beir.multifield.cqadupstack-tex.txt
```

#### cqadupstack-unix

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-unix.multifield \
    -topics beir-cqadupstack-unix \
    -output runs/run.beir.multifield.cqadupstack-unix.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-unix.test runs/run.beir.multifield.cqadupstack-unix.txt
```

#### cqadupstack-webmasters

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-webmasters.multifield \
    -topics beir-cqadupstack-webmasters \
    -output runs/run.beir.multifield.cqadupstack-webmasters.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-webmasters.test runs/run.beir.multifield.cqadupstack-webmasters.txt
```

#### cqadupstack-wordpress

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-wordpress.multifield \
    -topics beir-cqadupstack-wordpress \
    -output runs/run.beir.multifield.cqadupstack-wordpress.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-wordpress.test runs/run.beir.multifield.cqadupstack-wordpress.txt
```

#### quora

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-quora.multifield \
    -topics beir-quora \
    -output runs/run.beir.multifield.quora.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-quora.test runs/run.beir.multifield.quora.txt
```

#### dbpedia-entity

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-dbpedia-entity.multifield \
    -topics beir-dbpedia-entity \
    -output runs/run.beir.multifield.dbpedia-entity.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-dbpedia-entity.test runs/run.beir.multifield.dbpedia-entity.txt
```

#### scidocs

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-scidocs.multifield \
    -topics beir-scidocs \
    -output runs/run.beir.multifield.scidocs.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scidocs.test runs/run.beir.multifield.scidocs.txt
```

#### fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-fever.multifield \
    -topics beir-fever \
    -output runs/run.beir.multifield.fever.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fever.test runs/run.beir.multifield.fever.txt
```

#### climate-fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-climate-fever.multifield \
    -topics beir-climate-fever \
    -output runs/run.beir.multifield.climate-fever.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-climate-fever.test runs/run.beir.multifield.climate-fever.txt
```

#### scifact

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-scifact.multifield \
    -topics beir-scifact \
    -output runs/run.beir.multifield.scifact.txt \
    -bm25 \
    -removeQuery \
    -fields contents=1.0 \
    title=1.0
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scifact.test runs/run.beir.multifield.scifact.txt
```

<a id="condition-3"></a>

### 3. SPLADE-v3 (ONNX)

**Config**: [beir.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/beir.yaml)

#### trec-covid

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-trec-covid.splade-v3 \
    -topics beir-trec-covid \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.trec-covid.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-covid.test runs/run.beir.splade-v3.onnx.trec-covid.txt
```

#### bioasq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-bioasq.splade-v3 \
    -topics beir-bioasq \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.bioasq.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-bioasq.test runs/run.beir.splade-v3.onnx.bioasq.txt
```

#### nfcorpus

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-nfcorpus.splade-v3 \
    -topics beir-nfcorpus \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.nfcorpus.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nfcorpus.test runs/run.beir.splade-v3.onnx.nfcorpus.txt
```

#### nq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-nq.splade-v3 \
    -topics beir-nq \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.nq.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nq.test runs/run.beir.splade-v3.onnx.nq.txt
```

#### hotpotqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-hotpotqa.splade-v3 \
    -topics beir-hotpotqa \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.hotpotqa.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-hotpotqa.test runs/run.beir.splade-v3.onnx.hotpotqa.txt
```

#### fiqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-fiqa.splade-v3 \
    -topics beir-fiqa \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.fiqa.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fiqa.test runs/run.beir.splade-v3.onnx.fiqa.txt
```

#### signal1m

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-signal1m.splade-v3 \
    -topics beir-signal1m \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.signal1m.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-signal1m.test runs/run.beir.splade-v3.onnx.signal1m.txt
```

#### trec-news

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-trec-news.splade-v3 \
    -topics beir-trec-news \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.trec-news.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-news.test runs/run.beir.splade-v3.onnx.trec-news.txt
```

#### robust04

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-robust04.splade-v3 \
    -topics beir-robust04 \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.robust04.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-robust04.test runs/run.beir.splade-v3.onnx.robust04.txt
```

#### arguana

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-arguana.splade-v3 \
    -topics beir-arguana \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.arguana.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-arguana.test runs/run.beir.splade-v3.onnx.arguana.txt
```

#### webis-touche2020

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-webis-touche2020.splade-v3 \
    -topics beir-webis-touche2020 \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.webis-touche2020.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-webis-touche2020.test runs/run.beir.splade-v3.onnx.webis-touche2020.txt
```

#### cqadupstack-android

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-android.splade-v3 \
    -topics beir-cqadupstack-android \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-android.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-android.test runs/run.beir.splade-v3.onnx.cqadupstack-android.txt
```

#### cqadupstack-english

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-english.splade-v3 \
    -topics beir-cqadupstack-english \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-english.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-english.test runs/run.beir.splade-v3.onnx.cqadupstack-english.txt
```

#### cqadupstack-gaming

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gaming.splade-v3 \
    -topics beir-cqadupstack-gaming \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-gaming.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gaming.test runs/run.beir.splade-v3.onnx.cqadupstack-gaming.txt
```

#### cqadupstack-gis

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gis.splade-v3 \
    -topics beir-cqadupstack-gis \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-gis.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gis.test runs/run.beir.splade-v3.onnx.cqadupstack-gis.txt
```

#### cqadupstack-mathematica

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-mathematica.splade-v3 \
    -topics beir-cqadupstack-mathematica \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-mathematica.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-mathematica.test runs/run.beir.splade-v3.onnx.cqadupstack-mathematica.txt
```

#### cqadupstack-physics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-physics.splade-v3 \
    -topics beir-cqadupstack-physics \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-physics.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-physics.test runs/run.beir.splade-v3.onnx.cqadupstack-physics.txt
```

#### cqadupstack-programmers

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-programmers.splade-v3 \
    -topics beir-cqadupstack-programmers \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-programmers.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-programmers.test runs/run.beir.splade-v3.onnx.cqadupstack-programmers.txt
```

#### cqadupstack-stats

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-stats.splade-v3 \
    -topics beir-cqadupstack-stats \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-stats.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-stats.test runs/run.beir.splade-v3.onnx.cqadupstack-stats.txt
```

#### cqadupstack-tex

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-tex.splade-v3 \
    -topics beir-cqadupstack-tex \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-tex.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-tex.test runs/run.beir.splade-v3.onnx.cqadupstack-tex.txt
```

#### cqadupstack-unix

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-unix.splade-v3 \
    -topics beir-cqadupstack-unix \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-unix.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-unix.test runs/run.beir.splade-v3.onnx.cqadupstack-unix.txt
```

#### cqadupstack-webmasters

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-webmasters.splade-v3 \
    -topics beir-cqadupstack-webmasters \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-webmasters.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-webmasters.test runs/run.beir.splade-v3.onnx.cqadupstack-webmasters.txt
```

#### cqadupstack-wordpress

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-wordpress.splade-v3 \
    -topics beir-cqadupstack-wordpress \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.cqadupstack-wordpress.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-wordpress.test runs/run.beir.splade-v3.onnx.cqadupstack-wordpress.txt
```

#### quora

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-quora.splade-v3 \
    -topics beir-quora \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.quora.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-quora.test runs/run.beir.splade-v3.onnx.quora.txt
```

#### dbpedia-entity

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-dbpedia-entity.splade-v3 \
    -topics beir-dbpedia-entity \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.dbpedia-entity.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-dbpedia-entity.test runs/run.beir.splade-v3.onnx.dbpedia-entity.txt
```

#### scidocs

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-scidocs.splade-v3 \
    -topics beir-scidocs \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.scidocs.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scidocs.test runs/run.beir.splade-v3.onnx.scidocs.txt
```

#### fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-fever.splade-v3 \
    -topics beir-fever \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.fever.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fever.test runs/run.beir.splade-v3.onnx.fever.txt
```

#### climate-fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-climate-fever.splade-v3 \
    -topics beir-climate-fever \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.climate-fever.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-climate-fever.test runs/run.beir.splade-v3.onnx.climate-fever.txt
```

#### scifact

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchCollection \
    -threads 16 \
    -index beir-v1.0.0-scifact.splade-v3 \
    -topics beir-scifact \
    -encoder SpladeV3 \
    -output runs/run.beir.splade-v3.onnx.scifact.txt \
    -impact \
    -pretokenized \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scifact.test runs/run.beir.splade-v3.onnx.scifact.txt
```

<a id="condition-4"></a>

### 4. bge-base-en-v1.5 w/ flat indexes (ONNX)

**Config**: [beir.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/beir.yaml)

#### trec-covid

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-trec-covid.bge-base-en-v1.5.flat \
    -topics beir-trec-covid \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-covid.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-covid.test runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-covid.txt
```

#### bioasq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-bioasq.bge-base-en-v1.5.flat \
    -topics beir-bioasq \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.bioasq.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-bioasq.test runs/run.beir.bge-base-en-v1.5.flat.onnx.bioasq.txt
```

#### nfcorpus

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-nfcorpus.bge-base-en-v1.5.flat \
    -topics beir-nfcorpus \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.nfcorpus.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nfcorpus.test runs/run.beir.bge-base-en-v1.5.flat.onnx.nfcorpus.txt
```

#### nq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-nq.bge-base-en-v1.5.flat \
    -topics beir-nq \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.nq.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nq.test runs/run.beir.bge-base-en-v1.5.flat.onnx.nq.txt
```

#### hotpotqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-hotpotqa.bge-base-en-v1.5.flat \
    -topics beir-hotpotqa \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.hotpotqa.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-hotpotqa.test runs/run.beir.bge-base-en-v1.5.flat.onnx.hotpotqa.txt
```

#### fiqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-fiqa.bge-base-en-v1.5.flat \
    -topics beir-fiqa \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.fiqa.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fiqa.test runs/run.beir.bge-base-en-v1.5.flat.onnx.fiqa.txt
```

#### signal1m

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-signal1m.bge-base-en-v1.5.flat \
    -topics beir-signal1m \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.signal1m.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-signal1m.test runs/run.beir.bge-base-en-v1.5.flat.onnx.signal1m.txt
```

#### trec-news

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-trec-news.bge-base-en-v1.5.flat \
    -topics beir-trec-news \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-news.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-news.test runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-news.txt
```

#### robust04

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-robust04.bge-base-en-v1.5.flat \
    -topics beir-robust04 \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.robust04.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-robust04.test runs/run.beir.bge-base-en-v1.5.flat.onnx.robust04.txt
```

#### arguana

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-arguana.bge-base-en-v1.5.flat \
    -topics beir-arguana \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.arguana.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-arguana.test runs/run.beir.bge-base-en-v1.5.flat.onnx.arguana.txt
```

#### webis-touche2020

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.flat \
    -topics beir-webis-touche2020 \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.webis-touche2020.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-webis-touche2020.test runs/run.beir.bge-base-en-v1.5.flat.onnx.webis-touche2020.txt
```

#### cqadupstack-android

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-android \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-android.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-android.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-android.txt
```

#### cqadupstack-english

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-english \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-english.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-english.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-english.txt
```

#### cqadupstack-gaming

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-gaming \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gaming.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gaming.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gaming.txt
```

#### cqadupstack-gis

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-gis \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gis.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gis.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gis.txt
```

#### cqadupstack-mathematica

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-mathematica \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-mathematica.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-mathematica.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-mathematica.txt
```

#### cqadupstack-physics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-physics \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-physics.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-physics.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-physics.txt
```

#### cqadupstack-programmers

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-programmers \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-programmers.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-programmers.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-programmers.txt
```

#### cqadupstack-stats

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-stats \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-stats.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-stats.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-stats.txt
```

#### cqadupstack-tex

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-tex \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-tex.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-tex.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-tex.txt
```

#### cqadupstack-unix

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-unix \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-unix.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-unix.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-unix.txt
```

#### cqadupstack-webmasters

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-webmasters \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-webmasters.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-webmasters.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-webmasters.txt
```

#### cqadupstack-wordpress

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.flat \
    -topics beir-cqadupstack-wordpress \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-wordpress.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-wordpress.test runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-wordpress.txt
```

#### quora

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-quora.bge-base-en-v1.5.flat \
    -topics beir-quora \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.quora.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-quora.test runs/run.beir.bge-base-en-v1.5.flat.onnx.quora.txt
```

#### dbpedia-entity

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.flat \
    -topics beir-dbpedia-entity \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.dbpedia-entity.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-dbpedia-entity.test runs/run.beir.bge-base-en-v1.5.flat.onnx.dbpedia-entity.txt
```

#### scidocs

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-scidocs.bge-base-en-v1.5.flat \
    -topics beir-scidocs \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.scidocs.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scidocs.test runs/run.beir.bge-base-en-v1.5.flat.onnx.scidocs.txt
```

#### fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-fever.bge-base-en-v1.5.flat \
    -topics beir-fever \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.fever.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fever.test runs/run.beir.bge-base-en-v1.5.flat.onnx.fever.txt
```

#### climate-fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-climate-fever.bge-base-en-v1.5.flat \
    -topics beir-climate-fever \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.climate-fever.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-climate-fever.test runs/run.beir.bge-base-en-v1.5.flat.onnx.climate-fever.txt
```

#### scifact

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.search.SearchFlatDenseVectors \
    -threads 16 \
    -index beir-v1.0.0-scifact.bge-base-en-v1.5.flat \
    -topics beir-scifact \
    -encoder BgeBaseEn15 \
    -output runs/run.beir.bge-base-en-v1.5.flat.onnx.scifact.txt \
    -removeQuery
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scifact.test runs/run.beir.bge-base-en-v1.5.flat.onnx.scifact.txt
```

<a id="condition-5"></a>

### 5. Fusion: RRF (BM25 + BGE)

**Config**: [beir.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/beir.yaml)

#### trec-covid

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.trec-covid.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-covid.txt \
    -output runs/run.beir.fusion-rrf.trec-covid.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-covid.test runs/run.beir.fusion-rrf.trec-covid.txt
```

#### bioasq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.bioasq.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.bioasq.txt \
    -output runs/run.beir.fusion-rrf.bioasq.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-bioasq.test runs/run.beir.fusion-rrf.bioasq.txt
```

#### nfcorpus

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.nfcorpus.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.nfcorpus.txt \
    -output runs/run.beir.fusion-rrf.nfcorpus.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nfcorpus.test runs/run.beir.fusion-rrf.nfcorpus.txt
```

#### nq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.nq.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.nq.txt \
    -output runs/run.beir.fusion-rrf.nq.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nq.test runs/run.beir.fusion-rrf.nq.txt
```

#### hotpotqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.hotpotqa.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.hotpotqa.txt \
    -output runs/run.beir.fusion-rrf.hotpotqa.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-hotpotqa.test runs/run.beir.fusion-rrf.hotpotqa.txt
```

#### fiqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.fiqa.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.fiqa.txt \
    -output runs/run.beir.fusion-rrf.fiqa.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fiqa.test runs/run.beir.fusion-rrf.fiqa.txt
```

#### signal1m

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.signal1m.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.signal1m.txt \
    -output runs/run.beir.fusion-rrf.signal1m.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-signal1m.test runs/run.beir.fusion-rrf.signal1m.txt
```

#### trec-news

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.trec-news.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-news.txt \
    -output runs/run.beir.fusion-rrf.trec-news.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-news.test runs/run.beir.fusion-rrf.trec-news.txt
```

#### robust04

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.robust04.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.robust04.txt \
    -output runs/run.beir.fusion-rrf.robust04.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-robust04.test runs/run.beir.fusion-rrf.robust04.txt
```

#### arguana

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.arguana.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.arguana.txt \
    -output runs/run.beir.fusion-rrf.arguana.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-arguana.test runs/run.beir.fusion-rrf.arguana.txt
```

#### webis-touche2020

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.webis-touche2020.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.webis-touche2020.txt \
    -output runs/run.beir.fusion-rrf.webis-touche2020.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-webis-touche2020.test runs/run.beir.fusion-rrf.webis-touche2020.txt
```

#### cqadupstack-android

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-android.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-android.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-android.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-android.test runs/run.beir.fusion-rrf.cqadupstack-android.txt
```

#### cqadupstack-english

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-english.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-english.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-english.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-english.test runs/run.beir.fusion-rrf.cqadupstack-english.txt
```

#### cqadupstack-gaming

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-gaming.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gaming.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-gaming.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gaming.test runs/run.beir.fusion-rrf.cqadupstack-gaming.txt
```

#### cqadupstack-gis

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-gis.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gis.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-gis.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gis.test runs/run.beir.fusion-rrf.cqadupstack-gis.txt
```

#### cqadupstack-mathematica

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-mathematica.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-mathematica.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-mathematica.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-mathematica.test runs/run.beir.fusion-rrf.cqadupstack-mathematica.txt
```

#### cqadupstack-physics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-physics.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-physics.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-physics.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-physics.test runs/run.beir.fusion-rrf.cqadupstack-physics.txt
```

#### cqadupstack-programmers

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-programmers.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-programmers.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-programmers.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-programmers.test runs/run.beir.fusion-rrf.cqadupstack-programmers.txt
```

#### cqadupstack-stats

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-stats.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-stats.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-stats.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-stats.test runs/run.beir.fusion-rrf.cqadupstack-stats.txt
```

#### cqadupstack-tex

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-tex.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-tex.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-tex.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-tex.test runs/run.beir.fusion-rrf.cqadupstack-tex.txt
```

#### cqadupstack-unix

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-unix.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-unix.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-unix.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-unix.test runs/run.beir.fusion-rrf.cqadupstack-unix.txt
```

#### cqadupstack-webmasters

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-webmasters.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-webmasters.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-webmasters.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-webmasters.test runs/run.beir.fusion-rrf.cqadupstack-webmasters.txt
```

#### cqadupstack-wordpress

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-wordpress.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-wordpress.txt \
    -output runs/run.beir.fusion-rrf.cqadupstack-wordpress.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-wordpress.test runs/run.beir.fusion-rrf.cqadupstack-wordpress.txt
```

#### quora

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.quora.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.quora.txt \
    -output runs/run.beir.fusion-rrf.quora.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-quora.test runs/run.beir.fusion-rrf.quora.txt
```

#### dbpedia-entity

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.dbpedia-entity.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.dbpedia-entity.txt \
    -output runs/run.beir.fusion-rrf.dbpedia-entity.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-dbpedia-entity.test runs/run.beir.fusion-rrf.dbpedia-entity.txt
```

#### scidocs

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.scidocs.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.scidocs.txt \
    -output runs/run.beir.fusion-rrf.scidocs.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scidocs.test runs/run.beir.fusion-rrf.scidocs.txt
```

#### fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.fever.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.fever.txt \
    -output runs/run.beir.fusion-rrf.fever.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fever.test runs/run.beir.fusion-rrf.fever.txt
```

#### climate-fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.climate-fever.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.climate-fever.txt \
    -output runs/run.beir.fusion-rrf.climate-fever.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-climate-fever.test runs/run.beir.fusion-rrf.climate-fever.txt
```

#### scifact

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.scifact.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.scifact.txt \
    -output runs/run.beir.fusion-rrf.scifact.txt \
    -method rrf \
    -k 1000 \
    -depth 1000 \
    -rrf_k 60
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scifact.test runs/run.beir.fusion-rrf.scifact.txt
```

<a id="condition-6"></a>

### 6. Fusion: Average (BM25 + BGE) with normalization

**Config**: [beir.yaml](../../../src/main/resources/reproduce/from-prebuilt-indexes/configs/beir.yaml)

#### trec-covid

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.trec-covid.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-covid.txt \
    -output runs/run.beir.fusion-avg.trec-covid.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-covid.test runs/run.beir.fusion-avg.trec-covid.txt
```

#### bioasq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.bioasq.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.bioasq.txt \
    -output runs/run.beir.fusion-avg.bioasq.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-bioasq.test runs/run.beir.fusion-avg.bioasq.txt
```

#### nfcorpus

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.nfcorpus.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.nfcorpus.txt \
    -output runs/run.beir.fusion-avg.nfcorpus.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nfcorpus.test runs/run.beir.fusion-avg.nfcorpus.txt
```

#### nq

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.nq.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.nq.txt \
    -output runs/run.beir.fusion-avg.nq.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-nq.test runs/run.beir.fusion-avg.nq.txt
```

#### hotpotqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.hotpotqa.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.hotpotqa.txt \
    -output runs/run.beir.fusion-avg.hotpotqa.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-hotpotqa.test runs/run.beir.fusion-avg.hotpotqa.txt
```

#### fiqa

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.fiqa.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.fiqa.txt \
    -output runs/run.beir.fusion-avg.fiqa.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fiqa.test runs/run.beir.fusion-avg.fiqa.txt
```

#### signal1m

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.signal1m.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.signal1m.txt \
    -output runs/run.beir.fusion-avg.signal1m.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-signal1m.test runs/run.beir.fusion-avg.signal1m.txt
```

#### trec-news

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.trec-news.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.trec-news.txt \
    -output runs/run.beir.fusion-avg.trec-news.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-trec-news.test runs/run.beir.fusion-avg.trec-news.txt
```

#### robust04

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.robust04.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.robust04.txt \
    -output runs/run.beir.fusion-avg.robust04.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-robust04.test runs/run.beir.fusion-avg.robust04.txt
```

#### arguana

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.arguana.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.arguana.txt \
    -output runs/run.beir.fusion-avg.arguana.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-arguana.test runs/run.beir.fusion-avg.arguana.txt
```

#### webis-touche2020

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.webis-touche2020.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.webis-touche2020.txt \
    -output runs/run.beir.fusion-avg.webis-touche2020.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-webis-touche2020.test runs/run.beir.fusion-avg.webis-touche2020.txt
```

#### cqadupstack-android

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-android.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-android.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-android.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-android.test runs/run.beir.fusion-avg.cqadupstack-android.txt
```

#### cqadupstack-english

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-english.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-english.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-english.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-english.test runs/run.beir.fusion-avg.cqadupstack-english.txt
```

#### cqadupstack-gaming

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-gaming.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gaming.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-gaming.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gaming.test runs/run.beir.fusion-avg.cqadupstack-gaming.txt
```

#### cqadupstack-gis

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-gis.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-gis.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-gis.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-gis.test runs/run.beir.fusion-avg.cqadupstack-gis.txt
```

#### cqadupstack-mathematica

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-mathematica.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-mathematica.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-mathematica.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-mathematica.test runs/run.beir.fusion-avg.cqadupstack-mathematica.txt
```

#### cqadupstack-physics

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-physics.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-physics.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-physics.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-physics.test runs/run.beir.fusion-avg.cqadupstack-physics.txt
```

#### cqadupstack-programmers

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-programmers.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-programmers.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-programmers.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-programmers.test runs/run.beir.fusion-avg.cqadupstack-programmers.txt
```

#### cqadupstack-stats

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-stats.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-stats.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-stats.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-stats.test runs/run.beir.fusion-avg.cqadupstack-stats.txt
```

#### cqadupstack-tex

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-tex.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-tex.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-tex.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-tex.test runs/run.beir.fusion-avg.cqadupstack-tex.txt
```

#### cqadupstack-unix

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-unix.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-unix.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-unix.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-unix.test runs/run.beir.fusion-avg.cqadupstack-unix.txt
```

#### cqadupstack-webmasters

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-webmasters.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-webmasters.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-webmasters.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-webmasters.test runs/run.beir.fusion-avg.cqadupstack-webmasters.txt
```

#### cqadupstack-wordpress

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.cqadupstack-wordpress.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.cqadupstack-wordpress.txt \
    -output runs/run.beir.fusion-avg.cqadupstack-wordpress.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-wordpress.test runs/run.beir.fusion-avg.cqadupstack-wordpress.txt
```

#### quora

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.quora.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.quora.txt \
    -output runs/run.beir.fusion-avg.quora.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-quora.test runs/run.beir.fusion-avg.quora.txt
```

#### dbpedia-entity

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.dbpedia-entity.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.dbpedia-entity.txt \
    -output runs/run.beir.fusion-avg.dbpedia-entity.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-dbpedia-entity.test runs/run.beir.fusion-avg.dbpedia-entity.txt
```

#### scidocs

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.scidocs.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.scidocs.txt \
    -output runs/run.beir.fusion-avg.scidocs.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scidocs.test runs/run.beir.fusion-avg.scidocs.txt
```

#### fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.fever.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.fever.txt \
    -output runs/run.beir.fusion-avg.fever.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-fever.test runs/run.beir.fusion-avg.fever.txt
```

#### climate-fever

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.climate-fever.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.climate-fever.txt \
    -output runs/run.beir.fusion-avg.climate-fever.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-climate-fever.test runs/run.beir.fusion-avg.climate-fever.txt
```

#### scifact

Retrieval command:

```bash
java -cp $fatjar $jvm_args io.anserini.fusion.FuseRuns \
    -runs runs/run.beir.flat.scifact.txt \
    runs/run.beir.bge-base-en-v1.5.flat.onnx.scifact.txt \
    -output runs/run.beir.fusion-avg.scifact.txt \
    -method average \
    -k 1000 \
    -depth 1000 \
    -min_max_normalization
```

Evaluation commands:

```bash
java -cp $fatjar trec_eval -c -m ndcg_cut.10 beir-v1.0.0-scifact.test runs/run.beir.fusion-avg.scifact.txt
```


