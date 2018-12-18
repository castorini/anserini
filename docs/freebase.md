# Manipulating Freebase with Anserini

This page describes working with final Freebase data dump from 2015, downloadable [here](https://developers.google.com/freebase/).
For verification, the dump has MD5 checksum of `bc1be939be37c9aa9219f73923f60a3d`.

Let's first see how large it is:

```
$ gunzip -c freebase-rdf-latest.gz | wc
[after a while...]
3130753066 15151861316 425229008315
```

Interestingly, there are 3.13 billion triples in the dump, which is different from the 1.9 billion count referenced in the Freebase page.

## Indexing Freebase

There are two ways to main ways to index Freebase using Lucene.

The first is to treat each Freebase entity as a Lucene document.
In this case, property and values are stored as fields in each document.
For this, use the following command:

```
$ mvn exec:java -Dexec.mainClass="io.anserini.kg.IndexFreebase" -Dexec.args="-input freebase-rdf-latest.gz -index freebase-index-nodes-all"
```

This will yield 3,130,753,066 triples and 125,144,313 documents.

There's an option to keep only English literals objects (and everything else):

```
$ mvn exec:java -Dexec.mainClass="io.anserini.kg.IndexFreebase" -Dexec.args="-input freebase-rdf-latest.gz -index freebase-index-nodes-en -langEnOnly"
```

This will yield 1,922,701,053 triples and 125,144,313 documents added.

In the second approach, each triple forms an individual Lucene document. Run as follows:

```
$ mvn exec:java -Dexec.mainClass="io.anserini.kg.IndexFreebase" -Dexec.args="-input freebase-rdf-latest.gz -index freebase-index-triples-all -triples"
```

The issue, though, is that there are 3.13 billion triples, larger than the max 2.1 billion documents Lucene can store.
The program catches this exception and because of the error handling, the program does manage to successfully add 2,147,483,519 documents (the max) to the index.

However, if we storing each triple as a document and keep only English literal objects, we'll be fine:

```
$ mvn exec:java -Dexec.mainClass="io.anserini.kg.IndexFreebase" -Dexec.args="-input /tuna1/collections/freebase/freebase-rdf-latest.gz -index freebase-index-triples-en -triples -langEnOnly"
```

The yields a total of 1,922,701,053 triples/documents.

## Searching Freebase

Working with the "node as document" index, there are two ways to search Freebase.
The simplest is lookup by `mid`:

```
mvn exec:java -Dexec.mainClass="io.anserini.kg.LookupFreebaseNodes" \
 -Dexec.args="-index freebase-index-nodes-en -mid fb:m.02mjmr"
```

The above command looks up the entity corresponding to "Barack Obama".

It is also possible to perform a free text search over the textual labels of the nodes, as follows:

```
mvn exec:java -Dexec.mainClass="io.anserini.kg.LookupFreebaseNodes" \
 -Dexec.args="-index freebase-index-nodes-en -query 'Barack Obama'"
```

With the "triple as document" index, search for triples as follows:

```
mvn exec:java -Dexec.mainClass="io.anserini.kg.LookupFreebaseTriples" \
 -Dexec.args="-index freebase-index-triples-en -s fb:m.02mjmr -hits 1000"
```

Use the `-s` to find matching subjects, `-p` to find matching predicates, and `-o` to find matching objects.
These three options can be used in different combinations.

For example, to find all mappings to IMDB:

```
mvn exec:java -Dexec.mainClass="io.anserini.kg.LookupFreebaseTriples" \
 -Dexec.args="-index freebase-index-triples-en -p fbkey:authority.imdb.name -hits 1000"
```
