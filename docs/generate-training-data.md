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


### Search Freebase

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

#### Notes

* We index only using a single thread because the Freebase RDF dump sequentially lists all predicates of a single entity.
  We scan all predicates and objects of a particular subject before indexing it as a separate document.

---

### Generate training data

The following command generates positive training examples from a knowledge base, e.g., Freebase:

```
nohup sh target/appassembler/bin/TrainingDataGenerator \
 -index /path/to/freebase_index \
 -property birthdate \
 -output output_file.tsv \
 > log.examples &
```

The `-index` parameter is the path to the Freebase index folder.
The `-property` argument determines which property to generate training data for.
The training data is written to `-output` parameter in a TSV format.
The output file is in the following format:
```
/m/entity_1    entity_1_label    val1
/m/entity_2    entity_2_label    val2_a
/m/entity_2    entity_2_label    val2_b
/m/entity_3    entity_3_label    val3
```
Where `entity_i` is the freebase id (not URI) of the entity, usually in the format `/m/abcdef`.
The second column has the Freebase English label for this entity.
The value is the property value for the entity.
For some properties that represent relationships, e.g., `spouse` relationship,
the value in the third column can be an entity URI,
and a fourth column would have the label of the second entity.

#### Supported properties

You can choose which property to generate training data for using the `-property` argument.
Currently, only `birthdate` is supported.

##### Adding more properties
In order to add more properties, the code in `TrainingDataGenerator` class needs to be modified
to retrieve the values from the Freebase index.
