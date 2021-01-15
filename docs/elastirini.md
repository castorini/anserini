# Elastirini: Anserini Integration with Elasticsearch

Anserini provides code for indexing into an ELK stack, thus providing interoperable support existing test collections.

## Deploying Elasticsearch Locally

From the [Elasticsearch](http://elastic.co/start), download the correct distribution for you platform to the `anserini/` directory. 

Unpacking:

```
mkdir elastirini && tar -zxvf elasticsearch*.tar.gz -C elastirini --strip-components=1
```

Start running:

```
elastirini/bin/elasticsearch
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
sh target/appassembler/bin/IndexCollection -collection TrecCollection -generator DefaultLuceneDocumentGenerator \
 -es -es.index robust04 -threads 16 -input /path/to/disk45 -storePositions -storeDocvectors -storeRaw
```

We may need to wait a few minutes after indexing for the index to "catch up" before performing retrieval, otherwise the evaluation metrics may be off.
Run the following command to replicate Anserini BM25 retrieval:

```bash
sh target/appassembler/bin/SearchElastic -topicreader Trec -es.index robust04 \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -output runs/run.es.robust04.bm25.topics.robust04.txt
```

To evaluate effectiveness:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.es.robust04.bm25.topics.robust04.txt
map                   	all	0.2531
P_30                  	all	0.3102
```

## Indexing and Retrieval: Core18

We can replicate the [TREC Washington Post Corpus](regressions-core18.md) results in a similar way.
First, set up the proper schema using [this config](../src/main/resources/elasticsearch/index-config.core18.json):

```bash
cat src/main/resources/elasticsearch/index-config.core18.json \
 | curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/core18' -d @-
```

Indexing:

```bash
sh target/appassembler/bin/IndexCollection -collection WashingtonPostCollection -generator WashingtonPostGenerator \
 -es -es.index core18 -threads 8 -input /path/to/WashingtonPost -storePositions -storeDocvectors -storeContents
```

We may need to wait a few minutes after indexing for the index to "catch up" before performing retrieval, otherwise the evaluation metrics may be off.

Retrieval:

```bash
sh target/appassembler/bin/SearchElastic -topicreader Trec -es.index core18 \
  -topics src/main/resources/topics-and-qrels/topics.core18.txt \
  -output runs/run.es.core18.bm25.topics.core18.txt
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt runs/run.es.core18.bm25.topics.core18.txt
map                   	all	0.2495
P_30                  	all	0.3567
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
sh target/appassembler/bin/IndexCollection -collection JsonCollection -generator DefaultLuceneDocumentGenerator \
 -es -es.index msmarco-passage -threads 9 -input /path/to/msmarco-passage -storePositions -storeDocvectors -storeRaw
```

We may need to wait a few minutes after indexing for the index to "catch up" before performing retrieval, otherwise the evaluation metrics may be off.

Retrieval:

```bash
sh target/appassembler/bin/SearchElastic -topicreader TsvString -es.index msmarco-passage \
 -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -output runs/run.es.msmacro-passage.txt
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -m map src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.es.msmacro-passage.txt
map                   	all	0.1956
recall_1000           	all	0.8573
```

## Indexing and Retrieval: MS MARCO Document

We can replicate the [BM25 Baselines on MS MARCO (Doc)](experiments-msmarco-doc.md) results in a similar way.
First, set up the proper schema using [this config](../src/main/resources/elasticsearch/index-config.msmarco-doc.json):

```bash
cat src/main/resources/elasticsearch/index-config.msmarco-doc.json \
 | curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/msmarco-doc' -d @-
```

Indexing:

```bash
sh target/appassembler/bin/IndexCollection -collection CleanTrecCollection -generator DefaultLuceneDocumentGenerator \
 -es -es.index msmarco-doc -threads 1 -input /path/to/msmarco-doc -storePositions -storeDocvectors -storeRaw
```

We may need to wait a few minutes after indexing for the index to "catch up" before performing retrieval, otherwise the evaluation metrics may be off.

Retrieval:

```bash
sh target/appassembler/bin/SearchElastic -topicreader TsvInt -es.index msmarco-doc \
 -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output runs/run.es.msmacro-doc.txt
```

This can take potentially longer than `SearchCollection` with Lucene indexes.

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -m map src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.es.msmacro-doc.txt
map                   	all	0.2308
recall_1000           	all	0.8856
```

## Elasticsearch Integration Test

We have an end-to-end integration testing script `run_es_regression.py` for [Robust04](regressions-robust04.md), [Core18](regressions-core18.md), [MS MARCO passage](regressions-msmarco-passage.md) and [MS MARCO document](regressions-msmarco-doc.md):

```
# Check if Elasticsearch server is on
python src/main/python/run_es_regression.py --ping
# Check if collection exists
python src/main/python/run_es_regression.py --check-index-exists [collection]
# Create collection if it does not exist
python src/main/python/run_es_regression.py --create-index [collection]
# Delete collection if it exists
python src/main/python/run_es_regression.py --delete-index [collection]
# Insert documents from input directory into collection
python src/main/python/run_es_regression.py --insert-docs [collection] --input [directory]
# Search and evaluate on collection
python src/main/python/run_es_regression.py --evaluate [collection]

# Run end to end
python src/main/python/run_es_regression.py --regression [collection] --input [directory]
```

For the `collection` meta-parameter, use `robust04`, `core18`, `msmarco-passage`, or `msmarco-doc`, for each of the collections above, respectively.

## Replication Log

+ Results replicated by [@nikhilro](https://github.com/nikhilro) on 2020-01-26 (commit [`d5ee069`](https://github.com/castorini/anserini/commit/d5ee069399e6a306d7685bda756c1f19db721156)) for both [MS MARCO Passage](experiments-msmarco-passage.md) and [Robust04](regressions-robust04.md)
+ Results replicated by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-26 (commit [`7b76dfb`](https://github.com/castorini/anserini/commit/7b76dfbea7e0c01a3a5dc13e74f54852c780ec9b)) for both [MS MARCO Passage](experiments-msmarco-passage.md) and [Robust04](regressions-robust04.md)
+ Results replicated by [@HangCui0510](https://github.com/HangCui0510) on 2020-04-29 (commit [`07a9b05`](https://github.com/castorini/anserini/commit/07a9b053173637e15be79de4e7fce4d5a93d04fe)) for [MS Marco Passage](regressions-msmarco-passage.md), [Robust04](regressions-robust04.md) and [Core18](regressions-core18.md) using end-to-end [`run_es_regression`](../src/main/python/run_es_regression.py)
+ Results replicated by [@shaneding](https://github.com/shaneding) on 2020-05-25 (commit [`1de3274`](https://github.com/castorini/anserini/commit/1de3274b057a63382534c5277ffcd772c3fc0d43)) for [MS Marco Passage](regressions-msmarco-passage.md)
+ Results replicated by [@adamyy](https://github.com/adamyy) on 2020-05-29 (commit [`94893f1`](https://github.com/castorini/anserini/commit/94893f170e047d77c3ef5b8b995d7fbdd13f4298)) for [MS MARCO Passage](regressions-msmarco-passage.md), [MS MARCO Document](experiments-msmarco-doc.md)
+ Results replicated by [@YimingDou](https://github.com/YimingDou) on 2020-05-29 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489)) for [MS MARCO Passage](regressions-msmarco-passage.md)
+ Results replicated by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-17 (commit [`fad12be`](https://github.com/castorini/anserini/commit/fad12be2e37a075100707c3a674eb67bc0aa57ef)) for [Robust04](regressions-robust04.md), [Core18](regressions-core18.md), and [MS MARCO Passage](regressions-msmarco-passage.md)
+ Results replicated by [@lintool](https://github.com/lintool) on 2020-11-10 (commit [`e19755`](https://github.com/castorini/anserini/commit/e19755b5fa976127830597bc9fbca203b9f5ad24)), all commands and end-to-end regression script for all four collections
+ Results replicated by [@jrzhang12](https://github.com/jrzhang12) on 2021-01-02 (commit [`be4e44d`](https://github.com/castorini/anserini/commit/02c52ee606ba0ebe32c130af1e26d24d8f10566a)) for [MS MARCO Passage](regressions-msmarco-passage.md)

