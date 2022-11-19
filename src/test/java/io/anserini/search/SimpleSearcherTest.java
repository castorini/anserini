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
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.index.Constants;
import io.anserini.search.SimpleSearcher.Result;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSearcherTest extends IndexerTestBase {
  @Test
  public void testGettersAndSetters() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    assertTrue(searcher.get_analyzer() instanceof DefaultEnglishAnalyzer);

    searcher.set_language("ar");
    assertTrue(searcher.get_analyzer() instanceof ArabicAnalyzer);

    assertTrue(searcher.get_similarity() instanceof BM25Similarity);

    searcher.set_qld(100.0f);
    assertTrue(searcher.get_similarity() instanceof LMDirichletSimilarity);

    searcher.close();
  }

  @Test
  public void testGetDoc() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

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
  public void testBatchGetDoc() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    ArrayList<String> docIds = new ArrayList<>();
    docIds.add("doc1");
    docIds.add("doc2");
    docIds.add("doc3");
    docIds.add("fake_doc");

    Map<String, Document> results = searcher.batch_get_docs(docIds, 2);
    assertEquals("here is some text here is some more text. city.", results.get("doc1").get("contents"));
    assertEquals("more texts", results.get("doc2").get("contents"));
    assertEquals("here is a test", results.get("doc3").get("contents"));
    assertNull(results.get("fake_doc"));

    searcher.close();
  }

  @Test
  public void testGetDocByField() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.",
        searcher.doc_by_field("id", "doc1").get("contents"));
    assertEquals("more texts", searcher.doc_by_field("id", "doc2").get("contents"));
    assertEquals("here is a test", searcher.doc_by_field("id", "doc3").get("contents"));

    searcher.close();
  }

  @Test
  public void testGetContents() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

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
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

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
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits = searcher.search("test", 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals(0.5702000f, hits[0].score, 10e-6);
    assertEquals("here is a test", hits[0].contents);
    assertEquals("{\"contents\": \"here is a test\"}", hits[0].raw);

    // We can fetch the exact same information from the raw Lucene document also.
    assertEquals("doc3", hits[0].lucene_document.getField(Constants.ID).stringValue());
    assertEquals("here is a test", hits[0].lucene_document.getField(Constants.CONTENTS).stringValue());
    assertEquals("{\"contents\": \"here is a test\"}",
        hits[0].lucene_document.getField(Constants.RAW).stringValue());

    searcher.close();
  }

  @Test
  public void testSearch2() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    Result[] results;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.28830000f, results[0].score, 10e-6);
    assertEquals("here is some text here is some more text. city.", results[0].contents);
    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}", results[0].raw);

    results = searcher.search("text");
    assertEquals(2, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.28830000f, results[0].score, 10e-6);
    assertEquals("doc2", results[1].docid);
    assertEquals(1, results[1].lucene_docid);
    assertEquals(0.27329999f, results[1].score, 10e-6);

    results = searcher.search("test");
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.5702000f, results[0].score, 10e-6);

    searcher.close();
  }

  @Test
  public void testSearch3() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_bm25(3.5f, 0.9f);
    Result[] results;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc2", results[0].docid);
    assertEquals(1, results[0].lucene_docid);
    assertEquals(0.16070f, results[0].score, 10e-5);

    results = searcher.search("text");
    assertEquals(2, results.length);
    assertEquals("doc2", results[0].docid);
    assertEquals(1, results[0].lucene_docid);
    assertEquals(0.16070f, results[0].score, 10e-5);
    assertEquals("doc1", results[1].docid);
    assertEquals(0, results[1].lucene_docid);
    assertEquals(0.10870f, results[1].score, 10e-5);

    results = searcher.search("test");
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.33530f, results[0].score, 10e-5);

    searcher.close();
  }

  @Test
  public void testSearch4() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_qld(10);
    Result[] results;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc2", results[0].docid);
    assertEquals(1, results[0].lucene_docid);
    assertEquals(0.09910f, results[0].score, 10e-5);

    results = searcher.search("text");
    assertEquals(2, results.length);
    assertEquals("doc2", results[0].docid);
    assertEquals(1, results[0].lucene_docid);
    assertEquals(0.09910f, results[0].score, 10e-5);
    assertEquals("doc1", results[1].docid);
    assertEquals(0, results[1].lucene_docid);
    assertEquals(0.0f, results[1].score, 10e-5);

    results = searcher.search("test");
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.31850f, results[0].score, 10e-5);

    searcher.close();
  }

  @Test
  public void testSearch5() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_rm3();
    assertTrue(searcher.use_rm3());

    Result[] results;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.14417f, results[0].score, 10e-5);

    Map<String, Float> feedbackTerms = searcher.get_feedback_terms("text");
    assertEquals(1, feedbackTerms.size());
    assertEquals(0.5f, feedbackTerms.get("text"), 10e-5);

    searcher.unset_rm3();
    assertFalse(searcher.use_rm3());

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.28830f, results[0].score, 10e-5);

    feedbackTerms = searcher.get_feedback_terms("text");
    assertNull(feedbackTerms);

    searcher.close();
  }

  @Test
  public void testSearch6() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_rocchio();
    assertTrue(searcher.use_rocchio());

    Result[] results;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.28830f, results[0].score, 10e-5);

    Map<String, Float> feedbackTerms = searcher.get_feedback_terms("text");
    assertEquals(1, feedbackTerms.size());
    assertEquals(1.0f, feedbackTerms.get("text"), 10e-5);

    searcher.unset_rocchio();
    assertFalse(searcher.use_rocchio());

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.28830f, results[0].score, 10e-5);

    feedbackTerms = searcher.get_feedback_terms("text");
    assertNull(feedbackTerms);

    searcher.close();
  }

  @Test
  public void testSearchCustomQuery() throws Exception {
    // Test the ability to pass in an arbitrary Lucene query.
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits = searcher.search(new TermQuery(new Term(Constants.ID, "doc3")), 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals("here is a test", hits[0].contents);
    assertEquals("{\"contents\": \"here is a test\"}", hits[0].raw);

    searcher.close();
  }

  @Test
  public void testBatchSearch1() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
      
    List<String> queries = new ArrayList<>();
    queries.add("test");
    queries.add("more");

    List<String> qids = new ArrayList<>();
    qids.add("query_test");
    qids.add("query_more");

    Map<String, SimpleSearcher.Result[]> hits = searcher.batch_search(queries, qids, 10, 2);
    assertEquals(2, hits.size());

    assertEquals(1, hits.get("query_test").length);
    assertEquals("doc3", hits.get("query_test")[0].docid);

    assertEquals(2, hits.get("query_more").length);
    assertEquals("doc2", hits.get("query_more")[0].docid);
    assertEquals("doc1", hits.get("query_more")[1].docid);

    searcher.close();
  }

  @Test
  public void testBatchSearch2() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_rm3();

    List<String> queries = new ArrayList<>();
    queries.add("test");
    queries.add("more");

    List<String> qids = new ArrayList<>();
    qids.add("query_test");
    qids.add("query_more");

    Map<String, SimpleSearcher.Result[]> hits = searcher.batch_search(queries, qids, 10, 2);
    assertEquals(2, hits.size());

    assertEquals(1, hits.get("query_test").length);
    assertEquals("doc3", hits.get("query_test")[0].docid);

    assertEquals(2, hits.get("query_more").length);
    assertEquals("doc2", hits.get("query_more")[0].docid);
    assertEquals("doc1", hits.get("query_more")[1].docid);

    searcher.close();
  }

  @Test
  public void testFieldedSearch() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    Map<String, Float> fields = new HashMap<>();
    fields.put("id", 1.0f);
    fields.put("contents", 1.0f);

    SimpleSearcher.Result[] hits = searcher.search_fields("doc1", fields, 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);

    hits = searcher.search_fields("test", fields, 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);

    hits = searcher.search_fields("test", Map.of("id", 1.0f), 10);
    assertEquals(0, hits.length);

    searcher.close();
  }

  @Test
  public void testFieldedBatchSearch() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    List<String> queries = new ArrayList<>();
    queries.add("doc1");
    queries.add("test");

    List<String> qids = new ArrayList<>();
    qids.add("query_id");
    qids.add("query_contents");

    Map<String, Float> fields = new HashMap<>();
    fields.put("id", 1.0f);
    fields.put("contents", 1.0f);

    Map<String, SimpleSearcher.Result[]> hits = searcher.batch_search_fields(queries, qids, 10, 2, fields);
    assertEquals(2, hits.size());

    assertEquals(1, hits.get("query_id").length);
    assertEquals("doc1", hits.get("query_id")[0].docid);

    assertEquals(1, hits.get("query_contents").length);
    assertEquals("doc3", hits.get("query_contents")[0].docid);

    searcher.close();
  }

  @Test
  public void testTotalNumDocuments() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    assertEquals(3 ,searcher.get_total_num_docs());
  }
}
