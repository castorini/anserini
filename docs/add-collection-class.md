### Add New Collection Class

This is basically add a new document parser so that the document can be recognized and indexed.
Basically the collection class does two things:

1. Walk through the input directory to find the files based on some rules, e.g. files that end with .gz
2. Read the file in order to find and return documents

The detailed steps are:

1. Add a new Collection class under package `io.anserini.collection`. 
This class should extends [Collection](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/collection/Collection.java) class.
2. Implement class `FileSegment` and function `getFileSegmentPaths`, `createFileSegment`. 
Take the [TrecCollection](https://github.com/castorini/Anserini/blob/master/src/main/java/io/anserini/collection/TrecCollection.java) as an example.
3. Add a new Document reader under package `io.anserini.document`. 
This class should extends [SourceDocument](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/document/SourceDocument.java) class.
4. Implement function `readNextRecord` and `parseRecord`. 
Take the [TrecDocument](https://github.com/castorini/Anserini/blob/master/src/main/java/io/anserini/document/TrecDocument.java) as an example.
 

