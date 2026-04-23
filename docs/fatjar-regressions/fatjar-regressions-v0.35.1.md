# Anserini Fatjar Regresions (v0.35.1)

❗Anserini v0.35.1 is no longer the latest release.
The latest release is always linked from the main [Anserini](http://anserini.io/) site.

❗The published artifacts for Anserini v0.35.1 are problematic. See [Anserini #2468](https://github.com/castorini/anserini/pull/2468) for details.

Fetch the fatjar:

```bash
wget https://repo1.maven.org/maven2/io/anserini/anserini/0.35.1/anserini-0.35.1-fatjar.jar
```

Note that prebuilt indexes will be downloaded to `~/.cache/pyserini/indexes/`.
Currently, this path is hard-coded (see [Anserini #2322](https://github.com/castorini/anserini/issues/2322)).
If you want to change the download location, the current workaround is to use symlinks, i.e., symlink `~/.cache/pyserini/indexes/` to the actual path you desire.

## TREC 2024 RAG

Warning: The `msmarco-v2.1-doc` prebuilt index is 63 GB uncompressed.
The `msmarco-v2.1-doc-segmented` prebuilt index is 84 GB uncompressed.

Here are the instructions for reproducing runs on the MS MARCO V2.1 document corpus with prebuilt indexes (adjust number of threads based on available resources):

```bash
TOPICS=(msmarco-v2-doc-dev msmarco-v2-doc-dev2 trec2021-dl trec2022-dl trec2023-dl); for t in "${TOPICS[@]}"
do
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v2.1-doc -topics ${t} -output run.msmarco-v2.1-doc.bm25.${t}.txt -threads 16 -bm25
done
```

<details>
<summary>Evaluation</summary>

Run these commands for evaluation:

```
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt 
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt 
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl22-doc-msmarco-v2.1.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank qrels.msmarco-v2.1-doc.dev.txt run.msmarco-v2.1-doc.bm25.msmarco-v2-doc-dev.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank qrels.msmarco-v2.1-doc.dev2.txt run.msmarco-v2.1-doc.bm25.msmarco-v2-doc-dev2.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m map qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2021-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2021-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.100 qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2021-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.1000 qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2021-dl.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m map qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2022-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2022-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.100 qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2022-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.1000 qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2022-dl.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m map qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2023-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2023-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.100 qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2023-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.1000 qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc.bm25.trec2023-dl.txt
```

And these are the expected scores:

```
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
```

</details>

Here are the instructions for reproducing runs on the MS MARCO V2.1 segmented document corpus with prebuilt indexes (adjust number of threads based on available resources):

```bash
TOPICS=(msmarco-v2-doc-dev msmarco-v2-doc-dev2 trec2021-dl trec2022-dl trec2023-dl); for t in "${TOPICS[@]}"
do
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v2.1-doc-segmented -topics ${t} -output run.msmarco-v2.1-doc-segmented.bm25.${t}.txt -threads 16 -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000
done
```

<details>
<summary>Evaluation</summary>

Run these commands for evaluation:

```
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt 
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt 
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl22-doc-msmarco-v2.1.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank qrels.msmarco-v2.1-doc.dev.txt run.msmarco-v2.1-doc-segmented.bm25.msmarco-v2-doc-dev.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank qrels.msmarco-v2.1-doc.dev2.txt run.msmarco-v2.1-doc-segmented.bm25.msmarco-v2-doc-dev2.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m map qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2021-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2021-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.100 qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2021-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.1000 qrels.dl21-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2021-dl.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m map qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2022-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2022-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.100 qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2022-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.1000 qrels.dl22-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2022-dl.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m map qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2023-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2023-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.100 qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2023-dl.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m recall.1000 qrels.dl23-doc-msmarco-v2.1.txt run.msmarco-v2.1-doc-segmented.bm25.trec2023-dl.txt
```

And these are the expected scores:

```
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
```

</details>

To generate jsonl output containing the raw documents that can be reranked and further processed, use the `-outputRerankerRequests` option to specify an output file.
For example:

```bash
java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection \
  -index msmarco-v2.1-doc \
  -topics trec2023-dl \
  -output run.msmarco-v2.1-doc.bm25.trec2023-dl.txt \
  -bm25 -hits 20 \
  -outputRerankerRequests results.msmarco-v2.1-doc.bm25.trec2023-dl.jsonl
```

And the output looks something like:

```bash
$ head -n 1 results.msmarco-v2.1-doc.bm25.trec2023-dl.jsonl | jq 
{
  "query": {
    "text": "How does the process of digestion and metabolism of carbohydrates start",
    "qid": 2000138
  },
  "candidates": [
    {
      "docid": "msmarco_v2.1_doc_15_390497775",
      "score": 14.3364,
      "doc": {
        "url": "https://diabetestalk.net/blood-sugar/conversion-of-carbohydrates-to-glucose",
        "title": "Conversion Of Carbohydrates To Glucose | DiabetesTalk.Net",
        "headings": "...",
        "body": "..."
      }
    },
    {
      "docid": "msmarco_v2.1_doc_15_416962410",
      "score": 14.2271,
      "doc": {
        "url": "https://diabetestalk.net/insulin/how-is-starch-converted-to-glucose-in-the-body",
        "title": "How Is Starch Converted To Glucose In The Body? | DiabetesTalk.Net",
        "headings": "...",
        "body": "..."
      }
    },
    ...
  ]
}
```


## MS MARCO V1 Passage

Currently, Anserini provides support for the following models:

+ BM25
+ SPLADE++ EnsembleDistil: cached queries and ONNX query encoding
+ cosDPR-distil: cached queries and ONNX query encoding
+ bge-base-en-v1.5: cached queries and ONNX query encoding
+ cohere-embed-english-v3.0: cached queries and ONNX query encoding

The following snippet will generate the complete set of results for MS MARCO V1 Passage:

```bash
# BM25
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage -topics ${t} -output run.${t}.bm25.txt -threads 16 -bm25
done

# SPLADE++ ED
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage.splade-pp-ed -topics ${t}.splade-pp-ed -output run.${t}.splade-pp-ed.cached_q.txt -threads 16 -impact -pretokenized
    # Using ONNX
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index msmarco-v1-passage.splade-pp-ed -topics ${t} -encoder SpladePlusPlusEnsembleDistil -output run.${t}.splade-pp-ed.onnx.txt -threads 16 -impact -pretokenized
done

# cosDPR-distil
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using fp32 index, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil -topics ${t}.cos-dpr-distil -output run.${t}.cos-dpr-distil.fp32.cached_q.txt -threads 16 -efSearch 1000
    # Using fp32 index, ONNX
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil -topics ${t} -encoder CosDprDistil -output run.${t}.cos-dpr-distil.fp32.onnx.txt -threads 16 -efSearch 1000
    # Using int8 index, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil.quantized -topics ${t}.cos-dpr-distil -output run.${t}.cos-dpr-distil.int8.cached_q.txt -threads 16 -efSearch 1000
    # Using int8 index, ONNX
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cos-dpr-distil.quantized -topics ${t} -encoder CosDprDistil -output run.${t}.cos-dpr-distil.int8.onnx.txt -threads 16 -efSearch 1000
done

# bge-base-en-v1.5
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using fp32 index, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5 -topics ${t}.bge-base-en-v1.5 -output run.${t}.bge-base-en-v1.5.fp32.cached_q.txt -threads 16 -efSearch 1000
    # Using fp32 index, ONNX
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5 -topics ${t} -encoder BgeBaseEn15 -output run.${t}.bge-base-en-v1.5.fp32.onnx.txt -threads 16 -efSearch 1000
    # Using int8 index, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5.quantized -topics ${t}.bge-base-en-v1.5 -output run.${t}.bge-base-en-v1.5.int8.cached_q.txt -threads 16 -efSearch 1000
    # Using int8 index, ONNX
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.bge-base-en-v1.5.quantized -topics ${t} -encoder BgeBaseEn15 -output run.${t}.bge-base-en-v1.5.int8.onnx.txt -threads 16 -efSearch 1000
done

# cohere-embed-english-v3.0
TOPICS=(msmarco-v1-passage.dev dl19-passage dl20-passage); for t in "${TOPICS[@]}"
do
    # Using fp32 index, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cohere-embed-english-v3.0 -topics ${t}.cohere-embed-english-v3.0 -output run.${t}.cohere-embed-english-v3.0.fp32.cached_q.txt -threads 16 -efSearch 1000
    # Using int8 index, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index msmarco-v1-passage.cohere-embed-english-v3.0.quantized -topics ${t}.cohere-embed-english-v3.0 -output run.${t}.cohere-embed-english-v3.0.int8.cached_q.txt -threads 16 -efSearch 1000
done

```
Here are the expected scores (dev using MRR@10, DL19 and DL20 using nDCG@10):

|                                                         |    dev |   DL19 |   DL20 |
|:--------------------------------------------------------|-------:|-------:|-------:|
| BM25                                                    | 0.1840 | 0.5058 | 0.4796 |
| SPLADE++ ED (cached queries)                            | 0.3830 | 0.7317 | 0.7198 |
| SPLADE++ ED (ONNX)                                      | 0.3828 | 0.7308 | 0.7197 |
| cosDPR-distil w/ HNSW fp32 (cached queries)             | 0.3887 | 0.7250 | 0.7025 |
| cosDPR-distil w/ HNSW fp32 (ONNX)                       | 0.3887 | 0.7250 | 0.7025 |
| cosDPR-distil w/ HNSW int8 (cached queries)             | 0.3897 | 0.7240 | 0.7004 |
| cosDPR-distil w/ HNSW int8 (ONNX)                       | 0.3899 | 0.7247 | 0.6996 |
| bge-base-en-v1.5 w/ HNSW fp32 (cached queries)          | 0.3574 | 0.7065 | 0.6780 |
| bge-base-en-v1.5 w/ HNSW fp32 (ONNX)                    | 0.3575 | 0.7016 | 0.6768 |
| bge-base-en-v1.5 w/ HNSW int8 (cached queries)          | 0.3572 | 0.7016 | 0.6738 |
| bge-base-en-v1.5 w/ HNSW int8 (ONNX)                    | 0.3575 | 0.7017 | 0.6767 |
| cohere-embed-english-v3.0 w/ HNSW fp32 (cached queries) | 0.3647 | 0.6956 | 0.7245 |
| cohere-embed-english-v3.0 w/ HNSW int8 (cached queries) | 0.3656 | 0.6955 | 0.7262 |

And here's the snippet of code to perform the evaluation (which will yield the results above):

```bash
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl19-passage.txt
wget https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/qrels.dl20-passage.txt

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.bm25.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bm25.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bm25.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.splade-pp-ed.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.splade-pp-ed.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.splade-pp-ed.cached_q.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.splade-pp-ed.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.splade-pp-ed.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.splade-pp-ed.onnx.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.cos-dpr-distil.fp32.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil.fp32.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil.fp32.cached_q.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.cos-dpr-distil.fp32.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil.fp32.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil.fp32.onnx.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.cos-dpr-distil.int8.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil.int8.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil.int8.cached_q.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.cos-dpr-distil.int8.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cos-dpr-distil.int8.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cos-dpr-distil.int8.onnx.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.bge-base-en-v1.5.fp32.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5.fp32.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5.fp32.cached_q.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.bge-base-en-v1.5.fp32.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5.fp32.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5.fp32.onnx.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.bge-base-en-v1.5.int8.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5.int8.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5.int8.cached_q.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.bge-base-en-v1.5.int8.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.bge-base-en-v1.5.int8.onnx.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.bge-base-en-v1.5.int8.onnx.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.cohere-embed-english-v3.0.fp32.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cohere-embed-english-v3.0.fp32.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cohere-embed-english-v3.0.fp32.cached_q.txt

echo ''

java -cp anserini-0.35.1-fatjar.jar trec_eval -c -M 10 -m recip_rank qrels.msmarco-passage.dev-subset.txt run.msmarco-v1-passage.dev.cohere-embed-english-v3.0.int8.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl19-passage.txt                    run.dl19-passage.cohere-embed-english-v3.0.int8.cached_q.txt
java -cp anserini-0.35.1-fatjar.jar trec_eval -m ndcg_cut.10 -c qrels.dl20-passage.txt                    run.dl20-passage.cohere-embed-english-v3.0.int8.cached_q.txt
```

## BEIR

Currently, Anserini provides support for the following models:

+ Flat = BM25, "flat" bag-of-words baseline
+ MF = BM25, "multifield" bag-of-words baseline
+ S = SPLADE++ EnsembleDistil:
  + cached queries (Sp)
  + ONNX query encoding (So)
+ D = bge-base-en-v1.5
  + cached queries (Dp)
  + ONNX query encoding (Do)

The following snippet will generate the complete set of results for BEIR:

```bash
CORPORA=(trec-covid bioasq nfcorpus nq hotpotqa fiqa signal1m trec-news robust04 arguana webis-touche2020 cqadupstack-android cqadupstack-english cqadupstack-gaming cqadupstack-gis cqadupstack-mathematica cqadupstack-physics cqadupstack-programmers cqadupstack-stats cqadupstack-tex cqadupstack-unix cqadupstack-webmasters cqadupstack-wordpress quora dbpedia-entity scidocs fever climate-fever scifact); for c in "${CORPORA[@]}"
do
    # "flat" indexes
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.flat -topics beir-${c} -output run.beir.${c}.flat.txt -bm25 -removeQuery
    # "multifield" indexes
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.multifield -topics beir-${c} -output run.beir.${c}.multifield.txt -bm25 -removeQuery -fields contents=1.0 title=1.0
    # SPLADE++ ED, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c}.splade-pp-ed -output run.beir.${c}.splade-pp-ed.cached_q.txt -impact -pretokenized -removeQuery
    # SPLADE++ ED, ONNX
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchCollection -index beir-v1.0.0-${c}.splade-pp-ed -topics beir-${c} -encoder SpladePlusPlusEnsembleDistil -output run.beir.${c}.splade-pp-ed.onnx.txt -impact -pretokenized -removeQuery
    # BGE-base-en-v1.5, cached queries
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5 -topics beir-${c}.bge-base-en-v1.5 -output run.beir.${c}.bge.cached_q.txt -threads 16 -efSearch 1000 -removeQuery
    # BGE-base-en-v1.5, ONNX
    java -cp anserini-0.35.1-fatjar.jar io.anserini.search.SearchHnswDenseVectors -index beir-v1.0.0-${c}.bge-base-en-v1.5 -topics beir-${c} -encoder BgeBaseEn15 -output run.beir.${c}.bge.onnx.txt -threads 16 -efSearch 1000 -removeQuery
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
    java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.flat.txt
    java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.multifield.txt
    java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.splade-pp-ed.cached_q.txt
    java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.splade-pp-ed.onnx.txt
    java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.bge.cached_q.txt
    java -cp anserini-0.35.1-fatjar.jar trec_eval -c -m ndcg_cut.10 qrels.beir-v1.0.0-${c}.test.txt run.beir.${c}.bge.onnx.txt
done
```

