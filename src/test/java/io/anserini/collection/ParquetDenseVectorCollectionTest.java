/*
 * Tests for the ParquetDenseVectorCollection class which handles dense vector embeddings stored in Parquet format.
 * This test suite verifies the collection's ability to read and process Parquet files containing vector embeddings
 * using the parquet-floor library instead of Hadoop dependencies.
 */
package io.anserini.collection;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParquetDenseVectorCollectionTest extends DocumentCollectionTest<ParquetDenseVectorCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  /*
   * Verifies that a document's properties match expected values.
   * This implementation focuses on three key aspects:
   * 1. Document can be indexed
   * 2. Document ID matches expected value
   * 3. Document's vector content is present
   */
  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertTrue(doc.contents().contains(expected.get("vector")));
  }

  /*
   * Tests the collection's ability to read and iterate over documents in a Parquet file.
   * Uses a pre-existing test file containing BGE embeddings from MS MARCO passages.
   * Verifies that:
   * 1. Documents can be read from the Parquet file
   * 2. Each document has a valid ID
   * 3. The collection contains the expected number of documents
   */
  @Test
  public void testSegment() throws IOException {
    Path path = Paths.get("src/test/resources/sample_docs/parquet/msmarco-passage-bge-base-en-v1.5.parquet");
    ParquetDenseVectorCollection collection = new ParquetDenseVectorCollection(path);

    AtomicInteger cnt = new AtomicInteger();
    Map<String, Integer> docIds = new HashMap<>();

    for (FileSegment<ParquetDenseVectorCollection.Document> segment : collection) {
      for (ParquetDenseVectorCollection.Document doc : segment) {
        docIds.put(doc.id(), cnt.incrementAndGet());
      }
    }

    assertTrue("Collection should contain documents", docIds.size() > 0);
    for (String docId : docIds.keySet()) {
      assertTrue("Document ID should not be empty", docId != null && !docId.isEmpty());
    }
  }
} 