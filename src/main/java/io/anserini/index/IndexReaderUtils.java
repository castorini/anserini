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

import io.anserini.analysis.EnglishStemmingAnalyzer;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.analysis.AnalyzerUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import static java.util.stream.Collectors.joining;

/**
 * Class containing a bunch of static helper methods for accessing a Lucene inverted index.
 * This class provides a lot of functionality that is exposed in Python via Pyserini.
 */
public class IndexReaderUtils {
  private static final Logger LOG = LogManager.getLogger(IndexUtils.class);
  // The default analyzer used in indexing.
  private static final Analyzer DEFAULT_ANALYZER = IndexCollection.DEFAULT_ANALYZER;

  public enum DocumentVectorWeight {NONE, TF_IDF}

  /**
   * An individual posting in a postings list. Note that this class is used primarily for inspecting
   * the index, and not meant for actual searching.
   */
  public static class Posting {
    private int docId;
    private int termFreq;
    private int[] positions;

    /**
     * Constructor wrapping a {@link PostingsEnum} from Lucene.
     * @param postingsEnum posting from Lucene
     * @throws IOException if error encountered reading information from the posting
     */
    public Posting(PostingsEnum postingsEnum) throws IOException {
      this.docId = postingsEnum.docID();
      this.termFreq = postingsEnum.freq();
      this.positions = new int[this.termFreq];
      for (int j=0; j < this.termFreq; j++) {
        this.positions[j] = postingsEnum.nextPosition();
      }
    }

    /**
     * Returns the term frequency stored in this posting.
     * @return the term frequency stored in this posting
     */
    public int getTF() {
      return this.termFreq;
    }

    /**
     * Returns the internal Lucene docid associated with this posting.
     * @return the internal Lucene docid associated with this posting
     */
    public int getDocid() {
      return this.docId;
    }

    /**
     * Returns the positions in the document where this term is found.
     * @return the positions in the document where this term is found
     */
    public int[] getPositions() {
      return this.positions;
    }
  }

  /**
   * A term from the index. Note that this class is used primarily for inspecting the index, not
   * meant for actual searching.
   */
  public static class IndexTerm {
    private int docFreq;
    private String term;
    private long totalTermFreq;

    /**
     * Constructor wrapping a {@link TermsEnum} from Lucene.
     * @param term Lucene {@link TermsEnum} to wrap
     * @throws IOException if any errors are encountered
     */
    public IndexTerm(TermsEnum term) throws IOException {
      this.docFreq = term.docFreq();
      this.term = term.term().utf8ToString();
      this.totalTermFreq = term.totalTermFreq();
    }

    /**
     * Returns the number of documents containing the current term.
     * @return the number of documents containing the current term
     */
    public int getDF() {
      return this.docFreq;
    }

    /**
     * Returns the string representation of the current term.
     * @return the string representation of the current term
     */
    public String getTerm() {
      return this.term;
    }

    /**
     * Returns the total number of occurrences of the current term across all documents.
     * @return the total number of occurrences of the current term across all documents
     */
    public long getTotalTF() {
      return this.totalTermFreq;
    }
  }

  public static InputStream getReadFileStream(String path) throws IOException {
    InputStream fin = Files.newInputStream(Paths.get(path), StandardOpenOption.READ);
    BufferedInputStream in = new BufferedInputStream(fin);
    if (path.endsWith(".bz2")) {
      BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
      return bzIn;
    } else if (path.endsWith(".gz")) {
      GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
      return gzIn;
    } else if (path.endsWith(".zip")) {
      GzipCompressorInputStream zipIn = new GzipCompressorInputStream(in);
      return zipIn;
    }
    return in;
  }

  /**
   * Creates an {@link IndexReader} given a path.
   * @param path index path
   * @return index reader
   * @throws IOException if any errors are encountered
   */
  public static IndexReader getReader(String path) throws IOException {
    Directory dir = FSDirectory.open(Paths.get(path));
    return DirectoryReader.open(dir);
  }

  /**
   * Feeds a string through the {@link EnglishStemmingAnalyzer} and returns the list of stemmed tokens.
   * @param text input string
   * @return list of stemmed tokens
   */
  public static List<String> analyze(String text) {
    return analyzeWithAnalyzer(text, DEFAULT_ANALYZER);
  }

  /**
   * Feeds a string through an analyzer and returns the list of stemmed tokens.
   * @param text input string
   * @param analyzer analyzer to use
   * @return list of stemmed tokens
   */
  public static List<String> analyzeWithAnalyzer(String text, Analyzer analyzer) {
    return AnalyzerUtils.tokenize(analyzer, text);
  }

