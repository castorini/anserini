# Creating a topic index for Freebase in Anserini
 
### Indexing Freebase Entities
  
```
sh target/appassembler/bin/IndexFreebaseTopic -input ./freebase-sample.gz -index freebase.sample.index
```
  
### Search
  
After indexing is done, you should be able to search for documents. Following the sample example:
 
```
sh target/appassembler/bin/LookupFreebaseTopic -index ./freebase.sample.index  -query "Barrack Obama"
```