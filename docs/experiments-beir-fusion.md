# Anserini: Fusion on the BEIR Datasets

This page documents the results of and instructions for running fusion retrieval on the BEIR datasets with three different methods in Anserini. 

Currently, Anserini provides support for the following fusion methods:

+ RRF = Reciprocal Rank Fusion
+ Average = averaging scores on a list of runs, CombSUM
+ Interpolation = Weighted sum of two runs


## Results 

For all experiments recorded here, the values k = 1000, depth = 1000, rrf_k = 60, and alpha = 0.5 were used.

The runs of two models were fused: flat BM25 and flat bge-base-en-v1.5 with cached queries.

Since there were only two runs fused, the average and interpolation methods produced the same results.

The table below reports the effectiveness of the methods:

| Corpus                     | rrf:nDCG@10 | rrf:R@100 | rrf:R@1000 | avg:nDCG@10 | avg:R@100 | avg:R@1000 | interp:nDCG@10 | interp:R@100 | interp:R@1000 |
|----------------------------|------------:|----------:|-----------:|------------:|----------:|-----------:|---------------:|-------------:|--------------:|
| `trec-covid`               |    0.8041   |   0.1466  |   0.5030   |    0.6567   |   0.1255  |   0.3955   |     0.6567     |    0.1255    |     0.3955    |
| `bioasq`                   |    0.5278   |   0.8128  |   0.9281   |    0.5308   |   0.7869  |   0.9030   |     0.5308     |    0.7869    |     0.9030    |
| `nfcorpus`                 |    0.3729   |   0.3392  |   0.6541   |    0.3415   |   0.3003  |   0.6423   |     0.3415     |    0.3003    |     0.6423    |
| `nq`                       |    0.4832   |   0.9412  |   0.9874   |    0.3242   |   0.7922  |   0.8958   |     0.3242     |    0.7922    |     0.8958    |
| `hotpotqa`                 |    0.7389   |   0.8918  |   0.9472   |    0.6497   |   0.8184  |   0.8820   |     0.6497     |    0.8184    |     0.8820    |
| `fiqa`                     |    0.3671   |   0.7153  |   0.8979   |    0.2470   |   0.5639  |   0.7402   |     0.2470     |    0.5639    |     0.7402    |
| `signal1m`                 |    0.3527   |   0.4008  |   0.6139   |    0.3466   |   0.4077  |   0.5642   |     0.3466     |    0.4077    |     0.5642    |
| `trec-news`                |    0.4864   |   0.5545  |   0.8169   |    0.4162   |   0.4751  |   0.7051   |     0.4162     |    0.4751    |     0.7051    |
| `robust04`                 |    0.5087   |   0.4474  |   0.7237   |    0.4327   |   0.3963  |   0.6345   |     0.4327     |    0.3963    |     0.6345    |
| `arguana`                  |    0.5659   |   0.9865  |   0.9964   |    0.3986   |   0.9331  |   0.9879   |     0.3986     |    0.9331    |     0.9879    |
| `webis-touche2020`         |    0.3759   |   0.6169  |   0.8912   |    0.4519   |   0.5878  |   0.8621   |     0.4519     |    0.5878    |     0.8621    |
| `cqadupstack-android`      |    0.4652   |   0.8218  |   0.9537   |    0.3872   |   0.7076  |   0.8646   |     0.3872     |    0.7076    |     0.8646    |
| `cqadupstack-english`      |    0.4460   |   0.7520  |   0.8751   |    0.3601   |   0.6022  |   0.7394   |     0.3601     |    0.6022    |     0.7394    |
| `cqadupstack-gaming`       |    0.5613   |   0.8931  |   0.9661   |    0.4886   |   0.7956  |   0.8952   |     0.4886     |    0.7956    |     0.8952    |
| `cqadupstack-gis`          |    0.3679   |   0.7621  |   0.9054   |    0.2948   |   0.6487  |   0.8174   |     0.2948     |    0.6487    |     0.8174    |
| `cqadupstack-mathematica`  |    0.2747   |   0.6666  |   0.8781   |    0.2084   |   0.5173  |   0.7298   |     0.2084     |    0.5173    |     0.7298    |
| `cqadupstack-physics`      |    0.4143   |   0.7922  |   0.9327   |    0.3285   |   0.6549  |   0.8375   |     0.3285     |    0.6549    |     0.8375    |
| `cqadupstack-programmers`  |    0.3718   |   0.7530  |   0.9272   |    0.2891   |   0.5993  |   0.7745   |     0.2891     |    0.5993    |     0.7745    |
| `cqadupstack-stats`        |    0.3414   |   0.6624  |   0.8370   |    0.2796   |   0.5650  |   0.7310   |     0.2796     |    0.5650    |     0.7310    |
| `cqadupstack-tex`          |    0.2931   |   0.6331  |   0.8430   |    0.2332   |   0.5004  |   0.6907   |     0.2332     |    0.5004    |     0.6907    |
| `cqadupstack-unix`         |    0.3601   |   0.7482  |   0.9093   |    0.2829   |   0.5798  |   0.7626   |     0.2829     |    0.5798    |     0.7626    |
| `cqadupstack-webmasters`   |    0.3710   |   0.7534  |   0.9369   |    0.3130   |   0.6127  |   0.8088   |     0.3130     |    0.6127    |     0.8088    |
| `cqadupstack-wordpress`    |    0.3353   |   0.6869  |   0.8755   |    0.2625   |   0.5488  |   0.7571   |     0.2625     |    0.5488    |     0.7571    |
| `quora`                    |    0.8714   |   0.9966  |   0.9999   |    0.8019   |   0.9801  |   0.9950   |     0.8019     |    0.9801    |     0.9950    |
| `dbpedia-entity`           |    0.4190   |   0.5986  |   0.8095   |    0.3365   |   0.5019  |   0.6773   |     0.3365     |    0.5019    |     0.6773    |
| `scidocs`                  |    0.1948   |   0.4751  |   0.7477   |    0.1527   |   0.3735  |   0.5652   |     0.1527     |    0.3735    |     0.5652    |
| `fever`                    |    0.8108   |   0.9731  |   0.9859   |    0.6688   |   0.9317  |   0.9589   |     0.6688     |    0.9317    |     0.9589    |
| `climate-fever`            |    0.2812   |   0.6287  |   0.8218   |    0.1741   |   0.4590  |   0.6324   |     0.1741     |    0.4590    |     0.6324    |
| `scifact`                  |    0.7417   |   0.9767  |   0.9967   |    0.6806   |   0.9327  |   0.9800   |     0.6806     |    0.9327    |     0.9800    |

