### Add New Collection Class

This is basically add a new document parser so that the document can be recognized and indexed.
Basically the collection class does two things:
1. Walk through the input directory to find the files based on some rules, e.g. files that end with .gz
2. Read the file in order to find and return documents

The detailed steps are:

1. Add a new class under package `io.anserini.index.collections`. 
This class should extends [Collection](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/index/collections/Collection.java) class.
The name should be something like _MyOwnCollection_ where _MyOwn_ is the name of your collection class.
The class will be instanced as 
```java
c = (Collection)Class.forName("io.anserini.index.collections."+collectionClass+"Collection").newInstance();
```
2. In the constructor define your own `skippedFilePrefix`, `allowedFilePrefix`, `skippedFileSuffix`, `allowedFileSuffix`, `skippedDirs`.
The [discoverFiles](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/index/collections/Collection.java#L40) relies on these sets to decide how to include/exclude files and folders.
3. Override function `prepareInput` and `finishInput`. 
`prepareInput` takes a file path as the argument and you can initialize the `BufferReader` (or something like that) there.
`finishInput` is called after the file is processed and you can close the `BufferReader` (or something like that) here.
4. Add a new record reader under package `io.anserini.document`. 
This class should extends [Indexable](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/document/Indexable.java) class.
Typically the function `next` in `Collection` (since `Collection` implements `Iterator`) can call the function in the record reader to read one document at a time.

Please take a look at [TrecCollection](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/index/collections/TrecCollection.java) and [TrecRecord](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/document/TrecRecord.java) for full example.
 

