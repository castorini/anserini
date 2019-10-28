# Elastirini: Anserini Integration with Elasticsearch

Anserini provides code for indexing into an ELK stack, thus providing interoperable support existing test collections.

## Deploying ELK stack locally

We use [docker-elk](https://github.com/deviantony/docker-elk) to set up the ELK stack locally.

Before we start, make sure you have Docker and Docker Compose installed and running.

First, we clone the repository and switch into the directory.

```
git clone https://github.com/deviantony/docker-elk.git && cd docker-elk
```

Depending on the documents you are indexing, you probably also have to increase the ELK stack's heap size in `docker-compose.yml`:

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

`docker-compose up`

If at some point one of the ELK components is failing for some reason, or if you have changed its configurations while the containers are running, try restarting it. For instance, to restart Kibana:

`docker-compose restart kibana`

## Indexing

Once we have a local instance of Elasticsearch up and running, we can index using Elasticsearch through Elastirini.

First, let us create the index in Elasticsearch. We need to update <index_name> and BM25 parameters for our own purpose.

```
curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/<index_name>' \
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
                  "k1":"0.9",
                  "b":"0.4"
                }
              }
            }
          }
        }'
```

Here, the username and password are those defaulted by `docker-elk`. You can change these if you like.

Now, we can start indexing through Elastirini. Here, instead of passing in `-index` (to index with Lucene directly) or `-solr` (to index with Solr), we pass in `-es`. For example, to index [robust04](https://github.com/castorini/anserini/blob/master/docs/regressions-robust04.md), we could run:

```
sh target/appassembler/bin/IndexCollection -collection TrecCollection -generator JsoupGenerator -es -es.index robust04 -threads 16 -input /absolute/path/to/disk45 -storePositions -storeDocvectors -storeRawDocs
```

There are also other `-es` parameters that you can specify as you see fit.

You can also run the following command to replicate Anserini BM25 retrieval:

```
sh target/appassembler/bin/SearchElastic -topicreader Trec -es.index robust04 \
  -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt \
  -output run.es.robust04.bm25.topics.robust04.301-450.601-700.txt
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.es.robust04.bm25.topics.robust04.301-450.601-700.txt
```
# Elasticsearch on MSMARCO(Passage)
For Msmarco-passage data preparation, check Anserini: [BM25 Baselines on MS MARCO (Passage)](https://github.com/castorini/anserini/blob/master/docs/experiments-msmarco-passage.md). Similarly, there are three steps:

1.Create the index in Elasticsearch by calling `curl` command as above. Remember to update BM25 parameters with k1 = 0.82, b = 0.68.

2.Index documents as `JsonCollection` through Elastirini:
```
sh target/appassembler/bin/IndexCollection -collection JsonCollection -generator JsoupGenerator 
-es -es.index msmarco-passage -threads 9 -input msmarco-passage/collection_jsonl  -storePositions -storeDocvectors -storeRawDocs
```
3.Retrieving and Evaluating the dev set
Since there are many queries (> 100k), it would take a long time to retrieve all of them. To speed this up, we use only the queries that are in the qrels file:
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
To perform the evaulation with trec_eval, run:
```
./eval/trec_eval.9.0.4/trec_eval -c -mrecall.1000 -mmap \
    msmarco-passage/qrels.dev.small.tsv msmarco-passage/run.dev.small.tsv 
```
The output should be:
```
map                   all       0.1956
recall_1000           all       0.8573
```
Average precision and recall@1000 are the two metrics we care about the most. You can check the table in [BM25 Baselines on MS MARCO (Passage)](https://github.com/castorini/anserini/blob/master/docs/experiments-msmarco-passage.md) `BM25  Tuning` section for more information.
