# Anserini Fatjar Regresions (v0.38.0)

Fetch the fatjar:

```bash
wget https://repo1.maven.org/maven2/io/anserini/anserini/0.38.0/anserini-0.38.0-fatjar.jar
```

Note that prebuilt indexes will be downloaded to `~/.cache/pyserini/indexes/`.
Currently, this path is hard-coded (see [Anserini #2322](https://github.com/castorini/anserini/issues/2322)).
If you want to change the download location, the current workaround is to use symlinks, i.e., symlink `~/.cache/pyserini/indexes/` to the actual path you desire.

Let's start out by setting the `ANSERINI_JAR` and the `OUTPUT_DIR`:

```bash
export ANSERINI_JAR="anserini-0.38.0-fatjar.jar"
export OUTPUT_DIR="."
```

## Webapp and REST API

Anserini has a built-in webapp for interactive querying along with a REST API that can be used by other applications.
To start the REST API:

```bash
java -cp $ANSERINI_JAR io.anserini.server.Application --server.port=8081
```

And then navigate to [`http://localhost:8081/`](http://localhost:8081/) in your browser.

Here's a specific example of using the REST API to issue the query "How does the process of digestion and metabolism of carbohydrates start" to `msmarco-v2.1-doc`:

```bash
curl -X GET "http://localhost:8081/api/v1.0/indexes/msmarco-v2.1-doc/search?query=How%20does%20the%20process%20of%20digestion%20and%20metabolism%20of%20carbohydrates%20start"
```

The json results are the same as the output of the `-outputRerankerRequests` option in `SearchCollection`, described below for TREC 2024 RAG.
Use the `hits` parameter to specify the number of hits to return, e.g., `hits=1000` to return the top 1000 hits.
Switch to `msmarco-v2.1-doc-segmented` in the route to query the segmented docs instead.

Details of the built-in webapp and REST API can be found [here](../rest-api.md).

## TREC 2024 RAG

For the [TREC 2024 RAG Track](https://trec-rag.github.io/), we have thus far only implemented BM25 baselines on the MS MARCO V2.1 document corpus (both the doc and doc segmented variants).

❗ Beware, you need lots of space to run these experiments.
The `msmarco-v2.1-doc` prebuilt index is 63 GB uncompressed.
The `msmarco-v2.1-doc-segmented` prebuilt index is 84 GB uncompressed.
Both indexes will be downloaded automatically.

This release of Anserini comes with bindings for the test topics for the TREC 2024 RAG track (`-topics rag24.test`).
To generate jsonl output containing the raw documents that can be reranked and further processed, use the `-outputRerankerRequests` option to specify an output file.
For example:

```bash
java -cp $ANSERINI_JAR io.anserini.search.SearchCollection \
  -index msmarco-v2.1-doc \
  -topics rag24.test \
  -output $OUTPUT_DIR/run.msmarco-v2.1-doc.bm25.rag24.test.txt \
  -bm25 -hits 20 \
  -outputRerankerRequests $OUTPUT_DIR/results.msmarco-v2.1-doc.bm25.rag24.test.jsonl
```

And the output looks something like (pipe through `jq` to pretty-print):

```bash
$ head -n 1 $OUTPUT_DIR/results.msmarco-v2.1-doc.bm25.rag24.test.jsonl | jq
{
  "query": {
    "qid": "2024-105741",
    "text": "is it dangerous to have wbc over 15,000 without treatment?"
  },
  "candidates": [
    {
      "docid": "msmarco_v2.1_doc_38_1524878562",
      "score": 14.4877,
      "doc": {
        "url": "https://www.ebmconsult.com/articles/lab-test-white-blood-count-wbc",
        "title": "Lab Test: White Blood Cell Count, WBC",
        "headings": "...",
        "body": "..."
      }
    },
    {
      "docid": "msmarco_v2.1_doc_19_1675146822",
      "score": 14.3835,
      "doc": {
        "url": "https://fcer.org/white-blood-cells/",
        "title": "White Blood Cells (WBCs) - Definition, Function, and Ranges",
        "headings": "...",
        "body": "..."
      }
    },
    ...
  ]
}
```

Replace `-index msmarco-v2.1-doc` with `-index msmarco-v2.1-doc-segemented` if you want to search over the doc segments instead of the full docs.

Since the TREC 2024 RAG evaluation hasn't happened yet, there are no qrels for evaluation.
However, we _do_ have results based existing qrels that have been "projected" over from MS MARCO V2.0 passage judgments.
The table below reports effectiveness (dev in terms of RR@10, DL21-DL23, RAGgy in terms of nDCG@10):

|                                                                            |    dev |   dev2 |   DL21 |   DL22 |   DL23 |  RAGgy |
|:---------------------------------------------------------------------------|-------:|-------:|-------:|-------:|-------:|-------:|
| BM25 doc (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4)           | 0.1654 | 0.1732 | 0.5183 | 0.2991 | 0.2914 | 0.3631 |
| BM25 doc-segmented (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.1973 | 0.2000 | 0.5778 | 0.3576 | 0.3356 | 0.4227 |

The follow command will reproduce the above experiments:

```bash
java -cp $ANSERINI_JAR io.anserini.reproduce.RunMsMarco -collection msmarco-v2.1
```

<details>
<summary>Manual runs and evaluation</summary>

The following snippet will generate the complete set of results that corresponds to the above table:

```bash
# doc condition
TOPICS=(msmarco-v2-doc.dev msmarco-v2-doc.dev2 dl21-doc dl22-doc dl23-doc rag24.raggy-dev); for t in "${TOPICS[@]}"
do
    java -cp $ANSERINI_JAR io.anserini.search.SearchCollection -index msmarco-v2.1-doc -topics $t -output $OUTPUT_DIR/run.msmarco-v2.1.doc.${t}.txt -threads 16 -bm25
done

# doc-segmented condition
TOPICS=(msmarco-v2-doc.dev msmarco-v2-doc.dev2 dl21-doc dl22-doc dl23-doc rag24.raggy-dev); for t in "${TOPICS[@]}"
do
    java -cp $ANSERINI_JAR io.anserini.search.SearchCollection -index msmarco-v2.1-doc-segmented -topics $t -output $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.${t}.txt -threads 16 -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000
done
```

And here's the snippet of code to perform the evaluation (which will yield the results above):

```bash
# doc condition
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev $OUTPUT_DIR/run.msmarco-v2.1.doc.msmarco-v2-doc.dev.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev2 $OUTPUT_DIR/run.msmarco-v2.1.doc.msmarco-v2-doc.dev2.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl21-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl21-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl21-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl21-doc.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl22-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl22-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl22-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl22-doc.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl23-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl23-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl23-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc.dl23-doc.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc.rag24.raggy-dev.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc.rag24.raggy-dev.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc.rag24.raggy-dev.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc.rag24.raggy-dev.txt

# doc-segmented condition
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.msmarco-v2-doc.dev.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank msmarco-v2.1-doc.dev2 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.msmarco-v2-doc.dev2.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl21-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl21-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl21-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 dl21-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl21-doc.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl22-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl22-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl22-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 dl22-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl22-doc.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl23-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl23-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl23-doc.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 dl23-doc-msmarco-v2.1 $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.dl23-doc.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m map rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.rag24.raggy-dev.txt
java -cp $ANSERINI_JAR trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.rag24.raggy-dev.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.100 rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.rag24.raggy-dev.txt
java -cp $ANSERINI_JAR trec_eval -c -m recall.1000 rag24.raggy-dev $OUTPUT_DIR/run.msmarco-v2.1.doc-segmented.rag24.raggy-dev.txt
```

And these are the complete set of expected scores:

```
# doc condition
recip_rank            	all	0.1654
recip_rank            	all	0.1732

map                   	all	0.2281
recip_rank            	all	0.8466
ndcg_cut_10           	all	0.5183
recall_100            	all	0.3502
recall_1000           	all	0.6915

map                   	all	0.0841
recip_rank            	all	0.6623
ndcg_cut_10           	all	0.2991
recall_100            	all	0.1866
recall_1000           	all	0.4254

map                   	all	0.1089
recip_rank            	all	0.5783
ndcg_cut_10           	all	0.2914
recall_100            	all	0.2604
recall_1000           	all	0.5383

map                   	all	0.1251
recip_rank            	all	0.7060
ndcg_cut_10           	all	0.3631
recall_100            	all	0.2433
recall_1000           	all	0.5317

# doc-segmented condition
recip_rank            	all	0.1973
recip_rank            	all	0.2000

map                   	all	0.2609
recip_rank            	all	0.9026
ndcg_cut_10           	all	0.5778
recall_100            	all	0.3811
recall_1000           	all	0.7115

map                   	all	0.1079
recip_rank            	all	0.7213
ndcg_cut_10           	all	0.3576
recall_100            	all	0.2330
recall_1000           	all	0.4790

map                   	all	0.1391
recip_rank            	all	0.6519
ndcg_cut_10           	all	0.3356
recall_100            	all	0.3049
recall_1000           	all	0.5852

map                   	all	0.1561
recip_rank            	all	0.7465
ndcg_cut_10           	all	0.4227
recall_100            	all	0.2807
recall_1000           	all	0.5745
```

</details>


## MS MARCO V1 Passage

❗ Beware, the (automatically downloaded) indexes for running these experiments take up 200 GB in total.

Currently, Anserini provides support for the following models:

+ BM25
+ SPLADE++ EnsembleDistil: cached queries and ONNX query encoding
+ cosDPR-distil: cached queries and ONNX query encoding
+ bge-base-en-v1.5: cached queries and ONNX query encoding
+ cohere-embed-english-v3.0: cached queries and ONNX query encoding

The table below reports the effectiveness of the models (dev in terms of RR@10, DL19 and DL20 in terms of nDCG@10):

|                                                              |    dev |   DL19 |   DL20 |
|:-------------------------------------------------------------|-------:|-------:|-------:|
| BM25 (<i>k<sub><small>1</small></sub></i>=0.9, <i>b</i>=0.4) | 0.1840 | 0.5058 | 0.4796 |
| SPLADE++ EnsembleDistil (cached queries)                     | 0.3830 | 0.7317 | 0.7198 |
| SPLADE++ EnsembleDistil (ONNX)                               | 0.3828 | 0.7308 | 0.7197 |
| cosDPR-distil w/ HNSW fp32 (cached queries)                  | 0.3887 | 0.7250 | 0.7025 |
| cosDPR-distil w/ HNSW fp32 (ONNX)                            | 0.3887 | 0.7250 | 0.7025 |
| cosDPR-distil w/ HNSW int8 (cached queries)                  | 0.3897 | 0.7240 | 0.7004 |
| cosDPR-distil w/ HNSW int8 (ONNX)                            | 0.3899 | 0.7247 | 0.6996 |
| bge-base-en-v1.5 w/ HNSW fp32 (cached queries)               | 0.3574 | 0.7065 | 0.6780 |
| bge-base-en-v1.5 w/ HNSW fp32 (ONNX)                         | 0.3575 | 0.7016 | 0.6768 |
| bge-base-en-v1.5 w/ HNSW int8 (cached queries)               | 0.3572 | 0.7016 | 0.6738 |
| bge-base-en-v1.5 w/ HNSW int8 (ONNX)                         | 0.3575 | 0.7017 | 0.6767 |
| cohere-embed-english-v3.0 w/ HNSW fp32 (cached queries)      | 0.3647 | 0.6956 | 0.7245 |
| cohere-embed-english-v3.0 w/ HNSW int8 (cached queries)      | 0.3656 | 0.6955 | 0.7262 |

The follow command will reproduce the above experiments:

```bash
java -cp $ANSERINI_JAR io.anserini.reproduce.RunMsMarco -collection msmarco-v1-passage
```

<details>
<summary>Manual runs and evaluation</summary>

The following snippet will generate the complete set of results that corresponds to the above table:

```bash
# BM25
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    java -cp $ANSERINI_JAR io.anserini.search.SearchCollection -index msmarco-v1-passage -topics ${t} -output $OUTPUT_DIR/run.msmarco-v1-passage.bm25.${t}.txt -threads 16 -bm25
done

# SPLADE++ ED
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using cached queries
    java -cp $ANSERINI_JAR io.anserini.search.SearchCollection -index msmarco-v1-passage.splade-pp-ed -topics ${t}.splade-pp-ed -output $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.cached_q.${t}.splade-pp-ed.txt -threads 16 -impact -pretokenized
    # Using ONNX
    java -cp $ANSERINI_JAR io.anserini.search.SearchCollection -index msmarco-v1-passage.splade-pp-ed -topics ${t} -encoder SpladePlusPlusEnsembleDistil -output $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.onnx.${t}.txt -threads 16 -impact -pretokenized
done

# cosDPR-distil
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using fp32 index, cached queries
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil -topics ${t}.cos-dpr-distil -output $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.cached_q.${t}.cos-dpr-distil.txt -threads 16 -efSearch 1000
    # Using fp32 index, ONNX
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil -topics ${t} -encoder CosDprDistil -output $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.onnx.${t}.txt -threads 16 -efSearch 1000
    # Using int8 index, cached queries
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil.quantized -topics ${t}.cos-dpr-distil -output $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.cached_q.${t}.cos-dpr-distil.txt -threads 16 -efSearch 1000
    # Using int8 index, ONNX
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil.quantized -topics ${t} -encoder CosDprDistil -output $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.onnx.${t}.txt -threads 16 -efSearch 1000
done

# bge-base-en-v1.5
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using fp32 index, cached queries
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5 -topics ${t}.bge-base-en-v1.5 -output $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.cached_q.${t}.bge-base-en-v1.5.txt -threads 16 -efSearch 1000
    # Using fp32 index, ONNX
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5 -topics ${t} -encoder BgeBaseEn15 -output $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.onnx.${t}.txt -threads 16 -efSearch 1000
    # Using int8 index, cached queries
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5.quantized -topics ${t}.bge-base-en-v1.5 -output $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.cached_q.${t}.bge-base-en-v1.5.txt -threads 16 -efSearch 1000
    # Using int8 index, ONNX
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5.quantized -topics ${t} -encoder BgeBaseEn15 -output $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.onnx.${t}.txt -threads 16 -efSearch 1000
done

# cohere-embed-english-v3.0
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using fp32 index, cached queries
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cohere-embed-english-v3.0 -topics ${t}.cohere-embed-english-v3.0 -output $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.fp32.cached_q.${t}.cohere-embed-english-v3.0.txt -threads 16 -efSearch 1000
    # Using int8 index, cached queries
    java -cp $ANSERINI_JAR io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cohere-embed-english-v3.0.quantized -topics ${t}.cohere-embed-english-v3.0 -output $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.int8.cached_q.${t}.cohere-embed-english-v3.0.txt -threads 16 -efSearch 1000
done
```

And here's the snippet of code to perform the evaluation (which will yield the scores above):

```bash
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.bm25.msmarco-v1-passage.dev.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.bm25.dl19-passage.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.bm25.dl20-passage.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.cached_q.msmarco-v1-passage.dev.splade-pp-ed.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.cached_q.dl19-passage.splade-pp-ed.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.cached_q.dl20-passage.splade-pp-ed.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.onnx.msmarco-v1-passage.dev.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.onnx.dl19-passage.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.splade-pp-ed.onnx.dl20-passage.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.cached_q.msmarco-v1-passage.dev.cos-dpr-distil.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.cached_q.dl19-passage.cos-dpr-distil.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.cached_q.dl20-passage.cos-dpr-distil.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.onnx.msmarco-v1-passage.dev.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.onnx.dl19-passage.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.fp32.onnx.dl20-passage.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.cached_q.msmarco-v1-passage.dev.cos-dpr-distil.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.cached_q.dl19-passage.cos-dpr-distil.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.cached_q.dl20-passage.cos-dpr-distil.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.onnx.msmarco-v1-passage.dev.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.onnx.dl19-passage.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.cos-dpr-distil.int8.onnx.dl20-passage.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.cached_q.msmarco-v1-passage.dev.bge-base-en-v1.5.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.cached_q.dl19-passage.bge-base-en-v1.5.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.cached_q.dl20-passage.bge-base-en-v1.5.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.onnx.msmarco-v1-passage.dev.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.onnx.dl19-passage.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.fp32.onnx.dl20-passage.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.cached_q.msmarco-v1-passage.dev.bge-base-en-v1.5.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.cached_q.dl19-passage.bge-base-en-v1.5.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.cached_q.dl20-passage.bge-base-en-v1.5.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.onnx.msmarco-v1-passage.dev.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.onnx.dl19-passage.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.bge-base-en-v1.5.int8.onnx.dl20-passage.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.fp32.cached_q.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.fp32.cached_q.dl19-passage.cohere-embed-english-v3.0.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.fp32.cached_q.dl20-passage.cohere-embed-english-v3.0.txt
echo ''
java -cp $ANSERINI_JAR trec_eval -c -M 10 -m recip_rank msmarco-passage.dev $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.int8.cached_q.msmarco-v1-passage.dev.cohere-embed-english-v3.0.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl19-passage $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.int8.cached_q.dl19-passage.cohere-embed-english-v3.0.txt
java -cp $ANSERINI_JAR trec_eval -m ndcg_cut.10 -c dl20-passage $OUTPUT_DIR/run.msmarco-v1-passage.cohere-embed-english-v3.0.int8.cached_q.dl20-passage.cohere-embed-english-v3.0.txt
```

</details>

## BEIR

❗ Beware, the (automatically downloaded) indexes for running these experiments take up 374 GB in total.

Currently, Anserini provides support for the following models:

+ Flat = BM25, "flat" bag-of-words baseline
+ MF = BM25, "multifield" bag-of-words baseline
+ S = SPLADE++ EnsembleDistil:
  + cached queries (Sp)
  + ONNX query encoding (So)
+ Bf = bge-base-en-v1.5 (flat)
  + cached queries (Bfc)
  + ONNX query encoding (Bfo)
+ Bh = bge-base-en-v1.5 (HNSW)
  + cached queries (Bhc)
  + ONNX query encoding (Bhc)

The table below reports the effectiveness of the models (nDCG@10):

| Corpus                    |   Flat |     MF |     Sp |     So |    Bfc |    Bfo |    Bhc |    Bho |
|:--------------------------|-------:|-------:|-------:|-------:|-------:|-------:|-------:|-------:|
| `trec-covid`              | 0.5947 | 0.6559 | 0.7274 | 0.7270 | 0.7814 | 0.7815 | 0.7834 | 0.7835 |
| `bioasq`                  | 0.5225 | 0.4646 | 0.4980 | 0.4980 | 0.4149 | 0.4148 | 0.4042 | 0.4042 |
| `nfcorpus`                | 0.3218 | 0.3254 | 0.3470 | 0.3473 | 0.3735 | 0.3735 | 0.3735 | 0.3735 |
| `nq`                      | 0.3055 | 0.3285 | 0.5378 | 0.5372 | 0.5413 | 0.5415 | 0.5413 | 0.5415 |
| `hotpotqa`                | 0.6330 | 0.6027 | 0.6868 | 0.6868 | 0.7259 | 0.7259 | 0.7242 | 0.7241 |
| `fiqa`                    | 0.2361 | 0.2361 | 0.3475 | 0.3473 | 0.4065 | 0.4065 | 0.4065 | 0.4065 |
| `signal1m`                | 0.3304 | 0.3304 | 0.3008 | 0.3006 | 0.2886 | 0.2886 | 0.2869 | 0.2869 |
| `trec-news`               | 0.3952 | 0.3977 | 0.4152 | 0.4169 | 0.4425 | 0.4424 | 0.4411 | 0.4410 |
| `robust04`                | 0.4070 | 0.4070 | 0.4679 | 0.4651 | 0.4465 | 0.4435 | 0.4467 | 0.4437 |
| `arguana`                 | 0.3970 | 0.4142 | 0.5203 | 0.5218 | 0.6361 | 0.6228 | 0.6361 | 0.6228 |
| `webis-touche2020`        | 0.4422 | 0.3673 | 0.2468 | 0.2464 | 0.2570 | 0.2571 | 0.2570 | 0.2571 |
| `cqadupstack-android`     | 0.3801 | 0.3709 | 0.3904 | 0.3898 | 0.5075 | 0.5076 | 0.5075 | 0.5076 |
| `cqadupstack-english`     | 0.3453 | 0.3321 | 0.4079 | 0.4078 | 0.4857 | 0.4857 | 0.4855 | 0.4855 |
| `cqadupstack-gaming`      | 0.4822 | 0.4418 | 0.4957 | 0.4959 | 0.5965 | 0.5967 | 0.5965 | 0.5967 |
| `cqadupstack-gis`         | 0.2901 | 0.2904 | 0.3150 | 0.3148 | 0.4127 | 0.4131 | 0.4129 | 0.4133 |
| `cqadupstack-mathematica` | 0.2015 | 0.2046 | 0.2377 | 0.2379 | 0.3163 | 0.3163 | 0.3163 | 0.3163 |
| `cqadupstack-physics`     | 0.3214 | 0.3248 | 0.3599 | 0.3597 | 0.4722 | 0.4724 | 0.4722 | 0.4724 |
| `cqadupstack-programmers` | 0.2802 | 0.2963 | 0.3401 | 0.3399 | 0.4242 | 0.4238 | 0.4242 | 0.4238 |
| `cqadupstack-stats`       | 0.2711 | 0.2790 | 0.2990 | 0.2980 | 0.3732 | 0.3728 | 0.3732 | 0.3728 |
| `cqadupstack-tex`         | 0.2244 | 0.2086 | 0.2530 | 0.2529 | 0.3115 | 0.3115 | 0.3115 | 0.3115 |
| `cqadupstack-unix`        | 0.2749 | 0.2788 | 0.3167 | 0.3170 | 0.4219 | 0.4220 | 0.4219 | 0.4220 |
| `cqadupstack-webmasters`  | 0.3059 | 0.3008 | 0.3167 | 0.3166 | 0.4065 | 0.4072 | 0.4065 | 0.4072 |
| `cqadupstack-wordpress`   | 0.2483 | 0.2562 | 0.2733 | 0.2718 | 0.3547 | 0.3547 | 0.3547 | 0.3547 |
| `quora`                   | 0.7886 | 0.7886 | 0.8343 | 0.8344 | 0.8890 | 0.8876 | 0.8890 | 0.8876 |
| `dbpedia-entity`          | 0.3180 | 0.3128 | 0.4366 | 0.4374 | 0.4074 | 0.4073 | 0.4077 | 0.4076 |
| `scidocs`                 | 0.1490 | 0.1581 | 0.1591 | 0.1588 | 0.2170 | 0.2172 | 0.2170 | 0.2172 |
| `fever`                   | 0.6513 | 0.7530 | 0.7882 | 0.7879 | 0.8630 | 0.8629 | 0.8620 | 0.8620 |
| `climate-fever`           | 0.1651 | 0.2129 | 0.2297 | 0.2298 | 0.3119 | 0.3117 | 0.3119 | 0.3117 |
| `scifact`                 | 0.6789 | 0.6647 | 0.7041 | 0.7036 | 0.7408 | 0.7408 | 0.7408 | 0.7408 |

The follow command will reproduce the above experiments:

```bash
java -cp $ANSERINI_JAR io.anserini.reproduce.RunBeir
```

<details>
<summary>Manual runs and evaluation</summary>

The following snippet will generate the complete set of results that corresponds to the above table:

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    # "flat" indexes
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.flat -topics beir-${c} -output $OUTPUT_DIR/run.beir.flat.${c}.txt -bm25 -removeQuery
    # "multifield" indexes
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.multifield -topics beir-${c} -output $OUTPUT_DIR/run.beir.multifield.${c}.txt -bm25 -removeQuery -fields contents=1.0 title=1.0
    # SPLADE++ ED, cached queries
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c}.splade-pp-ed -output $OUTPUT_DIR/run.beir.splade-pp-ed.cached_q.${c}.txt -impact -pretokenized -removeQuery
    # SPLADE++ ED, ONNX
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c} -encoder SpladePlusPlusEnsembleDistil -output $OUTPUT_DIR/run.beir.splade-pp-ed.onnx.${c}.txt -impact -pretokenized -removeQuery
    # BGE-base-en-v1.5, flat, cached queries
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchFlatDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5.flat -topics beir-${c}.bge-base-en-v1.5 -output $OUTPUT_DIR/run.beir.bge-base-en-v1.5.flat.cached_q.${c}.txt -threads 16 -removeQuery
    # BGE-base-en-v1.5, flat, ONNX
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchFlatDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5.flat -topics beir-${c} -encoder BgeBaseEn15 -output $OUTPUT_DIR/run.beir.bge-base-en-v1.5.flat.onnx.${c}.txt -threads 16 -removeQuery
    # BGE-base-en-v1.5, HNSW, cached queries
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5.hnsw -topics beir-${c}.bge-base-en-v1.5 -output $OUTPUT_DIR/run.beir.bge-base-en-v1.5.hnsw.cached_q.${c}.txt -threads 16 -efSearch 1000 -removeQuery
    # BGE-base-en-v1.5, HNSW, ONNX
    java -cp $ANSERINI_JAR --add-modules jdk.incubator.vector io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5.hnsw -topics beir-${c} -encoder BgeBaseEn15 -output $OUTPUT_DIR/run.beir.bge-base-en-v1.5.hnsw.onnx.${c}.txt -threads 16 -efSearch 1000 -removeQuery
done
```

Note that `--add-modules jdk.incubator.vector` enables OpenJDK's [Panama](https://openjdk.org/projects/panama/) Vector API, which [accelerates vector search](https://www.elastic.co/blog/accelerating-vector-search-simd-instructions).
However, this is _not_ a score-preserving optimization.
Similarity scores are slightly different in some cases, which leads to slightly different nDCG@10 scores for some BEIR collection.

And here's the snippet of code to perform the evaluation (which will yield the scores above):

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    echo $c
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.flat.${c}.txt
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.multifield.${c}.txt
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.splade-pp-ed.cached_q.${c}.txt
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.splade-pp-ed.onnx.${c}.txt
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.bge-base-en-v1.5.flat.cached_q.${c}.txt
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.bge-base-en-v1.5.flat.onnx.${c}.txt
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.bge-base-en-v1.5.hnsw.cached_q.${c}.txt
    java -cp $ANSERINI_JAR trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt $OUTPUT_DIR/run.beir.bge-base-en-v1.5.hnsw.onnx.${c}.txt
done
```

</details>
