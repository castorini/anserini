# Elastirini: Anserini Integration with Elasticsearch

Anserini provides code for indexing into an ELK stack, thus providing interoperable support existing test collections.

## Deploying Elasticsearch Locally

Simple instructions for installing and running Elasticsearch can be found [here](http://elastic.co/start).
Basically, it's as simple as downloading the correct distribution for your platform, unpacking it, and running:

```
bin/elasticsearch
```

If you want to install Kibana, it's just another distribution to unpack and a similarly simple command.

## Indexing and Retrieval: Robust04

Once we have a local instance of Elasticsearch up and running, we can index using Elasticsearch through Elastirini.
In this example, we replicate experiments on [Robust04](regressions-robust04.md).

First, let's create the index in Elasticsearch.
We define the schema and the ranking function (BM25) using [this config](../src/main/resources/elasticsearch/index-config.robust04.json):

```bash
cat src/main/resources/elasticsearch/index-config.robust04.json \
 | curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/robust04' -d @-
```

The username and password are those defaulted by `docker-elk`. You can change these if you like.

Now, we can start indexing through Elastirini.
Here, instead of passing in `-index` (to index with Lucene directly), we use `-es` for Elasticsearch:

```bash
sh target/appassembler/bin/IndexCollection -collection TrecCollection -generator JsoupGenerator \
 -es -es.index robust04 -threads 16 -input /path/to/disk45 -storePositions -storeDocvectors -storeRawDocs
```

We can then run the following command to replicate Anserini BM25 retrieval:

```bash
sh target/appassembler/bin/SearchElastic -topicreader Trec -es.index robust04 \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -output run.es.robust04.bm25.topics.robust04.txt
```

To evaluate effectiveness:

```bash
$ eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.es.robust04.bm25.topics.robust04.txt
map                   	all	0.2531
P_30                  	all	0.3102
```

## Indexing and Retrieval: MS MARCO Passage

We can replicate the [BM25 Baselines on MS MARCO (Passage)](experiments-msmarco-passage.md) results in a similar way.
First, set up the proper schema using [this config](../src/main/resources/elasticsearch/index-config.msmarco-passage.json):

```bash
cat src/main/resources/elasticsearch/index-config.msmarco-passage.json \
 | curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/msmarco-passage' -d @-
```

Indexing:

```bash
sh target/appassembler/bin/IndexCollection -collection JsonCollection -generator JsoupGenerator \
 -es -es.index msmarco-passage -threads 9 -input /path/to/msmarco-passage -storePositions -storeDocvectors -storeRawDocs
```

Retrieval:

```bash
sh target/appassembler/bin/SearchElastic -topicreader TsvString -es.index msmarco-passage \
 -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -output run.es.msmacro-passage.txt
```

Evaluation:

```bash
$ ./eval/trec_eval.9.0.4/trec_eval -c -mrecall.1000 -mmap src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.es.msmacro-passage.txt
map                   	all	0.1956
recall_1000           	all	0.8573
```
