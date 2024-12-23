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

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.example.GroupWriteSupport;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Types;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.Rule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ParquetDenseVectorCollectionTest extends DocumentCollectionTest<ParquetDenseVectorCollection.Document> {
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    collectionPath = tempFolder.newFolder().toPath();
    collection = new ParquetDenseVectorCollection(collectionPath);
  }

  private void writeParquetFile(String filename, MessageType schema, List<Group> records) throws IOException {
    Path filePath = collectionPath.resolve(filename);
    Configuration conf = new Configuration();
    GroupWriteSupport.setSchema(schema, conf);
    try (ParquetWriter<Group> writer = ExampleParquetWriter.builder(new org.apache.hadoop.fs.Path(filePath.toString()))
        .withConf(conf)
        .build()) {
      for (Group record : records) {
        writer.write(record);
      }
    }
  }

  @Test
  public void testDoubleVectors() throws Exception {
    // Schema for double vectors
    MessageType schema = Types.buildMessage()
        .required(PrimitiveType.PrimitiveTypeName.BINARY).named("docid")
        .requiredGroup()
            .repeated(PrimitiveType.PrimitiveTypeName.DOUBLE).named("element")
        .named("vector")
        .named("test_schema");

    // Create a test record with double vector
    SimpleGroup record = new SimpleGroup(schema);
    record.add("docid", "doc1");
    Group vectorGroup = record.addGroup("vector");
    vectorGroup.add("element", 1.23456789);
    vectorGroup.add("element", 2.34567890);

    writeParquetFile("test_double.parquet", schema, List.of(record));

    // Test reading the file
    Iterator<ParquetDenseVectorCollection.Document> iter = collection.createFileSegment(collectionPath.resolve("test_double.parquet")).iterator();
    
    assertTrue(iter.hasNext());
    ParquetDenseVectorCollection.Document doc = iter.next();
    assertNotNull(doc);
    assertEquals("doc1", doc.id());
    // The vector should be converted to float, verify the values are approximately equal
    String contents = doc.contents();
    assertTrue(contents.contains("1.234"));  // Truncated due to float precision
    assertTrue(contents.contains("2.345"));
  }

  @Test
  public void testFloatVectors() throws Exception {
    // Schema for float vectors
    MessageType schema = Types.buildMessage()
        .required(PrimitiveType.PrimitiveTypeName.BINARY).named("docid")
        .requiredGroup()
            .repeated(PrimitiveType.PrimitiveTypeName.FLOAT).named("element")
        .named("vector")
        .named("test_schema");

    // Create a test record with float vector
    SimpleGroup record = new SimpleGroup(schema);
    record.add("docid", "doc1");
    Group vectorGroup = record.addGroup("vector");
    vectorGroup.add("element", 1.234f);
    vectorGroup.add("element", 2.345f);

    writeParquetFile("test_float.parquet", schema, List.of(record));

    // Test reading the file
    Iterator<ParquetDenseVectorCollection.Document> iter = collection.createFileSegment(collectionPath.resolve("test_float.parquet")).iterator();
    
    assertTrue(iter.hasNext());
    ParquetDenseVectorCollection.Document doc = iter.next();
    assertNotNull(doc);
    assertEquals("doc1", doc.id());
    String contents = doc.contents();
    assertTrue(contents.contains("1.234"));
    assertTrue(contents.contains("2.345"));
  }

  @Test(expected = IOException.class)
  public void testMissingVectorField() throws Exception {
    // Schema without vector field
    MessageType schema = Types.buildMessage()
        .required(PrimitiveType.PrimitiveTypeName.BINARY).named("docid")
        .named("test_schema");

    SimpleGroup record = new SimpleGroup(schema);
    record.add("docid", "doc1");

    writeParquetFile("test_missing_vector.parquet", schema, List.of(record));

    // This should throw an IOException when trying to read the missing vector field
    Iterator<ParquetDenseVectorCollection.Document> iter = collection.createFileSegment(collectionPath.resolve("test_missing_vector.parquet")).iterator();
    iter.next();
  }

  @Test(expected = IOException.class)
  public void testEmptyVector() throws Exception {
    // Schema for vectors
    MessageType schema = Types.buildMessage()
        .required(PrimitiveType.PrimitiveTypeName.BINARY).named("docid")
        .requiredGroup()
            .repeated(PrimitiveType.PrimitiveTypeName.FLOAT).named("element")
        .named("vector")
        .named("test_schema");

    // Create a test record with empty vector
    SimpleGroup record = new SimpleGroup(schema);
    record.add("docid", "doc1");
    record.addGroup("vector"); // Empty vector group

    writeParquetFile("test_empty_vector.parquet", schema, List.of(record));

    // This should throw an IOException when trying to read the empty vector
    Iterator<ParquetDenseVectorCollection.Document> iter = collection.createFileSegment(collectionPath.resolve("test_empty_vector.parquet")).iterator();
    iter.next();
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertTrue(doc.contents().contains(expected.get("vector")));
  }
} 