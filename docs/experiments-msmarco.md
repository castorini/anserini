# Anserini: Experiments on [MS MARCO](http://www.msmarco.org/)

## Data Prep

First, we need to download and extract the MS MARCO dataset:

```
DATA_DIR=./msmarco_data
mkdir ${DATA_DIR}

wget https://msmarco.blob.core.windows.net/msmarcoranking/collectionandqueries.tar.gz -P ${DATA_DIR}
tar -xvf ${DATA_DIR}/collectionandqueries.tar.gz -C ${DATA_DIR}
```

To confirm, `collectionandqueries.tar.gz` should have MD5 checksum of `fed5aa512935c7b62787cb68ac9597d6`.

Next, we need to convert the MS MARCO tsv collection into Anserini's jsonl files (which have one json object per line):

```
python ./src/main/python/msmarco/convert_collection_to_jsonl.py \
 --collection_path=${DATA_DIR}/collection.tsv --output_folder=${DATA_DIR}/collection_jsonl
```

The above script should generate 9 jsonl files in `${DATA_DIR}/collection_jsonl`, each with 1M lines (except for the last one, which should have 841,823 lines).

We can now index these docs as a `JsonCollection` using Anserini:

```
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 9 -input ${DATA_DIR}/collection_jsonl \
 -index ${DATA_DIR}/lucene-index-msmarco -optimize -storePositions -storeDocvectors -storeRawDocs 
```

The output message should be something like this:

```
2019-04-20 11:52:34,935 INFO  [main] index.IndexCollection (IndexCollection.java:647) - Total 8,841,823 documents indexed in 00:05:04
```

Your speed may vary... with a modern desktop machine with an SSD, indexing takes around a minute.

## Retrieving and Evaluating the Dev set

Since queries of the set are too many (+100k), it would take a long time to retrieve all of them. To speed this up, we use only the queries that are in the qrels file: 

```
python ./src/main/python/msmarco/filter_queries.py --qrels=${DATA_DIR}/qrels.dev.small.tsv \
 --queries=${DATA_DIR}/queries.dev.tsv --output_queries=${DATA_DIR}/queries.dev.small.tsv
```

The output queries file should contain 6980 lines.

We can now retrieve this smaller set of queries.

```
python ./src/main/python/msmarco/retrieve.py --index ${DATA_DIR}/lucene-index-msmarco \
 --qid_queries ${DATA_DIR}/queries.dev.small.tsv --output ${DATA_DIR}/run.dev.small.tsv --hits 1000
```

Retrieval speed will vary by machine:
On a modern desktop with an SSD, we can get ~0.04 per query (taking about five minutes).
On a slower machine with mechanical disks, the entire process might take as long as a couple of hours.
The option `-hits` specifies the of documents per query to be retrieved.
Thus, the output file should have approximately 6980 * 1000 = 6.9M lines. 

Finally, we can evaluate the retrieved documents using this the official MS MARCO evaluation script: 

```
python ./src/main/python/msmarco/msmarco_eval.py \
 ${DATA_DIR}/qrels.dev.small.tsv ${DATA_DIR}/run.dev.small.tsv
```

And the output should be like this:

```
#####################
MRR @10: 0.18751751034702308
QueriesRanked: 6980
#####################
```

We can also use the official TREC evaluation tool, `trec_eval`, to compute other metrics than MRR@10. 
For that we first need to convert runs and qrels files to the TREC format:

```
python ./src/main/python/msmarco/convert_msmarco_to_trec_run.py \
 --input_run ${DATA_DIR}/run.dev.small.tsv --output_run ${DATA_DIR}/run.dev.small.trec

python ./src/main/python/msmarco/convert_msmarco_to_trec_qrels.py \
 --input_qrels ${DATA_DIR}/qrels.dev.small.tsv --output_qrels ${DATA_DIR}/qrels.dev.small.trec
```

And run the `trec_eval` tool:

```
./eval/trec_eval.9.0.4/trec_eval -mrecall.1000 -mmap \
 ${DATA_DIR}/qrels.dev.small.trec ${DATA_DIR}/run.dev.small.trec
```

The output should be:

```
map                   	all	0.1956
recall_1000           	all	0.8578
```

Average precision and recall@1000 are the two metrics we care about the most.

## BM25 Tuning

Note that this figure differs slightly from the value reported in [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375) (see section on tuning BM25 parameters below), which uses the Anserini default of `b1=0.9`, `k=0.4`.

Tuning was accomplished with the `tune_bm25.py` script, using the queries found [here](https://github.com/castorini/Anserini-data/tree/master/MSMARCO).
There are five different sets of 10k samples (from the `shuf` command).
We tune on each individual set and then average parameter values across all five sets (this has the effect of regularization).
Note that we are currently optimizing recall@1000 since Anserini output will serve as input to later stage rerankers (e.g., based on BERT), and we want to maximize the number of relevant documents the rerankers have to work with.
The tuned parameters using this method are `b1=0.82`, `k=0.72`.
