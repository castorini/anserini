# Creating a topic index for Freebase in Anserini
 
### Indexing Freebase Entities
  
```
nohup sh target/appassembler/bin/IndexFreebaseTopic \ 
    -input ../freebase/freebase-rdf-latest.gz \
    -index freebase.topics.index \ 
    > log.freebase &
```
  
### Search
  
After indexing is done, you should be able to search for documents. Following the sample example:
 
```
sh target/appassembler/bin/LookupFreebaseTopic -index ./freebase.sample.index  -query "Barrack Obama"
```