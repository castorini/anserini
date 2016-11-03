### Add New Topic Reader

If you have a query file that can not be read by one of the current topic reader then you can add a new one by your own.

The detailed steps are:

1. Add a new class under package `io.anserini.search.query`. 
This class should extends [TopicReader](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/search/query/TopicReader.java) class.
The name should be something like _MyOwnTopicReader_ where _MyOwn_ is the name of your collection class.
The class will be instanced as 
```java
TopicReader tr = (TopicReader)Class.forName("io.anserini.search.query."+searchArgs.topicReader+"TopicReader")
            .getConstructor(Path.class).newInstance(topicsFile);
```
2. Implement `read` function so that a sorted map of `{query id : query string}` is returned by reading the query file.

Please take a look at [TopicReader](https://github.com/lintool/Anserini/blob/master/src/main/java/io/anserini/search/query/TrecTopicReader.java) for full example.
 

