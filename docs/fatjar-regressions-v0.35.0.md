# Fatjar Regresions (v0.35.0)

Fetch the fatjar:

```bash
wget https://repo1.maven.org/maven2/io/anserini/anserini/0.35.0/anserini-0.35.0-fatjar.jar
```

## MS MARCO V1 Passage

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
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage -topics ${t} -output run.${t}.bm25.txt -threads 16 -bm25
done

# SPLADE++ ED
TOPICS=(msmarco-v1-passage-dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using pre-encoded queries
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage-splade-pp-ed -topics ${t}-splade-pp-ed -output run.${t}.splade-pp-ed-pre.txt -threads 16 -impact -pretokenized
    # Using ONNX
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage-splade-pp-ed -topics ${t} -encoder SpladePlusPlusEnsembleDistil -output run.${t}.splade-pp-ed-onnx.txt -threads 16 -impact -pretokenized
done

# cosDPR-distil
TOPICS=(msmarco-v1-passage-dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using pre-encoded queries, full index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil -topics ${t}-cos-dpr-distil -output run.${t}.cos-dpr-distil-full-pre.txt -threads 16 -efSearch 1000
    # Using pre-encoded queries, quantized index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil-quantized -topics ${t}-cos-dpr-distil -output run.${t}.cos-dpr-distil-quantized-pre.txt -threads 16 -efSearch 1000
    # Using ONNX, full index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil -topics ${t} -encoder CosDprDistil -output run.${t}.cos-dpr-distil-full-onnx.txt -threads 16 -efSearch 1000
    # Using ONNX, quantized index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-cos-dpr-distil-quantized -topics ${t} -encoder CosDprDistil -output run.${t}.cos-dpr-distil-quantized-onnx.txt -threads 16 -efSearch 1000
done

# BGE-base-en-v1.5
TOPICS=(msmarco-v1-passage-dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using pre-encoded queries, full index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5 -topics ${t}-bge-base-en-v1.5 -output run.${t}.bge-base-en-v1.5-full-pre.txt -threads 16 -efSearch 1000
    # Using pre-encoded queries, quantized index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5-quantized -topics ${t}-bge-base-en-v1.5 -output run.${t}.bge-base-en-v1.5-quantized-pre.txt -threads 16 -efSearch 1000
    # Using ONNX, full index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5 -topics ${t} -encoder BgeBaseEn15 -output run.${t}.bge-base-en-v1.5-full-onnx.txt -threads 16 -efSearch 1000
    # Using ONNX, quantized index
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage-bge-base-en-v1.5-quantized -topics ${t} -encoder BgeBaseEn15 -output run.${t}.bge-base-en-v1.5-quantized-onnx.txt -threads 16 -efSearch 1000
done
```

Here are the expected scores (dev using MRR@10, DL19 and DL20 using nDCG@10):

|                                                |    dev |   DL19 |   DL20 |
|:-----------------------------------------------|-------:|-------:|-------:|
| BM25                                           | 0.1840 | 0.5058 | 0.4796 |
| SPLADE++ ED (pre-encoded)                      | 0.3830 | 0.7317 | 0.7198 |
| SPLADE++ ED (ONNX)                             | 0.3828 | 0.7308 | 0.7197 |
| cos-DPR: full HNSW (pre-encoded)               | 0.3887 | 0.7250 | 0.7025 |
| cos-DPR: quantized HNSW (pre-encoded)          | 0.3897 | 0.7240 | 0.7004 |
| cos-DPR: full HNSW (ONNX)                      | 0.3887 | 0.7250 | 0.7025 |
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

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bm25.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bm25.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bm25.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.splade-pp-ed-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.splade-pp-ed-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.splade-pp-ed-pre.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.splade-pp-ed-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.splade-pp-ed-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.splade-pp-ed-onnx.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-full-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-full-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-full-pre.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-quantized-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-quantized-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-quantized-pre.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-full-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-full-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-full-onnx.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.cos-dpr-distil-quantized-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil-quantized-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil-quantized-onnx.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-full-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-full-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-full-pre.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-quantized-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-quantized-pre.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-quantized-pre.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-full-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-full-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-full-onnx.txt

echo ''

java -cp anserini-0.35.0-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage-dev.bge-base-en-v1.5-quantized-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5-quantized-onnx.txt
java -cp anserini-0.35.0-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5-quantized-onnx.txt
```


## BEIR

Currently, Anserini provides support for the following models:

+ Flat = BM25, "flat" bag-of-words baseline
+ MF = BM25, "multifield" bag-of-words baseline
+ S = SPLADE++ EnsembleDistil:
    + Pre-encoded queries (Sp)
    + ONNX query encoding (So)
+ D = BGE-base-en-v1.5
    + Pre-encoded queries (Dp)
    + ONNX query encoding (Do)

The following snippet will generate the complete set of results for BEIR:

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    # "flat" indexes
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.flat -topics beir-${c} -output run.beir.${c}.flat.txt -bm25 -removeQuery
    # "multifield" indexes
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.multifield -topics beir-${c} -output run.beir.${c}.multifield.txt -bm25 -removeQuery -fields contents=1.0 title=1.0
    # SPLADE++ ED, pre-encoded queries
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c}.splade-pp-ed -output run.beir.${c}.splade-pp-ed-pre.txt -impact -pretokenized -removeQuery
    # SPLADE++ ED, ONNX
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c} -encoder SpladePlusPlusEnsembleDistil -output run.beir.${c}.splade-pp-ed-onnx.txt -impact -pretokenized -removeQuery
    # BGE-base-en-v1.5, pre-encoded queries
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5 -topics beir-${c}.bge-base-en-v1.5 -output run.beir.${c}.bge-pre.txt -threads 16 -efSearch 1000 -removeQuery
    # BGE-base-en-v1.5, ONNX
    java -cp anserini-0.35.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5 -topics beir-${c} -encoder BgeBaseEn15 -output run.beir.${c}.bge-onnx.txt -threads 16 -efSearch 1000 -removeQuery
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
    java -cp anserini-0.35.0-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.flat.txt
    java -cp anserini-0.35.0-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.multifield.txt
    java -cp anserini-0.35.0-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.splade-pp-ed-pre.txt
    java -cp anserini-0.35.0-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.splade-pp-ed-onnx.txt
    java -cp anserini-0.35.0-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.bge-pre.txt
    java -cp anserini-0.35.0-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.bge-onnx.txt
done
```

