#!/bin/sh

NAME=solr
INDEX_LOCATION=$PWD/../index/

docker rm $NAME

docker run --name $NAME \
    -v $INDEX_LOCATION/lucene-index.core17.pos+docvectors+rawdocs:/core17:rw \
    -v $INDEX_LOCATION/lucene-index.mb11.pos+docvectors+rawdocs:/mb11:rw \
    -p 8983:8983 \
    anserini-solr
