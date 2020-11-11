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

package io.anserini.index;

import io.anserini.IndexerTestBase;
import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.search.SearchArgs;
import io.anserini.search.SimpleSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IndexReaderUtilsTest extends IndexerTestBase {

  @Test
  public void testTermCounts() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, Long> termCountMap;

    termCountMap = IndexReaderUtils.getTermCounts(reader, "here");
    assertEquals(Long.valueOf(3), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "more");
    assertEquals(Long.valueOf(2), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "some");
    assertEquals(Long.valueOf(2), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "test");
    assertEquals(Long.valueOf(1), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "text");
    assertEquals(Long.valueOf(3), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "some text");
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    reader.close();
    dir.close();
  }

  @Test
  public void testTermCountsWithAnalyzer() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    DefaultEnglishAnalyzer analyzer = DefaultEnglishAnalyzer.newDefaultInstance();

    Map<String, Long> termCountMap;

    termCountMap = IndexReaderUtils.getTermCountsWithAnalyzer(reader, "here", analyzer);
    assertEquals(Long.valueOf(3), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCountsWithAnalyzer(reader, "more", analyzer);
    assertEquals(Long.valueOf(2), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCountsWithAnalyzer(reader, "some", analyzer);
    assertEquals(Long.valueOf(2), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCountsWithAnalyzer(reader, "test", analyzer);
    assertEquals(Long.valueOf(1), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCountsWithAnalyzer(reader, "text", analyzer);
    assertEquals(Long.valueOf(3), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCountsWithAnalyzer(reader, "some text", analyzer);
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    reader.close();
    dir.close();
  }

  @Test
  public void testIterateThroughTerms() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Iterator<IndexReaderUtils.IndexTerm> iter = IndexReaderUtils.getTerms(reader);
    IndexReaderUtils.IndexTerm term;

    term = iter.next();
    assertEquals("citi", term.getTerm());
    assertEquals(1, term.getDF());
    assertEquals(1, term.getTotalTF());

    term = iter.next();
    assertEquals("here", term.getTerm());
    assertEquals(2, term.getDF());
    assertEquals(3, term.getTotalTF());

    term = iter.next();
    assertEquals("more", term.getTerm());
    assertEquals(2, term.getDF());
    assertEquals(2, term.getTotalTF());

    term = iter.next();
    assertEquals("some", term.getTerm());
    assertEquals(1, term.getDF());
    assertEquals(2, term.getTotalTF());

    term = iter.next();
    assertEquals("test", term.getTerm());
    assertEquals(1, term.getDF());
    assertEquals(1, term.getTotalTF());

    term = iter.next();
    assertEquals("text", term.getTerm());
    assertEquals(2, term.getDF());
    assertEquals(3, term.getTotalTF());

    assertEquals(false, iter.hasNext());

    reader.close();
    dir.close();
  }

  @Test
  public void testPostingsNonExistings() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    assertNull(IndexReaderUtils.getPostingsList(reader, "asxe"));
    reader.close();
    dir.close();

  }

  @Test
  public void testPostingsLists1() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    List<IndexReaderUtils.Posting> postingsList;

    // here: (0, 2) [0, 4] (2, 1) [0]
    postingsList = IndexReaderUtils.getPostingsList(reader, "here");
    assertEquals(2, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {0, 4}, postingsList.get(0).getPositions());
    assertEquals(1, postingsList.get(1).getTF());
    assertEquals(2, postingsList.get(1).getDocid());
    assertArrayEquals(new int[] {0}, postingsList.get(1).getPositions());

    // more: (0, 1) [7] (1, 1) [0]
    postingsList = IndexReaderUtils.getPostingsList(reader, "more");
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {7}, postingsList.get(0).getPositions());
    assertEquals(1, postingsList.get(1).getTF());
    assertEquals(1, postingsList.get(1).getDocid());
    assertArrayEquals(new int[] {0}, postingsList.get(1).getPositions());

    // some: (0, 2) [2, 6]
    postingsList = IndexReaderUtils.getPostingsList(reader, "some");
    assertEquals(2, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {2, 6}, postingsList.get(0).getPositions());

    // test: (2, 1) [3]
    postingsList = IndexReaderUtils.getPostingsList(reader, "test");
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(2, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {3}, postingsList.get(0).getPositions());

    // tests: (2, 1) [3]
    // Note that 'tests' and 'test' both stem to the same string.
    postingsList = IndexReaderUtils.getPostingsList(reader, "tests");
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(2, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {3}, postingsList.get(0).getPositions());

    // text: (0, 2) [3, 8] (1, 1) [1]
    postingsList = IndexReaderUtils.getPostingsList(reader, "text");
    assertEquals(2, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {3, 8}, postingsList.get(0).getPositions());
    assertEquals(1, postingsList.get(1).getTF());
    assertEquals(1, postingsList.get(1).getDocid());
    assertArrayEquals(new int[] {1}, postingsList.get(1).getPositions());

    reader.close();
    dir.close();
  }

  @Test
  public void testPostingsLists2() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    List<IndexReaderUtils.Posting> postingsList;

    // Analyze the term, by default
    postingsList = IndexReaderUtils.getPostingsList(reader, "city");
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {9}, postingsList.get(0).getPositions());

    // Sanity check.
    assertNull(IndexReaderUtils.getPostingsList(reader, "citz"));

    // Tell method to analyze *explicitly*:
    postingsList = IndexReaderUtils.getPostingsList(reader, "city", true);
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {9}, postingsList.get(0).getPositions());

    // Tell method to analyze *explicitly*:
    postingsList = IndexReaderUtils.getPostingsList(reader, "city", IndexCollection.DEFAULT_ANALYZER);
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {9}, postingsList.get(0).getPositions());

    // Tell method to analyze *explicitly*, but pass in mismatched analyzer:
    assertNull(IndexReaderUtils.getPostingsList(reader, "city",
        DefaultEnglishAnalyzer.newStemmingInstance("krovetz")));

    // Tell method *not* to analyze:
    postingsList = IndexReaderUtils.getPostingsList(reader, "citi", false);
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {9}, postingsList.get(0).getPositions());

    // Tell method *not* to analyze:
    postingsList = IndexReaderUtils.getPostingsList(reader,
        AnalyzerUtils.analyze("city").get(0), false);
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {9}, postingsList.get(0).getPositions());

    reader.close();
    dir.close();
  }

  @Test
  public void computeAllTermBM25Weights() throws Exception {
    SearchArgs args = new SearchArgs();
    Similarity similarity = new BM25Similarity(Float.parseFloat(args.bm25_k1[0]), Float.parseFloat(args.bm25_b[0]));

    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    // The complete term/doc matrix
    Map<String, Map<String, Float>> termDocMatrix = new HashMap<>();

    // We're going to iterate through all the terms in the dictionary to build the term/doc matrix
    Terms terms = MultiTerms.getTerms(reader, "contents");
    TermsEnum termsEnum = terms.iterator();
    BytesRef text;
    while ((text = termsEnum.next()) != null) {
      String term = text.utf8ToString();

      IndexSearcher searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);

      TopDocs rs = searcher.search(new TermQuery(new Term("contents", term)), 3);
      for (int i=0; i<rs.scoreDocs.length; i++) {
        String docid = reader.document(rs.scoreDocs[i].doc).getField("id").stringValue();
        if (!termDocMatrix.containsKey(term))
          termDocMatrix.put(term, new HashMap<>());
        termDocMatrix.get(term).put(docid, rs.scoreDocs[i].score);
      }
    }

    int numDocs = reader.numDocs();
    // Iterate through the document vectors, and verify that we have the same values as in the term/doc matrix
    for (int i=0; i<numDocs; i++) {
      Terms termVector = reader.getTermVector(i, "contents");
      String docid = IndexReaderUtils.convertLuceneDocidToDocid(reader, i);

      // For this document, iterate through the terms.
      termsEnum = termVector.iterator();
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        float weight = IndexReaderUtils.getBM25AnalyzedTermWeight(reader, docid, term);
        assertEquals(termDocMatrix.get(term).get(docid), weight, 10e-6);
      }
    }

    reader.close();
    dir.close();
  }

  @Test
  public void computeBM25Weights() throws Exception {
    SearchArgs args = new SearchArgs();

    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals(0.43400, IndexReaderUtils.getBM25UnanalyzedTermWeightWithParameters(reader, "doc1",
        "city", IndexCollection.DEFAULT_ANALYZER,0.9f, 0.4f), 10e-5);
    assertEquals(0.43400, IndexReaderUtils.getBM25AnalyzedTermWeightWithParameters(reader, "doc1",
        "citi", 0.9f, 0.4f), 10e-5);

    assertEquals(0.0f, IndexReaderUtils.getBM25UnanalyzedTermWeightWithParameters(reader, "doc2",
        "city", IndexCollection.DEFAULT_ANALYZER,0.9f, 0.4f), 10e-5);
    assertEquals(0.0f, IndexReaderUtils.getBM25AnalyzedTermWeightWithParameters(reader, "doc2",
        "citi", 0.9f, 0.4f), 10e-5);

    assertEquals(0.570250, IndexReaderUtils.getBM25UnanalyzedTermWeightWithParameters(reader, "doc3",
        "test", IndexCollection.DEFAULT_ANALYZER,0.9f, 0.4f), 10e-5);
    assertEquals(0.570250, IndexReaderUtils.getBM25AnalyzedTermWeightWithParameters(reader, "doc3",
        "test", 0.9f, 0.4f), 10e-5);

    reader.close();
    dir.close();
  }

  @Test
  public void testDocumentVector() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, Long> documentVector;

    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc1");
    assertEquals(Long.valueOf(2), documentVector.get("here"));
    assertEquals(Long.valueOf(1), documentVector.get("more"));
    assertEquals(Long.valueOf(2), documentVector.get("some"));
    assertEquals(Long.valueOf(2), documentVector.get("text"));
    assertEquals(Long.valueOf(1), documentVector.get("citi"));

    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc2");
    assertEquals(Long.valueOf(1), documentVector.get("more"));
    assertEquals(Long.valueOf(1), documentVector.get("text"));

    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc3");
    assertEquals(Long.valueOf(1), documentVector.get("here"));
    assertEquals(Long.valueOf(1), documentVector.get("test"));

    // Invalid docid.
    assertTrue(IndexReaderUtils.getDocumentVector(reader, "foo") == null);

    reader.close();
    dir.close();
  }

  @Test
  public void testTermPositions() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, List<Integer>> termPositions;

    termPositions = IndexReaderUtils.getTermPositions(reader, "doc1");
    assertEquals(Integer.valueOf(0), termPositions.get("here").get(0));
    assertEquals(Integer.valueOf(4), termPositions.get("here").get(1));
    assertEquals(Integer.valueOf(2), termPositions.get("some").get(0));
    assertEquals(Integer.valueOf(6), termPositions.get("some").get(1));
    assertEquals(Integer.valueOf(3), termPositions.get("text").get(0));
    assertEquals(Integer.valueOf(8), termPositions.get("text").get(1));
    assertEquals(Integer.valueOf(7), termPositions.get("more").get(0));
    assertEquals(Integer.valueOf(9), termPositions.get("citi").get(0));

    termPositions = IndexReaderUtils.getTermPositions(reader, "doc2");
    assertEquals(Integer.valueOf(0), termPositions.get("more").get(0));
    assertEquals(Integer.valueOf(1), termPositions.get("text").get(0));

    termPositions = IndexReaderUtils.getTermPositions(reader, "doc3");
    assertEquals(Integer.valueOf(0), termPositions.get("here").get(0));
    assertEquals(Integer.valueOf(3), termPositions.get("test").get(0));

    // Invalid docid.
    assertTrue(IndexReaderUtils.getDocumentVector(reader, "foo") == null);

    reader.close();
    dir.close();
  }

  @Test
  public void testGetDocumentRaw() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}",
        IndexReaderUtils.documentRaw(reader, "doc1"));
    assertEquals("{\"contents\": \"more texts\"}",
        IndexReaderUtils.documentRaw(reader, "doc2"));
    assertEquals("{\"contents\": \"here is a test\"}",
        IndexReaderUtils.documentRaw(reader, "doc3"));
    assertNull(IndexReaderUtils.documentRaw(reader, "fake"));

    reader.close();
    dir.close();
  }

  @Test
  public void testGetDocumentContents() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals("here is some text here is some more text. city.",
        IndexReaderUtils.documentContents(reader, "doc1"));
    assertEquals("more texts",
        IndexReaderUtils.documentContents(reader, "doc2"));
    assertEquals("here is a test",
        IndexReaderUtils.documentContents(reader, "doc3"));
    assertNull(IndexReaderUtils.documentContents(reader, "fake"));

    reader.close();
    dir.close();
  }

  @Test
  public void testGetDocument() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}",
        IndexReaderUtils.document(reader, "doc1").get("raw"));
    assertEquals("{\"contents\": \"more texts\"}",
        IndexReaderUtils.document(reader, "doc2").get("raw"));
    assertEquals("{\"contents\": \"here is a test\"}",
        IndexReaderUtils.document(reader, "doc3").get("raw"));

    assertEquals("here is some text here is some more text. city.",
        IndexReaderUtils.document(reader, "doc1").get("contents"));
    assertEquals("more texts",
        IndexReaderUtils.document(reader, "doc2").get("contents"));
    assertEquals("here is a test",
        IndexReaderUtils.document(reader, "doc3").get("contents"));
    assertNull(IndexReaderUtils.document(reader, "fake"));

    reader.close();
    dir.close();
  }

  @Test
  public void testGetDocumentByField() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}",
        IndexReaderUtils.documentByField(reader, "id","doc1").get("raw"));
    assertEquals("{\"contents\": \"more texts\"}",
        IndexReaderUtils.documentByField(reader, "id", "doc2").get("raw"));
    assertEquals("{\"contents\": \"here is a test\"}",
        IndexReaderUtils.documentByField(reader, "id", "doc3").get("raw"));

    reader.close();
    dir.close();
  }

  @Test
  public void testDocidConversion() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals("doc1", IndexReaderUtils.convertLuceneDocidToDocid(reader, 0));
    assertEquals("doc2", IndexReaderUtils.convertLuceneDocidToDocid(reader, 1));
    assertEquals("doc3", IndexReaderUtils.convertLuceneDocidToDocid(reader, 2));
    assertEquals(null, IndexReaderUtils.convertLuceneDocidToDocid(reader, 42));

    assertEquals(0, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc1"));
    assertEquals(1, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc2"));
    assertEquals(2, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc3"));
    assertEquals(-1, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc42"));

    reader.close();
    dir.close();
  }

  @Test
  public void testComputeQueryDocumentScore() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(tempDir1.toString());
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    Similarity similarity = new BM25Similarity(0.9f, 0.4f);

    // A bunch of test queries...
    String[] queries = {"text city", "text", "city"};

    for (String query: queries) {
      SimpleSearcher.Result[] results = searcher.search(query);

      // Strategy is to loop over the results, compute query-document score individually, and compare.
      for (int i = 0; i < results.length; i++) {
        float score = IndexReaderUtils.computeQueryDocumentScoreWithSimilarity(
            reader, results[i].docid, query, similarity);
        assertEquals(score, results[i].score, 10e-5);
      }

      // This is hard coded - doc3 isn't retrieved by any of the queries.
      assertEquals(0.0f, IndexReaderUtils.computeQueryDocumentScoreWithSimilarity(
              reader, "doc3", query, similarity), 10e-6);
    }

    reader.close();
    dir.close();
  }

  @Test
  public void testGetIndexStats() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals(3, IndexReaderUtils.getIndexStats(reader).get("documents"));
    assertEquals(Long.valueOf(6), IndexReaderUtils.getIndexStats(reader).get("unique_terms"));

    reader.close();
    dir.close();
  }

  @Test
  public void testGetFieldInfo() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, FieldInfo> fields = IndexReaderUtils.getFieldInfo(reader);
    assertTrue(fields.containsKey("id"));
    assertTrue(fields.containsKey("contents"));
    assertTrue(fields.containsKey("raw"));
    assertEquals(3, fields.size());

    reader.close();
    dir.close();
  }

  @Test
  public void testGetFieldInfoDescription() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, String> fields = IndexReaderUtils.getFieldInfoDescription(reader);
    assertEquals("(indexOption: DOCS, hasVectors: false)", fields.get("id"));
    assertEquals("(indexOption: DOCS_AND_FREQS_AND_POSITIONS, hasVectors: true)", fields.get("contents"));
    assertEquals("(indexOption: NONE, hasVectors: false)", fields.get("raw"));
    assertEquals(3, fields.size());

    reader.close();
    dir.close();
  }

  @Test
  public void testMain() throws Exception {
    // See: https://github.com/castorini/anserini/issues/903
    Locale.setDefault(Locale.US);

    final ByteArrayOutputStream redirectedStdout = new ByteArrayOutputStream();
    PrintStream savedStdout = System.out;
    redirectedStdout.reset();
    System.setOut(new PrintStream(redirectedStdout));

    IndexReaderUtils.main(new String[] {"-index", tempDir1.toString(), "-stats"});
    System.setOut(savedStdout);

    String groundTruthOutput = "Index statistics\n" +
        "----------------\n" +
        "documents:             3\n" +
        "documents (non-empty): 3\n" +
        "unique terms:          6\n" +
        "total terms:           12\n";

    assertEquals(groundTruthOutput, redirectedStdout.toString());
  }

}
