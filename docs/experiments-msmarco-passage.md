# Anserini: BM25 Baselines on [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page contains instructions for running BM25 baselines on the MS MARCO *passage* ranking task.
Note that there is a separate [MS MARCO *document* ranking task](experiments-msmarco-doc.md).
We also have a [separate page](experiments-doc2query.md) describing document expansion experiments (Doc2query) for this task.

## Data Prep

We're going to use `msmarco-passage/` as the working directory.
First, we need to download and extract the MS MARCO passage dataset:

```
mkdir msmarco-passage

wget https://msmarco.blob.core.windows.net/msmarcoranking/collectionandqueries.tar.gz -P msmarco-passage
tar -xzvf msmarco-passage/collectionandqueries.tar.gz -C msmarco-passage
```

To confirm, `collectionandqueries.tar.gz` should have MD5 checksum of `31644046b18952c1386cd4564ba2ae69`.

Next, we need to convert the MS MARCO tsv collection into Anserini's jsonl files (which have one json object per line):

```
python ./src/main/python/msmarco/convert_collection_to_jsonl.py \
 --collection_path msmarco-passage/collection.tsv --output_folder msmarco-passage/collection_jsonl
```

The above script should generate 9 jsonl files in `msmarco-passage/collection_jsonl`, each with 1M lines (except for the last one, which should have 841,823 lines).

We can now index these docs as a `JsonCollection` using Anserini:

```
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 9 -input msmarco-passage/collection_jsonl \
 -index msmarco-passage/lucene-index-msmarco -storePositions -storeDocvectors -storeRawDocs 
```

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary... on a modern desktop with an SSD, indexing takes less than two minutes.

## Retrieving and Evaluating the Dev set

Since queries of the set are too many (+100k), it would take a long time to retrieve all of them. To speed this up, we use only the queries that are in the qrels file: 

```
python ./src/main/python/msmarco/filter_queries.py --qrels msmarco-passage/qrels.dev.small.tsv \
 --queries msmarco-passage/queries.dev.tsv --output_queries msmarco-passage/queries.dev.small.tsv
```

The output queries file should contain 6980 lines.

We can now retrieve this smaller set of queries:

```
python ./src/main/python/msmarco/retrieve.py --hits 1000 --threads 1 \
 --index msmarco-passage/lucene-index-msmarco --qid_queries msmarco-passage/queries.dev.small.tsv \
 --output msmarco-passage/run.dev.small.tsv
```

Note that by default, the above script uses BM25 with tuned parameters `k1=0.82`, `b=0.68` (more details below).
The option `-hits` specifies the of documents per query to be retrieved.
Thus, the output file should have approximately 6980 * 1000 = 6.9M lines. 

Retrieval speed will vary by machine:
On a modern desktop with an SSD, we can get ~0.06 s/query (taking about seven minutes). We can also perform multithreaded retrieval by changing the `--threads` argument.

Alternatively, we can run the same script implemented in Java, which is a bit faster:

```
./target/appassembler/bin/SearchMsmarco  -hits 1000 -threads 1 \
 -index msmarco-passage/lucene-index-msmarco -qid_queries msmarco-passage/queries.dev.small.tsv \
 -output msmarco-passage/run.dev.small.tsv
```

Similarly, we can perform multithreaded retrieval by changing the `-threads` argument.

Finally, we can evaluate the retrieved documents using this the official MS MARCO evaluation script: 

```
python ./src/main/python/msmarco/msmarco_eval.py \
 msmarco-passage/qrels.dev.small.tsv msmarco-passage/run.dev.small.tsv
```

And the output should be like this:

```
#####################
MRR @10: 0.18741227770955546
QueriesRanked: 6980
#####################
```

We can also use the official TREC evaluation tool, `trec_eval`, to compute other metrics than MRR@10. 
For that we first need to convert runs and qrels files to the TREC format:

