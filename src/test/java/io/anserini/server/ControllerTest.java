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

package io.anserini.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.anserini.index.IndexInfo;

public class ControllerTest {

  @Test
  public void testSearch() throws Exception {
    Controller controller = new Controller();
    Map<String, Object> results = controller.searchIndex("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw", "Albert Einstein", 10, "", null, null, null);
    
    assertNotNull("Search: Expected non-null results from searchIndex with valid parameters", results);
    
    Object candidatesObj = results.get("candidates");
    assertNotNull("Search: Expected 'candidates' key in results to be non-null", candidatesObj);
    assertTrue("Search: Expected 'candidates' to be an instance of List but found " + candidatesObj.getClass().getSimpleName(), candidatesObj instanceof List);
    
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> candidates = (List<Map<String, Object>>) candidatesObj;
    assertEquals("Search: Expected candidate list size to be 10 but got " + candidates.size(), 10, candidates.size());
    
    if (!candidates.isEmpty()) {
      Object docid = candidates.get(0).get("docid");
      assertNotNull("Search: Expected first candidate's 'docid' to be non-null", docid);
      assertEquals("Search: Expected first candidate's docid to be '3553430' but got " + docid, "3553430", docid);
    }
    
    assertThrows("Search: Calling searchIndex with a null index should throw IllegalArgumentException",
        IllegalArgumentException.class, () -> controller.searchIndex(null, "Albert Einstein", 10, "", null, null, null));
  }

  @Test
  public void testIndexNotFound() throws Exception {
    Controller controller = new Controller();
    assertThrows("IndexNotFound: Calling searchIndex with an invalid index 'nonexistent-index' should throw IllegalArgumentException",
        IllegalArgumentException.class, () -> controller.searchIndex("nonexistent-index", "Albert Einstein", 10, "", null, null, null));
  }

  @Test
  public void testGetIndexStatusValid() {
  Controller controller = new Controller();
    Map<String, Object> status = controller.getIndexStatus("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    assertNotNull("GetIndexStatus: Expected non-null status map for valid index 'beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw'", status);
    assertTrue("GetIndexStatus: Expected status map to contain key 'cached'", status.containsKey("cached"));
  }

  @Test
  public void testGetIndexStatusInvalidIndex() {
    Controller controller = new Controller();
    assertThrows("GetIndexStatus: Calling getIndexStatus with invalid index 'nonexistent-index' should throw IllegalArgumentException",
      IllegalArgumentException.class,() -> controller.getIndexStatus("nonexistent-index"));
  }

  @Test
  public void testListIndexes() throws Exception {
    Controller controller = new Controller();
    Map<String, Map<String, Object>> indexes = controller.listIndexes();
    int expectedIndexCount = IndexInfo.values().length;
    int actualIndexCount = indexes.size();
    assertEquals("ListIndexes: Expected " + expectedIndexCount + " indexes but found " + actualIndexCount, expectedIndexCount, actualIndexCount);
  }

  @Test
  public void testDocumentRetrieval() throws Exception {
    Controller controller = new Controller();
    
    Map<String, Object> doc = controller.getDocument("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw", "3553430");
    assertNotNull("Document retrieval: Expected non-null document for valid docid", doc);
    assertThrows("Document retrieval: Invalid index should throw IllegalArgumentException",
        IllegalArgumentException.class, () -> controller.getDocument("nonexistent-index", "3553430"));
  }

  @Test
  public void testIndexDetailsInListIndexes() throws Exception {
    Controller controller = new Controller();
    Map<String, Map<String, Object>> indexes = controller.listIndexes();
    
    for (Map.Entry<String, Map<String, Object>> entry : indexes.entrySet()) {
    Map<String, Object> indexInfo = entry.getValue();
      assertTrue("Index details: Should contain filename",  indexInfo.containsKey("filename"));
      assertTrue("Index details: Should contain corpus",  indexInfo.containsKey("corpus"));
      assertTrue("Index details: Should contain model",  indexInfo.containsKey("model"));
      assertTrue("Index details: Should contain urls",  indexInfo.containsKey("urls"));
      assertTrue("Index details: Should contain md5",  indexInfo.containsKey("md5"));
      assertTrue("Index details: Should contain indexType",  indexInfo.containsKey("indexType"));
      assertTrue("Index details: Should contain encoder",  indexInfo.containsKey("encoder"));
      assertTrue("Index details: Should contain queryGenerator",  indexInfo.containsKey("queryGenerator"));
      assertTrue("Index details: Should contain invertedIndex",  indexInfo.containsKey("invertedIndex"));
    }
  }
}
