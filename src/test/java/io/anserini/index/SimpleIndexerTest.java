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

package io.anserini.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.collection.FileSegment;
import io.anserini.collection.JsonCollection;
import io.anserini.search.SimpleSearcher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SimpleIndexerTest extends LuceneTestCase {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(SimpleIndexer.class.getName(), Level.ERROR);
  }

  @Test
  public void testJsonDoc() throws Exception {
    JsonCollection.Document doc1 =
            JsonCollection.Document.fromString("{\"id\": \"0\", \"contents\": \"Document 0\"}");

    JsonCollection.Document doc2 =
            JsonCollection.Document.fromFields("0", "Document 0");

    JsonCollection.Document doc3 = new JsonCollection.Document(
            new ObjectMapper().createObjectNode()
                    .put("id", "0")
                    .put("contents", "Document 0"));

    assertEquals(doc1.raw(), doc2.raw());
    assertEquals(doc2.raw(), doc3.raw());
  }

  @Test
  public void testBasic1() throws Exception {
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);
    SimpleIndexer indexer = new SimpleIndexer(tempDir.toString());

    int cnt = 0;
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        indexer.addRawDocument(doc.raw());
        cnt++;
      }
      segment.close();
    }

    indexer.close();
    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }

  @Test
  public void testBasic2() throws Exception {
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);
    SimpleIndexer indexer = new SimpleIndexer(tempDir.toString());

    int cnt = 0;
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        indexer.addJsonDocument(JsonCollection.Document.fromString(doc.raw()));
        cnt++;
      }
      segment.close();
    }

    indexer.close();
    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }

  @Test
  public void testBasic3() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);
    SimpleIndexer indexer = new SimpleIndexer(tempDir.toString());

    int cnt = 0;
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        indexer.addJsonNode(
                mapper.createObjectNode().put("id", doc.id()).put("contents", doc.contents()));
        cnt++;
      }
      segment.close();
    }

    indexer.close();
    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }

  @Test
  public void testInitWithArgs() throws Exception {
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);
    SimpleIndexer indexer = new SimpleIndexer(new String[] {
        "-input", "",
        "-index", tempDir.toString(),
        "-collection", "JsonCollection",
        "-language", "sw",
        "-storePositions", "-storeDocvectors", "-storeRaw",
    });

    int cnt = 0;
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        indexer.addRawDocument(doc.raw());
        cnt++;
      }
      segment.close();
    }

    indexer.close();
    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    // Set language to sw so that same Analyzer is used for indexing & searching
    searcher.set_language("sw");
    SimpleSearcher.Result[] hits = searcher.search("1.", 10);

    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }

  @Test
  public void testBatch1() throws Exception {
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);
    List<String> docs = new ArrayList<>();
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        docs.add(doc.raw());
      }
      segment.close();
    }

    SimpleIndexer indexer = new SimpleIndexer(tempDir.toString(), 4);
    int cnt = indexer.addRawDocuments(docs.toArray(new String[0]));
    indexer.close();

    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }

  @Test
  public void testBatch2() throws Exception {
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);
    List<JsonCollection.Document> docs = new ArrayList<>();
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        docs.add(JsonCollection.Document.fromString(doc.raw()));
      }
      segment.close();
    }

    SimpleIndexer indexer = new SimpleIndexer(tempDir.toString(), 4);
    int cnt = indexer.addJsonDocuments(docs.toArray(new JsonCollection.Document[0]));
    indexer.close();

    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }

  @Test
  public void testBatch3() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);
    List<JsonNode> docs = new ArrayList<>();
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        docs.add(mapper.createObjectNode().put("id", doc.id()).put("contents", doc.contents()));
      }
      segment.close();
    }

    SimpleIndexer indexer = new SimpleIndexer(tempDir.toString(), 4);
    int cnt = indexer.addJsonNodes(docs.toArray(new JsonNode[0]));
    indexer.close();

    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }
}
