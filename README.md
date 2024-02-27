Anserini <img src="docs/anserini-logo.png" width="300" />
========
[![build](https://github.com/castorini/anserini/actions/workflows/maven.yml/badge.svg)](https://github.com/castorini/anserini/actions)
[![codecov](https://codecov.io/gh/castorini/anserini/branch/master/graph/badge.svg)](https://codecov.io/gh/castorini/anserini)
[![Generic badge](https://img.shields.io/badge/Lucene-v9.9.1-brightgreen.svg)](https://archive.apache.org/dist/lucene/java/9.9.1/)
[![Maven Central](https://img.shields.io/maven-central/v/io.anserini/anserini?color=brightgreen)](https://central.sonatype.com/namespace/io.anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![doi](http://img.shields.io/badge/doi-10.1145%2F3239571-blue.svg?style=flat)](https://doi.org/10.1145/3239571)

Anserini is a toolkit for reproducible information retrieval research.
By building on Lucene, we aim to bridge the gap between academic information retrieval research and the practice of building real-world search applications.
Among other goals, our effort aims to be [the opposite of this](http://phdcomics.com/comics/archive.php?comicid=1689).[*](docs/reproducibility.md)
Anserini grew out of [a reproducibility study of various open-source retrieval engines in 2016](https://link.springer.com/chapter/10.1007/978-3-319-30671-1_30) (Lin et al., ECIR 2016). 
See [Yang et al. (SIGIR 2017)](https://dl.acm.org/doi/10.1145/3077136.3080721) and [Yang et al. (JDIQ 2018)](https://dl.acm.org/doi/10.1145/3239571) for overviews.

## 💥 Try It!

Anserini is packaged in a self-contained fatjar, which also provides the simplest way to get started.
Assuming you've already got Java installed, fetch the fatjar:

```bash
wget https://repo1.maven.org/maven2/io/anserini/anserini/0.24.2/anserini-0.24.2-fatjar.jar
```

The follow commands will generate a SPLADE++ ED run with the dev queries (encoded using ONNX) on the MS MARCO passage corpus:

```bash
java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection \
  -index msmarco-v1-passage-splade-pp-ed \
  -topics msmarco-v1-passage-dev \
  -encoder SpladePlusPlusEnsembleDistil \
  -output run.msmacro-dev.splade-pp-ed-onnx.txt \
  -impact -pretokenized
```

To evaluate:

```bash
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.splade-pp-ed-onnx.txt
```

See below for instructions on using Anserini to reproduce runs from MS MARCO passage and BEIR, all directly from the fatjar!

<details>
<summary>Regressions directly from the fatjar: MS MARCO passage</summary>

Currently, Anserini provides support for the following models:

+ BM25
+ SPLADE++ EnsembleDistil: pre-encoded queries and ONNX query encoding
+ cosDPR-distil: pre-encoded queries and ONNX query encoding
+ BGE-base-en-v1.5: pre-encoded queries and ONNX query encoding

The following snippet will generate the complete set of results for MS MARCO passage:

```bash
# BM25
TOPICS=(msmarco-v1-passage-dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage -topics ${t} -output run.${t}.bm25.txt -threads 16 -bm25
done

# SPLADE++ ED
TOPICS=(msmarco-v1-passage-dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using pre-encoded queries
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage-splade-pp-ed -topics ${t}-splade-pp-ed -output run.${t}.splade-pp-ed-pre.txt -threads 16 -impact -pretokenized
    # Using ONNX
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage-splade-pp-ed -topics ${t} -encoder SpladePlusPlusEnsembleDistil -output run.${t}.splade-pp-ed-onnx.txt -threads 16 -impact -pretokenized
done

# cosDPR-distil
TOPICS=(msmarco-v1-passage-dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using pre-encoded queries, full index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil -topics ${t}-cos-dpr-distil -output run.${t}.cos-dpr-distil-full-pre.txt -threads 16 -efSearch 1000
    # Using pre-encoded queries, quantized index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil-quantized -topics ${t}-cos-dpr-distil -output run.${t}.cos-dpr-distil-quantized-pre.txt -threads 16 -efSearch 1000
    # Using ONNX, full index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil -topics ${t} -encoder CosDprDistil -output run.${t}.cos-dpr-distil-full-onnx.txt -threads 16 -efSearch 1000
    # Using ONNX, quantized index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil-quantized -topics ${t} -encoder CosDprDistil -output run.${t}.cos-dpr-distil-quantized-onnx.txt -threads 16 -efSearch 1000
done

# BGE-base-en-v1.5
TOPICS=(msmarco-v1-passage-dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using pre-encoded queries, full index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5 -topics ${t}-bge-base-en-v1.5 -output run.${t}.bge-base-en-v1.5-full-pre.txt -threads 16 -efSearch 1000
    # Using pre-encoded queries, quantized index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5-quantized -topics ${t}-bge-base-en-v1.5 -output run.${t}.bge-base-en-v1.5-quantized-pre.txt -threads 16 -efSearch 1000
    # Using ONNX, full index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5 -topics ${t} -encoder BgeBaseEn15 -output run.${t}.bge-base-en-v1.5-full-onnx.txt -threads 16 -efSearch 1000
    # Using ONNX, quantized index
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5-quantized -topics ${t} -encoder BgeBaseEn15 -output run.${t}.bge-base-en-v1.5-quantized-onnx.txt -threads 16 -efSearch 1000
done
```
Here are the expected scores (dev measured in terms of MRR@10, DL19 and DL20 measured in terms of nDCG@10):

|                                                |    dev |   DL19 |   DL20 |
|:-----------------------------------------------|-------:|-------:|-------:|
| BM25                                           | 0.1840 | 0.5058 | 0.4796 |
| SPLADE++ ED (pre-encoded)                      | 0.3830 | 0.7317 | 0.7198 |
| SPLADE++ ED (ONNX)                             | 0.3828 | 0.7308 | 0.7197 |
| cos-DPR: full HNSW (pre-encoded)               | 0.3887 | 0.7250 | 0.7025 |
| cos-DPR: quantized HNSW (pre-encoded)          | 0.3897 | 0.7240 | 0.7004 |
| cos-DPR: full HNSW ONNX)                       | 0.3887 | 0.7250 | 0.7025 |
| cos-DPR: quantized HNSW (ONNX)                 | 0.3899 | 0.7247 | 0.6996 |
| BGE-base-en-v1.5: full HNSW (pre-encoded)      | 0.3574 | 0.7065 | 0.6780 |
| BGE-base-en-v1.5: quantized HNSW (pre-encoded) | 0.3572 | 0.7016 | 0.6738 |
| BGE-base-en-v1.5: full HNSW (ONNX)             | 0.3575 | 0.7016 | 0.6768 |
| BGE-base-en-v1.5: quantized HNSW (ONNX)        | 0.3575 | 0.7017 | 0.6767 |

And here's the snippet of code to perform the evaluation (which will yield the results above):

```bash
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl19-passage.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl20-passage.txt

java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bm25.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bm25.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bm25.txt

java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.splade-pp-ed-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.splade-pp-ed-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.splade-pp-ed-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.splade-pp-ed-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.splade-pp-ed-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.splade-pp-ed-onnx.txt

java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-full-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-full-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-full-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-quantized-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-quantized-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-quantized-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-full-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-full-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-full-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-quantized-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-quantized-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-quantized-onnx.txt

java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-full-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-full-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-full-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-quantized-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-quantized-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-quantized-pre.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-full-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-full-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-full-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-quantized-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-quantized-onnx.txt
java -cp anserini-0.24.2-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-quantized-onnx.txt
```

</details>
<details>
<summary>Regressions directly from the fatjar: BEIR</summary>

Currently, Anserini provides support for the following models:

+ Flat = "flat" bag-of-words baseline
+ MF = "multifield" bag-of-words baseline
+ S = SPLADE++ EnsembleDistil:
  + Pre-encoded queries (Sp)
  + On-the-fly query encoding using ONNX (So)
+ D = BGE-base-en-v1.5
  + Pre-encoded queries (Dp)
  + On-the-fly query encoding using ONNX (Do)

The following snippet will generate the complete set of results for BEIR:

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    # "flat" indexes
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.flat -topics beir-${c} -output run.beir.${c}.flat.txt -bm25 -removeQuery
    # "multifield" indexes
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.multifield -topics beir-${c} -output run.beir.${c}.multifield.txt -bm25 -removeQuery -fields contents=1.0 title=1.0
    # SPLADE++ ED, pre-encoded queries
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c}.splade-pp-ed -output run.beir.${c}.splade-pp-ed-pre.txt -impact -pretokenized -removeQuery
    # SPLADE++ ED, ONNX
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c} -encoder SpladePlusPlusEnsembleDistil -output run.beir.${c}.splade-pp-ed-onnx.txt -impact -pretokenized -removeQuery
    # BGE-base-en-v1.5, pre-encoded queries
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5 -topics beir-${c}.bge-base-en-v1.5 -output run.beir.${c}.bge-pre.txt -threads 16 -efSearch 1000 -removeQuery
    # BGE-base-en-v1.5, ONNX
    java -cp anserini-0.24.2-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5 -topics beir-${c} -encoder BgeBaseEn15 -output run.beir.${c}.bge-onnx.txt -threads 16 -efSearch 1000 -removeQuery
done
```

Here are the expected nDCG@10 scores:

| Corpus                     |   Flat |     MF |     Sp |     So |     Dp |     Do |
|:---------------------------|-------:|-------:|-------:|-------:|-------:|-------:|
| `trec-covid`               | 0.5947 | 0.6559 | 0.7274 | 0.7270 | 0.7834 | 0.7835 |
| `bioasq`                   | 0.5225 | 0.4646 | 0.4980 | 0.4980 | 0.4042 | 0.4042 |
| `nfcorpus`                 | 0.3218 | 0.3254 | 0.3470 | 0.3473 | 0.3735 | 0.3738 |
| `nq`                       | 0.3055 | 0.3285 | 0.5378 | 0.5372 | 0.5413 | 0.5415 |
| `hotpotqa`                 | 0.6330 | 0.6027 | 0.6868 | 0.6868 | 0.7242 | 0.7241 |
| `fiqa`                     | 0.2361 | 0.2361 | 0.3475 | 0.3473 | 0.4065 | 0.4065 |
| `signal1m`                 | 0.3304 | 0.3304 | 0.3008 | 0.3006 | 0.2869 | 0.2869 |
| `trec-news`                | 0.3952 | 0.3977 | 0.4152 | 0.4169 | 0.4411 | 0.4410 |
| `robust04`                 | 0.4070 | 0.4070 | 0.4679 | 0.4651 | 0.4467 | 0.4437 |
| `arguana`                  | 0.3970 | 0.4142 | 0.5203 | 0.5218 | 0.6361 | 0.6228 |
| `webis-touche2020`         | 0.4422 | 0.3673 | 0.2468 | 0.2464 | 0.2570 | 0.2571 |
| `cqadupstack-android`      | 0.3801 | 0.3709 | 0.3904 | 0.3898 | 0.5075 | 0.5076 |
| `cqadupstack-english`      | 0.3453 | 0.3321 | 0.4079 | 0.4078 | 0.4855 | 0.4855 |
| `cqadupstack-gaming`       | 0.4822 | 0.4418 | 0.4957 | 0.4959 | 0.5965 | 0.5967 |
| `cqadupstack-gis`          | 0.2901 | 0.2904 | 0.3150 | 0.3148 | 0.4129 | 0.4133 |
| `cqadupstack-mathematica`  | 0.2015 | 0.2046 | 0.2377 | 0.2379 | 0.3163 | 0.3163 |
| `cqadupstack-physics`      | 0.3214 | 0.3248 | 0.3599 | 0.3597 | 0.4722 | 0.4724 |
| `cqadupstack-programmers`  | 0.2802 | 0.2963 | 0.3401 | 0.3399 | 0.4242 | 0.4238 |
| `cqadupstack-stats`        | 0.2711 | 0.2790 | 0.2990 | 0.2980 | 0.3731 | 0.3728 |
| `cqadupstack-tex`          | 0.2244 | 0.2086 | 0.2530 | 0.2529 | 0.3115 | 0.3115 |
| `cqadupstack-unix`         | 0.2749 | 0.2788 | 0.3167 | 0.3170 | 0.4219 | 0.4220 |
| `cqadupstack-webmasters`   | 0.3059 | 0.3008 | 0.3167 | 0.3166 | 0.4065 | 0.4072 |
| `cqadupstack-wordpress`    | 0.2483 | 0.2562 | 0.2733 | 0.2718 | 0.3547 | 0.3547 |
| `quora`                    | 0.7886 | 0.7886 | 0.8343 | 0.8344 | 0.8890 | 0.8876 |
| `dbpedia-entity`           | 0.3180 | 0.3128 | 0.4366 | 0.4374 | 0.4077 | 0.4076 |
| `scidocs`                  | 0.1490 | 0.1581 | 0.1591 | 0.1588 | 0.2170 | 0.2172 |
| `fever`                    | 0.6513 | 0.7530 | 0.7882 | 0.7879 | 0.8620 | 0.8620 |
| `climate-fever`            | 0.1651 | 0.2129 | 0.2297 | 0.2298 | 0.3119 | 0.3117 |
| `scifact`                  | 0.6789 | 0.6647 | 0.7041 | 0.7036 | 0.7408 | 0.7408 |

And here's the snippet of code to perform the evaluation (which will yield the results above):

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.beir-v1.0.0-${c}.test.txt
    echo $c
    java -cp anserini-0.24.2-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.flat.txt
    java -cp anserini-0.24.2-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.multifield.txt
    java -cp anserini-0.24.2-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.splade-pp-ed-pre.txt
    java -cp anserini-0.24.2-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.splade-pp-ed-onnx.txt
    java -cp anserini-0.24.2-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.bge-pre.txt
    java -cp anserini-0.24.2-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.bge-onnx.txt
done
```

</details>

## 🎬 Installation

Most Anserini features are exposed in the [Pyserini](http://pyserini.io/) Python interface.
If you're more comfortable with Python, start there, although Anserini forms an important building block of Pyserini, so it remains worthwhile to learn about Anserini.

You'll need Java 11 and Maven 3.3+ to build Anserini.
Clone our repo with the `--recurse-submodules` option to make sure the `eval/` submodule also gets cloned (alternatively, use `git submodule update --init`).
Then, build using Maven:

```
mvn clean package appassembler:assemble
```

The `tools/` directory, which contains evaluation tools and other scripts, is actually [this repo](https://github.com/castorini/anserini-tools), integrated as a [Git submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules) (so that it can be shared across related projects).
Build as follows (you might get warnings, but okay to ignore):

```bash
cd tools/eval && tar xvfz trec_eval.9.0.4.tar.gz && cd trec_eval.9.0.4 && make && cd ../../..
cd tools/eval/ndeval && make && cd ../../..
```

With that, you should be ready to go.
The onboarding path for Anserini starts [here](docs/start-here.md)!

<details>
<summary>Windows tips</summary>

If you are using Windows, please use WSL2 to build Anserini. 
Please refer to the [WSL2 Installation](https://learn.microsoft.com/en-us/windows/wsl/install) document to install WSL2 if you haven't already.

Note that on Windows without WSL2, tests may fail due to encoding issues, see [#1466](https://github.com/castorini/anserini/issues/1466).
A simple workaround is to skip tests by adding `-Dmaven.test.skip=true` to the above `mvn` command.
See [#1121](https://github.com/castorini/pyserini/discussions/1121) for additional discussions on debugging Windows build errors.

</details>

## ⚗️ End-to-End Regression Experiments

Anserini is designed to support end-to-end experiments on various standard IR test collections out of the box.
Each of these [_end-to-end_ regressions](docs/regressions.md) starts from the raw corpus, builds the necessary index, performs retrieval runs, and generates evaluation results.
See individual pages for details.

<details>
<summary>MS MARCO V1 Passage Regressions</summary>

### MS MARCO V1 Passage Regressions

|                                            |                                          dev                                           |                                        DL19                                         |                                        DL20                                         |
|--------------------------------------------|:--------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------:|
| **Unsupervised Sparse**                    |                                                                                        |                                                                                     |                                                                                     |
| BoW baselines                              |                  [+](docs/regressions/regressions-msmarco-passage.md)                  |                  [+](docs/regressions/regressions-dl19-passage.md)                  |                  [+](docs/regressions/regressions-dl20-passage.md)                  |
| Quantized BM25                             |              [✓](docs/regressions/regressions-msmarco-passage-bm25-b8.md)              |              [✓](docs/regressions/regressions-dl19-passage-bm25-b8.md)              |              [✓](docs/regressions/regressions-dl20-passage-bm25-b8.md)              |
| WP baselines                               |                [+](docs/regressions/regressions-msmarco-passage-wp.md)                 |                [+](docs/regressions/regressions-dl19-passage-wp.md)                 |                [+](docs/regressions/regressions-dl20-passage-wp.md)                 |
| Huggingface WP baselines                   |              [+](docs/regressions/regressions-msmarco-passage-hgf-wp.md)               |              [+](docs/regressions/regressions-dl19-passage-hgf-wp.md)               |              [+](docs/regressions/regressions-dl20-passage-hgf-wp.md)               |
| doc2query                                  |             [+](docs/regressions/regressions-msmarco-passage-doc2query.md)             |                                                                                     |                                                                                     |
| doc2query-T5                               |           [+](docs/regressions/regressions-msmarco-passage-docTTTTTquery.md)           |           [+](docs/regressions/regressions-dl19-passage-docTTTTTquery.md)           |           [+](docs/regressions/regressions-dl20-passage-docTTTTTquery.md)           |
| **Learned Sparse (uniCOIL family)**        |                                                                                        |                                                                                     |                                                                                     |
| uniCOIL noexp                              |           [✓](docs/regressions/regressions-msmarco-passage-unicoil-noexp.md)           |           [✓](docs/regressions/regressions-dl19-passage-unicoil-noexp.md)           |           [✓](docs/regressions/regressions-dl20-passage-unicoil-noexp.md)           |
| uniCOIL with doc2query-T5                  |              [✓](docs/regressions/regressions-msmarco-passage-unicoil.md)              |              [✓](docs/regressions/regressions-dl19-passage-unicoil.md)              |              [✓](docs/regressions/regressions-dl20-passage-unicoil.md)              |
| uniCOIL with TILDE                         |      [✓](docs/regressions/regressions-msmarco-passage-unicoil-tilde-expansion.md)      |                                                                                     |                                                                                     |
| **Learned Sparse (other)**                 |                                                                                        |                                                                                     |                                                                                     |
| DeepImpact                                 |            [✓](docs/regressions/regressions-msmarco-passage-deepimpact.md)             |                                                                                     |                                                                                     |
| SPLADEv2                                   |        [✓](docs/regressions/regressions-msmarco-passage-distill-splade-max.md)         |                                                                                     |                                                                                     |
| SPLADE++ CoCondenser-EnsembleDistil        |           [✓](docs/regressions/regressions-msmarco-passage-splade-pp-ed.md)            |           [✓](docs/regressions/regressions-dl19-passage-splade-pp-ed.md)            |           [✓](docs/regressions/regressions-dl20-passage-splade-pp-ed.md)            |
| SPLADE++ CoCondenser-EnsembleDistil (ONNX) |         [✓](docs/regressions/regressions-msmarco-passage-splade-pp-ed-onnx.md)         |         [✓](docs/regressions/regressions-dl19-passage-splade-pp-ed-onnx.md)         |         [✓](docs/regressions/regressions-dl20-passage-splade-pp-ed-onnx.md)         |
| SPLADE++ CoCondenser-SelfDistil            |           [✓](docs/regressions/regressions-msmarco-passage-splade-pp-sd.md)            |           [✓](docs/regressions/regressions-dl19-passage-splade-pp-sd.md)            |           [✓](docs/regressions/regressions-dl20-passage-splade-pp-sd.md)            |
| SPLADE++ CoCondenser-SelfDistil (ONNX)     |         [✓](docs/regressions/regressions-msmarco-passage-splade-pp-sd-onnx.md)         |         [✓](docs/regressions/regressions-dl19-passage-splade-pp-sd-onnx.md)         |         [✓](docs/regressions/regressions-dl20-passage-splade-pp-sd-onnx.md)         |
| **Learned Dense** (HNSW)                   |                                                                                        |                                                                                     |                                                                                     |
| cosDPR-distil w/ HNSW fp32                 |        [✓](docs/regressions/regressions-msmarco-passage-cos-dpr-distil-hnsw.md)        |        [✓](docs/regressions/regressions-dl19-passage-cos-dpr-distil-hnsw.md)        |        [✓](docs/regressions/regressions-dl20-passage-cos-dpr-distil-hnsw.md)        |
| cosDPR-distil w/ HSNW fp32 (ONNX)          |     [✓](docs/regressions/regressions-msmarco-passage-cos-dpr-distil-hnsw-onnx.md)      |     [✓](docs/regressions/regressions-dl19-passage-cos-dpr-distil-hnsw-onnx.md)      |     [✓](docs/regressions/regressions-dl20-passage-cos-dpr-distil-hnsw-onnx.md)      |
| cosDPR-distil w/ HNSW int8                 |     [✓](docs/regressions/regressions-msmarco-passage-cos-dpr-distil-hnsw-int8.md)      |     [✓](docs/regressions/regressions-dl19-passage-cos-dpr-distil-hnsw-int8.md)      |     [✓](docs/regressions/regressions-dl20-passage-cos-dpr-distil-hnsw-int8.md)      |
| cosDPR-distil w/ HSNW int8 (ONNX)          |   [✓](docs/regressions/regressions-msmarco-passage-cos-dpr-distil-hnsw-int8-onnx.md)   |   [✓](docs/regressions/regressions-dl19-passage-cos-dpr-distil-hnsw-int8-onnx.md)   |   [✓](docs/regressions/regressions-dl20-passage-cos-dpr-distil-hnsw-int8-onnx.md)   |
| BGE-base-en-v1.5 w/ HNSW fp32              |       [✓](docs/regressions/regressions-msmarco-passage-bge-base-en-v1.5-hnsw.md)       |       [✓](docs/regressions/regressions-dl19-passage-bge-base-en-v1.5-hnsw.md)       |       [✓](docs/regressions/regressions-dl20-passage-bge-base-en-v1.5-hnsw.md)       |
| BGE-base-en-v1.5 w/ HNSW fp32 (ONNX)       |    [✓](docs/regressions/regressions-msmarco-passage-bge-base-en-v1.5-hnsw-onnx.md)     |    [✓](docs/regressions/regressions-dl19-passage-bge-base-en-v1.5-hnsw-onnx.md)     |    [✓](docs/regressions/regressions-dl20-passage-bge-base-en-v1.5-hnsw-onnx.md)     |
| BGE-base-en-v1.5 w/ HNSW int8              |    [✓](docs/regressions/regressions-msmarco-passage-bge-base-en-v1.5-hnsw-int8.md)     |    [✓](docs/regressions/regressions-dl19-passage-bge-base-en-v1.5-hnsw-int8.md)     |    [✓](docs/regressions/regressions-dl20-passage-bge-base-en-v1.5-hnsw-int8.md)     |
| BGE-base-en-v1.5 w/ HNSW int8 (ONNX)       |  [✓](docs/regressions/regressions-msmarco-passage-bge-base-en-v1.5-hnsw-int8-onnx.md)  |  [✓](docs/regressions/regressions-dl19-passage-bge-base-en-v1.5-hnsw-int8-onnx.md)  |  [✓](docs/regressions/regressions-dl20-passage-bge-base-en-v1.5-hnsw-int8-onnx.md)  |
| OpenAI Ada2 w/ HNSW fp32                   |            [✓](docs/regressions/regressions-msmarco-passage-openai-ada2.md)            |            [✓](docs/regressions/regressions-dl19-passage-openai-ada2.md)            |            [✓](docs/regressions/regressions-dl20-passage-openai-ada2.md)            |
| OpenAI Ada2 w/ HNSW int8                   |         [✓](docs/regressions/regressions-msmarco-passage-openai-ada2-int8.md)          |         [✓](docs/regressions/regressions-dl19-passage-openai-ada2-int8.md)          |         [✓](docs/regressions/regressions-dl20-passage-openai-ada2-int8.md)          |
| Cohere English v3 w/ HNSW fp32             |   [✓](docs/regressions/regressions-msmarco-passage-cohere-embed-english-v3-hnsw.md)    |   [✓](docs/regressions/regressions-dl19-passage-cohere-embed-english-v3-hnsw.md)    |   [✓](docs/regressions/regressions-dl20-passage-cohere-embed-english-v3-hnsw.md)    |
| Cohere English v3 w/ HNSW int8             | [✓](docs/regressions/regressions-msmarco-passage-cohere-embed-english-v3-hnsw-int8.md) | [✓](docs/regressions/regressions-dl19-passage-cohere-embed-english-v3-hnsw-int8.md) | [✓](docs/regressions/regressions-dl20-passage-cohere-embed-english-v3-hnsw-int8.md) |
| **Learned Dense** (Inverted; experimental) |                                                                                        |                                                                                     |                                                                                     |
| cosDPR-distil w/ "fake words"              |         [✓](docs/regressions/regressions-msmarco-passage-cos-dpr-distil-fw.md)         |         [✓](docs/regressions/regressions-dl19-passage-cos-dpr-distil-fw.md)         |         [✓](docs/regressions/regressions-dl20-passage-cos-dpr-distil-fw.md)         |
| cosDPR-distil w/ "LexLSH"                  |       [✓](docs/regressions/regressions-msmarco-passage-cos-dpr-distil-lexlsh.md)       |       [✓](docs/regressions/regressions-dl19-passage-cos-dpr-distil-lexlsh.md)       |       [✓](docs/regressions/regressions-dl20-passage-cos-dpr-distil-lexlsh.md)       |

### Available Corpora for Download

| Corpora                                                                                                            |   Size | Checksum                           |
|:-------------------------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [Quantized BM25](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-bm25-b8.tar)                    | 1.2 GB | `0a623e2c97ac6b7e814bf1323a97b435` |
| [uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil-noexp.tar)             | 2.7 GB | `f17ddd8c7c00ff121c3c3b147d2e17d8` |
| [uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil.tar)                  | 3.4 GB | `78eef752c78c8691f7d61600ceed306f` |
| [uniCOIL (TILDE)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil-tilde-expansion.tar)   | 3.9 GB | `12a9c289d94e32fd63a7d39c9677d75c` |
| [DeepImpact](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-deepimpact.tar)                     | 3.6 GB | `73843885b503af3c8b3ee62e5f5a9900` |
| [SPLADEv2](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-distill-splade-max.tar)               | 9.9 GB | `b5d126f5d9a8e1b3ef3f5cb0ba651725` |
| [SPLADE++ CoCondenser-EnsembleDistil](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-splade-pp-ed.tar)  | 4.2 GB | `e489133bdc54ee1e7c62a32aa582bc77` |
| [SPLADE++ CoCondenser-SelfDistil](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-splade-pp-sd.tar)      | 4.8 GB | `cb7e264222f2bf2221dd2c9d28190be1` |
| [cosDPR-distil](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-cos-dpr-distil.tar)                      |  57 GB | `e20ffbc8b5e7f760af31298aefeaebbd` |
| [BGE-base-en-v1.5](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-bge-base-en-v1.5.tar)                 |  59 GB | `353d2c9e72e858897ad479cca4ea0db1` |
| [OpenAI-ada2](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-openai-ada2.tar)                           | 109 GB | `a4d843d522ff3a3af7edbee789a63402` |
| [Cohere embed-english-v3.0](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-cohere-embed-english-v3.tar) |  38 GB | `6b7d9795806891b227378f6c290464a9` |

</details>
<details>
<summary>MS MARCO V1 Document Regressions</summary>

### MS MARCO V1 Document Regressions

|                                                                                               |                                   dev                                    |                                 DL19                                  |                                 DL20                                  |
|-----------------------------------------------------------------------------------------------|:------------------------------------------------------------------------:|:---------------------------------------------------------------------:|:---------------------------------------------------------------------:|
| **Unsupervised Lexical, Complete Doc**[*](docs/experiments-msmarco-doc-doc2query-details.md)  |
| BoW baselines                                                                                 |             [+](docs/regressions/regressions-msmarco-doc.md)             |             [+](docs/regressions/regressions-dl19-doc.md)             |             [+](docs/regressions/regressions-dl20-doc.md)             |
| WP baselines                                                                                  |           [+](docs/regressions/regressions-msmarco-doc-wp.md)            |           [+](docs/regressions/regressions-dl19-doc-wp.md)            |           [+](docs/regressions/regressions-dl20-doc-wp.md)            |
| Huggingface WP baselines                                                                      |         [+](docs/regressions/regressions-msmarco-doc-hgf-wp.md)          |         [+](docs/regressions/regressions-dl19-doc-hgf-wp.md)          |         [+](docs/regressions/regressions-dl20-doc-hgf-wp.md)          |
| doc2query-T5                                                                                  |      [+](docs/regressions/regressions-msmarco-doc-docTTTTTquery.md)      |      [+](docs/regressions/regressions-dl19-doc-docTTTTTquery.md)      |      [+](docs/regressions/regressions-dl20-doc-docTTTTTquery.md)      |
| **Unsupervised Lexical, Segmented Doc**[*](docs/experiments-msmarco-doc-doc2query-details.md) |
| BoW baselines                                                                                 |        [+](docs/regressions/regressions-msmarco-doc-segmented.md)        |        [+](docs/regressions/regressions-dl19-doc-segmented.md)        |        [+](docs/regressions/regressions-dl20-doc-segmented.md)        |
| WP baselines                                                                                  |      [+](docs/regressions/regressions-msmarco-doc-segmented-wp.md)       |      [+](docs/regressions/regressions-dl19-doc-segmented-wp.md)       |      [+](docs/regressions/regressions-dl20-doc-segmented-wp.md)       |
| doc2query-T5                                                                                  | [+](docs/regressions/regressions-msmarco-doc-segmented-docTTTTTquery.md) | [+](docs/regressions/regressions-dl19-doc-segmented-docTTTTTquery.md) | [+](docs/regressions/regressions-dl20-doc-segmented-docTTTTTquery.md) |
| **Learned Sparse Lexical**                                                                    |
| uniCOIL noexp                                                                                 | [✓](docs/regressions/regressions-msmarco-doc-segmented-unicoil-noexp.md) | [✓](docs/regressions/regressions-dl19-doc-segmented-unicoil-noexp.md) | [✓](docs/regressions/regressions-dl20-doc-segmented-unicoil-noexp.md) |
| uniCOIL with doc2query-T5                                                                     |    [✓](docs/regressions/regressions-msmarco-doc-segmented-unicoil.md)    |    [✓](docs/regressions/regressions-dl19-doc-segmented-unicoil.md)    |    [✓](docs/regressions/regressions-dl20-doc-segmented-unicoil.md)    |

### Available Corpora for Download

| Corpora                                                                                                                                         |   Size | Checksum                           |
|:------------------------------------------------------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [MS MARCO V1 doc: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil-noexp.tar)                   |  11 GB | `11b226e1cacd9c8ae0a660fd14cdd710` |
| [MS MARCO V1 doc: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil.tar)                        |  19 GB | `6a00e2c0c375cb1e52c83ae5ac377ebb` |

</details>
<details>
<summary>MS MARCO V2 Passage Regressions</summary>

### MS MARCO V2 Passage Regressions

|                                            |                                     dev                                     |                                 DL21                                  |                                 DL22                                  |
|--------------------------------------------|:---------------------------------------------------------------------------:|:---------------------------------------------------------------------:|:---------------------------------------------------------------------:|
| **Unsupervised Lexical, Original Corpus**  |
| baselines                                  |           [+](docs/regressions/regressions-msmarco-v2-passage.md)           |           [+](docs/regressions/regressions-dl21-passage.md)           |           [+](docs/regressions/regressions-dl22-passage.md)           |
| doc2query-T5                               |       [+](docs/regressions/regressions-msmarco-v2-passage-d2q-t5.md)        |       [+](docs/regressions/regressions-dl21-passage-d2q-t5.md)        |       [+](docs/regressions/regressions-dl22-passage-d2q-t5.md)        |
| **Unsupervised Lexical, Augmented Corpus** |
| baselines                                  |      [+](docs/regressions/regressions-msmarco-v2-passage-augmented.md)      |      [+](docs/regressions/regressions-dl21-passage-augmented.md)      |      [+](docs/regressions/regressions-dl22-passage-augmented.md)      |
| doc2query-T5                               |  [+](docs/regressions/regressions-msmarco-v2-passage-augmented-d2q-t5.md)   |  [+](docs/regressions/regressions-dl21-passage-augmented-d2q-t5.md)   |  [+](docs/regressions/regressions-dl22-passage-augmented-d2q-t5.md)   |
| **Learned Sparse Lexical**                 |
| uniCOIL noexp zero-shot                    | [✓](docs/regressions/regressions-msmarco-v2-passage-unicoil-noexp-0shot.md) | [✓](docs/regressions/regressions-dl21-passage-unicoil-noexp-0shot.md) | [✓](docs/regressions/regressions-dl22-passage-unicoil-noexp-0shot.md) |
| uniCOIL with doc2query-T5 zero-shot        |    [✓](docs/regressions/regressions-msmarco-v2-passage-unicoil-0shot.md)    |    [✓](docs/regressions/regressions-dl21-passage-unicoil-0shot.md)    |    [✓](docs/regressions/regressions-dl22-passage-unicoil-0shot.md)    |
| SPLADE++ CoCondenser-EnsembleDistil        |    [✓](docs/regressions/regressions-msmarco-v2-passage-splade-pp-ed.md)     |    [✓](docs/regressions/regressions-dl21-passage-splade-pp-ed.md)     |    [✓](docs/regressions/regressions-dl22-passage-splade-pp-ed.md)     |
| SPLADE++ CoCondenser-SelfDistil            |    [✓](docs/regressions/regressions-msmarco-v2-passage-splade-pp-sd.md)     |    [✓](docs/regressions/regressions-dl21-passage-splade-pp-sd.md)     |    [✓](docs/regressions/regressions-dl22-passage-splade-pp-sd.md)     |

### Available Corpora for Download

| Corpora                                                                                                              |  Size | Checksum                           |
|:---------------------------------------------------------------------------------------------------------------------|------:|:-----------------------------------|
| [uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_noexp_0shot.tar)      | 24 GB | `d9cc1ed3049746e68a2c91bf90e5212d` |
| [uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_0shot.tar)           | 41 GB | `1949a00bfd5e1f1a230a04bbc1f01539` |
| [SPLADE++ CoCondenser-EnsembleDistil](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2_passage_splade_pp_ed.tar) | 66 GB | `2cdb2adc259b8fa6caf666b20ebdc0e8` |
| [SPLADE++ CoCondenser-SelfDistil)](https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2_passage_splade_pp_sd.tar)    | 76 GB | `061930dd615c7c807323ea7fc7957877` |

</details>
<details>
<summary>MS MARCO V2 Document Regressions</summary>

### MS MARCO V2 Document Regressions

|                                         |                                         dev                                          |                                      DL21                                      |
|-----------------------------------------|:------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------:|
| **Unsupervised Lexical, Complete Doc**  |
| baselines                               |                 [+](docs/regressions/regressions-msmarco-v2-doc.md)                  |                 [+](docs/regressions/regressions-dl21-doc.md)                  |
| doc2query-T5                            |              [+](docs/regressions/regressions-msmarco-v2-doc-d2q-t5.md)              |              [+](docs/regressions/regressions-dl21-doc-d2q-t5.md)              |
| **Unsupervised Lexical, Segmented Doc** |
| baselines                               |            [+](docs/regressions/regressions-msmarco-v2-doc-segmented.md)             |            [+](docs/regressions/regressions-dl21-doc-segmented.md)             |
| doc2query-T5                            |         [+](docs/regressions/regressions-msmarco-v2-doc-segmented-d2q-t5.md)         |         [+](docs/regressions/regressions-dl21-doc-segmented-d2q-t5.md)         |
| **Learned Sparse Lexical**              |
| uniCOIL noexp zero-shot                 | [✓](docs/regressions/regressions-msmarco-v2-doc-segmented-unicoil-noexp-0shot-v2.md) | [✓](docs/regressions/regressions-dl21-doc-segmented-unicoil-noexp-0shot-v2.md) |
| uniCOIL with doc2query-T5 zero-shot     |    [✓](docs/regressions/regressions-msmarco-v2-doc-segmented-unicoil-0shot-v2.md)    |    [✓](docs/regressions/regressions-dl21-doc-segmented-unicoil-0shot-v2.md)    |

### Available Corpora for Download

| Corpora                                                                                                                                         |   Size | Checksum                           |
|:------------------------------------------------------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [MS MARCO V2 doc: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_doc_segmented_unicoil_noexp_0shot_v2.tar)       |  55 GB | `97ba262c497164de1054f357caea0c63` |
| [MS MARCO V2 doc: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_doc_segmented_unicoil_0shot_v2.tar)            |  72 GB | `c5639748c2cbad0152e10b0ebde3b804` |

</details>
<details>
<summary>BEIR (v1.0.0) Regressions</summary>

### BEIR (v1.0.0) Regressions

Key:

+ F1 = "flat" baseline (Lucene analyzer)
+ F2 = "flat" baseline (pre-tokenized with `bert-base-uncased` tokenizer)
+ MF = "multifield" baseline (Lucene analyzer)
+ U1 = uniCOIL (noexp)
+ S1 = SPLADE++ CoCondenser-EnsembleDistil: pre-encoded queries (✓), ONNX (O)
+ D1 = BGE-base-en-v1.5
  + D1o: original HNSW indexes: pre-encoded queries (✓), ONNX (O)
  + D1q: quantized HNSW indexes: pre-encoded queries (✓), ONNX (O)

See instructions below the table for how to reproduce results for a model on all BEIR corpora "in one go".

| Corpus                  |                                      F1                                       |                                        F2                                        |                                         MF                                          |                                           U1                                           |                                                                                        S1                                                                                        |                                                                                                D1o                                                                                                 |                                                                                                     D1q                                                                                                      |
|-------------------------|:-----------------------------------------------------------------------------:|:--------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| TREC-COVID              |       [✓](docs/regressions/regressions-beir-v1.0.0-trec-covid-flat.md)        |       [✓](docs/regressions/regressions-beir-v1.0.0-trec-covid-flat-wp.md)        |       [✓](docs/regressions/regressions-beir-v1.0.0-trec-covid-multifield.md)        |       [✓](docs/regressions/regressions-beir-v1.0.0-trec-covid-unicoil-noexp.md)        |              [✓](docs/regressions/regressions-beir-v1.0.0-trec-covid-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-trec-covid-splade-pp-ed-onnx.md)              |              [✓](docs/regressions/regressions-beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-onnx.md)              |              [✓](docs/regressions/regressions-beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-int8-onnx.md)              |
| BioASQ                  |         [✓](docs/regressions/regressions-beir-v1.0.0-bioasq-flat.md)          |         [✓](docs/regressions/regressions-beir-v1.0.0-bioasq-flat-wp.md)          |         [✓](docs/regressions/regressions-beir-v1.0.0-bioasq-multifield.md)          |         [✓](docs/regressions/regressions-beir-v1.0.0-bioasq-unicoil-noexp.md)          |                  [✓](docs/regressions/regressions-beir-v1.0.0-bioasq-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-bioasq-splade-pp-ed-onnx.md)                  |                  [✓](docs/regressions/regressions-beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-onnx.md)                  |                  [✓](docs/regressions/regressions-beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-int8-onnx.md)                  |
| NFCorpus                |        [✓](docs/regressions/regressions-beir-v1.0.0-nfcorpus-flat.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-nfcorpus-flat-wp.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-nfcorpus-multifield.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-nfcorpus-unicoil-noexp.md)         |                [✓](docs/regressions/regressions-beir-v1.0.0-nfcorpus-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-nfcorpus-splade-pp-ed-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-int8-onnx.md)                |
| NQ                      |           [✓](docs/regressions/regressions-beir-v1.0.0-nq-flat.md)            |           [✓](docs/regressions/regressions-beir-v1.0.0-nq-flat-wp.md)            |           [✓](docs/regressions/regressions-beir-v1.0.0-nq-multifield.md)            |           [✓](docs/regressions/regressions-beir-v1.0.0-nq-unicoil-noexp.md)            |                      [✓](docs/regressions/regressions-beir-v1.0.0-nq-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-nq-splade-pp-ed-onnx.md)                      |                      [✓](docs/regressions/regressions-beir-v1.0.0-nq-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-onnx.md)                      |                      [✓](docs/regressions/regressions-beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-int8-onnx.md)                      |
| HotpotQA                |        [✓](docs/regressions/regressions-beir-v1.0.0-hotpotqa-flat.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-hotpotqa-flat-wp.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-hotpotqa-multifield.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-hotpotqa-unicoil-noexp.md)         |                [✓](docs/regressions/regressions-beir-v1.0.0-hotpotqa-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-hotpotqa-splade-pp-ed-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-int8-onnx.md)                |
| FiQA-2018               |          [✓](docs/regressions/regressions-beir-v1.0.0-fiqa-flat.md)           |          [✓](docs/regressions/regressions-beir-v1.0.0-fiqa-flat-wp.md)           |          [✓](docs/regressions/regressions-beir-v1.0.0-fiqa-multifield.md)           |          [✓](docs/regressions/regressions-beir-v1.0.0-fiqa-unicoil-noexp.md)           |                    [✓](docs/regressions/regressions-beir-v1.0.0-fiqa-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-fiqa-splade-pp-ed-onnx.md)                    |                    [✓](docs/regressions/regressions-beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-onnx.md)                    |                    [✓](docs/regressions/regressions-beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-int8-onnx.md)                    |
| Signal-1M(RT)           |        [✓](docs/regressions/regressions-beir-v1.0.0-signal1m-flat.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-signal1m-flat-wp.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-signal1m-multifield.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-signal1m-unicoil-noexp.md)         |                [✓](docs/regressions/regressions-beir-v1.0.0-signal1m-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-signal1m-splade-pp-ed-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-int8-onnx.md)                |
| TREC-NEWS               |        [✓](docs/regressions/regressions-beir-v1.0.0-trec-news-flat.md)        |        [✓](docs/regressions/regressions-beir-v1.0.0-trec-news-flat-wp.md)        |        [✓](docs/regressions/regressions-beir-v1.0.0-trec-news-multifield.md)        |        [✓](docs/regressions/regressions-beir-v1.0.0-trec-news-unicoil-noexp.md)        |               [✓](docs/regressions/regressions-beir-v1.0.0-trec-news-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-trec-news-splade-pp-ed-onnx.md)               |               [✓](docs/regressions/regressions-beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-onnx.md)               |               [✓](docs/regressions/regressions-beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-int8-onnx.md)               |
| Robust04                |        [✓](docs/regressions/regressions-beir-v1.0.0-robust04-flat.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-robust04-flat-wp.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-robust04-multifield.md)         |        [✓](docs/regressions/regressions-beir-v1.0.0-robust04-unicoil-noexp.md)         |                [✓](docs/regressions/regressions-beir-v1.0.0-robust04-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-robust04-splade-pp-ed-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-onnx.md)                |                [✓](docs/regressions/regressions-beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-int8-onnx.md)                |
| ArguAna                 |         [✓](docs/regressions/regressions-beir-v1.0.0-arguana-flat.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-arguana-flat-wp.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-arguana-multifield.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-arguana-unicoil-noexp.md)         |                 [✓](docs/regressions/regressions-beir-v1.0.0-arguana-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-arguana-splade-pp-ed-onnx.md)                 |                 [✓](docs/regressions/regressions-beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-onnx.md)                 |                 [✓](docs/regressions/regressions-beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-int8-onnx.md)                 |
| Touche2020              |    [✓](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-flat.md)     |    [✓](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-flat-wp.md)     |    [✓](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-multifield.md)     |    [✓](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-unicoil-noexp.md)     |        [✓](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-splade-pp-ed-onnx.md)        |        [✓](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-onnx.md)        |        [✓](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-int8-onnx.md)        |
| CQADupStack-Android     |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-flat.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-flat-wp.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-multifield.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-unicoil-noexp.md)   |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-splade-pp-ed-onnx.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-onnx.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-int8-onnx.md)     |
| CQADupStack-English     |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-flat.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-flat-wp.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-multifield.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-unicoil-noexp.md)   |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-splade-pp-ed-onnx.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-onnx.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-int8-onnx.md)     |
| CQADupStack-Gaming      |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-flat.md)    |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-flat-wp.md)    |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-multifield.md)    |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-unicoil-noexp.md)    |      [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-splade-pp-ed-onnx.md)      |      [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-onnx.md)      |      [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-int8-onnx.md)      |
| CQADupStack-Gis         |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-flat.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-flat-wp.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-multifield.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-unicoil-noexp.md)     |         [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-splade-pp-ed-onnx.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-onnx.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-int8-onnx.md)         |
| CQADupStack-Mathematica | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-flat.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-flat-wp.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-multifield.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-unicoil-noexp.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-splade-pp-ed-onnx.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-onnx.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-int8-onnx.md) |
| CQADupStack-Physics     |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-flat.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-flat-wp.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-multifield.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-unicoil-noexp.md)   |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-splade-pp-ed-onnx.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-onnx.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-int8-onnx.md)     |
| CQADupStack-Programmers | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-flat.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-flat-wp.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-multifield.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-unicoil-noexp.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-splade-pp-ed-onnx.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-onnx.md) | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-int8-onnx.md) |
| CQADupStack-Stats       |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-flat.md)    |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-flat-wp.md)    |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-multifield.md)    |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-unicoil-noexp.md)    |       [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-splade-pp-ed-onnx.md)       |       [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-onnx.md)       |       [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-int8-onnx.md)       |
| CQADupStack-Tex         |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-flat.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-flat-wp.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-multifield.md)     |     [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-unicoil-noexp.md)     |         [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-splade-pp-ed-onnx.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-onnx.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-int8-onnx.md)         |
| CQADupStack-Unix        |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-flat.md)     |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-flat-wp.md)     |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-multifield.md)     |    [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-unicoil-noexp.md)     |        [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-splade-pp-ed-onnx.md)        |        [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-onnx.md)        |        [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-int8-onnx.md)        |
| CQADupStack-Webmasters  | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-flat.md)  | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-flat-wp.md)  | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-multifield.md)  | [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-unicoil-noexp.md)  |  [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-splade-pp-ed-onnx.md)  |  [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-onnx.md)  |  [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-int8-onnx.md)  |
| CQADupStack-Wordpress   |  [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-flat.md)  |  [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-flat-wp.md)  |  [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-multifield.md)  |  [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-unicoil-noexp.md)  |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-splade-pp-ed-onnx.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-onnx.md)   |   [✓](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-int8-onnx.md)   |
| Quora                   |          [✓](docs/regressions/regressions-beir-v1.0.0-quora-flat.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-quora-flat-wp.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-quora-multifield.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-quora-unicoil-noexp.md)          |                   [✓](docs/regressions/regressions-beir-v1.0.0-quora-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-quora-splade-pp-ed-onnx.md)                   |                   [✓](docs/regressions/regressions-beir-v1.0.0-quora-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-onnx.md)                   |                   [✓](docs/regressions/regressions-beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-int8-onnx.md)                   |
| DBPedia                 |     [✓](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-flat.md)      |     [✓](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-flat-wp.md)      |     [✓](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-multifield.md)      |     [✓](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-unicoil-noexp.md)      |          [✓](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-splade-pp-ed-onnx.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-onnx.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-int8-onnx.md)          |
| SCIDOCS                 |         [✓](docs/regressions/regressions-beir-v1.0.0-scidocs-flat.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-scidocs-flat-wp.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-scidocs-multifield.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-scidocs-unicoil-noexp.md)         |                 [✓](docs/regressions/regressions-beir-v1.0.0-scidocs-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-scidocs-splade-pp-ed-onnx.md)                 |                 [✓](docs/regressions/regressions-beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-onnx.md)                 |                 [✓](docs/regressions/regressions-beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-int8-onnx.md)                 |
| FEVER                   |          [✓](docs/regressions/regressions-beir-v1.0.0-fever-flat.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-fever-flat-wp.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-fever-multifield.md)          |          [✓](docs/regressions/regressions-beir-v1.0.0-fever-unicoil-noexp.md)          |                   [✓](docs/regressions/regressions-beir-v1.0.0-fever-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-fever-splade-pp-ed-onnx.md)                   |                   [✓](docs/regressions/regressions-beir-v1.0.0-fever-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-onnx.md)                   |                   [✓](docs/regressions/regressions-beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-int8-onnx.md)                   |
| Climate-FEVER           |      [✓](docs/regressions/regressions-beir-v1.0.0-climate-fever-flat.md)      |      [✓](docs/regressions/regressions-beir-v1.0.0-climate-fever-flat-wp.md)      |      [✓](docs/regressions/regressions-beir-v1.0.0-climate-fever-multifield.md)      |      [✓](docs/regressions/regressions-beir-v1.0.0-climate-fever-unicoil-noexp.md)      |           [✓](docs/regressions/regressions-beir-v1.0.0-climate-fever-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-climate-fever-splade-pp-ed-onnx.md)           |           [✓](docs/regressions/regressions-beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-onnx.md)           |           [✓](docs/regressions/regressions-beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-int8-onnx.md)           |
| SciFact                 |         [✓](docs/regressions/regressions-beir-v1.0.0-scifact-flat.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-scifact-flat-wp.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-scifact-multifield.md)         |         [✓](docs/regressions/regressions-beir-v1.0.0-scifact-unicoil-noexp.md)         |                 [✓](docs/regressions/regressions-beir-v1.0.0-scifact-splade-pp-ed.md) [O](docs/regressions/regressions-beir-v1.0.0-scifact-splade-pp-ed-onnx.md)                 |                 [✓](docs/regressions/regressions-beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw.md) [O](docs/regressions/regressions-beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-onnx.md)                 |                 [✓](docs/regressions/regressions-beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-int8.md) [O](docs/regressions/regressions-beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-int8-onnx.md)                 |

