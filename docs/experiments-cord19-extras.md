# Ingesting CORD-19 into Solr and Elasticsearch

This document describes how to ingest the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/) into Solr and Elasticsearch.
If you want to build or download Lucene indexes for CORD-19, see [this guide](experiments-cord19.md).

## Getting the Data

Follow the instructions [here](experiments-cord19.md) to get access to the data.
This version of the guide has been verified to work with the version of 2020/07/16, which is the corpus used in round 5 of the TREC-COVID challenge.

Download the corpus using our script:

```
python src/main/python/trec-covid/index_cord19.py --date 2020-07-16 --download
```

## Solr

From the Solr [archives](https://archive.apache.org/dist/lucene/solr/), download the Solr (non `-src`) version that matches Anserini's [Lucene version](https://github.com/castorini/anserini/blob/master/pom.xml#L36) to the `anserini/` directory.

Extract the archive:

```bash
mkdir solrini && tar -zxvf solr*.tgz -C solrini --strip-components=1
```

Start Solr (adjust memory usage with `-m` as appropriate):

```bash
solrini/bin/solr start -c -m 8G
```

Run the Solr bootstrap script to copy the Anserini JAR into Solr's classpath and upload the configsets to Solr's internal ZooKeeper:

```bash
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
```

Solr should now be available at [http://localhost:8983/](http://localhost:8983/) for browsing.

Next, create the collection:

```bash
solrini/bin/solr create -n anserini -c cord19
```

Adjust the schema (if there are errors, follow the instructions below):

```bash
curl -X POST -H 'Content-type:application/json' --data-binary @src/main/resources/solr/schemas/cord19.json \
 http://localhost:8983/solr/cord19/schema
```

**Note:** If there are errors from field conflicts, you'll need to reset the configset and recreate the collection (select [All] for the fields to replace):

```bash
solrini/bin/solr delete -c cord19
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
solrini/bin/solr create -n anserini -c cord19
```

We can now index into Solr:

```bash
sh target/appassembler/bin/IndexCollection -collection Cord19AbstractCollection -generator Cord19Generator \
 -threads 8 -input collections/cord19-2020-07-16 \
 -solr -solr.index cord19 -solr.zkUrl localhost:9983 \
 -storePositions -storeDocvectors -storeContents -storeRaw
```

Once indexing is complete, you can query in Solr at [`http://localhost:8983/solr/#/cord19/query`](http://localhost:8983/solr/#/cord19/query).

You'll need to make sure your query is searching the `contents` field, so the query should look something like `contents:"incubation period"`.

## Elasticsearch + Kibana

From the [Elasticsearch](http://elastic.co/start), download the correct distribution for your platform to the `anserini/` directory.
These instructions below work with version 7.10.0.

First, unpack and deploy Elasticsearch:

```bash
mkdir elastirini && tar -zxvf elasticsearch*.tar.gz -C elastirini --strip-components=1
elastirini/bin/elasticsearch
```

Upack and deploy Kibana: 

```bash
tar -zxvf kibana*.tar.gz -C elastirini --strip-components=1
elastirini/bin/kibana
```

Elasticsearch has a built-in safeguard to disable indexing if you're running low on disk space.
The error is something like "flood stage disk watermark [95%] exceeded on ..." with indexes placed into readonly mode.
Obviously, be careful, but if you're sure things are going to be okay and you won't run out of disk space, disable the safeguard as follows:

```
curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_cluster/settings -d '{ "transient": { "cluster.routing.allocation.disk.threshold_enabled": false } }'
```

Set up the proper schema using [this config](../src/main/resources/elasticsearch/index-config.cord19.json):

```bash
cat src/main/resources/elasticsearch/index-config.cord19.json \
 | curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/cord19' -d @-
```

Indexing abstracts:

```bash
sh target/appassembler/bin/IndexCollection -collection Cord19AbstractCollection -generator Cord19Generator \
 -es -es.index cord19 -threads 8 -input collections/cord19-2020-07-16 -storePositions -storeDocvectors -storeContents -storeRaw
```

We are now able to access interactive search and visualization capabilities from Kibana at [`http://localhost:5601/`](http://localhost:5601).

Here's an example: in the above webapp, create an "Index Pattern".
Set the index pattern to `cord19`, and use `publish_time` as the time filter.
Then navigate to "Discover" in Kibana to run a search.
If you're not getting any results, be sure you've expanded the date range, next to the search bar.

## Replication Log

+ Replicated by [@adamyy](https://github.com/adamyy) on 2020-05-29 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489)) on CORD-19 release of 2020/05/26.
+ Replicated by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-17 (commit [`fad12be`](https://github.com/castorini/anserini/commit/fad12be2e37a075100707c3a674eb67bc0aa57ef)) on CORD-19 release of 2020/06/19.
+ Replicated by [@LizzyZhang-tutu](https://github.com/LizzyZhang-tutu) on 2020-07-26 (commit [`fad12be`](https://github.com/castorini/anserini/commit/539f7d43a0183454a633f34aa20b46d2eeec1a19)) on CORD-19 release of 2020/07/25.
+ Replicated by [@lintool](https://github.com/lintool) on 2020-11-23 (commit [`746447`](https://github.com/castorini/anserini/commit/746447af47db5bb032eb551623c11219467c961e)) on CORD-19 release of 2020/07/16 with Solr v8.3.0 and ES/Kibana v7.10.0.
