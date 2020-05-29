# Ingesting CORD-19 into Solr and Elasticsearch

This document describes how to ingest the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/) into Solr and Elasticsearch.
If you want to build or download Lucene indexes for CORD-19, see [this guide](experiments-cord19.md).

## Getting the Data

Follow the instructions [here](experiments-cord19.md) to get access to the data.
This version of the guide has been verified to work with the version of 2020/05/26.

## Solr + Blacklight

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

Adjust the schema (if there are errors, follow the instructions below and come back):

```bash
curl -X POST -H 'Content-type:application/json' --data-binary @src/main/resources/solr/schemas/cord19.json \   
 http://localhost:8983/solr/cord19/schema
```

**Note:** if there are errors from field conflicts, you'll need to reset the configset and recreate the collection (select [All] for the fields to replace):

```bash
solrini/bin/solr delete -c cord19
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
solrini/bin/solr create -n anserini -c cord19
```

We can now index into Solr:

```bash
DATE=2020-05-26
DATA_DIR=./collections/cord19-"${DATE}"

sh target/appassembler/bin/IndexCollection -collection Cord19AbstractCollection -generator Cord19Generator \
   -threads 8 -input "${DATA_DIR}" \
   -solr -solr.index cord19 -solr.zkUrl localhost:9983 \
   -storePositions -storeDocvectors -storeContents -storeRaw
```

Once indexing is complete, you can query in Solr at [`http://localhost:8983/solr/#/cord19/query`](http://localhost:8983/solr/#/cord19/query).

Next, we can stand up an instance of [Blacklight](https://projectblacklight.org/) to provide a nice search interface; this is exactly the same instance that runs our basic (non-neural) [Covidex](https://basic.covidex.ai/).

To begin, ensure that you have Ruby 2.6.5+ and Ruby on Rails 6.0+ installed.

Once the approriate ruby and ruby on rails version is installed, navigate to a directory outside of Anserini and clone the [Gooselight2](https://github.com/castorini/gooselight2):

```bash
cd ..
git clone https://github.com/castorini/gooselight2.git
```

Then navigate into the `gooselight2/covid` directory, and run the following commands, if a `yarn` error occurs with `rails db:migrate` run `yarn install --check-files` to update yarn:

```bash
bundle install
rails db:migrate
rails s
```

The rails should now be avaliable on [`http://localhost:3000`](http://localhost:3000)

## Elasticsearch + Kibana

From the [Elasticsearch](http://elastic.co/start), download the correct distribution for you platform to the `anserini/` directory.
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

Set up the proper schema using [this config](../src/main/resources/elasticsearch/index-config.cord19.json):

```bash
cat src/main/resources/elasticsearch/index-config.cord19.json \
 | curl --user elastic:changeme -XPUT -H 'Content-Type: application/json' 'localhost:9200/cord19' -d @-
```

Indexing (Abstract, Full-Text, Paragraph):

```bash
sh target/appassembler/bin/IndexCollection -collection Cord19AbstractCollection -generator Cord19Generator \
 -es -es.index cord19 -threads 8 -input path/to/cord19 -storePositions -storeDocvectors -storeContents -storeRaw

sh target/appassembler/bin/IndexCollection -collection Cord19FullTextCollection -generator Cord19Generator \
 -es -es.index cord19 -threads 8 -input path/to/cord19 -storePositions -storeDocvectors -storeContents -storeRaw

sh target/appassembler/bin/IndexCollection -collection Cord19ParagraphCollection -generator Cord19Generator \
 -es -es.index cord19 -threads 8 -input path/to/cord19 -storePositions -storeDocvectors -storeContents -storeRaw
```
We are now able to get visualizations from Kibana at [`http://localhost:5601`](http://localhost:5601)

## Replication Log
