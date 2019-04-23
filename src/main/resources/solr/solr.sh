#!/usr/bin/env bash

# Solr version to download (i.e., 7.6.0)
SOLR_VERSION=7.6.0

# Solr directory name
SOLR_DIR=solr-${SOLR_VERSION}

# Download Solr
wget "https://archive.apache.org/dist/lucene/solr/$SOLR_VERSION/solr-$SOLR_VERSION.tgz" -O solr.tgz

# Extract archive
tar -zxvf solr.tgz

# Copy anserini into lib dir
mkdir ${SOLR_DIR}/lib && cp ././../../../../target/anserini-*-fatjar.jar ${SOLR_DIR}/lib

# Extract lang.zip in each configset
unzip anserini/conf/lang.zip -d anserini/conf
unzip anserini-twitter/conf/lang.zip -d anserini-twitter/conf

echo "###"
echo "# Start Solr: $SOLR_DIR/bin/solr start -c -m 8G"
echo "# Import configsets:"
echo "#  - $SOLR_DIR/bin/solr zk -z localhost:9983 upconfig -n anserini -d anserini"
echo "#  - $SOLR_DIR/bin/solr zk -z localhost:9983 upconfig -n anserini-twitter -d anserini-twitter"
echo "# Browse Solr: http://localhost:8983"
echo "###"
