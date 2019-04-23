#!/usr/bin/env bash

# Solr version to download (i.e., 8.0.0)
SOLR_VERSION=8.0.0

# Solr directory name
SOLR_DIR=solr-${SOLR_VERSION}

# Download Solr
wget "http://mirror.csclub.uwaterloo.ca/apache/lucene/solr/$SOLR_VERSION/solr-$SOLR_VERSION.tgz" -O solr.tgz

# Extract archive
tar -zxvf solr.tgz

# Copy anserini into lib dir
mkdir ${SOLR_DIR}/lib && cp ././../../../../target/anserini-*-fatjar.jar ${SOLR_DIR}/lib

# Copy configsets
cp -r anserini ${SOLR_DIR}/server/solr/configsets
cp -r anserini-twitter ${SOLR_DIR}/server/solr/configsets

echo "###"
echo "# To start Solr, run: $SOLR_DIR/bin/solr start -c -m 8G"
echo "###"