# Anserini: Fusion on the BEIR Datasets

This page documents the results of and instructions for running fusion retrieval on the BEIR datasets with three different methods in Anserini. 

Currently, Anserini provides support for the following fusion methods:

+ RRF = Reciprocal Rank Fusion
+ Average = averaging scores on a list of runs, CombSUM
+ Interpolation = Weighted sum of two runs


## Results 

For all experiments recorded here, the values k = 1000, depth = 1000, rrf_k = 60, and alpha = 0.5 were used.

The runs of two models were fused: flat BM25 and flat bge-base-en-v1.5 with ONNX.

Since there were only two runs fused, the average and interpolation methods produced the same results.

Three metrics were used for evaluation: nDCG@10, R@100, and R@1000.

The table below reports the effectiveness of the methods with the nDCG@10 metric:

| Corpus                    |    RRF | Average | Interpolation |
|---------------------------|-------:|--------:|--------------:|
| `trec-covid`              | 0.8041 |  0.6567 |     0.6567    |
| `bioasq`                  | 0.5278 |  0.5308 |     0.5308    |
| `nfcorpus`                | 0.3725 |  0.3416 |     0.3416    |
| `nq`                      | 0.4831 |  0.3241 |     0.3241    |
| `hotpotqa`                | 0.7389 |  0.6497 |     0.6497    |
| `fiqa`                    | 0.3671 |  0.2470 |     0.2470    |
| `signal1m`                | 0.3533 |  0.3463 |     0.3463    |
| `trec-news`               | 0.4855 |  0.4162 |     0.4162    |
| `robust04`                | 0.5087 |  0.4327 |     0.4327    |
| `arguana`                 | 0.5586 |  0.3986 |     0.3986    |
| `webis-touche2020`        | 0.3771 |  0.4510 |     0.4510    |
| `cqadupstack-android`     | 0.4652 |  0.3872 |     0.3872    |
| `cqadupstack-english`     | 0.4461 |  0.3601 |     0.3601    |
| `cqadupstack-gaming`      | 0.5615 |  0.4886 |     0.4886    |
| `cqadupstack-gis`         | 0.3679 |  0.2948 |     0.2948    |
| `cqadupstack-mathematica` | 0.2751 |  0.2084 |     0.2084    |
| `cqadupstack-physics`     | 0.4143 |  0.3283 |     0.3283    |
| `cqadupstack-programmers` | 0.3715 |  0.2891 |     0.2891    |
| `cqadupstack-stats`       | 0.3414 |  0.2796 |     0.2796    |
| `cqadupstack-tex`         | 0.2931 |  0.2332 |     0.2332    |
| `cqadupstack-unix`        | 0.3597 |  0.2829 |     0.2829    |
| `cqadupstack-webmasters`  | 0.3711 |  0.3130 |     0.3130    |
| `cqadupstack-wordpress`   | 0.3353 |  0.2625 |     0.2625    |
| `quora`                   | 0.8682 |  0.8009 |     0.8009    |
| `dbpedia-entity`          | 0.4190 |  0.3365 |     0.3365    |
| `scidocs`                 | 0.1948 |  0.1527 |     0.1527    |
| `fever`                   | 0.8108 |  0.6688 |     0.6688    |
| `climate-fever`           | 0.2812 |  0.1742 |     0.1742    |
| `scifact`                 | 0.7420 |  0.6806 |     0.6806    |


The table below reports the effectiveness of the methods with the R@100 metric:

| Corpus                    |    RRF | Average | Interpolation |
|---------------------------|-------:|--------:|--------------:|
| `trec-covid`              | 0.1467 |  0.1255 |     0.1255    |
| `bioasq`                  | 0.8128 |  0.7869 |     0.7869    |
| `nfcorpus`                | 0.3391 |  0.3003 |     0.3003    |
| `nq`                      | 0.9415 |  0.7922 |     0.7922    |
| `hotpotqa`                | 0.8917 |  0.8184 |     0.8184    |
| `fiqa`                    | 0.7160 |  0.5639 |     0.5639    |
| `signal1m`                | 0.4008 |  0.4077 |     0.4077    |
| `trec-news`               | 0.5545 |  0.4751 |     0.4751    |
| `robust04`                | 0.4474 |  0.3963 |     0.3963    |
| `arguana`                 | 0.9879 |  0.9331 |     0.9331    |
| `webis-touche2020`        | 0.6169 |  0.5878 |     0.5878    |
| `cqadupstack-android`     | 0.8203 |  0.7076 |     0.7076    |
| `cqadupstack-english`     | 0.7520 |  0.6022 |     0.6022    |
| `cqadupstack-gaming`      | 0.8933 |  0.7956 |     0.7956    |
| `cqadupstack-gis`         | 0.7621 |  0.6487 |     0.6487    |
| `cqadupstack-mathematica` | 0.6666 |  0.5173 |     0.5173    |
| `cqadupstack-physics`     | 0.7921 |  0.6549 |     0.6549    |
| `cqadupstack-programmers` | 0.7530 |  0.5993 |     0.5993    |
| `cqadupstack-stats`       | 0.6616 |  0.5650 |     0.5650    |
| `cqadupstack-tex`         | 0.6331 |  0.5004 |     0.5004    |
| `cqadupstack-unix`        | 0.7481 |  0.5798 |     0.5798    |
| `cqadupstack-webmasters`  | 0.7543 |  0.6127 |     0.6127    |
| `cqadupstack-wordpress`   | 0.6869 |  0.5488 |     0.5488    |
| `quora`                   | 0.9966 |  0.9793 |     0.9793    |
| `dbpedia-entity`          | 0.5985 |  0.5019 |     0.5019    |
| `scidocs`                 | 0.4751 |  0.3735 |     0.3735    |
| `fever`                   | 0.9731 |  0.9317 |     0.9317    |
| `climate-fever`           | 0.6288 |  0.4590 |     0.4590    |
| `scifact`                 | 0.9767 |  0.9327 |     0.9327    |


