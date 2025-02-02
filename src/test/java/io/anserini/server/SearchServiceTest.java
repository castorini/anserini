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

public class SearchServiceTest {

  @Test
  public void testBasicSearch() throws Exception {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    List<Map<String, Object>> results = service.search("Albert Einstein", 10);
    assertNotNull("BasicSearch: search('Albert Einstein', 10) returned null results", results);
    assertTrue("BasicSearch: Expected results size <= 10 but got " + results.size(), results.size() <= 10);
  }

  @Test
  public void testInvalidSearchParameters() {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    assertThrows("InvalidSearchParameters: search(\"\") should throw IllegalArgumentException (query must be non-empty)",
        IllegalArgumentException.class, () -> service.search("", 10, 100, null, null));
    assertThrows("InvalidSearchParameters: search('query', 0) should throw IllegalArgumentException (result count must be > 0)",
        IllegalArgumentException.class, () -> service.search("query", 0, 100, null, null));
  }

  @Test
  public void testEncoderOverrides() {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    
    assertThrows("EncoderOverrides: setEncoderOverride(null) should throw IllegalArgumentException (encoder parameter must be non-null)",
        IllegalArgumentException.class, () -> service.setEncoderOverride(null));
    assertThrows("EncoderOverrides: setEncoderOverride(\"\") should throw IllegalArgumentException (encoder parameter must be non-empty)",
        IllegalArgumentException.class, () -> service.setEncoderOverride(""));
    assertThrows("EncoderOverrides: setEncoderOverride('someOtherEncoder') is not supported for beir-v1; expected IllegalArgumentException",
        IllegalArgumentException.class, () -> service.setEncoderOverride("someOtherEncoder"));
  }

  @Test
  public void testQueryOverrides() {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    
    assertThrows("QueryOverrides: setQueryGeneratorOverride(null) should throw IllegalArgumentException (queryGenerator must be non-null)",
        IllegalArgumentException.class, () -> service.setQueryGeneratorOverride(null));
    assertThrows("QueryOverrides: setQueryGeneratorOverride(\"\") should throw IllegalArgumentException (queryGenerator must be non-empty)",
        IllegalArgumentException.class, () -> service.setQueryGeneratorOverride(""));
    assertThrows("QueryOverrides: setQueryGeneratorOverride('someOtherQueryGenerator') is not supported for beir-v1; expected IllegalArgumentException",
        IllegalArgumentException.class, () -> service.setQueryGeneratorOverride("someOtherQueryGenerator"));
    SearchService nonHnswService = new SearchService("beir-v1.0.0-cqadupstack-english.flat");
    assertThrows("QueryOverrides: setEfSearchOverride('100') on a non-HNSW index should throw IllegalArgumentException (efSearch supported only for HNSW indexes)",
        IllegalArgumentException.class, () -> nonHnswService.setEfSearchOverride("100"));
    assertThrows("QueryOverrides: setQueryGeneratorOverride('someOtherQueryGenerator') on a non-HNSW index should throw IllegalArgumentException (queryGenerator supported only for HNSW indexes)",
        IllegalArgumentException.class, () -> nonHnswService.setQueryGeneratorOverride("someOtherQueryGenerator"));
    assertThrows("QueryOverrides: setEncoderOverride('someOtherEncoder') on a non-HNSW index should throw IllegalArgumentException (encoder supported only for HNSW indexes)",
        IllegalArgumentException.class, () -> nonHnswService.setEncoderOverride("someOtherEncoder"));
  }

  @Test
  public void testHnswSearch() throws Exception {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");

    List<Map<String, Object>> results = service.search("test query", 5, 100, null, null);
    assertNotNull("HNSW Search: search('test query', 5, 100, null, null) returned null results", results);
    assertTrue("HNSW Search: Expected results size <= 5 but got " + results.size(), results.size() <= 5);
  }

  @Test
  public void testEfSearchOverride() {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");

    service.setEfSearchOverride("200");
    int efSearch = service.getEfSearchOverride();
    assertEquals("EfSearchOverride: Expected efSearch to be 200 but got " + efSearch, 200, efSearch);
    assertThrows("EfSearchOverride: Setting negative value '-1' should throw IllegalArgumentException (efSearch must be > 0)",
        IllegalArgumentException.class, () -> service.setEfSearchOverride("-1"));
    assertThrows("EfSearchOverride: Setting non-numeric value 'Albert Einstein' should throw IllegalArgumentException (efSearch must be numeric)",
        IllegalArgumentException.class,
        () -> service.setEfSearchOverride("Albert Einstein"));
  }

  @Test
  public void testGetDocument() throws Exception {
    SearchService service = new SearchService("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw");
    List<Map<String, Object>> results = service.search("test query", 1);
    assertNotNull("GetDocument: search('test query', 1) returned null results", results);
    if (!results.isEmpty()) {
      String docid = (String) results.get(0).get("docid");
      Map<String, Object> doc = service.getDocument(docid);
      assertNotNull("GetDocument: getDocument('" + docid + "') returned null document", doc);
    }
  }

  @Test
  public void testInvalidIndex() {
    assertThrows("InvalidIndex: Constructing SearchService with index 'nonexistent-index' should throw RuntimeException",
        RuntimeException.class, () -> new SearchService("nonexistent-index"));
  }
}