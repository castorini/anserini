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

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 /**
  * Tests for the ParquetDenseVectorCollection class which handles dense vector embeddings stored in Parquet format.
  * This test suite verifies the collection's ability to read and process Parquet files containing vector embeddings
  * using the parquet-floor library instead of Hadoop dependencies.
  */
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
    * 4. We test both double and float segments
    */
   @Test
   public void testDoubleSegment() throws IOException {
     Path path = Paths.get("src/test/resources/sample_docs/parquet/msmarco-passage-bge-base-en-v1.5.parquet-double");
     ParquetDenseVectorCollection collection = new ParquetDenseVectorCollection(path);

     AtomicInteger cnt = new AtomicInteger();
     Map<String, Integer> docIds = new HashMap<>();

     for (FileSegment<ParquetDenseVectorCollection.Document> segment : collection) {
       for (ParquetDenseVectorCollection.Document doc : segment) {
         docIds.put(doc.id(), cnt.incrementAndGet());
         // Verify vector format - should be a comma-separated list of numbers
         String contents = doc.contents();
         assertTrue("Vector content should not be empty", contents != null && !contents.isEmpty());
         String[] values = contents.split(",");
         assertEquals("Vector should have 768 dimensions", 768, values.length);
         // Verify each value can be parsed as a number
         for (String value : values) {
           try {
             Double.parseDouble(value.trim());
           } catch (NumberFormatException e) {
             throw new AssertionError("Vector value is not a valid number: " + value);
           }
         }
       }
     }

     assertEquals("Collection should contain exactly 18 documents", 18, docIds.size());
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

     assertEquals("Collection should contain exactly 18 documents", 18, docIds.size());
     for (String docId : docIds.keySet()) {
       assertTrue("Document ID should not be empty", docId != null && !docId.isEmpty());
     }
   }
 }