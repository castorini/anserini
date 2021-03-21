package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class C4CollectionTest extends DocumentCollectionTest<C4Collection.Document> {
    @Before
    public void setUp() throws Exception {
        super.setUp();
        collectionPath = Paths.get("src/test/resources/sample_docs/c4");
        collection = new C4Collection(collectionPath);
        Path segment = Paths.get("src/test/resources/sample_docs/c4/test.json");

        segmentPaths.add(segment);
        segmentDocCounts.put(segment, 2);

        totalSegments = 1;
        totalDocs = 2;
        expected.put("test.json-0", Map.of("id", "test.json-0", "text", "test text", "timestamp", "1556008007", "url", "http://www.test.com"));
        expected.put("test.json-1", Map.of("id", "test.json-1", "text", "test text2", "timestamp", "1587630407", "url", "http://www.test2.com"));
    }

    @Override
    void checkDocument(SourceDocument doc, Map<String, String> expected) {
        assertTrue(doc.indexable());
        assertEquals(expected.get("id"), doc.id());
        assertEquals(expected.get("text"), doc.contents());
        assertEquals((long) Long.valueOf(expected.get("timestamp")), ((C4Collection.Document) doc).getTimestamp());
        assertEquals(expected.get("url"), ((C4Collection.Document) doc).getUrl());
    }
}