To reproduce the SPLADE++ CoCondenser-EnsembleDistil results, start by downloading the collection:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/beir-v1.0.0-splade-pp-ed.tar -P collections/
tar xvf collections/beir-v1.0.0-splade-pp-ed.tar -C collections/
```

The tarball is 42 GB and has MD5 checksum `9c7de5b444a788c9e74c340bf833173b`.
Once you've unpacked the data, the following commands will loop over all BEIR corpora and run the regressions:

```bash
MODEL="splade-pp-ed"; CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    echo "Running $c..."
    python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-${c}-${MODEL} > logs/log.beir-v1.0.0-${c}-${MODEL} 2>&1
done
```

You can verify the results by examining the log files in `logs/`.

For the other models, modify the above commands as follows:

| Key | Corpus             | Checksum                           | `MODEL`                 |
|:----|:-------------------|:-----------------------------------|:------------------------|
| F1  | `corpus`           | `faefd5281b662c72ce03d22021e4ff6b` | `flat`                  |
| F2  | `corpus-wp`        | `3cf8f3dcdcadd49362965dd4466e6ff2` | `flat-wp`               |
| MF  | `corpus`           | `faefd5281b662c72ce03d22021e4ff6b` | `multifield`            |
| U1  | `unicoil-noexp`    | `4fd04d2af816a6637fc12922cccc8a83` | `unicoil-noexp`         |
| S1  | `splade-pp-ed`     | `9c7de5b444a788c9e74c340bf833173b` | `splade-pp-ed`          |
| D1  | `bge-base-en-v1.5` | `e4e8324ba3da3b46e715297407a24f00` | `bge-base-en-v1.5-hnsw` |

The "Corpus" above should be substituted into the full file name `beir-v1.0.0-${corpus}.tar`, e.g., `beir-v1.0.0-bge-base-en-v1.5.tar`.

</details>
<details>
<summary>Cross-lingual and Multi-lingual Regressions</summary>

### Cross-lingual and Multi-lingual Regressions

+ Regressions for Mr. TyDi (v1.1) baselines: [ar](docs/regressions/regressions-mrtydi-v1.1-ar.md), [bn](docs/regressions/regressions-mrtydi-v1.1-bn.md), [en](docs/regressions/regressions-mrtydi-v1.1-en.md), [fi](docs/regressions/regressions-mrtydi-v1.1-fi.md), [id](docs/regressions/regressions-mrtydi-v1.1-id.md), [ja](docs/regressions/regressions-mrtydi-v1.1-ja.md), [ko](docs/regressions/regressions-mrtydi-v1.1-ko.md), [ru](docs/regressions/regressions-mrtydi-v1.1-ru.md), [sw](docs/regressions/regressions-mrtydi-v1.1-sw.md), [te](docs/regressions/regressions-mrtydi-v1.1-te.md), [th](docs/regressions/regressions-mrtydi-v1.1-th.md)
+ Regressions for MIRACL (v1.0) baselines: [ar](docs/regressions/regressions-miracl-v1.0-ar.md), [bn](docs/regressions/regressions-miracl-v1.0-bn.md), [en](docs/regressions/regressions-miracl-v1.0-en.md), [es](docs/regressions/regressions-miracl-v1.0-es.md), [fa](docs/regressions/regressions-miracl-v1.0-fa.md), [fi](docs/regressions/regressions-miracl-v1.0-fi.md), [fr](docs/regressions/regressions-miracl-v1.0-fr.md), [hi](docs/regressions/regressions-miracl-v1.0-hi.md), [id](docs/regressions/regressions-miracl-v1.0-id.md), [ja](docs/regressions/regressions-miracl-v1.0-ja.md), [ko](docs/regressions/regressions-miracl-v1.0-ko.md), [ru](docs/regressions/regressions-miracl-v1.0-ru.md), [sw](docs/regressions/regressions-miracl-v1.0-sw.md), [te](docs/regressions/regressions-miracl-v1.0-te.md), [th](docs/regressions/regressions-miracl-v1.0-th.md), [zh](docs/regressions/regressions-miracl-v1.0-zh.md)
+ Regressions for TREC 2022 NeuCLIR Track BM25 (query translation): [Persian](docs/regressions/regressions-neuclir22-fa-qt.md), [Russian](docs/regressions/regressions-neuclir22-ru-qt.md), [Chinese](docs/regressions/regressions-neuclir22-zh-qt.md)
+ Regressions for TREC 2022 NeuCLIR Track BM25 (document translation): [Persian](docs/regressions/regressions-neuclir22-fa-dt.md), [Russian](docs/regressions/regressions-neuclir22-ru-dt.md), [Chinese](docs/regressions/regressions-neuclir22-zh-dt.md)
+ Regressions for TREC 2022 NeuCLIR Track SPLADE (query translation): [Persian](docs/regressions/regressions-neuclir22-fa-qt-splade.md), [Russian](docs/regressions/regressions-neuclir22-ru-qt-splade.md), [Chinese](docs/regressions/regressions-neuclir22-zh-qt-splade.md)
+ Regressions for TREC 2022 NeuCLIR Track SPLADE (document translation): [Persian](docs/regressions/regressions-neuclir22-fa-dt-splade.md), [Russian](docs/regressions/regressions-neuclir22-ru-dt-splade.md), [Chinese](docs/regressions/regressions-neuclir22-zh-dt-splade.md)
+ Regressions for HC4 (v1.0) baselines on HC4 corpora: [Persian](docs/regressions/regressions-hc4-v1.0-fa.md), [Russian](docs/regressions/regressions-hc4-v1.0-ru.md), [Chinese](docs/regressions/regressions-hc4-v1.0-zh.md)
+ Regressions for HC4 (v1.0) baselines on original NeuCLIR22 corpora: [Persian](docs/regressions/regressions-hc4-neuclir22-fa.md), [Russian](docs/regressions/regressions-hc4-neuclir22-ru.md), [Chinese](docs/regressions/regressions-hc4-neuclir22-zh.md)
+ Regressions for HC4 (v1.0) baselines on translated NeuCLIR22 corpora: [Persian](docs/regressions/regressions-hc4-neuclir22-fa-en.md), [Russian](docs/regressions/regressions-hc4-neuclir22-ru-en.md), [Chinese](docs/regressions/regressions-hc4-neuclir22-zh-en.md)
+ Regressions for [NTCIR-8 ACLIA (IR4QA subtask, Monolingual Chinese)](docs/regressions/regressions-ntcir8-zh.md)
+ Regressions for [CLEF 2006 Monolingual French](docs/regressions/regressions-clef06-fr.md)
+ Regressions for [TREC 2002 Monolingual Arabic](docs/regressions/regressions-trec02-ar.md)
+ Regressions for FIRE 2012 monolingual baselines: [Bengali](docs/regressions/regressions-fire12-bn.md), [Hindi](docs/regressions/regressions-fire12-hi.md), [English](docs/regressions/regressions-fire12-en.md)
+ Regressions for CIRAL (v1.0) BM25 (query translation): [Hausa](docs/regressions/regressions-ciral-v1.0-ha.md), [Somali](docs/regressions/regressions-ciral-v1.0-so.md), [Swahili](docs/regressions/regressions-ciral-v1.0-sw.md), [Yoruba](docs/regressions/regressions-ciral-v1.0-yo.md)
+ Regressions for CIRAL (v1.0) BM25 (document translation): [Hausa](docs/regressions/regressions-ciral-v1.0-ha-en.md), [Somali](docs/regressions/regressions-ciral-v1.0-so-en.md), [Swahili](docs/regressions/regressions-ciral-v1.0-sw-en.md), [Yoruba](docs/regressions/regressions-ciral-v1.0-yo-en.md)

</details>
<details>
<summary>Other Regressions</summary>

### Other Regressions

+ Regressions for [Disks 1 &amp; 2 (TREC 1-3)](docs/regressions/regressions-disk12.md), [Disks 4 &amp; 5 (TREC 7-8, Robust04)](docs/regressions/regressions-disk45.md), [AQUAINT (Robust05)](docs/regressions/regressions-robust05.md)
+ Regressions for [the New York Times Corpus (Core17)](docs/regressions/regressions-core17.md), [the Washington Post Corpus (Core18)](docs/regressions/regressions-core18.md)
+ Regressions for [Wt10g](docs/regressions/regressions-wt10g.md), [Gov2](docs/regressions/regressions-gov2.md)
+ Regressions for [ClueWeb09 (Category B)](docs/regressions/regressions-cw09b.md), [ClueWeb12-B13](docs/regressions/regressions-cw12b13.md), [ClueWeb12](docs/regressions/regressions-cw12.md)
+ Regressions for [Tweets2011 (MB11 &amp; MB12)](docs/regressions/regressions-mb11.md), [Tweets2013 (MB13 &amp; MB14)](docs/regressions/regressions-mb13.md)
+ Regressions for Complex Answer Retrieval (CAR17): [v1.5](docs/regressions/regressions-car17v1.5.md), [v2.0](docs/regressions/regressions-car17v2.0.md), [v2.0 with doc2query](docs/regressions/regressions-car17v2.0-doc2query.md)
+ Regressions for TREC News Tracks (Background Linking Task): [2018](docs/regressions/regressions-backgroundlinking18.md), [2019](docs/regressions/regressions-backgroundlinking19.md), [2020](docs/regressions/regressions-backgroundlinking20.md)
+ Regressions for [FEVER Fact Verification](docs/regressions/regressions-fever.md)
+ Regressions for DPR Wikipedia QA baselines: [100-word splits](docs/regressions/regressions-wikipedia-dpr-100w-bm25.md), [6/3 sliding window sentences](docs/regressions/regressions-wiki-all-6-3-tamber-bm25.md)

</details>

## 📃 Additional Documentation

The experiments described below are not associated with rigorous end-to-end regression testing and thus provide a lower standard of reproducibility.
For the most part, manual copying and pasting of commands into a shell is required to reproduce our results.

<details>
<summary>MS MARCO V1</summary>

### MS MARCO V1

+ Reproducing [BM25 baselines for MS MARCO Passage Ranking](docs/experiments-msmarco-passage.md)
+ Reproducing [BM25 baselines for MS MARCO Document Ranking](docs/experiments-msmarco-doc.md)
+ Reproducing [baselines for the MS MARCO Document Ranking Leaderboard](docs/experiments-msmarco-doc-leaderboard.md)
+ Reproducing [doc2query results](docs/experiments-doc2query.md) (MS MARCO Passage Ranking and TREC-CAR)
+ Reproducing [docTTTTTquery results](docs/experiments-docTTTTTquery.md) (MS MARCO Passage and Document Ranking)
+ Notes about reproduction issues with [MS MARCO Document Ranking w/ docTTTTTquery](docs/experiments-msmarco-doc-doc2query-details.md)

</details>
<details>
<summary>MS MARCO V2</summary>

### MS MARCO V2

+ Reproducing [BM25 baselines on the MS MARCO V2 Collections](docs/experiments-msmarco-v2.md)

</details>
<details>
<summary>TREC-COVID and CORD-19</summary>

### TREC-COVID and CORD-19

+ [Indexing AI2's COVID-19 Open Research Dataset](docs/experiments-cord19.md)
+ [Baselines for the TREC-COVID Challenge](docs/experiments-covid.md)
+ [Baselines for the TREC-COVID Challenge using doc2query](docs/experiments-covid-doc2query.md)

</details>
<details>
<summary>Other Experiments and Features</summary>

### Other Experiments and Features

+ [Working with the 20 Newsgroups Dataset](docs/experiments-20newsgroups.md)
+ [Guide to BM25 baselines for the FEVER Fact Verification Task](docs/experiments-fever.md)
+ [Guide to reproducing "Neural Hype" Experiments](docs/experiments-forum2018.md)
+ [Guide to running experiments on the AI2 Open Research Corpus](docs/experiments-openresearch.md)
+ [Experiments from Yang et al. (JDIQ 2018)](docs/experiments-jdiq2018.md)
+ Runbooks for TREC 2018: [[Anserini group](docs/runbook-trec2018-anserini.md)] [[h2oloo group](docs/runbook-trec2018-h2oloo.md)]
+ Runbook for [ECIR 2019 paper on axiomatic semantic term matching](docs/runbook-ecir2019-axiomatic.md)
+ Runbook for [ECIR 2019 paper on cross-collection relevance feedback](docs/runbook-ecir2019-ccrf.md)
+ Support for [approximate nearest-neighbor search](docs/approximate-nearestneighbor.md) on dense vectors with inverted indexes

</details>

## 🙋 How Can I Contribute?

If you've found Anserini to be helpful, we have a simple request for you to contribute back.
In the course of [reproducing](docs/reproducibility.md) baseline results on standard test collections, please let us know if you're successful by sending us a pull request with a simple note, like what appears at the bottom of [the page for Disks 4 &amp; 5](docs/regressions/regressions-disk45.md).
Reproducibility is important to us, and we'd like to know about successes as well as failures.
Since the regression documentation is auto-generated, pull requests should be sent against the [raw templates](https://github.com/castorini/anserini/tree/master/src/main/resources/docgen/templates).
Then the regression documentation can be generated using the [`bin/build.sh`](bin/build.sh) script.
In turn, you'll be recognized as a [contributor](https://github.com/castorini/anserini/graphs/contributors).

Beyond that, there are always [open issues](https://github.com/castorini/anserini/issues) we would appreciate help on!

## 📜️ Release History

+ v0.24.2: February 27, 2024 [[Release Notes](docs/release-notes/release-notes-v0.24.2.md)]
+ v0.24.1: January 27, 2024 [[Release Notes](docs/release-notes/release-notes-v0.24.1.md)]
+ v0.24.0: December 28, 2023 [[Release Notes](docs/release-notes/release-notes-v0.24.0.md)]
+ v0.23.0: November 16, 2023 [[Release Notes](docs/release-notes/release-notes-v0.23.0.md)]
+ v0.22.1: October 18, 2023 [[Release Notes](docs/release-notes/release-notes-v0.22.1.md)]
+ v0.22.0: August 28, 2023 [[Release Notes](docs/release-notes/release-notes-v0.22.0.md)]
+ v0.21.0: March 31, 2023 [[Release Notes](docs/release-notes/release-notes-v0.21.0.md)]
+ v0.20.0: January 20, 2023 [[Release Notes](docs/release-notes/release-notes-v0.20.0.md)]

<details>
<summary>older... (and historic notes)</summary>

+ v0.16.2: December 12, 2022 [[Release Notes](docs/release-notes/release-notes-v0.16.2.md)]
+ v0.16.1: November 2, 2022 [[Release Notes](docs/release-notes/release-notes-v0.16.1.md)]
+ v0.16.0: October 23, 2022 [[Release Notes](docs/release-notes/release-notes-v0.16.0.md)]
+ v0.15.0: September 22, 2022 [[Release Notes](docs/release-notes/release-notes-v0.15.0.md)]
+ v0.14.4: July 31, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.4.md)]
+ v0.14.3: May 9, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.3.md)]
+ v0.14.2: March 24, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.2.md)]
+ v0.14.1: February 27, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.1.md)]
+ v0.14.0: January 10, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.0.md)]
+ v0.13.5: November 2, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.5.md)]
+ v0.13.4: October 22, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.4.md)]
+ v0.13.3: August 22, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.3.md)]
+ v0.13.2: July 20, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.2.md)]
+ v0.13.1: June 29, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.1.md)]
+ v0.13.0: June 22, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.0.md)]
+ v0.12.0: April 29, 2021 [[Release Notes](docs/release-notes/release-notes-v0.12.0.md)]
+ v0.11.0: February 13, 2021 [[Release Notes](docs/release-notes/release-notes-v0.11.0.md)]
+ v0.10.1: January 8, 2021 [[Release Notes](docs/release-notes/release-notes-v0.10.1.md)]
+ v0.10.0: November 25, 2020 [[Release Notes](docs/release-notes/release-notes-v0.10.0.md)]
+ v0.9.4: June 25, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.4.md)]
+ v0.9.3: May 26, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.3.md)]
+ v0.9.2: May 14, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.2.md)]
+ v0.9.1: May 6, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.1.md)]
+ v0.9.0: April 18, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.0.md)]
+ v0.8.1: March 22, 2020 [[Release Notes](docs/release-notes/release-notes-v0.8.1.md)]
+ v0.8.0: March 11, 2020 [[Release Notes](docs/release-notes/release-notes-v0.8.0.md)]
+ v0.7.2: January 25, 2020 [[Release Notes](docs/release-notes/release-notes-v0.7.2.md)]
+ v0.7.1: January 9, 2020 [[Release Notes](docs/release-notes/release-notes-v0.7.1.md)]
+ v0.7.0: December 13, 2019 [[Release Notes](docs/release-notes/release-notes-v0.7.0.md)]
+ v0.6.0: September 6, 2019 [[Release Notes](docs/release-notes/release-notes-v0.6.0.md)][[Known Issues](docs/known-issues/known-issues-v0.6.0.md)]
+ v0.5.1: June 11, 2019 [[Release Notes](docs/release-notes/release-notes-v0.5.1.md)]
+ v0.5.0: June 5, 2019 [[Release Notes](docs/release-notes/release-notes-v0.5.0.md)]
+ v0.4.0: March 4, 2019 [[Release Notes](docs/release-notes/release-notes-v0.4.0.md)]
+ v0.3.0: December 16, 2018 [[Release Notes](docs/release-notes/release-notes-v0.3.0.md)]
+ v0.2.0: September 10, 2018 [[Release Notes](docs/release-notes/release-notes-v0.2.0.md)]
+ v0.1.0: July 4, 2018 [[Release Notes](docs/release-notes/release-notes-v0.1.0.md)]

## 📜️ Historical Notes

+ Anserini was upgraded to Lucene 9.3 at commit [`272565`](https://github.com/castorini/anserini/commit/27256551e958f39495b04e89ef55de9d27f33414) (8/2/2022): this upgrade created backward compatibility issues, see [#1952](https://github.com/castorini/anserini/issues/1952).
Anserini will automatically detect Lucene 8 indexes and disable consistent tie-breaking to avoid runtime errors.
However, Lucene 9 code running on Lucene 8 indexes may give slightly different results than Lucene 8 code running on Lucene 8 indexes.
Lucene 8 code will _not_ run on Lucene 9 indexes.
Pyserini has also been upgraded and similar issues apply: Lucene 9 code running on Lucene 8 indexes may give slightly different results than Lucene 8 code running on Lucene 8 indexes.
+ Anserini was upgraded to Java 11 at commit [`17b702d`](https://github.com/castorini/anserini/commit/17b702d9c3c0971e04eb8386ab83bf2fb2630714) (7/11/2019) from Java 8.
Maven 3.3+ is also required.
+ Anserini was upgraded to Lucene 8.0 as of commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019); prior to that, the toolkit uses Lucene 7.6.
Based on [preliminary experiments](docs/lucene7-vs-lucene8.md), query evaluation latency has been much improved in Lucene 8.
As a result of this upgrade, results of all regressions have changed slightly.
To reproducible old results from Lucene 7.6, use [v0.5.1](https://github.com/castorini/anserini/releases).

</details>

## ✨ References

+ Jimmy Lin, Matt Crane, Andrew Trotman, Jamie Callan, Ishan Chattopadhyaya, John Foley, Grant Ingersoll, Craig Macdonald, Sebastiano Vigna. [Toward Reproducible Baselines: The Open-Source IR Reproducibility Challenge.](https://link.springer.com/chapter/10.1007/978-3-319-30671-1_30) _ECIR 2016_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Enabling the Use of Lucene for Information Retrieval Research.](https://dl.acm.org/doi/10.1145/3077136.3080721) _SIGIR 2017_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Reproducible Ranking Baselines Using Lucene.](https://dl.acm.org/doi/10.1145/3239571) _Journal of Data and Information Quality_, 10(4), Article 16, 2018.

## 🙏 Acknowledgments

This research is supported in part by the Natural Sciences and Engineering Research Council (NSERC) of Canada.
Previous support came from the U.S. National Science Foundation under IIS-1423002 and CNS-1405688.
Any opinions, findings, and conclusions or recommendations expressed do not necessarily reflect the views of the sponsors.
