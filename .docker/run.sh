#!/bin/sh

# Name of the Docker container
NAME=solr

# Location for the index files.
INDEX_LOCATION=$PWD/../index/

# Get rid of an old container, if any.
docker rm $NAME

# Start the container with a few data sets mounted as volumes.
docker run --name $NAME \
    -v $INDEX_LOCATION/lucene-index.core17.pos+docvectors+rawdocs:/core17:rw \
    -v $INDEX_LOCATION/lucene-index.mb11.pos+docvectors+rawdocs:/mb11:rw \
    -p 8983:8983 \
    anserini-solr
