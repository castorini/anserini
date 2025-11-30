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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.parquet.schema.Type.Repetition.REQUIRED;

/**
 * Unit tests for {@link FineWebCollection}.
 *
 * These tests focus on the behavior of {@link FineWebCollection.Document},
 * in particular how it extracts ids, contents, and metadata fields from
 * Parquet {@link Group} records, and how it generates fallback ids when
 * an explicit id field is not present.
 */
public class FineWebCollectionTest extends LuceneTestCase {

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
}


