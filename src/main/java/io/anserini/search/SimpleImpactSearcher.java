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

import io.anserini.index.Constants;
import org.apache.lucene.analysis.Analyzer;
import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.IndexReaderUtils;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.RocchioReranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.QueryEncoder;
import io.anserini.search.similarity.ImpactSimilarity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import java.util.ArrayList;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

import ai.onnxruntime.OrtException;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


/**
 * Class that exposes basic search functionality, designed specifically to provide the bridge between Java and Python
 * via pyjnius. Note that methods are named according to Python conventions (e.g., snake case instead of camel case).
 */
public class SimpleImpactSearcher implements Closeable {
  private static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(Constants.ID, SortField.Type.STRING_VAL));
  private static final Logger LOG = LogManager.getLogger(SimpleImpactSearcher.class);

  protected IndexReader reader;
  protected Similarity similarity;
  protected BagOfWordsQueryGenerator generator;
  protected Analyzer analyzer;
  protected RerankerCascade cascade;
  protected IndexSearcher searcher = null;
  protected boolean backwardsCompatibilityLucene8;
  private QueryEncoder queryEncoder = null;
  protected boolean useRM3;
  protected boolean useRocchio;

  /**
   * This class is meant to serve as the bridge between Anserini and Pyserini.
   * Note that we are adopting Python naming conventions here on purpose.
   */
  public static class Result {
    public String docid;
    public int lucene_docid;
    public float score;
    public String contents;
    public String raw;
    public Document lucene_document;

    public Result(String docid, int lucene_docid, float score, String contents, String raw, Document lucene_document) {
      this.docid = docid;
      this.lucene_docid = lucene_docid;
      this.score = score;
      this.contents = contents;
      this.raw = raw;
      this.lucene_document = lucene_document;
    }
  }

  protected SimpleImpactSearcher() {
  }

  /**
   * Creates a {@code SimpleImpactSearcher}.
   *
   * @param indexDir index directory
   * @throws IOException if errors encountered during initialization
   */
  public SimpleImpactSearcher(String indexDir) throws IOException {
    this(indexDir, new WhitespaceAnalyzer());
  }

  /**
   * Creates a {@code SimpleImpactSearcher}.
   *
   * @param indexDir index directory
   * @param queryEncoder query encoder
   * @throws IOException if errors encountered during initialization
   */
  public SimpleImpactSearcher(String indexDir, String queryEncoder) throws IOException {
    this(indexDir, new WhitespaceAnalyzer());
    this.set_onnx_query_encoder(queryEncoder);
  }

  /**
   * Creates a {@code SimpleImpactSearcher}.
   *
   * @param indexDir index directory
   * @param queryEncoder query encoder
   * @param analyzer Analyzer
   * @throws IOException if errors encountered during initialization
   */
  public SimpleImpactSearcher(String indexDir, String queryEncoder, Analyzer analyzer) throws IOException {
    this(indexDir, analyzer);
    this.set_onnx_query_encoder(queryEncoder);
  }

  /**
   * Creates a {@code SimpleImpactSearcher}.
   *
   * @param indexDir index directory
   * @param analyzer Analyzer
   * @throws IOException if errors encountered during initialization
   */
  public SimpleImpactSearcher(String indexDir, Analyzer analyzer) throws IOException {
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IOException(indexDir + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));

    // Fix for index compatibility issue between Lucene 8 and 9: https://github.com/castorini/anserini/issues/1952
    // If we detect an older index version, we turn off consistent tie-breaking, which avoids accessing docvalues,
    // which is the source of the incompatibility.
    this.backwardsCompatibilityLucene8 = !reader.toString().contains("lucene.version=9");

    // Default to using ImpactSimilarity.
    this.similarity = new ImpactSimilarity();
    this.analyzer = analyzer;
    this.generator = new BagOfWordsQueryGenerator();
    this.useRM3 = false;
    this.useRocchio = false;
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  /**
   * Sets the query encoder
   * 
   * @param encoder the query encoder
   */
  public void set_onnx_query_encoder(String encoder) {
    if (emptyEncoder()) {
      try {
        this.queryEncoder = (QueryEncoder) Class.forName("io.anserini.search.query." + encoder + "QueryEncoder")
          .getConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }  
  }

  private boolean emptyEncoder(){
    return this.queryEncoder == null;
  }

  /**
   * Sets the analyzer used.
   *
   * @param analyzer analyzer to use
   */
  public void set_analyzer(Analyzer analyzer) {
    this.analyzer = analyzer;
  }

  /**
   * Returns the analyzer used.
   *
   * @return analyzed used
   */
  public Analyzer get_analyzer(){
    return this.analyzer;
  }

  /**
   * Determines if RM3 query expansion is enabled.
   *
   * @return true if RM query expansion is enabled; false otherwise.
   */
  public boolean use_rm3() {
    return useRM3;
  }

  /**
   * Disables RM3 query expansion.
   */
  public void unset_rm3() {
    this.useRM3 = false;
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  /**
   * Enables RM3 query expansion with default parameters.
   */
  public void set_rm3() {
    SearchCollection.Args defaults = new SearchCollection.Args();
    set_rm3(Integer.parseInt(defaults.rm3_fbTerms[0]), Integer.parseInt(defaults.rm3_fbDocs[0]),
        Float.parseFloat(defaults.rm3_originalQueryWeight[0]));
  }

  /**
   * Enables RM3 query expansion with default parameters.
   *
   * @param collectionClass class for on-the-fly document parsing if index does not contain docvectors
   */
  public void set_rm3(String collectionClass) {
    SearchCollection.Args defaults = new SearchCollection.Args();
    set_rm3(collectionClass, Integer.parseInt(defaults.rm3_fbTerms[0]), Integer.parseInt(defaults.rm3_fbDocs[0]),
        Float.parseFloat(defaults.rm3_originalQueryWeight[0]));
  }

  /**
   * Enables RM3 query expansion with specified parameters.
   *
   * @param fbTerms number of expansion terms
   * @param fbDocs number of expansion documents
   * @param originalQueryWeight weight to assign to the original query
   */
  public void set_rm3(int fbTerms, int fbDocs, float originalQueryWeight) {
    set_rm3(null, fbTerms, fbDocs, originalQueryWeight, false, true);
  }

  /**
   * Enables RM3 query expansion with specified parameters.
   *
   * @param collectionClass class for on-the-fly document parsing if index does not contain docvectors
   * @param fbTerms number of expansion terms
   * @param fbDocs number of expansion documents
   * @param originalQueryWeight weight to assign to the original query
   */
  public void set_rm3(String collectionClass, int fbTerms, int fbDocs, float originalQueryWeight) {
    set_rm3(collectionClass, fbTerms, fbDocs, originalQueryWeight, false, true);
  }

  /**
   * Enables RM3 query expansion with specified parameters.
   *
   * @param collectionClass class for on-the-fly document parsing if index does not contain docvectors
   * @param fbTerms number of expansion terms
   * @param fbDocs number of expansion documents
   * @param originalQueryWeight weight to assign to the original query
   * @param outputQuery flag to print original and expanded queries
   * @param filterTerms whether to filter terms to be English only
   */
  public void set_rm3(String collectionClass, int fbTerms, int fbDocs, float originalQueryWeight, boolean outputQuery, boolean filterTerms) {
    Class clazz = null;
    try {
      if (collectionClass != null) {
        clazz = Class.forName("io.anserini.collection." + collectionClass);
      }
    } catch (ClassNotFoundException e) {
      LOG.error("collectionClass: " + collectionClass + " not found!");
    }

    useRM3 = true;
    cascade = new RerankerCascade("rm3");
    cascade.add(new Rm3Reranker(this.analyzer, clazz, Constants.CONTENTS,
        fbTerms, fbDocs, originalQueryWeight, outputQuery, filterTerms));
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  /**
   * Determines if Rocchio query expansion is enabled.
   *
   * @return true if Rocchio query expansion is enabled; false otherwise.
   */
  public boolean use_rocchio() {
    return useRocchio;
  }

  /**
   * Disables Rocchio query expansion.
   */
  public void unset_rocchio() {
    this.useRocchio = false;
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  /**
   * Enables Rocchio query expansion with default parameters.
   */
  public void set_rocchio() {
    SearchCollection.Args defaults = new SearchCollection.Args();
    set_rocchio(null, Integer.parseInt(defaults.rocchio_topFbTerms[0]), Integer.parseInt(defaults.rocchio_topFbDocs[0]),
        Integer.parseInt(defaults.rocchio_bottomFbTerms[0]), Integer.parseInt(defaults.rocchio_bottomFbDocs[0]),
        Float.parseFloat(defaults.rocchio_alpha[0]), Float.parseFloat(defaults.rocchio_beta[0]),
        Float.parseFloat(defaults.rocchio_gamma[0]), false, false);
  }

  /**
   * Enables Rocchio query expansion with default parameters.
   *
   * @param collectionClass class for on-the-fly document parsing if index does not contain docvectors
   */
  public void set_rocchio(String collectionClass) {
    SearchCollection.Args defaults = new SearchCollection.Args();
    set_rocchio(collectionClass, Integer.parseInt(defaults.rocchio_topFbTerms[0]), Integer.parseInt(defaults.rocchio_topFbDocs[0]),
        Integer.parseInt(defaults.rocchio_bottomFbTerms[0]), Integer.parseInt(defaults.rocchio_bottomFbDocs[0]),
        Float.parseFloat(defaults.rocchio_alpha[0]), Float.parseFloat(defaults.rocchio_beta[0]),
        Float.parseFloat(defaults.rocchio_gamma[0]), false, false);
  }

  /**
   * Enables Rocchio query expansion with specified parameters.
   *
   * @param collectionClass class for on-the-fly document parsing if index does not contain docvectors
   * @param topFbTerms number of relevant expansion terms
   * @param topFbDocs number of relevant expansion documents
   * @param bottomFbTerms number of nonrelevant expansion terms
   * @param bottomFbDocs number of nonrelevant expansion documents
   * @param alpha weight to assign to the original query
   * @param beta weight to assign to the relevant document vectors
   * @param gamma weight to assign to the nonrelevant document vectors
   * @param outputQuery flag to print original and expanded queries
   * @param useNegative flag to use negative feedback
   */
  public void set_rocchio(String collectionClass, int topFbTerms, int topFbDocs, int bottomFbTerms, int bottomFbDocs, float alpha, float beta, float gamma, boolean outputQuery, boolean useNegative) {
    Class clazz = null;
    try {
      if (collectionClass != null) {
        clazz = Class.forName("io.anserini.collection." + collectionClass);
      }
    } catch (ClassNotFoundException e) {
      LOG.error("collectionClass: " + collectionClass + " not found!");
    }

    useRocchio = true;
    cascade = new RerankerCascade("rocchio");
    cascade.add(new RocchioReranker(this.analyzer, clazz, Constants.CONTENTS,
        topFbTerms, topFbDocs, bottomFbTerms, bottomFbDocs, alpha, beta, gamma, outputQuery, useNegative));
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  /**
   * Returns the {@link Similarity} (i.e., scoring function) currently being used.
   *
   * @return the {@link Similarity} currently being used
   */
  public Similarity get_similarity() {
    return similarity;
  }

  /**
   * Returns the number of documents in the index.
   *
   * @return the number of documents in the index
   */
  public int get_total_num_docs() {
    // Create an IndexSearch only once. Note that the object is thread safe.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    return searcher.getIndexReader().maxDoc();
  }

  /**
   * helper function to change Map<String, Int> to Map<String, Float>
   *
   * @param Map<String,Int> map needs to be transform
   * @return a map in the form of Map<String, Float>
   */
  private Map<String, Float> intToFloat(Map<String,Integer> input) {
    Map<String, Float> transformed = new HashMap<>();
    for (Map.Entry<String, Integer> entry : input.entrySet()) {
      transformed.put(entry.getKey(), entry.getValue().floatValue()); 
    }
    return transformed;
  }

  /**
   * Closes this searcher.
   */
  @Override
  public void close() throws IOException {
    try {
      reader.close();
    } catch (Exception e) {
      // Eat any exceptions.
    }
  }

  /**
   * Searches in batch using multiple threads.
   *
   * @param encoded_queries list of queries
   * @param qids    list of unique query ids
   * @param k       number of hits
   * @param threads number of threads
   * @return a map of query id to search results
   */
  public Map<String, Result[]> batch_search(List<Map<String, Integer>> encoded_queries,
                                            List<String> qids,
                                            int k,
                                            int threads) {
    // Create the IndexSearcher here, if needed. We do it here because if we leave the creation to the search
    // method, we might end up with a race condition as multiple threads try to concurrently create the IndexSearcher.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    ConcurrentHashMap<String, Result[]> results = new ConcurrentHashMap<>();

    int queryCnt = encoded_queries.size();
    for (int q = 0; q < queryCnt; ++q) {
      Map<String, Integer> query = encoded_queries.get(q);
      String qid = qids.get(q);
      executor.execute(() -> {
        try {
          results.put(qid, search(query, k));
        } catch (IOException e) {
          throw new CompletionException(e);
        } catch (OrtException e) {
          throw new CompletionException(e);
        }
      });
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        // Opportunity to perform status logging, but no-op here because logging interferes with Python tqdm
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    if (queryCnt != executor.getCompletedTaskCount()) {
      throw new RuntimeException("queryCount = " + queryCnt +
          " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());
    }

    return results;
  }

  /**
   * Searches in batch using multiple threads.
   *
   * @param queries list of String queries
   * @param qids    list of unique query ids
   * @param k       number of hits
   * @param threads number of threads
   * @return a map of query id to search results
   */
  public Map<String, Result[]> batch_search_queries(List<String> queries,
                                            List<String> qids,
                                            int k,
                                            int threads) {
    // Create the IndexSearcher here, if needed. We do it here because if we leave the creation to the search
    // method, we might end up with a race condition as multiple threads try to concurrently create the IndexSearcher.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    ConcurrentHashMap<String, Result[]> results = new ConcurrentHashMap<>();

    int queryCnt = queries.size();
    for (int q = 0; q < queryCnt; ++q) {
      String query = queries.get(q);
      String qid = qids.get(q);
      executor.execute(() -> {
        try {
          results.put(qid, search(query, k));
        } catch (IOException e) {
          throw new CompletionException(e);
        } catch (OrtException e) {
          throw new CompletionException(e);
        }
      });
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        // Opportunity to perform status logging, but no-op here because logging interferes with Python tqdm
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    if (queryCnt != executor.getCompletedTaskCount()) {
      throw new RuntimeException("queryCount = " + queryCnt +
          " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());
    }

    return results;
  }

  /**
   * Encodes the query using the onnx encoder
   * 
   * @param queryString query string
   * @throws OrtException if errors encountered during encoding
   * @return encoded query
   */
  public Map<String, Integer> encodeWithOnnx(String queryString) throws OrtException {
    // if no query encoder, assume its encoded query split by whitespace
    if (this.queryEncoder == null){
      List<String> queryTokens = AnalyzerUtils.analyze(analyzer, queryString);
      Map<String, Integer> queryTokensFreq = queryTokens.stream().collect(Collectors.toMap(
         e->e, (a)->1, Integer::sum));
      return queryTokensFreq;
    }

    Map<String, Integer> encodedQ = this.queryEncoder.getEncodedQueryMap(queryString);
    return encodedQ;
  }

  /**
   * Encodes the weight map using the onnx encoder
   * 
   * @param queryWeight query weight map
   * @throws OrtException if errors encountered during encoding
   * @return encoded query
   */
  public String encodeWithOnnx(Map<String, Integer> queryWeight) throws OrtException {
    String encodedQ = "";
    List<String> encodedQuery = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : queryWeight.entrySet()) {
      String token = entry.getKey();
      Integer tokenWeight = entry.getValue();
      for (int i = 0; i < tokenWeight; ++i) {
        encodedQuery.add(token);
      }
    }
    encodedQ = String.join(" ", encodedQuery);
    
    return encodedQ;
  }


  /**
   * Searches the collection, returning 10 hits by default.
   *
   * @param encoded_q query
   * @return array of search results
   * @throws IOException if error encountered during search
   * @throws OrtException if error encountered during search
   */
  public Result[] search(Map<String, Integer> encoded_q) throws IOException, OrtException {
    return search(encoded_q, 10);
  }

  /**
   * Searches the collection, returning 10 hits by default.
   *
   * @param q raw string query
   * @return array of search results
   * @throws IOException if error encountered during search
   * @throws OrtException if error encountered during search
   */
  public Result[] search(String q) throws IOException, OrtException {
    return search(q, 10);
  }

  /**
   * Searches the collection.
   *
   * @param encoded_q query
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   * @throws OrtException if error encountered during search
   */
  public Result[] search(Map<String, Integer> encoded_q, int k) throws IOException, OrtException {
    Map<String, Float> float_encoded_q = intToFloat(encoded_q);
    Query query = generator.buildQuery(Constants.CONTENTS, float_encoded_q);
    String encodedQuery = encodeWithOnnx(encoded_q);
    return _search(query, encodedQuery, k);
  }

  /**
   * Searches the collection.
   *
   * @param q string query
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   * @throws OrtException if error encountered during search
   */
  public Result[] search(String q, int k) throws IOException, OrtException {
    // make encoded query from raw query
    Map<String, Integer> encoded_q = encodeWithOnnx(q);
    String encodedQuery = encodeWithOnnx(encoded_q);
    Query query = generator.buildQuery(Constants.CONTENTS, analyzer, encodedQuery);
    return _search(query, encodedQuery, k);
  }

  // internal implementation
  protected Result[] _search(Query query, String encodedQuery, int k) throws IOException, OrtException {
    // Create an IndexSearch only once. Note that the object is thread safe.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    SearchCollection.Args searchArgs = new SearchCollection.Args();
    searchArgs.arbitraryScoreTieBreak = this.backwardsCompatibilityLucene8;
    searchArgs.hits = k;

    // encoded query can be tokenized using whitespace analyzer
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, encodedQuery);

    TopDocs rs;
    RerankerContext context;
    if (this.backwardsCompatibilityLucene8) {
      rs = searcher.search(query, k);
    } else {
      rs = searcher.search(query, k, BREAK_SCORE_TIES_BY_DOCID, true);
    }
    context = new RerankerContext<>(searcher, null, query, null,
        encodedQuery, queryTokens, null, searchArgs);

    ScoredDocuments hits = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

    Result[] results = new Result[hits.ids.length];
    for (int i = 0; i < hits.ids.length; i++) {
      Document doc = hits.documents[i];
      String docid = doc.getField(Constants.ID).stringValue();

      IndexableField field;
      field = doc.getField(Constants.CONTENTS);
      String contents = field == null ? null : field.stringValue();

      field = doc.getField(Constants.RAW);
      String raw = field == null ? null : field.stringValue();

      results[i] = new Result(docid, hits.ids[i], hits.scores[i], contents, raw, doc);
    }

    return results;
  }

  /**
   * Fetches the Lucene {@link Document} based on an internal Lucene docid.
   *
   * @param lucene_docid internal Lucene docid
   * @return corresponding Lucene {@link Document}
   */
  public Document doc(int lucene_docid) {
    try {
      return reader.document(lucene_docid);
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Returns the Lucene {@link Document} based on a collection docid.
   *
   * @param docid collection docid
   * @return corresponding Lucene {@link Document}
   */
  public Document doc(String docid) {
    return IndexReaderUtils.document(reader, docid);
  }

  /**
   * Fetches the Lucene {@link Document} based on some field other than its unique collection docid.
   * For example, scientific articles might have DOIs.
   *
   * @param field field
   * @param id    unique id
   * @return corresponding Lucene {@link Document} based on the value of a specific field
   */
  public Document doc_by_field(String field, String id) {
    return IndexReaderUtils.documentByField(reader, field, id);
  }

  /**
   * Returns the "contents" field of a document based on an internal Lucene docid.
   *
   * @param lucene_docid internal Lucene docid
   * @return the "contents" field the document
   */
  public String doc_contents(int lucene_docid) {
    try {
      return reader.document(lucene_docid).get(Constants.CONTENTS);
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Returns the "contents" field of a document based on a collection docid.
   *
   * @param docid collection docid
   * @return the "contents" field the document
   */
  public String doc_contents(String docid) {
    return IndexReaderUtils.documentContents(reader, docid);
  }

  /**
   * Returns the "raw" field of a document based on an internal Lucene docid.
   *
   * @param lucene_docid internal Lucene docid
   * @return the "raw" field the document
   */
  public String doc_raw(int lucene_docid) {
    try {
      return reader.document(lucene_docid).get(Constants.RAW);
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Returns the "raw" field of a document based on a collection docid.
   *
   * @param docid collection docid
   * @return the "raw" field the document
   */
  public String doc_raw(String docid) {
    return IndexReaderUtils.documentRaw(reader, docid);
  }
}
  