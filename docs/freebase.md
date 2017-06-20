# Working with Freebase Using Anserini

## Node Lookup

Build an index over nodes in the Freebase knowledge graph:

```
nohup sh target/appassembler/bin/IndexFreebaseNodes \
 -input freebase-rdf-latest.gz -index lucene-index.freebase.nodes >& log.freebase.nodes.txt &
```

This then allows lookup of facts for a particular `mid`:

```
sh target/appassembler/bin/LookupFreebaseNode -index lucene-index.freebase.nodes \
  -mid fb:m.02mjmr
```

## Topic Search

Build an index over topic in the Freebase knowledge graph:

```
nohup sh target/appassembler/bin/IndexFreebaseTopics \
 -input freebase-rdf-latest.gz -index lucene-index.freebase.topics >& log.freebase.topics.txt &
```

This then allows searching for the `mid` of a particular topic:

```
sh target/appassembler/bin/LookupFreebaseTopic -index lucene-index.freebase.topics -query "Barrack Obama"
```
