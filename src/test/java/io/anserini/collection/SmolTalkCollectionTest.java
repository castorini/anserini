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

import org.apache.lucene.tests.util.LuceneTestCase;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link SmolTalkCollection}.
 *
 * Tests verify that the collection correctly:
 * - Reads Parquet files with nested message arrays
 * - Extracts user-assistant Q&A pairs as separate documents
 * - Auto-generates document IDs in format {filename}_{row}_{pair_index}
 * - Extracts question, answer, and source fields
 */
public class SmolTalkCollectionTest extends LuceneTestCase {

  @Test
  public void testReadStandardParquetFile() throws IOException {
    Path parquetPath = Paths.get("src/test/resources/sample_docs/smoltalk/smoltalk_standard.parquet");
    SmolTalkCollection collection = new SmolTalkCollection(parquetPath);

    List<SmolTalkCollection.Document> docs = new ArrayList<>();
    Map<String, String> docContents = new HashMap<>();
    Map<String, Map<String, String>> docFields = new HashMap<>();

    for (FileSegment<SmolTalkCollection.Document> segment : collection) {
      for (SmolTalkCollection.Document doc : segment) {
        docs.add(doc);
        docContents.put(doc.id(), doc.contents());
        docFields.put(doc.id(), new HashMap<>(doc.fields()));
      }
    }

    // Should have 5 Q&A pairs total:
    // Row 0: 1 pair, Row 1: 3 pairs, Row 2: 1 pair
    assertEquals("Should extract 5 Q&A pairs from parquet file", 5, docs.size());

    // Verify document IDs follow expected format
    assertTrue("First doc should be smoltalk_standard_0_0",
        docContents.containsKey("smoltalk_standard_0_0"));
    assertTrue("Second row first pair should be smoltalk_standard_1_0",
        docContents.containsKey("smoltalk_standard_1_0"));
    assertTrue("Second row third pair should be smoltalk_standard_1_2",
        docContents.containsKey("smoltalk_standard_1_2"));
    assertTrue("Third row should be smoltalk_standard_2_0",
        docContents.containsKey("smoltalk_standard_2_0"));
  }

  @Test
  public void testDocumentContent() throws IOException {
    Path parquetPath = Paths.get("src/test/resources/sample_docs/smoltalk/smoltalk_standard.parquet");
    SmolTalkCollection collection = new SmolTalkCollection(parquetPath);

    Map<String, SmolTalkCollection.Document> docsById = new HashMap<>();
    for (FileSegment<SmolTalkCollection.Document> segment : collection) {
      for (SmolTalkCollection.Document doc : segment) {
        docsById.put(doc.id(), doc);
      }
    }

    // Check first document (simple Q&A about France)
    SmolTalkCollection.Document doc1 = docsById.get("smoltalk_standard_0_0");
    assertNotNull("Document smoltalk_standard_0_0 should exist", doc1);
    assertTrue("Contents should contain 'Question:'", doc1.contents().contains("Question:"));
    assertTrue("Contents should contain 'Answer:'", doc1.contents().contains("Answer:"));
    assertTrue("Contents should mention France", doc1.contents().contains("France"));
    assertTrue("Contents should mention Paris", doc1.contents().contains("Paris"));

    // Check question and answer fields
    assertEquals("What is the capital of France?", doc1.getQuestion());
    assertEquals("The capital of France is Paris.", doc1.getAnswer());
    assertEquals("test-source-1", doc1.getSource());
  }

  @Test
  public void testMultiTurnConversation() throws IOException {
    Path parquetPath = Paths.get("src/test/resources/sample_docs/smoltalk/smoltalk_standard.parquet");
    SmolTalkCollection collection = new SmolTalkCollection(parquetPath);

    Map<String, SmolTalkCollection.Document> docsById = new HashMap<>();
    for (FileSegment<SmolTalkCollection.Document> segment : collection) {
      for (SmolTalkCollection.Document doc : segment) {
        docsById.put(doc.id(), doc);
      }
    }

    // Row 1 should have 3 Q&A pairs about Python learning
    SmolTalkCollection.Document pair0 = docsById.get("smoltalk_standard_1_0");
    SmolTalkCollection.Document pair1 = docsById.get("smoltalk_standard_1_1");
    SmolTalkCollection.Document pair2 = docsById.get("smoltalk_standard_1_2");

    assertNotNull("First pair should exist", pair0);
    assertNotNull("Second pair should exist", pair1);
    assertNotNull("Third pair should exist", pair2);

    // Verify content of each pair
    assertTrue("First pair should be about learning Python",
        pair0.getQuestion().contains("learn Python"));
    assertTrue("Second pair should be about resources",
        pair1.getQuestion().contains("resources"));
    assertTrue("Third pair should be about time to proficiency",
        pair2.getQuestion().contains("proficient"));

    // All should have same source
    assertEquals("test-source-2", pair0.getSource());
    assertEquals("test-source-2", pair1.getSource());
    assertEquals("test-source-2", pair2.getSource());
  }

  @Test
  public void testDocumentIndexable() throws IOException {
    Path parquetPath = Paths.get("src/test/resources/sample_docs/smoltalk/smoltalk_standard.parquet");
    SmolTalkCollection collection = new SmolTalkCollection(parquetPath);

    for (FileSegment<SmolTalkCollection.Document> segment : collection) {
      for (SmolTalkCollection.Document doc : segment) {
        assertTrue("All documents should be indexable", doc.indexable());
        assertNotNull("Document ID should not be null", doc.id());
        assertFalse("Document ID should not be empty", doc.id().isEmpty());
        assertNotNull("Document contents should not be null", doc.contents());
        assertFalse("Document contents should not be empty", doc.contents().isEmpty());
      }
    }
  }

  @Test
  public void testRawJsonOutput() throws IOException {
    Path parquetPath = Paths.get("src/test/resources/sample_docs/smoltalk/smoltalk_standard.parquet");
    SmolTalkCollection collection = new SmolTalkCollection(parquetPath);

    for (FileSegment<SmolTalkCollection.Document> segment : collection) {
      for (SmolTalkCollection.Document doc : segment) {
        String raw = doc.raw();
        assertNotNull("Raw JSON should not be null", raw);
        assertTrue("Raw JSON should start with '{'", raw.startsWith("{"));
        assertTrue("Raw JSON should end with '}'", raw.endsWith("}"));
        assertTrue("Raw JSON should contain id field", raw.contains("\"id\""));
        assertTrue("Raw JSON should contain question field", raw.contains("\"question\""));
        assertTrue("Raw JSON should contain answer field", raw.contains("\"answer\""));
      }
      segment.close();
    }
  }

  @Test
  public void testCollectionIteration() throws IOException {
    Path parquetPath = Paths.get("src/test/resources/sample_docs/smoltalk/smoltalk_standard.parquet");
    SmolTalkCollection collection = new SmolTalkCollection(parquetPath);

    int segmentCount = 0;
    int totalDocCount = 0;

    for (FileSegment<SmolTalkCollection.Document> segment : collection) {
      segmentCount++;
      for (@SuppressWarnings("unused") SmolTalkCollection.Document doc : segment) {
        totalDocCount++;
      }
      segment.close();
    }

    assertEquals("Should have 1 segment (1 parquet file)", 1, segmentCount);
    assertEquals("Should have 5 Q&A pairs total", 5, totalDocCount);
  }
}

