#!/usr/bin/env sh

# Set specific field types and multiValued field properties in Solr schema
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
    "name":"authors",
    "type":"string",
    "stored":true,
    "multiValued":true
  },
  "add-field":{
    "name":"sigs",
    "type":"string",
    "stored":true,
    "multiValued":true
  },
  "add-field":{
    "name":"venues",
    "type":"string",
    "stored":true,
    "multiValued":true
  },
  "add-field":{
    "name":"pages",
    "type":"string",
    "stored":true,
    "docValues": false
  }
}' http://localhost:8983/solr/acl/schema
