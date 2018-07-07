# IndexUtils

`IndexUtils` is a facility to print out various index statistics
Possible parameters are:

```
-index (required)
```

Path of the index

```
-stats
```

Print index statistics.
_NOTICE:_ number of unique terms is only available if the index is built using 1 thread, i.e. `IndexCollection -threads 1`

```
-printTermInfo term
```

Print term info: stemmed string, total counts in the collection, document counts, etc.

```
-convertDocidToLuceneDocid (external docid)
```

Converts a collection lookupDocid (in the raw corpus) to a Lucene internal lookupDocid (an non-deterministic integer)


```
-convertLuceneDocidToDocid (docid [Integer])
```

Converts to a Lucene internal lookupDocid to a collection lookupDocid  (default: 0)


```
-dumpRawDoc docid
```

Dumps raw document.
_NOTICE:_ available only if the raw documents are stored with indexing option `-storeRawDocs`


```
-dumpSentences docid
```

Splits the fetched document into sentences (if stored in the index).
_NOTICE:_ available only if the raw documents are stored with indexing option `-storeRawDocs`


```
-printDocvector docid
```

Prints the document vector of a document
_NOTICE:_ available only if the raw documents are stored with indexing option `-storeDocvectors`
