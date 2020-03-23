#!/usr/bin/env sh

# Set specific field types and multiValued field properties in Solr schema
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "replace-field":{
    "name":"authors",
    "type":"string",
    "stored":true,
    "multiValued":true
  },
  "replace-field":{
    "name":"source_x",
    "type":"string",
    "stored":true,
    "docValues": true
  },
  "replace-field":{
    "name":"pmcid",
    "type":"string",
    "stored":true,
    "docValues": true
  },
  "replace-field":{
    "name":"pubmed_id",
    "type":"string",
    "stored":true,
    "docValues": true
  },
  "replace-field":{
    "name":"publish_time",
    "type":"string",
    "stored":true,
    "docValues":true
  },
  "replace-field":{
    "name":"doi",
    "type":"string",
    "stored":true,
    "docValues":true
  },
  "replace-field":{
    "name":"journal",
    "type":"string",
    "stored":true,
    "docValues":true
  },
  "replace-field":{
    "name":"sha",
    "type":"string",
    "stored":true,
    "docValues":true
  },
  "replace-field":{
    "name":"year",
    "type":"pint",
    "stored":true,
    "docValues":true
  }
}' http://localhost:8983/solr/covid/schema
