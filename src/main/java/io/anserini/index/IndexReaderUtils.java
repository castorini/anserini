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

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.search.SearchArgs;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.PhraseQueryGenerator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class containing a bunch of static helper methods for accessing a Lucene inverted index.
 * This class provides a lot of functionality that is exposed in Python via Pyserini.
 */
public class IndexReaderUtils {

  /**
   * An individual posting in a postings list. Note that this class is used primarily for inspecting
   * the index, and not meant for actual searching.
   */
  public static class Posting {
    private final int docId;
    private final int termFreq;
    private final int[] positions;

    /**
     * Constructor wrapping a {@link PostingsEnum} from Lucene.
     *
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
     *
     * @return the term frequency stored in this posting
     */
    public int getTF() {
      return this.termFreq;
    }

    /**
     * Returns the internal Lucene docid associated with this posting.
     *
     * @return the internal Lucene docid associated with this posting
     */
    public int getDocid() {
      return this.docId;
    }

    /**
     * Returns the positions in the document where this term is found.
     *
     * @return the positions in the document where this term is found
     */
    public int[] getPositions() {
      return this.positions;
    }
  }

  /**
   * A term from the index. Note that this class is used primarily for inspecting the index, not meant for actual
   * searching.
   */
  public static class IndexTerm {
    private final int docFreq;
    private final String term;
    private final long totalTermFreq;

    /**
     * Constructor wrapping a {@link TermsEnum} from Lucene.
     *
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
     *
     * @return the number of documents containing the current term
     */
    public int getDF() {
      return this.docFreq;
    }

    /**
     * Returns the string representation of the current term.
     *
     * @return the string representation of the current term
     */
    public String getTerm() {
      return this.term;
    }

    /**
     * Returns the total number of occurrences of the current term across all documents.
     *
     * @return the total number of occurrences of the current term across all documents
     */
    public long getTotalTF() {
      return this.totalTermFreq;
    }
  }

  /**
   * Creates an {@link IndexReader} given a path.
   *
   * @param path index path
   * @return index reader
   * @throws IOException if any errors are encountered
   */
  public static IndexReader getReader(String path) throws IOException {
    Directory dir = FSDirectory.open(Paths.get(path));
    return DirectoryReader.open(dir);
  }

  /**
   * Returns count information on a term or a phrase.
   *
   * @param reader index reader
   * @param termStr term
   * @return df (+cf if only one term) of the phrase using default analyzer
   * @throws IOException if error encountered during access to index
   */
  public static Map<String, Long> getTermCounts(IndexReader reader, String termStr)
      throws IOException {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    return getTermCountsWithAnalyzer(reader, termStr, analyzer);
  }

  /**
   * Returns count information on a term or a phrase.
   *
   * @param reader index reader
   * @param termStr term
   * @param analyzer analyzer to use
   * @return df (+cf if only one term) of the phrase
   * @throws IOException if error encountered during access to index
   */
  public static Map<String, Long> getTermCountsWithAnalyzer(IndexReader reader, String termStr, Analyzer analyzer)
      throws IOException {
    if (AnalyzerUtils.analyze(analyzer, termStr).size() > 1) {
      Query query = new PhraseQueryGenerator().buildQuery(IndexArgs.CONTENTS, analyzer, termStr);
      IndexSearcher searcher = new IndexSearcher(reader);
      TotalHitCountCollector totalHitCountCollector = new TotalHitCountCollector();
      searcher.search(query, totalHitCountCollector);
      return Map.ofEntries(
        Map.entry("docFreq", (long) totalHitCountCollector.getTotalHits())
      );
    }

    Term t = new Term(IndexArgs.CONTENTS, AnalyzerUtils.analyze(analyzer, termStr).get(0));
    Map<String, Long> termInfo = Map.ofEntries(
      Map.entry("collectionFreq", reader.totalTermFreq(t)),
      Map.entry("docFreq", (long) reader.docFreq(t))
    );
    return termInfo;
  }

