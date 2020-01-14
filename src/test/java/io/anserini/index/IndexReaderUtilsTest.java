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
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IndexReaderUtilsTest extends IndexerTestBase {

  @Test
  public void testAnalyzer() throws ParseException {
    // EnglishAnalyzer by default.
    assertEquals("citi", String.join(" ", IndexReaderUtils.analyze("city")));
    assertEquals("citi buse", String.join(" ", IndexReaderUtils.analyze("city buses")));

    // Shouldn't change the term
    assertEquals("city", String.join(" ", IndexReaderUtils.analyzeWithAnalyzer("city", new WhitespaceAnalyzer())));
    assertEquals("city buses", String.join(" ", IndexReaderUtils.analyzeWithAnalyzer("city buses", new WhitespaceAnalyzer())));
  }

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
  }

  @Test
  public void testIterateThroughTerms() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Iterator<IndexReaderUtils.IndexTerm> iter = IndexReaderUtils.getTerms(reader);
    IndexReaderUtils.IndexTerm term;

    // here
    if (iter.hasNext()) {
      term = iter.next();
      assertEquals("here", term.getTerm());
      assertEquals(2, term.getDF());
      assertEquals(3, term.getTotalTF());
    }

    // more
    if (iter.hasNext()) {
      term = iter.next();
      assertEquals("more", term.getTerm());
      assertEquals(2, term.getDF());
      assertEquals(2, term.getTotalTF());
    }

    // some
    if (iter.hasNext()) {
      term = iter.next();
      assertEquals("some", term.getTerm());
      assertEquals(1, term.getDF());
      assertEquals(2, term.getTotalTF());
    }

    // test
    if (iter.hasNext()) {
      term = iter.next();
      assertEquals("test", term.getTerm());
      assertEquals(1, term.getDF());
      assertEquals(1, term.getTotalTF());
    }

    if (iter.hasNext()) {
      term = iter.next();
      assertEquals("text", term.getTerm());
      assertEquals(2, term.getDF());
      assertEquals(3, term.getTotalTF());
    }

    assertEquals(false, iter.hasNext());
  }

  @Test
  public void testPostingsLists() throws Exception {
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

    // text: (0, 2) [3, 8] (1, 1) [1]
    postingsList = IndexReaderUtils.getPostingsList(reader, "text");
    assertEquals(2, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {3, 8}, postingsList.get(0).getPositions());
    assertEquals(1, postingsList.get(1).getTF());
    assertEquals(1, postingsList.get(1).getDocid());
    assertArrayEquals(new int[] {1}, postingsList.get(1).getPositions());
  }

  @Test
  public void computeAllTermBM25Weights() throws Exception {
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
      System.out.println(term);

      IndexSearcher searcher = new IndexSearcher(reader);
      searcher.setSimilarity(new BM25Similarity());

      TopDocs rs = searcher.search(new TermQuery(new Term("contents", term)), 3);
      for (int i=0; i<rs.scoreDocs.length; i++) {
        String docid = reader.document(rs.scoreDocs[i].doc).getField("id").stringValue();
        System.out.println(docid + " " + rs.scoreDocs[i].score);
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
      System.out.println(reader.document(i) + " " + docid);

      // For this document, iterate through the terms.
      termsEnum = termVector.iterator();
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        float weight = IndexReaderUtils.getBM25TermWeight(reader, docid, term);
        System.out.println(term + " " + weight);
        assertEquals(termDocMatrix.get(term).get(docid), weight, 10e-6);
      }
    }
  }

  @Test
  public void testDocumentVector() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, Long> documentVector;

    System.out.println("doc1");
    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc1");
    assertEquals(Long.valueOf(2), documentVector.get("here"));
    assertEquals(Long.valueOf(1), documentVector.get("more"));
    assertEquals(Long.valueOf(2), documentVector.get("some"));
    assertEquals(Long.valueOf(2), documentVector.get("text"));

    System.out.println("doc2");
    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc2");
    assertEquals(Long.valueOf(1), documentVector.get("more"));
    assertEquals(Long.valueOf(1), documentVector.get("text"));

    System.out.println("doc3");
    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc3");
    assertEquals(Long.valueOf(1), documentVector.get("here"));
    assertEquals(Long.valueOf(1), documentVector.get("test"));
  }

  @Test
  public void testRawDoc() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals("here is some text here is some more text", IndexReaderUtils.getRawDocument(reader, "doc1"));
    assertEquals("more texts", IndexReaderUtils.getRawDocument(reader, "doc2"));
    assertEquals("here is a test", IndexReaderUtils.getRawDocument(reader, "doc3"));
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

  }
}
