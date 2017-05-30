# Creating an entity index for Freebase in Anserini

### FreebaseTopic Document
FreebaseTopicDocument has four fields:
- topicMid - the MID of the topic
- title - the object value of the (topicMid, http://rdf.freebase.com/key/wikipedia.en_title)
- label - title - the object value of the (topicMid, http://www.w3.org/2000/01/rdf-schema#label)
- text - all the values separated by space of the (topicMid, http://rdf.freebase.com/key/wikipedia.en)



### Indexing Freebase Entities

```
nohup sh target/appassembler/bin/IndexFreebaseTopic -collection FreebaseTopicCollection \
 -input /path/to/freebase/freebase-rdf-latest.gz \
 -index freebase.entities.index \
 > log.freebase &
```

### Search

After indexing is done, you should be able to search for documents. Following the sample example:

```
sh target/appassembler/bin/SearchFreebaseTopic -index freebase.entities.index \
  -entity http://rdf.freebase.com/ns/m.02mjmr
```