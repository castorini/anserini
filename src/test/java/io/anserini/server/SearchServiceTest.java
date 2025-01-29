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

import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class SearchServiceTest {
  
  @Test
  public void testBasicSearch() throws Exception {
    SearchService service = new SearchService("msmarco-v1-passage");
    List<Map<String, Object>> results = service.search("Albert Einstein", 10);
    assertNotNull(results);
    assertTrue(results.size() <= 10);
  }

  @Test
  public void testInvalidSearchParameters() {
    SearchService service = new SearchService("msmarco-v1-passage");
    assertThrows(IllegalArgumentException.class, () -> {
      service.search("", 10);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      service.search("query", 0);
    });
  }

  @Test
  public void testHnswSearch() throws Exception {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    List<Map<String, Object>> results = service.search("test query", 5, 100, null, null);
    assertNotNull(results);
    assertTrue(results.size() <= 5);
  }

  @Test
  public void testSettingsOverrides() {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    service.setEfSearchOverride("200");
    assertEquals(200, (int) service.getEfSearchOverride());

    assertThrows(IllegalArgumentException.class, () -> {
      service.setEfSearchOverride("-1");
    });
  }

  @Test
  public void testGetDocument() throws Exception {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    List<Map<String, Object>> results = service.search("test query", 1);
    assertNotNull(results);
    if (!results.isEmpty()) {
      String docid = (String) results.get(0).get("docid");
      Map<String, Object> doc = service.getDocument(docid);
      assertNotNull(doc);
    }
  }

  @Test
  public void testInvalidIndex() {
    assertThrows(RuntimeException.class, () -> {
      new SearchService("nonexistent-index");
    });
  }
}