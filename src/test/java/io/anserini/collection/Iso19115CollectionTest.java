package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Iso19115CollectionTest extends DocumentCollectionTest<Iso19115Collection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();
    collectionPath = Paths.get("src/test/resources/sample_docs/Iso19115");
    collection = new Iso19115Collection(collectionPath);
    Path segment = Paths.get("src/test/resources/sample_docs/Iso19115/output.json");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("12957", Map.of("id", "12957", "title", "Test title", "abstract", "Test abstract"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("title"), ((Iso19115Collection.Document) doc).getTitle());
    assertEquals(expected.get("abstract"), ((Iso19115Collection.Document) doc).getAbstract());
  }
}
