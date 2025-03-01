/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

public class ParquetDenseVectorCollectionTest extends DocumentCollectionTest<ParquetDenseVectorCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertTrue(doc.contents().contains(expected.get("vector")));
  }

  @Test
  public void testDoubleSegment() throws IOException {
    Path path = Paths.get("src/test/resources/sample_docs/parquet/msmarco-passage-bge-base-en-v1.5.parquet-double");
    ParquetDenseVectorCollection collection = new ParquetDenseVectorCollection(path);

    AtomicInteger cnt = new AtomicInteger();
    Map<String, Integer> docIds = new HashMap<>();

    for (FileSegment<ParquetDenseVectorCollection.Document> segment : collection) {
      for (ParquetDenseVectorCollection.Document doc : segment) {
        docIds.put(doc.id(), cnt.incrementAndGet());
        String contents = doc.contents();
        assertTrue("Vector content should not be empty", contents != null && !contents.isEmpty());
        contents = contents.replaceAll("[\\[\\]]", "");
        String[] values = contents.split(",");
        assertEquals("Vector should have 768 dimensions", 768, values.length);
        for (String value : values) {
          try {
            Double.parseDouble(value.trim());
          } catch (NumberFormatException e) {
            throw new AssertionError("Vector value is not a valid number: " + value);
          }
        }
      }
    }

    assertEquals("Collection should contain exactly 10 documents", 10, docIds.size());
    for (String docId : docIds.keySet()) {
      assertTrue("Document ID should not be empty", docId != null && !docId.isEmpty());
    }
  }

  @Test
  public void testFloatSegment() throws IOException {
    Path path = Paths.get("src/test/resources/sample_docs/parquet/msmarco-passage-bge-base-en-v1.5.parquet-float");
    ParquetDenseVectorCollection collection = new ParquetDenseVectorCollection(path);

    AtomicInteger cnt = new AtomicInteger();
    Map<String, Integer> docIds = new HashMap<>();

    for (FileSegment<ParquetDenseVectorCollection.Document> segment : collection) {
      for (ParquetDenseVectorCollection.Document doc : segment) {
        docIds.put(doc.id(), cnt.incrementAndGet());
        String contents = doc.contents();
        assertTrue("Vector content should not be empty", contents != null && !contents.isEmpty());
        contents = contents.replaceAll("[\\[\\]]", "");
        String[] values = contents.split(",");
        assertEquals("Vector should have 768 dimensions", 768, values.length);
        for (String value : values) {
          try {
            Float.parseFloat(value.trim());
          } catch (NumberFormatException e) {
            throw new AssertionError("Vector value is not a valid number: " + value);
          }
        }
      }
    }

    assertEquals("Collection should contain exactly 10 documents", 10, docIds.size());
    for (String docId : docIds.keySet()) {
      assertTrue("Document ID should not be empty", docId != null && !docId.isEmpty());
    }
  }

  @Test
  public void testSnowflakeParquetFormat() throws IOException {
    Path path = Paths.get("src/test/resources/sample_docs/parquet/snowflake-msmarco-arctic-embed/snowflake.parquet");
  
  ParquetDenseVectorCollection collection = new ParquetDenseVectorCollection(path).withDocidField("doc_id").withVectorField("embedding").withNormalizeVectors(true);

  AtomicInteger cnt = new AtomicInteger();
  Map<String, Integer> docIds = new HashMap<>();

  for (FileSegment<ParquetDenseVectorCollection.Document> segment : collection) {
    for (ParquetDenseVectorCollection.Document doc : segment) {
      docIds.put(doc.id(), cnt.incrementAndGet());
      String contents = doc.contents();
      assertTrue("Vector content should not be empty", contents != null && !contents.isEmpty());
      contents = contents.replaceAll("[\\[\\]]", "");
      String[] values = contents.split(",");

      assertEquals("Vector should have 1024 dimensions", 1024, values.length);

      double sumSquares = 0.0;
      for (String value : values) {
        try {
          double val = Double.parseDouble(value.trim());
          sumSquares += val * val;
        } catch (NumberFormatException e) {
          throw new AssertionError("Vector value is not a valid number: " + value);
        }
      }
      assertEquals("Vectors should be normalized to unit length", 1.0, Math.sqrt(sumSquares), 0.01);
    }
  }

  assertEquals("Collection should contain the expected 3 documents", 3, docIds.size());
  
}
}