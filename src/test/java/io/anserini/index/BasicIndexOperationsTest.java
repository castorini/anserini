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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BasicIndexOperationsTest extends IndexerTestBase {

  // A very simple example of how to iterate through terms in an index and dump out postings.
  private void dumpPostings(IndexReader reader) throws IOException {
    // This is how you iterate through terms in the postings list.
    TermsEnum termsEnum = MultiTerms.getTerms(reader, "contents").iterator();
    BytesRef bytesRef = termsEnum.next();
    while (bytesRef != null) {
      // This is the current term in the dictionary.
      String token = bytesRef.utf8ToString();
      Term term = new Term("contents", token);

      // How to dump out positional info as well, from test case at:
      // https://github.com/apache/lucene-solr/blob/master/lucene/core/src/test/org/apache/lucene/index/TestPostingsOffsets.java
      System.out.print(token + " (df = " + reader.docFreq(term) + "):");
      PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader, "contents", bytesRef);
      while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        System.out.print(String.format(" (%d, %d)", postingsEnum.docID(), postingsEnum.freq()));
        System.out.print(" [");
        for (int j=0; j<postingsEnum.freq(); j++) {
          System.out.print((j != 0 ? ", " : "") + postingsEnum.nextPosition());
        }
        System.out.print("]");
      }
      System.out.println("");

      bytesRef = termsEnum.next();
    }
  }

  @Test
  public void readNorms() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<Integer, Integer> norms = new HashMap<>();
    for (LeafReaderContext context : reader.leaves()) {
      LeafReader leafReader = context.reader();
      NumericDocValues docValues = leafReader.getNormValues("contents");
      while (docValues.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        norms.put(docValues.docID() + context.docBase, SmallFloat.byte4ToInt((byte) docValues.longValue()));
      }
    }

    assertEquals(3, norms.size());
    assertEquals(7, (int) norms.get(0));
    assertEquals(2, (int) norms.get(1));
    assertEquals(2, (int) norms.get(2));
  }

  @Test
  public void testReadingPostings() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    assertEquals(3, reader.numDocs());
    assertEquals(1, reader.leaves().size());

    System.out.println("Dumping out postings...");
    dumpPostings(reader);

    assertEquals(2, reader.docFreq(new Term("contents", "here")));
    assertEquals(2, reader.docFreq(new Term("contents", "more")));
    assertEquals(1, reader.docFreq(new Term("contents", "some")));
    assertEquals(1, reader.docFreq(new Term("contents", "test")));
    assertEquals(2, reader.docFreq(new Term("contents", "text")));

    PostingsEnum postingsEnum;
    // here (df = 2): (0, 2) [0, 4] (2, 1) [0]
    postingsEnum = MultiTerms.getTermPostingsEnum(reader, "contents", new BytesRef("here"));
    assertEquals(0, postingsEnum.nextDoc());
    assertEquals(0, postingsEnum.docID());
    assertEquals(2, postingsEnum.freq());
    assertEquals(0, postingsEnum.nextPosition());
    assertEquals(4, postingsEnum.nextPosition());
    assertEquals(2, postingsEnum.nextDoc());
    assertEquals(2, postingsEnum.docID());
    assertEquals(1, postingsEnum.freq());
    assertEquals(0, postingsEnum.nextPosition());
    assertEquals(DocIdSetIterator.NO_MORE_DOCS, postingsEnum.nextDoc());

    // more (df = 2): (0, 1) [7] (1, 1) [0]
    postingsEnum = MultiTerms.getTermPostingsEnum(reader, "contents", new BytesRef("more"));
    assertEquals(0, postingsEnum.nextDoc());
    assertEquals(0, postingsEnum.docID());
    assertEquals(1, postingsEnum.freq());
    assertEquals(7, postingsEnum.nextPosition());
    assertEquals(1, postingsEnum.nextDoc());
    assertEquals(1, postingsEnum.docID());
    assertEquals(1, postingsEnum.freq());
    assertEquals(0, postingsEnum.nextPosition());
    assertEquals(DocIdSetIterator.NO_MORE_DOCS, postingsEnum.nextDoc());

    // some (df = 1): (0, 2) [2, 6]
    postingsEnum = MultiTerms.getTermPostingsEnum(reader, "contents", new BytesRef("some"));
    assertEquals(0, postingsEnum.nextDoc());
    assertEquals(0, postingsEnum.docID());
    assertEquals(2, postingsEnum.freq());
    assertEquals(2, postingsEnum.nextPosition());
    assertEquals(6, postingsEnum.nextPosition());
    assertEquals(DocIdSetIterator.NO_MORE_DOCS, postingsEnum.nextDoc());

    // test (df = 1): (2, 1) [3]
    postingsEnum = MultiTerms.getTermPostingsEnum(reader, "contents", new BytesRef("test"));
    assertEquals(2, postingsEnum.nextDoc());
    assertEquals(2, postingsEnum.docID());
    assertEquals(1, postingsEnum.freq());
    assertEquals(3, postingsEnum.nextPosition());
    assertEquals(DocIdSetIterator.NO_MORE_DOCS, postingsEnum.nextDoc());

    // text (df = 2): (0, 2) [3, 8] (1, 1) [1]
    postingsEnum = MultiTerms.getTermPostingsEnum(reader, "contents", new BytesRef("text"));
    assertEquals(0, postingsEnum.nextDoc());
    assertEquals(0, postingsEnum.docID());
    assertEquals(2, postingsEnum.freq());
    assertEquals(3, postingsEnum.nextPosition());
    assertEquals(8, postingsEnum.nextPosition());
    assertEquals(1, postingsEnum.nextDoc());
    assertEquals(1, postingsEnum.docID());
    assertEquals(1, postingsEnum.freq());
    assertEquals(1, postingsEnum.nextPosition());
    assertEquals(DocIdSetIterator.NO_MORE_DOCS, postingsEnum.nextDoc());

    reader.close();
  }

  // This test case iterates through all documents in the index and prints out the document vector:
  // For each term, we print out the term frequency.
  @Test
  public void testIterateThroughDocumentVector() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    int numDocs = reader.numDocs();
    // Iterate through the document vectors
    for (int i=0; i<numDocs; i++) {
      System.out.println(reader.document(i));
      Terms terms = reader.getTermVector(i, "contents");
      TermsEnum te = terms.iterator();

      // For this document, iterate through the terms.
      Term term;
      while (te.next() != null) {
        term = new Term("contents", te.term());
        long tf = te.totalTermFreq();
        // Print out the term and its term frequency
        System.out.println(term.bytes().utf8ToString() + " " + tf);
      }
    }
  }

  // This test case iterates through all documents in the index and prints out the document vector:
  // For each term, we print out the term frequency and the BM25 weight.
  @Test
  public void testIterateThroughDocumentVectorComputeBM25() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());

    int numDocs = reader.numDocs();
    // Iterate through the document vectors
    for (int i=0; i<numDocs; i++) {
      String docid = reader.document(i).getField("id").stringValue();
      System.out.println(reader.document(i));
      System.out.println(i+ ": " + docid);
      Terms terms = reader.getTermVector(i, "contents");
      TermsEnum te = terms.iterator();

      // For this document, iterate through the terms.
      while (te.next() != null) {
        String term = new Term("contents", te.term()).bytes().utf8ToString();
        long tf = te.totalTermFreq();

        // The way to compute the BM25 score is to issue a query with the exact docid and the
        // term in question, and look at the retrieval score.
        Query filterQuery = new ConstantScoreQuery(new TermQuery(new Term("id", docid)));     // the docid
        Query termQuery = new TermQuery(new Term("contents", term));  // the term
        BooleanQuery.Builder builder = new BooleanQuery.Builder();        // must have both
        builder.add(filterQuery, BooleanClause.Occur.MUST);
        builder.add(termQuery, BooleanClause.Occur.MUST);
        Query finalQuery = builder.build();
        TopDocs rs = searcher.search(finalQuery, 1);                 // issue the query

        // The BM25 weight is the maxScore
        System.out.println(term + " " + tf + " " + (rs.scoreDocs.length == 0 ? Float.NaN : rs.scoreDocs[0].score - 1));
      }
    }
  }
}
