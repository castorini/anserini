# Manipulating Freebase with Anserini

## Indexing Freebase

```
mvn exec:java -Dexec.mainClass="io.anserini.kg.freebase.IndexFreebase" \
 -Dexec.args="-input /tuna1/collections/freebase/freebase-rdf-latest.gz -index freebase-index" > log.freebase &
```

The command above builds an index from the Freebase RDF dump (available as a single `.gz` file). Each document in the index corresponds to a single Freebase entity with 
a unique URI. The fields of this document correspond to the different predicates
and their objects. All fields are stored in the index.

## Searching Freebase

After indexing is done, there are two ways to search Freebase.
The simplest is lookup by `mid`:

```
mvn exec:java -Dexec.mainClass="io.anserini.kg.freebase.LookupFreebase" \
 -Dexec.args="-index freebase-index -mid fb:m.02mjmr"
```

The above command looks up the entity corresponding to "Barack Obama".

It is also possible to perform a free text search over the textual labels of the nodes, as follows:

```
mvn exec:java -Dexec.mainClass="io.anserini.kg.freebase.LookupFreebase" \
 -Dexec.args="-index freebase-index -query 'Barack Obama'"
```
