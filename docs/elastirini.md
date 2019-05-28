# Elastirini - Elasticsearch integration with Anserini

## Setup

We use [docker-elk](https://github.com/deviantony/docker-elk) to set up the ELK stack locally; please see the link for more setup and configuration instructions.

Note: depending on the documents you are indexing, you probably also have to increase the ELK stack's heap size by adjusting the corresponding `ES_JAVA_OPTS` (for Elasticsearch) and `LS_JAVA_OPTS` (for Logstash) in `docker-compose.yml` in `docker-elk`.

## Index

To index with Elasticsearch, instead of passing in `-index` (to index with Lucene directly) or `-solr` (to index with Solr), we pass in `-es`. For example, to index [robust04](https://github.com/castorini/Anserini/blob/master/docs/experiments-robust04.md), we could run:
`sh target/appassembler/bin/IndexCollection -collection TrecCollection -generator JsoupGenerator -es -threads 16 -input [absolute path to disk45] -storePositions -storeDocvectors -storeRawDocs`

There are additional `-es` parameters for using Elasticsearch in Anserini; you could specify them as you see fit.
