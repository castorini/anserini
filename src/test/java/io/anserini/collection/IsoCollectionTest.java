package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class IsoCollectionTest extends DocumentCollectionTest<IsoCollection.Document> {
  @Before
  public void setUp() throws Exception {
    collectionPath = Paths.get("src/test/resources/sample_docs/Iso19115");
    collection = new IsoCollection(collectionPath);
    Path segment = Paths.get("src/test/resources/sample_docs/Iso19115/output.json");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("12958", Map.of("id", "12958", "title", "Test title", "abstract", "Test abstract"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("title"), ((IsoCollection.Document) doc).getTitle());
    assertEquals(expected.get("abstract"), ((IsoCollection.Document) doc).getAbstract());
  }
}
