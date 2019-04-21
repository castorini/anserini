# Anserini: Experiments on [MS MARCO](http://www.msmarco.org/)

## Downloading the data:
First, we need to download and extract the MS MARCO dataset:
```
DATA_DIR=./msmarco_data
mkdir ${DATA_DIR}

wget https://msmarco.blob.core.windows.net/msmarcoranking/collectionandqueries.tar.gz -P ${DATA_DIR}
tar -xvf ${DATA_DIR}/collectionandqueries.tar.gz -C ${DATA_DIR}
```

## Converting MS MARCO docs to Anserini's jsonl files
We now need to convert MS MARCO tsv collection to Anserini's jsonl files 
(which have one json object per line).
```
python ./src/main/python/msmarco/convert_collection_to_jsonl.py --collection_path=${DATA_DIR}/collection.tsv --output_folder=${DATA_DIR}/collection_jsonl
```

It should generate 9 jsonl files in ${DATA_DIR}/collection_jsonl, each with 1M lines/docs (except for the last one, which should have 841,823 lines).

## Indexing
We can now index these docs using JsonCollection.
```
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection -generator LuceneDocumentGenerator -threads 9 -input ${DATA_DIR}/collection_jsonl -index ${DATA_DIR}/lucene-index-msmarco -optimize
```

The output message should be like this:
```
2019-04-20 11:52:34,935 INFO  [main] index.IndexCollection (IndexCollection.java:647) - Total 8,841,823 documents indexed in 00:05:04
```

# Retrieving Dev set
Since queries of the set are too many (+100k), it would take a long time to retrieve all of them. To speed this up, we use only the queries that are in the qrels file: 
```
python ./src/main/python/msmarco/filter_queries.py --qrels=${DATA_DIR}/qrels.dev.small.tsv --queries=${DATA_DIR}/queries.dev.tsv --output_queries=${DATA_DIR}/queries.dev.small.tsv
```
The output queries file should contain 6980 lines.

We can now retrieve this smaller set of queries.
```
python ./src/main/python/msmarco/retrieve.py -index ${DATA_DIR}/lucene-index-msmarco -qid_queries ${DATA_DIR}/queries.dev.small.tsv -output ${DATA_DIR}/run.dev.small.tsv -hits 1000
```

It should take 2-3 hours to retrieve the documents. 
'hits' specifies the of documents per query to be retrieved. Thus, the output
file should have approximately 6980 * 1000 = 6.9M lines. 

In case you want to compare your retrieved docs against ours, we made our output
available [here](https://drive.google.com/open?id=1Z0IEY6Z8jPqQMTLVj-MQdyU4VV-ZuQqJ).


# Evaluating Dev Set
Finally, we can evaluate the retrieved documents using this the official MS MARCO evaluation script: 
```
python ./src/main/python/msmarco/msmarco_eval.py ${DATA_DIR}/qrels.dev.small.tsv ${DATA_DIR}/run.dev.small.tsv
```

And the output should be like this:
```
#####################
MRR @10: 0.18388092964024202
QueriesRanked: 6980
#####################
```
