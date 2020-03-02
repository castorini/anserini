#!/usr/bin/env sh

if [ $# -eq 0 ]; then
  echo "usage: sh core-collection.sh [COLLECTION_NAME]"
  exit 0
fi

# Set specific field types and multiValued field properties in Solr schema
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
    "name":"authors",
    "type":"string",
    "stored":true,
    "multiValued":true,
    "docValues":true
  },
  "add-field":{
    "name":"contributors",
    "type":"string",
    "stored":true,
    "multiValued":true,
    "docValues":true
  },
  "add-field":{
    "name":"identifiers",
    "type":"string",
    "stored":true,
    "multiValued":true,
    "docValues":true
  },
  "add-field":{
    "name":"journals",
    "type":"string",
    "stored":true,
    "multiValued":true,
    "docValues":false
  },
  "add-field":{
    "name":"relations",
    "type":"string",
    "stored":true,
    "multiValued":true,
    "docValues":true
  },
  "add-field":{
    "name":"subjects",
    "type":"string",
    "stored":true,
    "multiValued":true,
    "docValues":true
  },
  "add-field":{
    "name":"topics",
    "type":"string",
    "stored":true,
    "multiValued":true,
    "docValues":true
  },
  "add-field":{
    "name":"datePublished",
    "type":"string",
    "stored":true
  }
}' http://localhost:8983/solr/$1/schema
