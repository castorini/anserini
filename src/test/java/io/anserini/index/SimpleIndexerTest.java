package io.anserini.index;

import io.anserini.collection.FileSegment;
import io.anserini.collection.JsonCollection;
import io.anserini.search.SearchCollection;
import io.anserini.search.SimpleSearcher;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleIndexerTest {

  @Test
  public void test1() throws IOException {
    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);

    SimpleIndexer indexer = new SimpleIndexer("tmp/");
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        System.out.println(doc.id() + "\t" + doc.contents());
        indexer.addDocument(doc.id(), doc.contents());
      }
      segment.close();
    }

    indexer.close();

    SimpleSearcher searcher = new SimpleSearcher("tmp/");
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    System.out.println(hits[0].docid + " " + hits[0].score);
    assertEquals(1, hits.length);

    searcher.close();
  }

}
