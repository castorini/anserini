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

Download Solr version 8.11.2 (binary release) from [here](https://solr.apache.org/downloads.html) and extract the archive:

```bash
mkdir solrini && tar -zxvf solr*.tgz -C solrini --strip-components=1
```

Solr 8.11.2 is the last release in the 8.x series, and unfortunately, these instructions do not work for Solr 9.x.

Start Solr:

```bash
solrini/bin/solr start -c -m 16G
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

**Note:** If there are errors from field conflicts, you'll need to reset the configset and recreate the collection:

```bash
solrini/bin/solr delete -c cord19
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
solrini/bin/solr create -n anserini -c cord19
```

We can now index into Solr:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19AbstractCollection \
  -input collections/cord19-2020-07-16 \
  -generator Cord19Generator \
  -solr \
  -solr.index cord19 \
  -solr.zkUrl localhost:9983 \
  -threads 8  \
  -storePositions -storeDocvectors -storeContents -storeRaw
```

Once indexing is complete, you can query in Solr at [`http://localhost:8983/solr/#/cord19/query`](http://localhost:8983/solr/#/cord19/query).

You'll need to make sure your query is searching the `contents` field, so the query should look something like `contents:"incubation period"`.

## Elasticsearch + Kibana

From [here](http://elastic.co/start), download the latest Elasticsearch and Kibanna distributions for you platform to the `anserini/` directory (which as of 7/31/2022 is v8.3.3).

First, unpack Elasticsearch:

```bash
mkdir elastirini && tar -zxvf elasticsearch*.tar.gz -C elastirini --strip-components=1
```

To make life easier, disable Elasticsearch's built-in security features.
In the file `elastirini/config/elasticsearch.yml`, add the following line:

```
xpack.security.enabled: false
```

Start running:

```bash
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

```bash
curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_cluster/settings \
  -d '{ "transient": { "cluster.routing.allocation.disk.threshold_enabled": false } }'
```

Set up the proper schema using [this config](../src/main/resources/elasticsearch/index-config.cord19.json):

```bash
cat src/main/resources/elasticsearch/index-config.cord19.json \
 | curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/cord19' -d @-
```

Indexing abstracts:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19AbstractCollection \
  -input collections/cord19-2020-07-16 \
  -generator Cord19Generator \
  -es \
  -es.index cord19 \
  -threads 8 \
  -storePositions -storeDocvectors -storeContents -storeRaw
```

We are now able to access interactive search and visualization capabilities from Kibana at [`http://localhost:5601/`](http://localhost:5601).

Here's an example:

1. Click on the hamburger icon, then click "Dashboard" under "Analytics".
2. Create "Data View": set the name to `cord19`, and use `publish_time` as the timestamp field. (Note, "Data Views" used to be called "Index Patterns".)
3. Go back to "Discover" under "Analytics"; now run a search, e.g., "incubation period". Be sure to expand the date, which is a dropdown box to the right of the search box; something like "Last 10 years" works well.
4. You should be able to see search results as well as a histogram of the dates in which those articles are published!

## Reproduction Log[*](reproducibility.md)

+ Reproduced by [@adamyy](https://github.com/adamyy) on 2020-05-29 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489)) on CORD-19 release of 2020/05/26.
+ Reproduced by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-17 (commit [`fad12be`](https://github.com/castorini/anserini/commit/fad12be2e37a075100707c3a674eb67bc0aa57ef)) on CORD-19 release of 2020/06/19.
+ Reproduced by [@LizzyZhang-tutu](https://github.com/LizzyZhang-tutu) on 2020-07-26 (commit [`fad12be`](https://github.com/castorini/anserini/commit/539f7d43a0183454a633f34aa20b46d2eeec1a19)) on CORD-19 release of 2020/07/25.
+ Reproduced by [@lintool](https://github.com/lintool) on 2020-11-23 (commit [`746447a`](https://github.com/castorini/anserini/commit/746447af47db5bb032eb551623c11219467c961e)) on CORD-19 release of 2020/07/16 with Solr v8.3.0 and ES/Kibana v7.10.0.
+ Reproduced by [@lintool](https://github.com/lintool) on 2021-11-02 (commit [`cb0c44c`](https://github.com/castorini/anserini/commit/cb0c44cd209c4cad3327942216a736aa4bbe21cc)) on CORD-19 release of 2020/07/16 with Solr v8.10.1 and ES/Kibana v7.15.1.
+ Reproduced by [@lintool](https://github.com/lintool) on 2022-03-21 (commit [`3d1fc34`](https://github.com/castorini/anserini/commit/3d1fc3457b993832b4682c0482b26d8271d02ec6) on CORD-19 release of 2020/07/16 with Solr v8.11.1 and ES/Kibana v8.1.0.
+ Reproduced by [@lintool](https://github.com/lintool) on 2022-07-31 (commit [`2a0cb16`](https://github.com/castorini/anserini/commit/2a0cb16829b347e38801b9972b349de498dadf03)) (v0.14.4) on CORD-19 release of 2020/07/16 with Solr v8.11.2 and ES/Kibana v8.3.3.
