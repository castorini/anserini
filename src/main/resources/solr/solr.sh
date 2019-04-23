#!/usr/bin/env bash

###
# This script assumes a single-node SolrCloud instance is running locally.
###

# Solr install directory
SOLR_DIR=$1

# Solr's ZooKeeper URL
ZOOKEEPER_URL=$2

# Copy anserini into lib dir
mkdir ${SOLR_DIR}/lib && cp ././../../../../target/anserini-*-fatjar.jar ${SOLR_DIR}/lib

# Extract lang.zip in each configset
unzip anserini/conf/lang.zip -d anserini/conf
unzip anserini-twitter/conf/lang.zip -d anserini-twitter/conf

# Upload configset to Solr
$SOLR_DIR/bin/solr zk -z ${ZOOKEEPER_URL:-localhost:9983} upconfig -n anserini -d anserini
$SOLR_DIR/bin/solr zk -z ${ZOOKEEPER_URL:-localhost:9983} upconfig -n anserini-twitter -d anserini-twitter