  /**
   * Returns the document frequency of a term. Simply dispatches to <code>docFreq</code> but wraps the exception so
   * that the caller doesn't need to deal with it; this is potentially dangerous but makes code less verbose.
   *
   * @param reader index reader
   * @param term term
   * @return the document frequency of a term
   */
  public static long getDF(IndexReader reader, String term) {
    try {
      return reader.docFreq(new Term(IndexArgs.CONTENTS, term));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns iterator over all terms in the collection.
   *
   * @param reader index reader
   * @return iterator over IndexTerm
   * @throws IOException if error encountered during access to index
   */
  public static Iterator<IndexTerm> getTerms(IndexReader reader) throws IOException {
    return new Iterator<>() {
      private final TermsEnum curTerm = MultiTerms.getTerms(reader, "contents").iterator();
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

  /**
   * Returns the postings list for an unanalyzed term. That is, the method analyzes the term before looking up its
   * postings list.
   *
   * @param reader index reader
   * @param term unanalyzed term
   * @return the postings list for an unanalyzed term
   */
  public static List<Posting> getPostingsList(IndexReader reader, String term) {
    return _getPostingsList(reader, AnalyzerUtils.analyze(term).get(0));
  }

  /**
   * Returns the postings list for a term.
   *
   * @param reader index reader
   * @param term term
   * @param analyze whether or not the method should analyze the term first
   * @return the postings list for a term
   */
  public static List<Posting> getPostingsList(IndexReader reader, String term, boolean analyze) {
    return _getPostingsList(reader, analyze ? AnalyzerUtils.analyze(term).get(0) : term);
  }

  /**
   * Returns the postings list for a term after analysis with a specific analyzer.
   *
   * @param reader index reader
   * @param term term
   * @param analyzer analyzer
   * @return the postings list for an unanalyzed term
   */
  public static List<Posting> getPostingsList(IndexReader reader, String term, Analyzer analyzer) {
    return _getPostingsList(reader, AnalyzerUtils.analyze(analyzer, term).get(0));
  }

  // Internal helper: takes the analyzed form in all cases.
  private static List<Posting> _getPostingsList(IndexReader reader, String analyzedTerm) {
    try {
      Term t = new Term(IndexArgs.CONTENTS, analyzedTerm);
      PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader, IndexArgs.CONTENTS, t.bytes());

      List<Posting> postingsList = new ArrayList<>();
      while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        postingsList.add(new Posting(postingsEnum));
      }

      return postingsList;
    } catch (Exception e) {
      return null;
    }
  }

  // These are bindings for Pyserini to work, because Pyjnius can't seem to distinguish overloaded methods.
  // jnius.JavaException: No methods matching your arguments
  public static List<Posting> getPostingsListForUnanalyzedTerm(IndexReader reader, String term) {
    return getPostingsList(reader, term, true);
  }

  public static List<Posting> getPostingsListForAnalyzedTerm(IndexReader reader, String term) {
    return getPostingsList(reader, term, false);
  }

  public static List<Posting> getPostingsListWithAnalyzer(IndexReader reader, String term, Analyzer analyzer) {
    return getPostingsList(reader, term, analyzer);
  }

  /**
   * Returns the document vector for a particular document as a map of terms to term frequencies. Note that this
   * method explicitly returns {@code null} if the document does not exist (as opposed to an empty map), so that the
   * caller is explicitly forced to handle this case.
   *
   * @param reader index reader
   * @param docid collection docid
   * @return the document vector for a particular document as a map of terms to term frequencies or {@code null} if
   * document does not exist.
   * @throws IOException if error encountered during query
   * @throws NotStoredException if the term vector is not stored
   */
  public static Map<String, Long> getDocumentVector(IndexReader reader, String docid) throws IOException, NotStoredException {
    int ldocid = convertDocidToLuceneDocid(reader, docid);
    if (ldocid == -1) {
      return null;
    }
    Terms terms = reader.getTermVector(ldocid, IndexArgs.CONTENTS);
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
   * Returns the term position mapping for a particular document. Note that this method explicitly returns
   * {@code null} if the document does not exist (as opposed to an empty map), so that the caller is explicitly forced
   * to handle this case.
   *
   * @param reader index reader
   * @param docid collection docid
   * @return term position mapping for a particular document or {@code null} if document does not exist.
   * @throws IOException if error encountered during query
   * @throws NotStoredException if the term vector is not stored
   */
  public static Map<String, List<Integer>> getTermPositions(IndexReader reader, String docid) throws IOException, NotStoredException {
    int ldocid = convertDocidToLuceneDocid(reader, docid);
    if (ldocid == -1) {
      return null;
    }
    Terms terms = reader.getTermVector(ldocid, IndexArgs.CONTENTS);
    if (terms == null) {
      throw new NotStoredException("Document vector not stored!");
    }
    TermsEnum termIter = terms.iterator();
    if (termIter == null) {
      throw new NotStoredException("Document vector not stored!");
    }

    Map<String, List<Integer>> termPosition = new HashMap<>();
    PostingsEnum positionIter = null;

    while ((termIter.next()) != null) {
      List<Integer> positions = new ArrayList<>();
      long termFreq = termIter.totalTermFreq();
      positionIter = termIter.postings(positionIter, PostingsEnum.POSITIONS);
      positionIter.nextDoc();
      for ( int i = 0; i < termFreq; i++ ) {
        positions.add(positionIter.nextPosition());
      }
      termPosition.put(termIter.term().utf8ToString(), positions);
    }

    return termPosition;
  }

  /**
   * Returns the Lucene {@link Document} based on a collection docid. The method is named to be consistent with Lucene's
   * {@link IndexReader#document(int)}, contra Java's standard method naming conventions.
   *
   * @param reader index reader
   * @param docid collection docid
   * @return corresponding Lucene {@link Document}
   */
  public static Document document(IndexReader reader, String docid) {
    try {
      return reader.document(IndexReaderUtils.convertDocidToLuceneDocid(reader, docid));
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Fetches the Lucene {@link Document} based on some field other than its unique collection docid. For example,
   * scientific articles might have DOIs. The method is named to be consistent with Lucene's
   * {@link IndexReader#document(int)}, contra Java's standard method naming conventions.
   *
   * @param reader index reader
   * @param field field
   * @param id unique id
   * @return corresponding Lucene {@link Document} based on the value of a specific field
   */
  public static Document documentByField(IndexReader reader, String field, String id) {
    try {
      IndexSearcher searcher = new IndexSearcher(reader);
      Query q = new TermQuery(new Term(field, id));
      TopDocs rs = searcher.search(q, 1);
      ScoreDoc[] hits = rs.scoreDocs;

      if (hits == null || hits.length == 0) {
        // Either the id doesn't exist or there are multiple documents with the same id. In both cases, return null.
        return null;
      }

      return reader.document(hits[0].doc);
    } catch (IOException e) {
      // Silently eat the error and return null
      return null;
    }
  }

  /**
   * Returns the "raw" field of a document based on a collection docid. The method is named to be consistent with
   * Lucene's {@link IndexReader#document(int)}, contra Java's standard method naming conventions.
   *
   * @param reader index reader
   * @param docid collection docid
   * @return the "raw" field the document
   */
  public static String documentRaw(IndexReader reader, String docid) {
    try {
      return reader.document(convertDocidToLuceneDocid(reader, docid)).get(IndexArgs.RAW);
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Returns the "contents" field of a document based on a collection docid. The method is named to be consistent with
   * Lucene's {@link IndexReader#document(int)}, contra Java's standard method naming conventions.
   *
   * @param reader index reader
   * @param docid collection docid
   * @return the "contents" field the document
   */
  public static String documentContents(IndexReader reader, String docid) {
    try {
      return reader.document(convertDocidToLuceneDocid(reader, docid)).get(IndexArgs.CONTENTS);
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Computes the BM25 weight of an analyzed term in a particular document (with Anserini default parameters).
   *
   * @param reader index reader
   * @param docid collection docid
   * @param term analyzed term
   * @return BM25 weight of the term in the specified document
   * @throws IOException if error encountered during query
   */
  public static float getBM25AnalyzedTermWeight(IndexReader reader, String docid, String term) throws IOException {
    SearchArgs args = new SearchArgs();
    return getBM25AnalyzedTermWeightWithParameters(reader, docid, term,
        Float.parseFloat(args.bm25_k1[0]), Float.parseFloat(args.bm25_b[0]));
  }

  /**
   * Computes the BM25 weight of an analyzed term in a particular document.
   *
   * @param reader index reader
   * @param docid collection docid
   * @param term analyzed term
   * @param k1 k1 setting for BM25
   * @param b b setting for BM25
   * @return BM25 weight of the term in the specified document
   * @throws IOException if error encountered during query
   */
  public static float getBM25AnalyzedTermWeightWithParameters(IndexReader reader, String docid, String term, float k1, float b)
      throws IOException {
    // We compute the BM25 score by issuing a single-term query with an additional filter clause that restricts
    // consideration to only the docid in question, and then returning the retrieval score.
    //
    // This implementation is inefficient, but as the advantage of using the existing Lucene similarity, which means
    // that we don't need to copy the scoring function and keep it in sync wrt code updates.

    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity(k1, b));

    Query filterQuery = new ConstantScoreQuery(new TermQuery(new Term(IndexArgs.ID, docid)));
    Query termQuery = new TermQuery(new Term(IndexArgs.CONTENTS, term));
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filterQuery, BooleanClause.Occur.MUST);
    builder.add(termQuery, BooleanClause.Occur.MUST);
    Query finalQuery = builder.build();
    TopDocs rs = searcher.search(finalQuery, 1);

    // The BM25 weight is the score of the first (and only) hit, but remember to remove 1 for the ConstantScoreQuery.
    // If we get zero results, indicates that term isn't found in the document.
    return rs.scoreDocs.length == 0 ? 0 : rs.scoreDocs[0].score - 1;
  }

  /**
   * Computes the BM25 weight of an unanalyzed term in a particular document (with Anserini default parameters).
   *
   * @param reader index reader
   * @param docid collection docid
   * @param term analyzed term
   * @return BM25 weight of the term in the specified document
   * @throws IOException if error encountered during query
   */
  public static float getBM25UnanalyzedTermWeight(IndexReader reader, String docid, String term) throws IOException {
    SearchArgs args = new SearchArgs();
    return getBM25UnanalyzedTermWeightWithParameters(reader, docid, term, IndexCollection.DEFAULT_ANALYZER,
        Float.parseFloat(args.bm25_k1[0]), Float.parseFloat(args.bm25_b[0]));
  }

  /**
   * Computes the BM25 weight of an unanalyzed term in a particular document.
   *
   * @param reader index reader
   * @param docid collection docid
   * @param term unanalyzed term
   * @param analyzer analyzer
   * @param k1 k1 setting for BM25
   * @param b b setting for BM25
   * @return BM25 weight of the term in the specified document
   * @throws IOException if error encountered during query
   */
  public static float getBM25UnanalyzedTermWeightWithParameters(IndexReader reader, String docid, String term,
                                                                Analyzer analyzer, float k1, float b)
      throws IOException {
    String analyzed = AnalyzerUtils.analyze(analyzer, term).get(0);
    return getBM25AnalyzedTermWeightWithParameters(reader, docid, analyzed, k1, b);
  }

  /**
   * Computes the BM25 score of a document with respect to a query. Assumes default BM25 parameter settings and
   * Anserini's default analyzer.
   *
   * @param reader index reader
   * @param docid docid of the document to score
   * @param q query
   * @return the score of the document with respect to the query
   * @throws IOException if error encountered during query
   */
  public static float computeQueryDocumentScore(IndexReader reader, String docid, String q) throws IOException {
    SearchArgs args = new SearchArgs();
    return computeQueryDocumentScoreWithSimilarityAndAnalyzer(reader, docid, q,
        new BM25Similarity(Float.parseFloat(args.bm25_k1[0]), Float.parseFloat(args.bm25_b[0])),
        IndexCollection.DEFAULT_ANALYZER);
  }

  /**
   * Computes the score of a document with respect to a query given a scoring function. Assumes Anserini's default
   * analyzer.
   *
   * @param reader index reader
   * @param docid docid of the document to score
   * @param q query
   * @param similarity scoring function
   * @return the score of the document with respect to the query
   * @throws IOException if error encountered during query
   */
  public static float computeQueryDocumentScoreWithSimilarity(
      IndexReader reader, String docid, String q, Similarity similarity)
      throws IOException {
    return computeQueryDocumentScoreWithSimilarityAndAnalyzer(reader, docid, q, similarity,
        IndexCollection.DEFAULT_ANALYZER);
  }

  /**
   * Computes the score of a document with respect to a query given a scoring function and an analyzer.
   *
   * @param reader index reader
   * @param docid docid of the document to score
   * @param q query
   * @param similarity scoring function
   * @param analyzer analyzer to use
   * @return the score of the document with respect to the query
   * @throws IOException if error encountered during query
   */
  public static float computeQueryDocumentScoreWithSimilarityAndAnalyzer(
      IndexReader reader, String docid, String q, Similarity similarity, Analyzer analyzer)
      throws IOException {
    // We compute the query-document score by issuing the query with an additional filter clause that restricts
    // consideration to only the docid in question, and then returning the retrieval score.
    //
    // This implementation is inefficient, but as the advantage of using the existing Lucene similarity, which means
    // that we don't need to copy the scoring function and keep it in sync wrt code updates.

    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    Query query = new BagOfWordsQueryGenerator().buildQuery(IndexArgs.CONTENTS, analyzer, q);

    Query filterQuery = new ConstantScoreQuery(new TermQuery(new Term(IndexArgs.ID, docid)));
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filterQuery, BooleanClause.Occur.MUST);
    builder.add(query, BooleanClause.Occur.MUST);
    Query finalQuery = builder.build();

    TopDocs rs = searcher.search(finalQuery, 1);

    // We want the score of the first (and only) hit, but remember to remove 1 for the ConstantScoreQuery.
    // If we get zero results, indicates that term isn't found in the document.
    return rs.scoreDocs.length == 0 ? 0 : rs.scoreDocs[0].score - 1;
  }

  // TODO: Write a variant of computeQueryDocumentScore that takes a set of documents.

  /**
   * Converts a collection docid to a Lucene internal docid.
   *
   * @param reader index reader
   * @param docid collection docid
   * @return corresponding Lucene internal docid, or -1 if docid not found
   */
  public static int convertDocidToLuceneDocid(IndexReader reader, String docid) {
    try {
      IndexSearcher searcher = new IndexSearcher(reader);
      Query q = new TermQuery(new Term(IndexArgs.ID, docid));
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
   * Converts a Lucene internal docid to a collection docid.
   *
   * @param reader index reader
   * @param docid Lucene internal docid
   * @return corresponding collection docid, or <code>null</code> if not found.
   */
  public static String convertLuceneDocidToDocid(IndexReader reader, int docid) {
    if (docid >= reader.maxDoc())
      return null;

    try {
      return reader.document(docid).get(IndexArgs.ID);
    } catch (IOException e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Returns index statistics.
   *
   * @param reader index reader
   * @return map from name of statistic to its value
   */
  public static Map<String, Object> getIndexStats(IndexReader reader) {
    Map<String, Object> indexStats = new HashMap<>();
    try {
      Terms terms = MultiTerms.getTerms(reader, IndexArgs.CONTENTS);

      indexStats.put("documents", reader.numDocs());
      indexStats.put("non_empty_documents", reader.getDocCount(IndexArgs.CONTENTS));
      indexStats.put("unique_terms", terms.size());
      indexStats.put("total_terms", reader.getSumTotalTermFreq(IndexArgs.CONTENTS));
    } catch (IOException e) {
      // Eat any exceptions and just return null.
      return null;
    }
    return indexStats;
  }

  /**
   * Returns {@code FieldInfo} for indexed fields.
   *
   * @param reader index reader
   * @return map from name of field to its {@code FieldInfo}
   */
  public static Map<String, FieldInfo> getFieldInfo(IndexReader reader) {
    Map<String, FieldInfo> fields = new HashMap<>();

    FieldInfos fieldInfos = FieldInfos.getMergedFieldInfos(reader);
    for (FieldInfo fi : fieldInfos) {
      fields.put(fi.name, fi);
    }

    return fields;
  }

  /**
   * Returns string summary of {@code FieldInfo} for indexed fields.
   *
   * @param reader index reader
   * @return map from name of field to its {@code FieldInfo} string summary
   */
  public static Map<String, String>  getFieldInfoDescription(IndexReader reader) {
    Map<String, String> description = new HashMap<>();

    FieldInfos fieldInfos = FieldInfos.getMergedFieldInfos(reader);
    for (FieldInfo fi : fieldInfos) {
      description.put(fi.name, "(" + "indexOption: " + fi.getIndexOptions() + ", hasVectors: " + fi.hasVectors() + ")");
    }

    return description;
  }

  // This is needed by src/main/python/run_regression.py

  public static final class Args {
    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    String index;

    @Option(name = "-stats", usage = "print index statistics")
    boolean stats;
  }

  public static void main(String[] argv) throws Exception {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));
    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    IndexReader reader = IndexReaderUtils.getReader(args.index);
    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader);

    if (args.stats) {
      Terms terms = MultiTerms.getTerms(reader, IndexArgs.CONTENTS);

      System.out.println("Index statistics");
      System.out.println("----------------");
      System.out.println("documents:             " + results.get("documents"));
      System.out.println("documents (non-empty): " + results.get("non_empty_documents"));
      System.out.println("unique terms:          " + results.get("unique_terms"));
      System.out.println("total terms:           " + results.get("total_terms"));
    }

    reader.close();
  }
}
