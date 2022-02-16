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

package io.anserini.search;

import io.anserini.IndexerTestBase;
import io.anserini.index.IndexArgs;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleImpactSearcherTest extends IndexerTestBase {

  @Test
  public void testGetDoc() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.",
        searcher.document(0).get("contents"));
    assertEquals("more texts", searcher.document(1).get("contents"));
    assertEquals("here is a test", searcher.document(2).get("contents"));
    assertNull(searcher.document(3));

    assertEquals("here is some text here is some more text. city.",
        searcher.document("doc1").get("contents"));
    assertEquals("more texts", searcher.document("doc2").get("contents"));
    assertEquals("here is a test", searcher.document("doc3").get("contents"));
    assertNull(searcher.document(3));

    searcher.close();
  }

  @Test
  public void testGetDocByField() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.",
        searcher.documentByField("id", "doc1").get("contents"));
    assertEquals("more texts", searcher.documentByField("id", "doc2").get("contents"));
    assertEquals("here is a test", searcher.documentByField("id", "doc3").get("contents"));

    searcher.close();
  }

  @Test
  public void testGetContents() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.",
        searcher.documentContents(0));
    assertEquals("more texts",
        searcher.documentContents(1));
    assertEquals("here is a test",
        searcher.documentContents(2));
    assertNull(searcher.document(3));

    assertEquals("here is some text here is some more text. city.",
        searcher.documentContents("doc1"));
    assertEquals("more texts",
        searcher.documentContents("doc2"));
    assertEquals("here is a test",
        searcher.documentContents("doc3"));
    assertNull(searcher.documentContents("doc42"));

    searcher.close();
  }

  @Test
  public void testGetRaw() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}",
        searcher.documentRaw(0));
    assertEquals("{\"contents\": \"more texts\"}",
        searcher.documentRaw(1));
    assertEquals("{\"contents\": \"here is a test\"}",
        searcher.documentRaw(2));
    assertNull(searcher.document(3));

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}",
        searcher.documentRaw("doc1"));
    assertEquals("{\"contents\": \"more texts\"}",
        searcher.documentRaw("doc2"));
    assertEquals("{\"contents\": \"here is a test\"}",
        searcher.documentRaw("doc3"));
    assertNull(searcher.documentContents("doc42"));

    searcher.close();
  }

  @Test
  public void testSearch1() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    Map<String, Float> testQuery = new HashMap<>();
    testQuery.put("test", 1.2f);

    SimpleImpactSearcher.Result[] hits = searcher.search(testQuery, 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals(1.2f, hits[0].score, 10e-6);
    assertEquals("here is a test", hits[0].contents);
    assertEquals("{\"contents\": \"here is a test\"}", hits[0].raw);

    // We can fetch the exact same information from the raw Lucene document also.
    assertEquals("doc3",
        hits[0].lucene_document.getField(IndexArgs.ID).stringValue());
    assertEquals("here is a test",
        hits[0].lucene_document.getField(IndexArgs.CONTENTS).stringValue());
    assertEquals("{\"contents\": \"here is a test\"}",
        hits[0].lucene_document.getField(IndexArgs.RAW).stringValue());

    searcher.close();
  }

  @Test
  public void testSearch2() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    Map<String, Float> testQuery = new HashMap<>();
    testQuery.put("text", 1.2f);

    SimpleImpactSearcher.Result[] results;

    results = searcher.search(testQuery, 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(2.4f, results[0].score, 10e-6);
    assertEquals("here is some text here is some more text. city.", results[0].contents);
    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}", results[0].raw);

    results = searcher.search(testQuery);
    assertEquals(2, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals("doc2", results[1].docid);
    assertEquals(1, results[1].lucene_docid);
    assertEquals(2.4f, results[0].score, 10e-6);
    assertEquals(1.2f, results[1].score, 10e-6);

    Map<String, Float> testQuery2 = new HashMap<>();
    testQuery2.put("test", 0.125f);

    results = searcher.search(testQuery2);
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.125f, results[0].score, 10e-6);

    searcher.close();
  }

  @Test
  public void testBatchSearch() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());
    Map<String, Float> testQuery1 = new HashMap<>();
    testQuery1.put("tests", 0.1f);
    testQuery1.put("test", 0.1f);
    Map<String, Float> testQuery2 = new HashMap<>();
    testQuery2.put("more", 1.5f);

    List<Map<String, Float>> queries = new ArrayList<>();
    queries.add(testQuery1);
    queries.add(testQuery2);

    List<String> qids = new ArrayList<>();
    qids.add("query_test");
    qids.add("query_more");

    Map<String, SimpleImpactSearcher.Result[]> hits = searcher.batchSearch(queries, qids, 10, 2);
    assertEquals(2, hits.size());

    assertEquals(1, hits.get("query_test").length);
    assertEquals("doc3", hits.get("query_test")[0].docid);

    assertEquals(2, hits.get("query_more").length);
    assertEquals("doc1", hits.get("query_more")[0].docid);
    assertEquals("doc2", hits.get("query_more")[1].docid);

    searcher.close();
  }

  @Test
  public void testTotalNumDocuments() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());
    assertEquals(3 ,searcher.getTotalNumDocuments());
  }
}
