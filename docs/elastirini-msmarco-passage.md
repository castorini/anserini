# Elastirini: BM25 baseline on MSMARCO(Passage)

This page contains instructions for running BM25 baselines on the MS MARCO passage ranking task.

# Deploying ELK stack locally

We use docker-elk to set up the ELK stack locally.
Before we start, make sure you have Docker and Docker Compose installed and running.
First, we clone the repository and switch into the directory.
```
git clone https://github.com/deviantony/docker-elk.git && cd docker-elk
```
Depending on the documents you are indexing, you probably also have to increase the ELK stack's heap size in docker-compose.yml:
To increase Elasticsearch's heap size:
```
sed -i 's/ES_JAVA_OPTS: "-Xmx256m -Xms256m"/ES_JAVA_OPTS: "-Xmx1g -Xms512m"/' docker-compose.yml
```
If you are on MacOS:
```
sed -i '' 's/ES_JAVA_OPTS: "-Xmx256m -Xms256m"/ES_JAVA_OPTS: "-Xmx1g -Xms512m"/' docker-compose.yml
```

To increase Logstash's heap size:
```
sed -i 's/LS_JAVA_OPTS: "-Xmx256m -Xms256m"/LS_JAVA_OPTS: "-Xmx1g -Xms512m"/' docker-compose.yml
```

If you are on MacOS:
```
sed -i '' 's/LS_JAVA_OPTS: "-Xmx256m -Xms256m"/LS_JAVA_OPTS: "-Xmx1g -Xms512m"/' docker-compose.yml
```

Note `-Xmx` is the maximum memory that can be allocated, and `-Xms` is the initial memory allocated. You can specify these values as needed.
You can further specify general configurations for any of the ELK components by changing the file `[name]/config/[name].yml`. For instance, to further specify the configuration of Elasticsearch, you can make changes to `elasticsearch/config/elasticsearch.yml`.
Then, we can build and start the Docker containers for the ELK stack to run.
```
docker-compose up
```

If at some point one of the ELK components is failing for some reason, or if you have changed its configurations while the containers are running, try restarting it. For instance, to restart Kibana:
```
docker-compose restart kibana
```

# Indexing
Once we have a local instance of Elasticsearch up and running, we can index using Elasticsearch through Elastirini.
First, let us create the index in Elasticsearch(note that by default, the above script uses BM25 with tuned parameters k1=0.82, b=0.68

```
curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/msmarco-passage' \
    -d '{
          "mappings":{
            "dynamic_templates":[
              {
                "all_text":{
                  "match_mapping_type":"string",
                  "mapping":{
                    "type":"text",
                    "analyzer":"english"
                  }
                }
              }
            ],
            "properties":{
              "id":{
                "type":"keyword"
              },
              "id_long":{
                "type":"keyword"
              },
              "contents":{
                "type":"text",
                "store": false,
                "index": true,
                "analyzer": "english"
              },
              "raw":{
                "type":"text",
                "store": true,
                "index": false
              },
              "epoch":{
                "type":"date",
                "format":"epoch_second"
              },
              "published_date":{
                "type":"date",
                "format":"epoch_millis"
              }
            }
          },
          "settings":{
            "index":{
              "refresh_interval":"60s",
              "similarity":{
                "default":{
                  "type":"BM25",
                  "k1":"0.82",
                  "b":"0.68"
                }
              }
            }
          }
        }'
```
Here, the username and password are those defaulted by `docker-elk`. You can change these if you like.



# Data Prep

We're going to use msmarco-passage/ as the working directory. First, we need to download and extract the MS MARCO passage dataset:
```
mkdir msmarco-passage

wget https://msmarco.blob.core.windows.net/msmarcoranking/collectionandqueries.tar.gz -P msmarco-passage
tar -xzvf msmarco-passage/collectionandqueries.tar.gz -C msmarco-passage
```
To confirm, `collectionandqueries.tar.gz` should have MD5 checksum
of `31644046b18952c1386cd4564ba2ae69`.

Next, we need to convert the MS MARCO tsv collection into jsonl files (which have one json object per line):
```
python ./src/main/python/msmarco/convert_collection_to_jsonl.py \
 --collection_path msmarco-passage/collection.tsv --output_folder msmarco-passage/collection_jsonl
```

The above script should generate 9 jsonl files in `msmarco-passage/collection_jsonl`, each with 1M lines (except for the last one, which should have 841,823 lines).

Now, we can start indexing through Elastirini. Here, instead of passing in  `-index `(to index with Lucene directly) or `-solr `(to index with Solr), we pass in `-es`. To index MSMARCO-Passage, we could run:
```
sh target/appassembler/bin/IndexCollection -collection JsonCollection -generator JsoupGenerator 
-es -es.index msmarco-passage -threads 9 -input msmarco-passage/collection_jsonl  -storePositions -storeDocvectors -storeRawDocs
```

Retrieving and Evaluating the Dev set
Since queries of the set are too many (+100k), it would take a long time to retrieve all of them. To speed this up, we use only the queries that are in the qrels file:
```
python ./src/main/python/msmarco/filter_queries.py --qrels msmarco-passage/qrels.dev.small.tsv \
 --queries msmarco-passage/queries.dev.tsv --output_queries msmarco-passage/queries.dev.small.tsv
```
The output queries file should contain 6980 lines.

We can now retrieve this smaller set of queries with Elastirini, it takes about half hour on a modern desktop with an SSD:
```
sh target/appassembler/bin/SearchElastic -topicreader TsvString -es.index msmarco-passage \
   -topics msmarco-passage/queries.dev.small.tsv \
   -output msmarco-passage/run.dev.small.tsv
```
There are also other -es parameters that you can specify as you see fit.
You can also run the following command to replicate Anserini BM25 retrieval:
```
./eval/trec_eval.9.0.4/trec_eval -c -mrecall.1000 -mmap \
	msmarco-passage/qrels.dev.small.tsv msmarco-passage/run.dev.small.tsv 
```
The output should be:
```
map                   all  	   0.1956
recall_1000           all	   0.8573
```
Average precision and recall@1000 are the two metrics we care about the most.


## BM25 Tuning

Note that this figure differs slightly from the value reported in [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375), which uses the Anserini (system-wide) default of `k1=0.9`, `b=0.4`.

Tuning was accomplished with the `tune_bm25.py` script, using the queries found [here](https://github.com/castorini/Anserini-data/tree/master/MSMARCO).
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


