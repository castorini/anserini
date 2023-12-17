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

import io.anserini.index.IndexerTestBase;
import io.anserini.index.Constants;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleImpactSearcherTest extends IndexerTestBase {

  private static Map<String, Integer> EXPECTED_ENCODED_QUERY = new HashMap<>();

  static {
    EXPECTED_ENCODED_QUERY.put("here", 156);
    EXPECTED_ENCODED_QUERY.put("a", 31);
    EXPECTED_ENCODED_QUERY.put("test", 149);
  }

  @Test
  public void testGetDoc() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.",
        searcher.doc(0).get("contents"));
    assertEquals("more texts", searcher.doc(1).get("contents"));
    assertEquals("here is a test", searcher.doc(2).get("contents"));
    assertNull(searcher.doc(3));

    assertEquals("here is some text here is some more text. city.",
        searcher.doc("doc1").get("contents"));
    assertEquals("more texts", searcher.doc("doc2").get("contents"));
    assertEquals("here is a test", searcher.doc("doc3").get("contents"));
    assertNull(searcher.doc(3));

    searcher.close();
  }

  @Test
  public void testGetDocByField() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.",
        searcher.doc_by_field("id", "doc1").get("contents"));
    assertEquals("more texts", searcher.doc_by_field("id", "doc2").get("contents"));
    assertEquals("here is a test", searcher.doc_by_field("id", "doc3").get("contents"));

    searcher.close();
  }

  @Test
  public void testGetContents() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.", searcher.doc_contents(0));
    assertEquals("more texts", searcher.doc_contents(1));
    assertEquals("here is a test", searcher.doc_contents(2));
    assertNull(searcher.doc(3));

    assertEquals("here is some text here is some more text. city.", searcher.doc_contents("doc1"));
    assertEquals("more texts", searcher.doc_contents("doc2"));
    assertEquals("here is a test", searcher.doc_contents("doc3"));
    assertNull(searcher.doc_contents("doc42"));

    searcher.close();
  }

  @Test
  public void testGetRaw() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}",
        searcher.doc_raw(0));
    assertEquals("{\"contents\": \"more texts\"}",
        searcher.doc_raw(1));
    assertEquals("{\"contents\": \"here is a test\"}",
        searcher.doc_raw(2));
    assertNull(searcher.doc(3));

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}",
        searcher.doc_raw("doc1"));
    assertEquals("{\"contents\": \"more texts\"}",
        searcher.doc_raw("doc2"));
    assertEquals("{\"contents\": \"here is a test\"}",
        searcher.doc_raw("doc3"));
    assertNull(searcher.doc_contents("doc42"));

    searcher.close();
  }

  @Test
  public void testSearch1() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    Map<String, Integer> testQuery = new HashMap<>();
    testQuery.put("test", 1);

    ScoredDoc[] hits = searcher.search(testQuery, 10);
    ScoredDoc[] hits_string = searcher.search("test", 10);
    assertEquals(hits_string.length, hits.length);
    assertEquals(hits_string[0].docid, hits[0].docid);
    assertEquals(hits_string[0].lucene_docid, hits[0].lucene_docid);
    assertEquals(hits_string[0].score, hits[0].score, 10e-6);
    assertEquals(searcher.doc_contents(hits_string[0].docid), searcher.doc_contents(hits[0].docid));
    assertEquals(searcher.doc_raw(hits_string[0].docid), searcher.doc_raw(hits[0].docid));

    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals(1.0f, hits[0].score, 10e-6);
    assertEquals("here is a test", searcher.doc_contents(hits[0].docid));
    assertEquals("{\"contents\": \"here is a test\"}", searcher.doc_raw(hits[0].docid));

    // We can fetch the exact same information from the raw Lucene document also.
    assertEquals("doc3",
        hits[0].lucene_document.getField(Constants.ID).stringValue());
    assertEquals("here is a test",
        hits[0].lucene_document.getField(Constants.CONTENTS).stringValue());
    assertEquals("{\"contents\": \"here is a test\"}",
        hits[0].lucene_document.getField(Constants.RAW).stringValue());

    searcher.close();
  }

  @Test
  public void testSearch2() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());

    Map<String, Integer> testQuery = new HashMap<>();
    testQuery.put("text", 1);

    ScoredDoc[] results;

    results = searcher.search(testQuery, 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(2.0f, results[0].score, 10e-6);
    assertEquals("here is some text here is some more text. city.", searcher.doc_contents(results[0].docid));
    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}", searcher.doc_raw(results[0].docid));

    results = searcher.search(testQuery);
    assertEquals(2, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals("doc2", results[1].docid);
    assertEquals(1, results[1].lucene_docid);
    assertEquals(2.0f, results[0].score, 10e-6);
    assertEquals(1.0f, results[1].score, 10e-6);

    Map<String, Integer> testQuery2 = new HashMap<>();
    testQuery2.put("test", 1);

    results = searcher.search(testQuery2);
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(1.0f, results[0].score, 10e-6);

    searcher.close();
  }

  @Test
  public void testBatchSearch() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());
    Map<String, Integer> testQuery1 = new HashMap<>();
    testQuery1.put("tests", 1);
    testQuery1.put("test", 1);
    Map<String, Integer> testQuery2 = new HashMap<>();
    testQuery2.put("more", 3);

    List<Map<String, Integer>> queries = new ArrayList<>();
    queries.add(testQuery1);
    queries.add(testQuery2);

    List<String> qids = new ArrayList<>();
    qids.add("query_test");
    qids.add("query_more");

    Map<String, ScoredDoc[]> hits = searcher.batch_search(queries, qids, 10, 2);
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
    assertEquals(3 ,searcher.get_total_num_docs());
  }

  @Test
  public void testOnnxEncodedQuery() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());
    Map<String, Integer> testQuery1 = new HashMap<>();
    testQuery1.put("text", 2);
    String encodedQuery = searcher.encode_with_onnx(testQuery1);
    assertEquals("text text" ,encodedQuery);
  }

  @Test
  public void testOnnxEncoder() throws Exception{
    SimpleImpactSearcher searcher = new SimpleImpactSearcher();
    searcher.set_onnx_query_encoder("SpladePlusPlusEnsembleDistil");

    Map<String, Integer> encoded_query = searcher.encode_with_onnx("here is a test");
    assertEquals(encoded_query.get("here"), EXPECTED_ENCODED_QUERY.get("here"), 2e-4);
    assertEquals(encoded_query.get("a"), EXPECTED_ENCODED_QUERY.get("a"), 2e-4);
    assertEquals(encoded_query.get("test"), EXPECTED_ENCODED_QUERY.get("test"), 2e-4);
  }

  @Test
  public void testSearch3() throws Exception {
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());
    searcher.set_rm3();
    assertTrue(searcher.use_rm3());

    ScoredDoc[] results;

    Map<String, Integer> testQuery1 = new HashMap<>();
    testQuery1.put("text", 1);

    results = searcher.search(testQuery1, 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(1.0f, results[0].score, 10e-5);

    Map<String, Integer> testQuery2 = new HashMap<>();
    testQuery2.put("test", 1);

    results = searcher.search(testQuery2);
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.5f, results[0].score, 10e-5);

    Map<String, Integer> testQuery3 = new HashMap<>();
    testQuery3.put("more", 1);

    results = searcher.search(testQuery3);

    assertEquals(2, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.5f, results[0].score, 10e-5);
    assertEquals("doc2", results[1].docid);
    assertEquals(1, results[1].lucene_docid);
    assertEquals(0.5f, results[1].score, 10e-5);

    searcher.unset_rm3();
    assertFalse(searcher.use_rm3());

    results = searcher.search(testQuery1, 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(2.0f, results[0].score, 10e-5);

    searcher.close();
  }

  @Test
  public void testSearch4() throws Exception {
    // This adds Rocchio on top of "testSearch1"
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());
    searcher.set_rocchio();
    assertTrue(searcher.use_rocchio());

    ScoredDoc[] results;

    Map<String, Integer> testQuery1 = new HashMap<>();
    testQuery1.put("text", 1);

    results = searcher.search(testQuery1, 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(2.0f, results[0].score, 10e-5);

    Map<String, Integer> testQuery2 = new HashMap<>();
    testQuery2.put("test", 1);

    results = searcher.search(testQuery2);
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(1.0f, results[0].score, 10e-5);

    Map<String, Integer> testQuery3 = new HashMap<>();
    testQuery3.put("more", 1);

    results = searcher.search(testQuery3);
    assertEquals(2, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(1.0f, results[0].score, 10e-5);
    assertEquals("doc2", results[1].docid);
    assertEquals(1, results[1].lucene_docid);
    assertEquals(1.0f, results[1].score, 10e-5);


    searcher.unset_rocchio();
    assertFalse(searcher.use_rocchio());

    results = searcher.search(testQuery1, 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(2.0f, results[0].score, 10e-5);

    searcher.close();
  }

  @Test
  public void testSearch5() throws Exception {
    // pretokenized test case for whitespace analyzer, so stopwords wont be removed
    SimpleImpactSearcher isearcher_roc = new SimpleImpactSearcher(super.tempDir1.toString());
    isearcher_roc.set_rocchio();
    assertTrue(isearcher_roc.use_rocchio());

    SimpleImpactSearcher isearcher_rm3 = new SimpleImpactSearcher(super.tempDir1.toString());
    isearcher_rm3.set_rm3();
    assertTrue(isearcher_rm3.use_rm3());

    ScoredDoc[] iresults;

    String query = "this is a a a a a test";
    iresults = isearcher_roc.search(query, 1);

    assertEquals(1, iresults.length);
    assertEquals("doc3", iresults[0].docid);
    assertEquals(2, iresults[0].lucene_docid);
    assertEquals(0.18899, iresults[0].score, 10e-5);

    iresults = isearcher_rm3.search(query, 1);

    assertEquals(1, iresults.length);
    assertEquals("doc3", iresults[0].docid);
    assertEquals(2, iresults[0].lucene_docid);
    assertEquals(0.06250, iresults[0].score, 10e-5);

    isearcher_roc.close();
    isearcher_rm3.close();
  }

  @Test
  public void testSearch6() throws Exception {
    // test the expanded query result should be diff with testSearch5
    SimpleImpactSearcher searcher = new SimpleImpactSearcher(super.tempDir1.toString());
    searcher.set_onnx_query_encoder("UniCoil");

    ScoredDoc[] results;

    String query = "this is a a a a a test";
    results = searcher.search(query, 1);

    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(130.0, results[0].score, 10e-5);

    searcher.close();
  }

}
