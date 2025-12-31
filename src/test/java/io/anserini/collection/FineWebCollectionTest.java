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
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Types;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.parquet.schema.Type.Repetition.REQUIRED;

/**
 * Unit tests and integration tests for {@link FineWebCollection}.
 *
 * Unit tests focus on the behavior of {@link FineWebCollection.Document},
 * in particular how it extracts ids, contents, and metadata fields from
 * Parquet {@link Group} records, and how it generates fallback ids when
 * an explicit id field is not present.
 *
 * Integration tests read from actual Parquet files to verify end-to-end
 * functionality including file parsing, iteration, and document extraction.
 */
public class FineWebCollectionTest extends LuceneTestCase {

  // ==========================================================================
  // Helper methods for unit tests (in-memory SimpleGroup construction)
  // ==========================================================================

  private MessageType createSchema(String... fieldNames) {
    Types.MessageTypeBuilder builder = Types.buildMessage();
    for (String field : fieldNames) {
      builder.addField(
          Types.primitive(PrimitiveTypeName.BINARY, REQUIRED)
              .as(LogicalTypeAnnotation.stringType())
              .named(field));
    }
    return builder.named("fineweb");
  }

  private SimpleGroup createGroup(MessageType schema) {
    return new SimpleGroup(schema);
  }

  // ==========================================================================
  // Unit tests: Test Document class behavior with in-memory SimpleGroup
  // ==========================================================================

  @Test
  public void testDocumentWithExplicitIdAndTextField() throws Exception {
    Path samplePath = Paths.get("src/test/resources/sample_docs/fineweb/doc1.txt");
    String text = Files.readString(samplePath, StandardCharsets.UTF_8).trim();

    MessageType schema = createSchema("id", "text", "url");
    SimpleGroup group = createGroup(schema);
    group.add("id", "fw-doc-1");
    group.add("text", text);
    group.add("url", "http://example.com/fineweb/1");

    Path segmentPath = Paths.get("shard_00000.parquet");
    FineWebCollection.Document doc =
        new FineWebCollection.Document(group, "id", null, segmentPath, 0L);

    assertEquals("fw-doc-1", doc.id());
    assertEquals(text, doc.contents());
    assertTrue(doc.indexable());

    // The metadata map should contain non-id, non-contents fields.
    assertTrue(doc.fields().containsKey("url"));
    assertEquals("http://example.com/fineweb/1", doc.fields().get("url"));

    String raw = doc.raw();
    assertNotNull(raw);
    assertTrue(raw.contains("\"id\""));
    assertTrue(raw.contains("\"text\""));
    assertTrue(raw.contains("\"url\""));
  }

  @Test
  public void testDocumentUsesAlternativeIdField() throws Exception {
    MessageType schema = createSchema("docid", "text");
    SimpleGroup group = createGroup(schema);
    group.add("docid", "DOC-123");
    group.add("text", "some fineweb text");

    Path segmentPath = Paths.get("shard_00001.parquet");
    FineWebCollection.Document doc =
        new FineWebCollection.Document(group, "id", null, segmentPath, 1L);

    // Even though the configured idField is "id", the implementation should
    // fall back to "docid" if present.
    assertEquals("DOC-123", doc.id());
    assertEquals("some fineweb text", doc.contents());
  }

  @Test
  public void testDocumentAutoGeneratesIdWhenMissing() throws Exception {
    Path samplePath = Paths.get("src/test/resources/sample_docs/fineweb/doc2.txt");
    String text = Files.readString(samplePath, StandardCharsets.UTF_8).trim();

    MessageType schema = createSchema("text");
    SimpleGroup group = createGroup(schema);
    group.add("text", text);

    Path segmentPath = Paths.get("shard_00000.parquet");
    long rowNumber = 5L;

    FineWebCollection.Document doc =
        new FineWebCollection.Document(group, "id", null, segmentPath, rowNumber);

    // When no explicit id-like field is found, the id should be generated from
    // the segment name and row number: shard_00000_5
    assertEquals("shard_00000_" + rowNumber, doc.id());
    assertEquals(text, doc.contents());
  }

  @Test
  public void testDocumentUsesCustomContentsField() throws Exception {
    MessageType schema = createSchema("id", "contents");
    SimpleGroup group = createGroup(schema);
    group.add("id", "fw-doc-contents");
    group.add("contents", "fineweb contents field text");

    Path segmentPath = Paths.get("shard_00002.parquet");
    FineWebCollection.Document doc =
        new FineWebCollection.Document(group, "id", "contents", segmentPath, 2L);

    assertEquals("fw-doc-contents", doc.id());
    assertEquals("fineweb contents field text", doc.contents());
  }

  @Test
  public void testMissingContentsThrows() throws Exception {
    MessageType schema = createSchema("id");
    SimpleGroup group = createGroup(schema);
    group.add("id", "fw-no-contents");

    Path segmentPath = Paths.get("shard_00003.parquet");
    FineWebCollection.Document doc =
        new FineWebCollection.Document(group, "id", null, segmentPath, 3L);

    expectThrows(RuntimeException.class, doc::contents);
  }

