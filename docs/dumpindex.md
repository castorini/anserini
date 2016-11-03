# DumpIndex

`DumpIndex` is a facility to print out various index statistics 
Possible parameters are:

```
-index (required)
```

Path of the index

```
-s (optional)
```

Print statistics for the Repository.
_NOTICE:_ number of unique terms is only available if the index is built using 1 thread, i.e. `IndexCollection -threads 1`

```
-t (optional)
```

Print term info: stemmed string, total counts in the collection, document counts

```
-di (optional: could be a list of strings)
```

Print the internal document IDs of documents. This is useful for the rest of the options below since they all take the interal document ID as the input


```
-dn (optional: positive integer)
```

Print the text representation of a document ID (default: 0)


```
-dt (optional: positive integer)
```

Print the text of a document (default: 0)
_NOTICE:_ available only if the raw documents are stored


```
-dv (optional: positive integer)
```

Print the document vector of a document (default: 0)
_NOTICE:_ available only if the doc vectors are stored, i.e. `IndexCollection -docvectors`