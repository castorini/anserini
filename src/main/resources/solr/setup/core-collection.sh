#!/usr/bin/env sh

if [ $# -eq 0 ]; then
  echo "usage: sh core-collection.sh [COLLECTION_NAME]"
  exit 0
fi

# Set specific field types and multiValued field properties in Solr schema
curl -X POST -H 'Content-type:application/json' --data-binary '@src/main/resources/solr/schemas/core-collection.json' http://localhost:8983/solr/"$1"/schema