/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
import io.anserini.search.SimpleSearcher.Result;
import io.anserini.search.query.PhraseQueryGenerator;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SimpleSearcherTest extends IndexerTestBase {

  @Test
  public void testGetDoc() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

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
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    assertEquals("here is some text here is some more text. city.",
        searcher.documentByField("id", "doc1").get("contents"));
    assertEquals("more texts", searcher.documentByField("id", "doc2").get("contents"));
    assertEquals("here is a test", searcher.documentByField("id", "doc3").get("contents"));

    searcher.close();
  }

  @Test
  public void testGetContents() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

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
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

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
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits = searcher.search("test", 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals(0.5702000f, hits[0].score, 10e-6);
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
    assertEquals("doc2", results[1].docid);
    assertEquals(1, results[1].lucene_docid);
    assertEquals(0.28830000f, results[0].score, 10e-6);
    assertEquals(0.27329999f, results[1].score, 10e-6);

    results = searcher.search("test");
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(2, results[0].lucene_docid);
    assertEquals(0.5702000f, results[0].score, 10e-6);

    searcher.close();
  }

  @Test
  public void testSearchCustomQuery() throws Exception {
    // Test the ability to pass in an arbitrary Lucene query.
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits = searcher.search(new TermQuery(new Term(IndexArgs.ID, "doc3")), 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals("here is a test", hits[0].contents);
    assertEquals("{\"contents\": \"here is a test\"}", hits[0].raw);

    searcher.close();
  }

  @Test
  public void testBatchSearch() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
      
    List<String> queries = new ArrayList<>();
    queries.add("test");
    queries.add("more");

    List<String> qids = new ArrayList<>();
    qids.add("query_test");
    qids.add("query_more");

    Map<String, SimpleSearcher.Result[]> hits = searcher.batchSearch(queries, qids, 10, 2);
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

    SimpleSearcher.Result[] hits = searcher.searchFields("doc1", fields, 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);

    hits = searcher.searchFields("test", fields, 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);

    hits = searcher.searchFields("test", Map.of("id", 1.0f), 10);
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

    Map<String, SimpleSearcher.Result[]> hits = searcher.batchSearchFields(queries, qids, 10, 2, fields);
    assertEquals(2, hits.size());

    assertEquals(1, hits.get("query_id").length);
    assertEquals("doc1", hits.get("query_id")[0].docid);

    assertEquals(1, hits.get("query_contents").length);
    assertEquals("doc3", hits.get("query_contents")[0].docid);

    searcher.close();
  }

  @Test
  public void testMain() throws Exception {
    Random random = new Random();
    String tmpFile = "tmp" + random.nextInt() + ".txt";
    String contents;

    SimpleSearcher.main(new String[] {"-index", super.tempDir1.toString(),
        "-topics", "src/main/resources/topics-and-qrels/topics.robust04.txt",
        "-output", tmpFile});

    contents = Files.readString(Paths.get(tmpFile), StandardCharsets.US_ASCII);
    assertEquals("620 Q0 doc3 1 0.570200 Anserini\n", contents);

    SimpleSearcher.main(new String[] {"-index", super.tempDir1.toString(), "-threads", "2",
        "-topics", "src/main/resources/topics-and-qrels/topics.robust04.txt",
        "-output", tmpFile});

    contents = Files.readString(Paths.get(tmpFile), StandardCharsets.US_ASCII);
    assertEquals("620 Q0 doc3 1 0.570200 Anserini\n", contents);

    SimpleSearcher.main(new String[] {"-index", super.tempDir1.toString(), "-rm3",
        "-topics", "src/main/resources/topics-and-qrels/topics.robust04.txt",
        "-output", tmpFile});

    contents = Files.readString(Paths.get(tmpFile), StandardCharsets.US_ASCII);
    assertEquals("620 Q0 doc3 1 0.095000 Anserini\n", contents);

    SimpleSearcher.main(new String[] {"-index", super.tempDir1.toString(), "-qld",
        "-topics", "src/main/resources/topics-and-qrels/topics.robust04.txt",
        "-output", tmpFile});

    contents = Files.readString(Paths.get(tmpFile), StandardCharsets.US_ASCII);
    assertEquals("620 Q0 doc3 1 0.004500 Anserini\n", contents);

    new File(tmpFile).delete();
  }

  @Test
  public void testTotalNumDocuments() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    assertEquals(3 ,searcher.getTotalNumDocuments());
  }
}
