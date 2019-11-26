/**
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

import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import static java.util.stream.Collectors.joining;

public class IndexReaderUtils {
  private static final Logger LOG = LogManager.getLogger(IndexUtils.class);

  public enum DocumentVectorWeight {NONE, TF_IDF}

  public static class NotStoredException extends Exception {
    public NotStoredException(String message) {
      super(message);
    }
  }

  public static class Posting {
    private int docId;
    private int termFreq;
    private int[] positions;

    public Posting() {}

    public Posting(PostingsEnum postingsEnum) throws IOException {
      this.docId = postingsEnum.docID();
      this.termFreq = postingsEnum.freq();
      this.positions = new int[this.termFreq];
      for (int j=0; j < this.termFreq; j++) {
        this.positions[j] = postingsEnum.nextPosition();
      }
    }

    public int getTF() {
      return this.termFreq;
    }

    public int getDocid() {
      return this.docId;
    }

    public int[] getPositions() {
      return this.positions;
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

  public static IndexReader getReader(String path) throws IOException {
    Directory dir = FSDirectory.open(Paths.get(path));
    return DirectoryReader.open(dir);
  }

  public static String analyzeTerm(IndexReader reader, String termStr) throws IOException, ParseException {
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    TermQuery q = (TermQuery) qp.parse(termStr);
    Term t = q.getTerm();

    // Return stemmed form
    return q.toString(LuceneDocumentGenerator.FIELD_BODY);
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

  public static List<Posting> getPostingsList(IndexReader reader, String termStr) throws IOException, ParseException {
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    TermQuery q = (TermQuery) qp.parse(termStr);
    Term t = q.getTerm();

    PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader, LuceneDocumentGenerator.FIELD_BODY, t.bytes());

    List<Posting> postingsList = new ArrayList<>();
    while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
      postingsList.add(new Posting(postingsEnum));
    }

    return postingsList;
  }

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
   * Computes the BM25 weight of a term in a particular document.
   * @param reader index reader
   * @param docid external collection docid
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

  public static int convertDocidToLuceneDocid(IndexReader reader, String docid) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q = new TermQuery(new Term(LuceneDocumentGenerator.FIELD_ID, docid));
    TopDocs rs = searcher.search(q, 1);
    ScoreDoc[] hits = rs.scoreDocs;

    if (hits == null || hits.length == 0) {
      LOG.warn(String.format("Docid %s not found!", docid));
      return -1;
    }

    return hits[0].doc;
  }

  public static String convertLuceneDocidToDocid(IndexReader reader, int docid) throws IOException {
    Document d = reader.document(docid);
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_ID);
    if (doc == null) {
      // Really shouldn't happen!
      throw new RuntimeException();
    }
    return doc.stringValue();
  }
}
