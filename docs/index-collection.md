# IndexCollection

`IndexCollection` builds the index from document records.
Possible parameters are:

```
-collection (required)
```

Collection Class: indicates what type of the document is, should be one of [Trec|Gov2|CW09|CW12]

```
-input (required)
```

Path of the directory that holds the raw documents

```
-index (required)
```

Path of the directory that holds the output index. The directory will be replaced if already exists

```
-threads (required)
```

Using how many threads to build the index


```
-storePositions (optional)
```

Boolean switch to index -storePositions (default: false)


```
-storeDocvectors (optional)
```

Boolean switch to store document vectors (default: false).
_NOTICE:_ `-storePositions` MUST also be `true` in order to switch `-storeDocvectors` to `true`


```
-optimize (optional)
```

Boolean switch to optimize index (force merge) (default: false)


```
-doclimit (optional)
```

Maximum number of documents to index (-1 to index everything). This is useful especially for testing purpose (default: -1) 


```
-keepstopwords (optional)
```

Boolean switch to keep stopwords (default: false)