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

The above script should generate 9 jsonl files in ${DATA_DIR}/collection_jsonl, each with 1M lines/docs (except for the last one, which should have 841,823 lines).

We can now index these docs as a `JsonCollection` using Anserini:

```
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 9 -input ${DATA_DIR}/collection_jsonl \
 -index ${DATA_DIR}/lucene-index-msmarco -optimize
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

In case you want to compare your retrieved docs against ours, we made our output
available [here](https://drive.google.com/open?id=1Z0IEY6Z8jPqQMTLVj-MQdyU4VV-ZuQqJ).

Finally, we can evaluate the retrieved documents using this the official MS MARCO evaluation script: 

```
python ./src/main/python/msmarco/msmarco_eval.py ${DATA_DIR}/qrels.dev.small.tsv ${DATA_DIR}/run.dev.small.tsv
```

And the output should be like this:

```
#####################
MRR @10: 0.1906588552326375
QueriesRanked: 6980
#####################
```

Note that this figure differs slightly from the value reported in [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375), which uses the Anserini default of `b1=0.9`, `k=0.4`, yielding `MRR@10 = 0.18388092964024202`.
Subsequent tuning (after publication) on the dev set obtains `b1=0.6`, `k=0.8`, which yields the figure above; this is the default setting in `retrieve.py` above.