```
python ./src/main/python/msmarco/convert_msmarco_to_trec_run.py \
 --input_run msmarco-passage/run.dev.small.tsv --output_run msmarco-passage/run.dev.small.trec

python ./src/main/python/msmarco/convert_msmarco_to_trec_qrels.py \
 --input_qrels msmarco-passage/qrels.dev.small.tsv --output_qrels msmarco-passage/qrels.dev.small.trec
```

And run the `trec_eval` tool:

```
./eval/trec_eval.9.0.4/trec_eval -c -mrecall.1000 -mmap \
 msmarco-passage/qrels.dev.small.trec msmarco-passage/run.dev.small.trec
```

The output should be:

```
map                   	all	0.1957
recall_1000           	all	0.8573
```

Average precision and recall@1000 are the two metrics we care about the most.

## BM25 Tuning

Note that this figure differs slightly from the value reported in [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375), which uses the Anserini (system-wide) default of `k1=0.9`, `b=0.4`.

Tuning was accomplished with the [`tune_bm25.py`](../src/main/python/msmarco/tune_bm25.py) script, using the queries found [here](https://github.com/castorini/Anserini-data/tree/master/MSMARCO); the basic approach is grid search of parameter values in tenth increments.
There are five different sets of 10k samples (using the `shuf` command).
We tuned on each individual set and then averaged parameter values across all five sets (this has the effect of regularization).
Note that we optimized recall@1000 since Anserini output serves as input to later stage rerankers (e.g., based on BERT), and we want to maximize the number of relevant documents the rerankers have to work with.
The tuned parameters using this method are `k1=0.82`, `b=0.68`.

Here's the comparison between the Anserini default and tuned parameters:

Setting                     | MRR@10 | MAP    | Recall@1000 |
:---------------------------|-------:|-------:|------------:|
Default (`k1=0.9`, `b=0.4`) | 0.1840 | 0.1926 | 0.8526
Tuned (`k1=0.82`, `b=0.68`) | 0.1874 | 0.1957 | 0.8573

Anserini was upgraded to Lucene 8.0 as of commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019); prior to that, the toolkit uses Lucene 7.6.
The above results are based on Lucene 8.0, but Lucene 7.6 results can be replicated with [v0.5.1](https://github.com/castorini/anserini/releases);
the effectiveness differences are very small.
For convenience, here are the effectiveness numbers with Lucene 7.6 (v0.5.1):

Setting                     | MRR@10 | MAP    | Recall@1000 |
:---------------------------|-------:|-------:|------------:|
Default (`k1=0.9`, `b=0.4`) | 0.1839 | 0.1925 | 0.8526
Tuned (`k1=0.82`, `b=0.72`) | 0.1875 | 0.1956 | 0.8578



## Replication Log

+ Results replicated by [@ronakice](https://github.com/ronakice) on 2019-08-12 (commit [`5b29d16`](https://github.com/castorini/anserini/commit/5b29d1654abc5e8a014c2230da990ab2f91fb340))
+ Results replicated by [@MathBunny](https://github.com/MathBunny) on 2019-08-12 (commit [`5b29d16`](https://github.com/castorini/anserini/commit/5b29d1654abc5e8a014c2230da990ab2f91fb340))
+ Results replicated by [@JMMackenzie](https://github.com/JMMackenzie) on 2020-01-08 (commit [`f63cd22`](https://github.com/castorini/anserini/commit/f63cd2275fa5a9d4da2d17e5f983a3308e8b50ce ))
+ Results replicated by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-08 (commit [`5cc923d`](https://github.com/castorini/anserini/commit/5cc923d5c02777d8b25df32ff2e2a59be5badfdd))
+ Results replicated by [@LuKuuu](https://github.com/LuKuuu) on 2020-01-15 (commit [`f21137b`](https://github.com/castorini/anserini/commit/f21137b44f1115d25d1ff8ecaf7780c36498c5de))