The table below reports the effectiveness of the methods with the R@1000 metric:

| Corpus                    |    RRF | Average | Interpolation |
|---------------------------|-------:|--------:|--------------:|
| `trec-covid`              | 0.5029 |  0.3955 |     0.3955    |
| `bioasq`                  | 0.9281 |  0.9030 |     0.9030    |
| `nfcorpus`                | 0.6540 |  0.6422 |     0.6422    |
| `nq`                      | 0.9874 |  0.8958 |     0.8958    |
| `hotpotqa`                | 0.9473 |  0.8820 |     0.8820    |
| `fiqa`                    | 0.8979 |  0.7402 |     0.7402    |
| `signal1m`                | 0.6139 |  0.5642 |     0.5642    |
| `trec-news`               | 0.8169 |  0.7051 |     0.7051    |
| `robust04`                | 0.7237 |  0.6345 |     0.6345    |
| `arguana`                 | 0.9964 |  0.9893 |     0.9893    |
| `webis-touche2020`        | 0.8912 |  0.8621 |     0.8621    |
| `cqadupstack-android`     | 0.9537 |  0.8646 |     0.8646    |
| `cqadupstack-english`     | 0.8751 |  0.7394 |     0.7394    |
| `cqadupstack-gaming`      | 0.9661 |  0.8952 |     0.8952    |
| `cqadupstack-gis`         | 0.9054 |  0.8174 |     0.8174    |
| `cqadupstack-mathematica` | 0.8781 |  0.7298 |     0.7298    |
| `cqadupstack-physics`     | 0.9337 |  0.8375 |     0.8375    |
| `cqadupstack-programmers` | 0.9272 |  0.7745 |     0.7745    |
| `cqadupstack-stats`       | 0.8363 |  0.7310 |     0.7310    |
| `cqadupstack-tex`         | 0.8430 |  0.6907 |     0.6907    |
| `cqadupstack-unix`        | 0.9097 |  0.7626 |     0.7626    |
| `cqadupstack-webmasters`  | 0.9369 |  0.8088 |     0.8088    |
| `cqadupstack-wordpress`   | 0.8761 |  0.7571 |     0.7571    |
| `quora`                   | 0.9999 |  0.9950 |     0.9950    |
| `dbpedia-entity`          | 0.8096 |  0.6773 |     0.6773    |
| `scidocs`                 | 0.7477 |  0.5652 |     0.5652    |
| `fever`                   | 0.9859 |  0.9589 |     0.9589    |
| `climate-fever`           | 0.8220 |  0.6324 |     0.6324    |
| `scifact`                 | 0.9967 |  0.9800 |     0.9800    |


## Run and Evaluate

❗ Beware, the (automatically downloaded) indexes for running these experiments take up 374 GB in total.

Let's start out by setting the `ANSERINI_JAR` and the `OUTPUT_DIR`. Note that the jar must be post v0.39.0. The following is an example from the root directory of Anserini after building.

```bash
export ANSERINI_JAR="target/anserini-0.39.1-SNAPSHOT-fatjar.jar"
export OUTPUT_DIR="./runs"
```

The following snippet will generate the complete set of results that corresponds to the above table:

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    echo $c
    # bm 25
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.flat -topics beir-${c} -output $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 -bm25 -removeQuery -hits 1000

    # bge 
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchFlatDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5.flat -topics beir-${c} -encoder BgeBaseEn15 -output $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-onnx -hits 1000 -removeQuery -threads 16
    
    # rrf fuse
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.fusion.FuseTrecRuns -runs $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-onnx -output $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt -method rrf -k 1000 -depth 1000 -rrf_k 60 -alpha 0.5

    # avg fuse
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.fusion.FuseTrecRuns -runs $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-onnx -output $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt -method average -k 1000 -depth 1000 -rrf_k 60 -alpha 0.5

    # interp fuse
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.fusion.FuseTrecRuns -runs $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-onnx -output $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt -method interpolation -k 1000 -depth 1000 -rrf_k 60 -alpha 0.5
done
```

And here's the snippet of code to perform the evaluation (which will yield the scores above):

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    echo $c
    echo rrf
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.100 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt

    echo avg
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.100 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt
    
    echo interp
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.100 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-onnx.topics.beir-v1.0.0-${c}.test.txt
done
```