  public static Map<String, Long> getTermCounts(IndexReader reader, String termStr) throws IOException, ParseException {
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    TermQuery q = (TermQuery) qp.parse(termStr);
    Term t = q.getTerm();

    Map<String, Long> termInfo = Map.ofEntries(
        Map.entry("collectionFreq", reader.totalTermFreq(t)),
        Map.entry("docFreq", Long.valueOf(reader.docFreq(t)))
    );

    return termInfo;
  }

  /**
   * Returns iterator over all terms in the collection.
   * @param reader index reader
   * @return iterator over IndexTerm
   * @throws IOException if error encountered during access to index
   */
  public static Iterator<IndexTerm> getTerms(IndexReader reader) throws IOException {
    return new Iterator<IndexTerm>() {
      private TermsEnum curTerm = MultiTerms.getTerms(reader, "contents").iterator();
      private BytesRef bytesRef = null;

      @Override
      public boolean hasNext() {
        try {
          // Make sure iterator is positioned.
          if (this.bytesRef == null) {
            return true;
          }

          BytesRef originalPos = BytesRef.deepCopyOf(this.bytesRef);
          if (this.curTerm.next() == null) {
            return false;
          } else {
            // Move curTerm back to original position.
            return this.curTerm.seekExact(originalPos);
          }
        } catch (IOException e) {
          return false;
        }
      }

      @Override
      public IndexTerm next() {
        try {
          this.bytesRef = this.curTerm.next();
          return new IndexTerm(this.curTerm);
        } catch (IOException e) {
          return null;
        }
      }
    };
  }

  public static List<Posting> getPostingsList(IndexReader reader, String termStr)
      throws IOException, ParseException {
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    TermQuery q = (TermQuery) qp.parse(termStr);
    Term t = q.getTerm();

    PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader,
        LuceneDocumentGenerator.FIELD_BODY, t.bytes());

