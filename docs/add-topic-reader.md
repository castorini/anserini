### Add New Topic Reader

If you have a query file that can not be read by one of the current topic reader then you can add a new one by your own.

The detailed steps are:

1. Add a new class under package `io.anserini.search.query`. 
This class should extends [TopicReader](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/search/query/TopicReader.java) class.
The name should be something like _MyOwnTopicReader_ where _MyOwn_ is the name of your collection class.
2. Implement `read` function. Take [TrecTopicReader](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/search/query/TrecTopicReader.java) as an example.
 

