Anserini
========

Build using Maven:

```
mvn clean package appassembler:assemble
```

Index Gov2 (count index):

```
sh target/appassembler/bin/IndexGov2 -dataDir /path/to/gov2/ \
 -indexPath lucene-index.gov2.cnt -threadCount 32 -docCountLimit -1 
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`.