    List<Posting> postingsList = new ArrayList<>();
    while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
      postingsList.add(new Posting(postingsEnum));
    }

    return postingsList;
  }

  /**
   * Returns the document vector for a particular document as a map of terms to term frequencies.
   * @param reader index reader
   * @param docid collection docid
   * @return the document vector for a particular document as a map of terms to term frequencies
   * @throws IOException if error encountered during query
   * @throws NotStoredException if the term vector is not stored
   */
  public static Map<String, Long> getDocumentVector(IndexReader reader, String docid) throws IOException, NotStoredException {
    Terms terms = reader.getTermVector(convertDocidToLuceneDocid(reader, docid), LuceneDocumentGenerator.FIELD_BODY);
    if (terms == null) {
      throw new NotStoredException("Document vector not stored!");
    }
    TermsEnum te = terms.iterator();
    if (te == null) {
      throw new NotStoredException("Document vector not stored!");
    }

    Map<String, Long> docVector = new HashMap<>();
    while ((te.next()) != null) {
      docVector.put(te.term().utf8ToString(), te.totalTermFreq());
    }

    return docVector;
  }

  /**
   * Returns the raw document given its collection docid.
   * @param reader index reader
   * @param docid collection docid
   * @return the raw document given its collection docid, or <code>null</code> if not found.
   */
  public static String getRawDocument(IndexReader reader, String docid) {
    try {
      Document rawDoc = reader.document(convertDocidToLuceneDocid(reader, docid));

      if (rawDoc == null) {
        return null;
      }
      return rawDoc.get(LuceneDocumentGenerator.FIELD_RAW);
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Computes the BM25 weight of a term (prior to analysis) in a particular document.
   * @param reader index reader
   * @param docid collection docid
   * @param term term (prior to analysis)
   * @return BM25 weight of the term in the specified document
   * @throws IOException if error encountered during query
   */
  public static float getBM25TermWeight(IndexReader reader, String docid, String term) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());

    // The way to compute the BM25 score is to issue a query with the exact docid and the
    // term in question, and look at the retrieval score.
    Query filterQuery = new ConstantScoreQuery(new TermQuery(new Term(LuceneDocumentGenerator.FIELD_ID, docid)));
    Query termQuery = new TermQuery(new Term(LuceneDocumentGenerator.FIELD_BODY, term));
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filterQuery, BooleanClause.Occur.MUST);
    builder.add(termQuery, BooleanClause.Occur.MUST);
    Query finalQuery = builder.build();
    TopDocs rs = searcher.search(finalQuery, 1);

    // The BM25 weight is the score of the first (and only) hit, but remember to remove 1 for the ConstantScoreQuery
    return rs.scoreDocs.length == 0 ? Float.NaN : rs.scoreDocs[0].score - 1;
  }

  public static void dumpDocumentVectors(IndexReader reader, String reqDocidsPath, DocumentVectorWeight weight) throws IOException {
    String outFileName = weight == null ? reqDocidsPath+".docvector.tar.gz" : reqDocidsPath+".docvector." + weight +".tar.gz";
    LOG.info("Start dump document vectors with weight " + weight);

    InputStream in = getReadFileStream(reqDocidsPath);
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(in));
    FileOutputStream fOut = new FileOutputStream(new File(outFileName));
    BufferedOutputStream bOut = new BufferedOutputStream(fOut);
    GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
    TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);

    Map<Term, Integer> docFreqMap = new HashMap<>();

    int numNonEmptyDocs = reader.getDocCount(LuceneDocumentGenerator.FIELD_BODY);

    String docid;
    int counter = 0;
    while ((docid = bRdr.readLine()) != null) {
      counter++;

      int internalDocid = convertDocidToLuceneDocid(reader, docid);
      if (internalDocid == -1) {
        continue;
      }

      // get term frequency
      Terms terms = reader.getTermVector(internalDocid, LuceneDocumentGenerator.FIELD_BODY);
      if (terms == null) {
        // We do not throw exception here because there are some
        //  collections in which part of documents don't have document vectors
        LOG.warn("Document vector not stored for doc " + docid);
        continue;
      }

      TermsEnum te = terms.iterator();
      if (te == null) {
        LOG.warn("Document vector not stored for doc " + docid);
        continue;
      }

      Term term;
      long freq;

      // iterate every term and write and store in Map
      Map<String, String> docVectors = new HashMap<>();
      while ((te.next()) != null) {
        term = new Term(LuceneDocumentGenerator.FIELD_BODY, te.term());
        freq = te.totalTermFreq();

        switch (weight) {
          case NONE:
            docVectors.put(term.bytes().utf8ToString(), String.valueOf(freq));
            break;

          case TF_IDF:
            int docFreq;
            if (docFreqMap.containsKey(term)) {
              docFreq = docFreqMap.get(term);
            } else {
              try {
                docFreq = reader.docFreq(term);
              } catch (Exception e) {
                LOG.error("Cannot find term " + term.toString() + " in indexing file.");
                continue;
              }
              docFreqMap.put(term, docFreq);
            }
            float tfIdf = (float) (freq * Math.log(numNonEmptyDocs * 1.0 / docFreq));
            docVectors.put(term.bytes().utf8ToString(), String.format("%.6f", tfIdf));
            break;
        }
      }

      // Count size and write
      byte[] bytesOut = docVectors.entrySet()
              .stream()
              .map(e -> e.getKey()+" "+e.getValue())
              .collect(joining("\n"))
              .getBytes(StandardCharsets.UTF_8);

      TarArchiveEntry tarEntry = new TarArchiveEntry(new File(docid));
      tarEntry.setSize(bytesOut.length + String.format("<DOCNO>%s</DOCNO>\n", docid).length());
      tOut.putArchiveEntry(tarEntry);
      tOut.write(String.format("<DOCNO>%s</DOCNO>\n", docid).getBytes());
      tOut.write(bytesOut);
      tOut.closeArchiveEntry();

      if (counter % 100000 == 0) {
        LOG.info(counter + " files have been dumped.");
      }
    }
    tOut.close();
    LOG.info("Document Vectors are output to: " + outFileName);
  }

  /**
   * Converts a collection docid to a Lucene internal docid
   * @param reader index reader
   * @param docid collection docid
   * @return corresponding Lucene internal docid, or -1 if docid not found
   */
  public static int convertDocidToLuceneDocid(IndexReader reader, String docid) {
    try {
      IndexSearcher searcher = new IndexSearcher(reader);
      Query q = new TermQuery(new Term(LuceneDocumentGenerator.FIELD_ID, docid));
      TopDocs rs = searcher.search(q, 1);
      ScoreDoc[] hits = rs.scoreDocs;

      if (hits == null || hits.length == 0) {
        // Silently eat the error and return -1
        return -1;
      }

      return hits[0].doc;
    } catch (IOException e) {
      // Silently eat the error and return -1
      return -1;
    }
  }

  /**
   * Converts a Lucene internal docid to a collection docid
   * @param reader index reader
   * @param docid Lucene internal docid
   * @return corresponding collection docid, or <code>null</code> if not found.
   */
  public static String convertLuceneDocidToDocid(IndexReader reader, int docid) {
    if (docid >= reader.maxDoc())
      return null;

    try {
      Document d = reader.document(docid);
      if (d == null) {
        return null;
      }
      IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_ID);
      if (doc == null) {
        // Really shouldn't happen! Index not properly built?
        return null;
      }
      return doc.stringValue();
    } catch (IOException e) {
      return null;
    }
  }
}
