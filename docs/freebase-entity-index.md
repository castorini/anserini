# Creating an entity index for Freebase in Anserini

### Test on Sample Data
- paste this to a file and gzip it
- try indexing on this sample freebase file (make sure file is tab-delimited):
```
http://rdf.freebase.com/ns/m.02mjmr http://rdf.freebase.com/key/wikipedia.en_title  "Barack_Obama"  .
http://rdf.freebase.com/ns/m.02mjmr http://rdf.freebase.com/key/wikipedia.en    "Barack_Hussein_Obama_II"   .
http://rdf.freebase.com/ns/m.02mjmr http://rdf.freebase.com/key/wikipedia.en    "Barack_Hussein_Obama"   .
http://rdf.freebase.com/ns/m.02mjmr http://rdf.freebase.com/key/wikipedia.en    "Obama"   .
http://rdf.freebase.com/ns/m.02mjmr http://rdf.freebase.com/key/wikipedia.en    "Mr. President"    .
http://rdf.freebase.com/ns/m.72kgt2 http://rdf.freebase.com/key/wikipedia.en_title  "Random Person"    .
http://rdf.freebase.com/ns/m.72kgt2 http://rdf.freebase.com/key/wikipedia.en    "Random Alias" .
http://rdf.freebase.com/ns/m.72kgt2 http://rdf.freebase.com/key/fsfasasdfd.gh   "fasfasdaa"    .
http://rdf.freebase.com/ns/m.9z9z9z http://rdf.freebase.com/key/zzzzdsdzzz.zz   "zzzzzzzzzz"    .
```

### Indexing Freebase Entities

```
nohup sh target/appassembler/bin/IndexFreebaseEntity -collection FreebaseEntityCollection \
 -input /path/to/freebase/freebase-rdf-latest.gz \
 -index freebase.entities.index \
 > log.freebase &
```

#### Search

After indexing is done, you should be able to search for documents. Following the sample example:

```
sh target/appassembler/bin/SearchFreebaseEntity -index freebase.entities.index \
  -entity http://rdf.freebase.com/ns/m.02mjmr
```