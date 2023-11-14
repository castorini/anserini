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

import io.anserini.index.IndexerWithoutDocvectorsTestBase;
import io.anserini.search.SimpleSearcher.Result;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// This is a variant of SimpleSearcherTest where we use an index that does not store docvectors.
// Here, we test that relevance feedback still works using on-the-fly document parsing.
public class SimpleSearcherWithoutDocvectorsTest extends IndexerWithoutDocvectorsTestBase {
  @Test
  public void testSearch5() throws Exception {
    // Counterpart of testSearch5 in SimpleSearcherTest
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_rm3("JsonCollection");
    assertTrue(searcher.use_rm3());

    Result[] results;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.14420f, results[0].score, 10e-5);

    Map<String, Float> feedbackTerms = searcher.get_feedback_terms("text");
    assertEquals(1, feedbackTerms.size());
    assertEquals(0.5f, feedbackTerms.get("text"), 10e-5);

    results = searcher.search("test");
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.28510f, results[0].score, 10e-5);

    results = searcher.search("more");
    assertEquals(2, results.length);
    assertEquals("doc2", results[0].docid);
    assertEquals(1, results[0].lucene_docid);
    assertEquals(0.13660f, results[0].score, 10e-5);
    assertEquals("doc1", results[1].docid);
    assertEquals(0, results[1].lucene_docid);
    assertEquals(0.10400f, results[1].score, 10e-5);

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
    // Counterpart of testSearch6 in SimpleSearcherTest
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_rocchio("JsonCollection");
    assertTrue(searcher.use_rocchio());

    Result[] results;
    Map<String, Float> feedbackTerms;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0, results[0].lucene_docid);
    assertEquals(0.28830f, results[0].score, 10e-5);

    // Note that the feedback term is just the query, the scores are the same.
    feedbackTerms = searcher.get_feedback_terms("text");
    assertEquals(1, feedbackTerms.size());
    assertEquals(1.0f, feedbackTerms.get("text"), 10e-5);

    results = searcher.search("test");
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.57020f, results[0].score, 10e-5);

    // Note that the feedback term is just the query, the scores are the same.
    feedbackTerms = searcher.get_feedback_terms("test");
    assertEquals(1, feedbackTerms.size());
    assertEquals(1.0f, feedbackTerms.get("test"), 10e-5);

    results = searcher.search("more");
    assertEquals(2, results.length);
    assertEquals("doc2", results[0].docid);
    assertEquals(1, results[0].lucene_docid);
    assertEquals(0.27330f, results[0].score, 10e-5);
    assertEquals("doc1", results[1].docid);
    assertEquals(0, results[1].lucene_docid);
    assertEquals(0.20800f, results[1].score, 10e-5);

    // Note that the feedback term is just the query, the scores are the same.
    feedbackTerms = searcher.get_feedback_terms("more");
    assertEquals(1, feedbackTerms.size());
    assertEquals(1.0f, feedbackTerms.get("more"), 10e-5);

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
  public void testBatchSearch5() throws Exception {
    // Counterpart of testBatchSearch5 in SimpleSearcherTest
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_rm3("JsonCollection");

    List<String> queries = new ArrayList<>();
    queries.add("text");
    queries.add("test");
    queries.add("more");

    List<String> qids = new ArrayList<>();
    qids.add("query_text");
    qids.add("query_test");
    qids.add("query_more");

    Map<String, SimpleSearcher.Result[]> hits = searcher.batch_search(queries, qids, 10, 2);
    assertEquals(3, hits.size());

    assertEquals(2, hits.get("query_text").length);
    assertEquals("doc1", hits.get("query_text")[0].docid);
    assertEquals(0, hits.get("query_text")[0].lucene_docid);
    assertEquals(0.14420f, hits.get("query_text")[0].score, 10e-5);
    assertEquals("doc2", hits.get("query_text")[1].docid);
    assertEquals(1, hits.get("query_text")[1].lucene_docid);
    assertEquals(0.13660f, hits.get("query_text")[1].score, 10e-5);

    assertEquals(1, hits.get("query_test").length);
    assertEquals("doc3", hits.get("query_test")[0].docid);
    assertEquals(2, hits.get("query_test")[0].lucene_docid);
    assertEquals(0.28510f, hits.get("query_test")[0].score, 10e-5);

    assertEquals(2, hits.get("query_more").length);
    assertEquals("doc2", hits.get("query_more")[0].docid);
    assertEquals(1, hits.get("query_more")[0].lucene_docid);
    assertEquals(0.13660f, hits.get("query_more")[0].score, 10e-5);
    assertEquals("doc1", hits.get("query_more")[1].docid);
    assertEquals(0, hits.get("query_more")[1].lucene_docid);
    assertEquals(0.10400f, hits.get("query_more")[1].score, 10e-5);

    searcher.close();
  }

  @Test
  public void testBatchSearch6() throws Exception {
    // Counterpart of testBatchSearch6 in SimpleSearcherTest
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    searcher.set_rocchio("JsonCollection");

    List<String> queries = new ArrayList<>();
    queries.add("text");
    queries.add("test");
    queries.add("more");

    List<String> qids = new ArrayList<>();
    qids.add("query_text");
    qids.add("query_test");
    qids.add("query_more");

    Map<String, SimpleSearcher.Result[]> hits = searcher.batch_search(queries, qids, 10, 2);
    assertEquals(3, hits.size());

    assertEquals(2, hits.get("query_text").length);
    assertEquals("doc1", hits.get("query_text")[0].docid);
    assertEquals(0, hits.get("query_text")[0].lucene_docid);
    assertEquals(0.28830f, hits.get("query_text")[0].score, 10e-5);
    assertEquals("doc2", hits.get("query_text")[1].docid);
    assertEquals(1, hits.get("query_text")[1].lucene_docid);
    assertEquals(0.27330f, hits.get("query_text")[1].score, 10e-5);

    assertEquals(1, hits.get("query_test").length);
    assertEquals("doc3", hits.get("query_test")[0].docid);
    assertEquals(2, hits.get("query_test")[0].lucene_docid);
    assertEquals(0.57020f, hits.get("query_test")[0].score, 10e-5);

    assertEquals(2, hits.get("query_more").length);
    assertEquals("doc2", hits.get("query_more")[0].docid);
    assertEquals(1, hits.get("query_more")[0].lucene_docid);
    assertEquals(0.27330f, hits.get("query_more")[0].score, 10e-5);
    assertEquals("doc1", hits.get("query_more")[1].docid);
    assertEquals(0, hits.get("query_more")[1].lucene_docid);
    assertEquals(0.20800f, hits.get("query_more")[1].score, 10e-5);

    searcher.close();
  }
}
