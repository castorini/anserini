# Anserini generating training data from Freebase

### Indexing Freebase

```
nohup sh target/appassembler/bin/IndexRDF -collection RDFCollection \
 -input /path/to/freebase/freebase-rdf-latest.gz \
 -index freebase.index \
 > log.freebase &
```

The command above builds an index from the Freebase RDF dump.
Freebase RDF dump is available as a single `.gz` file 
that can be downloaded from [here](https://developers.google.com/freebase/).

Each document in the index corresponds to a single Freebase entity with 
a unique URI. The fields of this document correspond to the different predicates
and their objects. All fields are stored in the index.

#### Indexing specific predicates
In order to search for specific predicates, the predicate must be indexed.
There is an optional argument `-predicates` that accepts multiple values separated
by spaces.

An example command to index `date_of_birth` is as follows:

```
nohup sh target/appassembler/bin/IndexRDF -collection RDFCollection \
 -input /path/to/freebase/freebase-rdf-latest.gz \
 -index freebase.index \
 -predicates http://rdf.freebase.com/ns/people.person.date_of_birth \
 > log.freebase &
```


#### Search

After indexing is done, you should be able to search for documents.
The simplest search is retrieving all predicate values of a particular
subject as follows:

```
sh target/appassembler/bin/SearchRDF \
  -index freebase.index \
  -subject http://rdf.freebase.com/ns/m.02mjmr
```

Limiting to a particular predicate is done using an optional `-predicate` argument

```
sh target/appassembler/bin/SearchRDF \
  -index freebase.index \
  -subject http://rdf.freebase.com/ns/m.02mjmr \
  -predicate http://rdf.freebase.com/ns/people.person.date_of_birth
```

### Notes

* We index only using a single thread because the Freebase RDF dump sequentially lists all predicates of a single entity.
  We scan all predicates and objects of a particular subject before indexing it as a separate document.