  // ==========================================================================
  // Integration tests: Read from actual Parquet files
  // ==========================================================================

  @Test
  public void testReadStandardParquetFile() throws IOException {
    // This parquet file has fields: id, text, url, language
    Path parquetPath = Paths.get("src/test/resources/sample_docs/fineweb/fineweb_standard.parquet");
    FineWebCollection collection = new FineWebCollection(parquetPath);

    List<FineWebCollection.Document> docs = new ArrayList<>();
    Map<String, String> docContents = new HashMap<>();
    Map<String, Map<String, String>> docMetadata = new HashMap<>();

    for (FileSegment<FineWebCollection.Document> segment : collection) {
      for (FineWebCollection.Document doc : segment) {
        docs.add(doc);
        docContents.put(doc.id(), doc.contents());
        docMetadata.put(doc.id(), new HashMap<>(doc.fields()));
      }
    }

    // Verify we read 3 documents
    assertEquals("Should read 2 documents from parquet file", 2, docs.size());

    // Verify document IDs
    assertTrue("Should contain fineweb-doc-001", docContents.containsKey("fineweb-doc-001"));
    assertTrue("Should contain fineweb-doc-003", docContents.containsKey("fineweb-doc-003"));

    // Verify content of first document
    assertEquals(
        "This is the first test document for FineWeb collection testing.",
        docContents.get("fineweb-doc-001"));

    // Verify content with special characters (third document)
    assertTrue(
        "Third document should contain special characters",
        docContents.get("fineweb-doc-003").contains("café"));

    // Verify metadata fields are extracted
    assertEquals("http://example.com/page1", docMetadata.get("fineweb-doc-001").get("url"));
    assertEquals("en", docMetadata.get("fineweb-doc-001").get("language"));
    assertEquals("multi", docMetadata.get("fineweb-doc-003").get("language"));

    // Verify all documents are indexable
    for (FineWebCollection.Document doc : docs) {
      assertTrue("All documents should be indexable", doc.indexable());
      assertNotNull("Raw content should not be null", doc.raw());
    }
  }

  @Test
  public void testReadParquetWithAlternativeFieldNames() throws IOException {
    // This parquet file uses 'docid' instead of 'id' and 'content' instead of 'text'
    Path parquetPath = Paths.get("src/test/resources/sample_docs/fineweb/fineweb_alternative_fields.parquet");
    FineWebCollection collection = new FineWebCollection(parquetPath);

    List<FineWebCollection.Document> docs = new ArrayList<>();
    for (FileSegment<FineWebCollection.Document> segment : collection) {
      for (FineWebCollection.Document doc : segment) {
        docs.add(doc);
      }
    }

    // Verify we read 2 documents
    assertEquals("Should read 2 documents from parquet file", 2, docs.size());

    // The collection should fall back to 'docid' for ID and 'content' for contents
    assertEquals("alt-doc-001", docs.get(0).id());
    assertEquals("alt-doc-002", docs.get(1).id());

    // Verify contents are read from 'content' field
    assertTrue(docs.get(0).contents().contains("alternative field names"));
    assertTrue(docs.get(1).contents().contains("docid field"));

    // Verify 'source' metadata field
    assertEquals("web", docs.get(0).fields().get("source"));
    assertEquals("crawl", docs.get(1).fields().get("source"));
  }

  @Test
  public void testCollectionIteration() throws IOException {
    // Test that we can iterate through all segments properly
    Path parquetPath = Paths.get("src/test/resources/sample_docs/fineweb/fineweb_standard.parquet");
    FineWebCollection collection = new FineWebCollection(parquetPath);

    int segmentCount = 0;
    int totalDocCount = 0;

    for (FileSegment<FineWebCollection.Document> segment : collection) {
      segmentCount++;
      for (FineWebCollection.Document doc : segment) {
        totalDocCount++;
        // Verify each document has required fields
        assertNotNull("Document ID should not be null", doc.id());
        assertFalse("Document ID should not be empty", doc.id().isEmpty());
        assertNotNull("Document contents should not be null", doc.contents());
        assertFalse("Document contents should not be empty", doc.contents().isEmpty());
      }
      segment.close();
    }

    assertEquals("Should have 1 segment (1 parquet file)", 1, segmentCount);
    assertEquals("Should have 2 documents total", 2, totalDocCount);
  }

  @Test
  public void testRawJsonOutput() throws IOException {
    Path parquetPath = Paths.get("src/test/resources/sample_docs/fineweb/fineweb_standard.parquet");
    FineWebCollection collection = new FineWebCollection(parquetPath);

    for (FileSegment<FineWebCollection.Document> segment : collection) {
      for (FineWebCollection.Document doc : segment) {
        String raw = doc.raw();
        assertNotNull("Raw JSON should not be null", raw);
        assertTrue("Raw JSON should start with '{'", raw.startsWith("{"));
        assertTrue("Raw JSON should end with '}'", raw.endsWith("}"));
        assertTrue("Raw JSON should contain id field", raw.contains("\"id\""));
        assertTrue("Raw JSON should contain text field", raw.contains("\"text\""));
      }
      segment.close();
    }
  }
}