## Run and Evaluate

❗ Beware, the (automatically downloaded) indexes for running these experiments take up 374 GB in total.

The following snippet will generate the complete set of results that corresponds to the above table:

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    echo $c
    # bm 25
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.flat -topics beir-${c} -output $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 -bm25 -removeQuery -hits 1000

    # bge 
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchFlatDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5.flat -topics beir-${c}.bge-base-en-v1.5 -output $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-cached -hits 1000 -removeQuery -threads 16
    
    # rrf fuse
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.fusion.FuseTrecRuns -runs $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-cached -output $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt -method rrf -k 1000 -depth 1000 -rrf_k 60 -alpha 0.5

    # avg fuse
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.fusion.FuseTrecRuns -runs $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-cached -output $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt -method average -k 1000 -depth 1000 -rrf_k 60 -alpha 0.5

    # interp fuse
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.fusion.FuseTrecRuns -runs $OUTPUT_DIR/run.inverted.beir-v1.0.0-${c}.flat.test.bm25 $OUTPUT_DIR/run.flat.beir-v1.0.0-${c}.bge-base-en-v1.5.test.bge-flat-cached -output $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt -method interpolation -k 1000 -depth 1000 -rrf_k 60 -alpha 0.5
done
```

And here's the snippet of code to perform the evaluation (which will yield the scores above):

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    echo $c
    echo rrf
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.100 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.rrf.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt

    echo avg
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.100 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.avg.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt
    
    echo interp
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.100 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt

    java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/runs.fuse.interp.beir-v1.0.0-${c}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-${c}.test.txt
done
```