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

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import io.anserini.index.IndexReaderUtils;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.RocchioReranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.QueryGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.te.TeluguAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class that exposes basic search functionality, designed specifically to provide the bridge between Java and Python
 * via pyjnius.  Note that methods are named according to Python conventions (e.g., snake case instead of camel case).
 */
public class SimpleSearcher implements Closeable {
  private static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(Constants.ID, SortField.Type.STRING_VAL));
  private static final Logger LOG = LogManager.getLogger(SimpleSearcher.class);

  protected IndexReader reader;
  protected Similarity similarity;
  protected Analyzer analyzer;
  protected RerankerCascade cascade;
  protected QueryGenerator generator = new BagOfWordsQueryGenerator();
  protected boolean useRM3;
  protected boolean useRocchio;
  protected boolean backwardsCompatibilityLucene8;

  protected IndexSearcher searcher = null;

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

  protected SimpleSearcher() {
  }

  /**
   * Creates a {@code SimpleSearcher}.
   *
   * @param indexDir index directory
   * @throws IOException if errors encountered during initialization
   */
  public SimpleSearcher(String indexDir) throws IOException {
    this(indexDir, IndexCollection.DEFAULT_ANALYZER);
  }

  /**
   * Creates a {@code SimpleSearcher} with a specified analyzer.
   *
   * @param indexDir index directory
   * @param analyzer analyzer to use
   * @throws IOException if errors encountered during initialization
   */
  public SimpleSearcher(String indexDir, Analyzer analyzer) throws IOException {
    SearchCollection.Args defaults = new SearchCollection.Args();
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));

    // Fix for index compatibility issue between Lucene 8 and 9: https://github.com/castorini/anserini/issues/1952
    // If we detect an older index version, we turn off consistent tie-breaking, which avoids accessing docvalues,
    // which is the source of the incompatibility.
    this.backwardsCompatibilityLucene8 = !reader.toString().contains("lucene.version=9");

    // Default to using BM25.
    this.similarity = new BM25Similarity(Float.parseFloat(defaults.bm25_k1[0]), Float.parseFloat(defaults.bm25_b[0]));
    this.analyzer = analyzer;
    this.useRM3 = false;
    this.useRocchio = false;
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
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
   * Sets the language.
   *
   * @param language language
   */
  public void set_language(String language) {
    if (language.equals("ar")) {
      this.analyzer = new ArabicAnalyzer();
    } else if (language.equals("bn")) {
      this.analyzer = new BengaliAnalyzer();
    } else if (language.equals("de")) {
      this.analyzer = new GermanAnalyzer();
    } else if (language.equals("da")) {
      this.analyzer = new DanishAnalyzer();
    } else if (language.equals("es")) {
      this.analyzer = new SpanishAnalyzer();
    } else if (language.equals("fa")) {
      this.analyzer = new PersianAnalyzer();
    } else if (language.equals("fi")) {
      this.analyzer = new FinnishAnalyzer();
    } else if (language.equals("fr")) {
      this.analyzer = new FrenchAnalyzer();
    } else if (language.equals("hi")) {
      this.analyzer = new HindiAnalyzer();
    } else if (language.equals("hu")) {
      this.analyzer = new HungarianAnalyzer();
    } else if (language.equals("id")) {
      this.analyzer = new IndonesianAnalyzer();
    } else if (language.equals("it")) {
      this.analyzer = new ItalianAnalyzer();
    } else if (language.equals("ja")) {
      this.analyzer = new JapaneseAnalyzer();
    } else if (language.equals("nl")) {
      this.analyzer = new DutchAnalyzer();
    } else if (language.equals("no")) {
      this.analyzer = new NorwegianAnalyzer();
    } else if (language.equals("pl")) {
      this.analyzer = new MorfologikAnalyzer();
    } else if (language.equals("pt")) {
      this.analyzer = new PortugueseAnalyzer();
    } else if (language.equals("ru")) {
      this.analyzer = new RussianAnalyzer();
    } else if (language.equals("sv")) {
      this.analyzer = new SwedishAnalyzer();
    } else if (language.equals("te")) {
      this.analyzer = new TeluguAnalyzer();
    } else if (language.equals("th")) {
      this.analyzer = new ThaiAnalyzer();
    } else if (language.equals("tr")) {
      this.analyzer = new TurkishAnalyzer();
    } else if (language.equals("uk")) {
      this.analyzer = new UkrainianMorfologikAnalyzer();
    } else if (language.equals("zh") || language.equals("ko")) {
      this.analyzer = new CJKAnalyzer();
    } else if (language.equals("sw") || language.equals("te")) {
      this.analyzer = new WhitespaceAnalyzer();
      // For Mr.TyDi: sw and te do not have custom Lucene analyzers, so just use whitespace analyzer.
    }
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
   * Enables RM3 query expansion with specified parameters.
   *
   * @param fbTerms number of expansion terms
   * @param fbDocs number of expansion documents
   * @param originalQueryWeight weight to assign to the original query
   */
  public void set_rm3(int fbTerms, int fbDocs, float originalQueryWeight) {
    set_rm3(fbTerms, fbDocs, originalQueryWeight, false, true);
  }

  /**
   * Enables RM3 query expansion with specified parameters.
   *
   * @param fbTerms number of expansion terms
   * @param fbDocs number of expansion documents
   * @param originalQueryWeight weight to assign to the original query
   * @param outputQuery flag to print original and expanded queries
   * @param filterTerms whether to filter terms to be English only
   */
  public void set_rm3(int fbTerms, int fbDocs, float originalQueryWeight, boolean outputQuery, boolean filterTerms) {
    useRM3 = true;
    cascade = new RerankerCascade("rm3");
    cascade.add(new Rm3Reranker(this.analyzer, null, Constants.CONTENTS,
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
    set_rocchio(Integer.parseInt(defaults.rocchio_topFbTerms[0]), Integer.parseInt(defaults.rocchio_topFbDocs[0]),
        Integer.parseInt(defaults.rocchio_bottomFbTerms[0]), Integer.parseInt(defaults.rocchio_bottomFbDocs[0]),
        Float.parseFloat(defaults.rocchio_alpha[0]), Float.parseFloat(defaults.rocchio_beta[0]),
        Float.parseFloat(defaults.rocchio_gamma[0]), false, false);
  }

  /**
   * Enables Rocchio query expansion with specified parameters.
   *
   * @param topFbTerms number of relevant expansion terms
   * @param topFbDocs number of relevant expansion documents
   * @param bottomFbTerms number of nonrelevant expansion terms
   * @param bottomFbDocs number of nonrelevant expansion documents
   * @param alpha weight to assign to the original query
   * @param beta weight to assign to the relevant document vectors
   * @param gamma weight to assign to the nonrelevant document vectors
   * @param outputQuery flag to print original and expanded queries
   */
  public void set_rocchio(int topFbTerms, int topFbDocs, int bottomFbTerms, int bottomFbDocs, float alpha, float beta, float gamma, boolean outputQuery, boolean useNegative) {
    useRocchio = true;
    cascade = new RerankerCascade("rocchio");
    cascade.add(new RocchioReranker(this.analyzer, null, Constants.CONTENTS,
        topFbTerms, topFbDocs, bottomFbTerms, bottomFbDocs, alpha, beta, gamma, outputQuery, useNegative));
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  /**
   * Specifies use of query likelihood with Dirichlet smoothing as the scoring function.
   *
   * @param mu mu smoothing parameter
   */
  public void set_qld(float mu) {
    this.similarity = new LMDirichletSimilarity(mu);

    // We need to re-initialize the searcher
    searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);
  }

  /**
   * Specifies use of BM25 as the scoring function.
   *
   * @param k1 k1 parameter
   * @param b b parameter
   */
  public void set_bm25(float k1, float b) {
    this.similarity = new BM25Similarity(k1, b);

    // We need to re-initialize the searcher
    searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);
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
   public int get_total_num_docs(){
     // Create an IndexSearch only once. Note that the object is thread safe.
     if (searcher == null) {
       searcher = new IndexSearcher(reader);
       searcher.setSimilarity(similarity);
     }

     return searcher.getIndexReader().maxDoc();
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
   * Searches the collection in batch using multiple threads.
   *
   * @param queries list of queries
   * @param qids list of unique query ids
   * @param k number of hits
   * @param threads number of threads
   * @return a map of query id to search results
   */
  public Map<String, Result[]> batch_search(List<String> queries,
                                            List<String> qids,
                                            int k,
                                            int threads) {
    return batch_search_fields(this.generator, queries, qids, k, threads, new HashMap<>());
  }

  /**
   * Searches the collection in batch using multiple threads.
   *
   * @param generator the method for generating queries
   * @param queries list of queries
   * @param qids list of unique query ids
   * @param k number of hits
   * @param threads number of threads
   * @return a map of query id to search results
   */
  public Map<String, Result[]> batch_search(QueryGenerator generator,
                                            List<String> queries,
                                            List<String> qids,
                                            int k,
                                            int threads) {
    return batch_search_fields(generator, queries, qids, k, threads, new HashMap<>());
  }

  /**
   * Searches the provided fields weighted by their boosts, in batch using multiple threads.
   *
   * @param queries list of queries
   * @param qids list of unique query ids
   * @param k number of hits
   * @param threads number of threads
   * @param fields map of fields to search with weights
   * @return a map of query id to search results
   */
  public Map<String, Result[]> batch_search_fields(List<String> queries,
                                                   List<String> qids,
                                                   int k,
                                                   int threads,
                                                   Map<String, Float> fields) {
    return batch_search_fields(this.generator, queries, qids, k, threads, fields);
  }

  /**
   * Searches the provided fields weighted by their boosts, in batch using multiple threads.
   *
   * @param generator the method for generating queries
   * @param queries list of queries
   * @param qids list of unique query ids
   * @param k number of hits
   * @param threads number of threads
   * @param fields map of fields to search with weights
   * @return a map of query id to search results
   */
  public Map<String, Result[]> batch_search_fields(QueryGenerator generator,
                                                   List<String> queries,
                                                   List<String> qids,
                                                   int k,
                                                   int threads,
                                                   Map<String, Float> fields) {
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
          if (fields.size() > 0) {
            results.put(qid, search_fields(generator, query, fields, k));
          } else {
            results.put(qid, search(generator, query, k));
          }
        } catch (IOException e) {
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
   * Searches the collection, returning 10 hits by default.
   *
   * @param q query
   * @return array of search results
   * @throws IOException if error encountered during search
   */
  public Result[] search(String q) throws IOException {
    return search(q, 10);
  }

  /**
   * Searches the collection, returning a specified number of hits.
   *
   * @param q query
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   */
  public Result[] search(String q, int k) throws IOException {
    Query query = new BagOfWordsQueryGenerator().buildQuery(Constants.CONTENTS, analyzer, q);
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);

    return _search(query, queryTokens, q, k);
  }

  /**
   * Searches the collection with a pre-constructed Lucene {@link Query}.
   *
   * @param query Lucene query
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   */
  public Result[] search(Query query, int k) throws IOException {
    return _search(query, null, null, k);
  }

  /**
   * Searches the collection with a specified {@link QueryGenerator}.
   *
   * @param generator the method for generating queries
   * @param q query
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   */
  public Result[] search(QueryGenerator generator, String q, int k) throws IOException {
    Query query = generator.buildQuery(Constants.CONTENTS, analyzer, q);
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);

    return _search(query, queryTokens, q, k);
  }

  // internal implementation
  protected Result[] _search(Query query, List<String> queryTokens, String queryString, int k) throws IOException {
    // Create an IndexSearch only once. Note that the object is thread safe.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    SearchCollection.Args searchArgs = new SearchCollection.Args();
    searchArgs.arbitraryScoreTieBreak = this.backwardsCompatibilityLucene8;
    searchArgs.hits = k;

    TopDocs rs;
    RerankerContext context;
    if (this.backwardsCompatibilityLucene8) {
      rs = searcher.search(query, useRM3 ? searchArgs.rerankcutoff : k);
    } else {
      rs = searcher.search(query, useRM3 ? searchArgs.rerankcutoff : k, BREAK_SCORE_TIES_BY_DOCID, true);
    }
    context = new RerankerContext<>(searcher, null, query, null,
          queryString, queryTokens, null, searchArgs);

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
   * Returns a map of the feedback terms.
   *
   * @param q query
   * @return map of the feedback terms and their weights
   * @throws IOException if error encountered during search
   */
  public Map<String, Float> get_feedback_terms(String q) throws IOException {
    Query query = new BagOfWordsQueryGenerator().buildQuery(Constants.CONTENTS, analyzer, q);
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);

    return _get_feedback_terms(query, queryTokens, q, 10);
  }

  // internal implementation:
  // This initial implementation is very janky. We basically still perform retrieval, but just throw away the results.
  protected Map<String, Float> _get_feedback_terms(Query query, List<String> queryTokens, String queryString, int k) throws IOException {
    // Create an IndexSearch only once. Note that the object is thread safe.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    SearchCollection.Args searchArgs = new SearchCollection.Args();
    searchArgs.arbitraryScoreTieBreak = this.backwardsCompatibilityLucene8;
    searchArgs.hits = k;

    TopDocs rs;
    RerankerContext context;
    if (this.backwardsCompatibilityLucene8) {
      rs = searcher.search(query, useRM3 ? searchArgs.rerankcutoff : k);
    } else {
      rs = searcher.search(query, useRM3 ? searchArgs.rerankcutoff : k, BREAK_SCORE_TIES_BY_DOCID, true);
    }
    context = new RerankerContext<>(searcher, null, query, null,
        queryString, queryTokens, null, searchArgs);

    cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

    return context.feedbackTerms;
  }

  /**
   * Searches the provided fields weighted by their boosts.
   *
   * @param q query
   * @param fields map of fields to search with weights
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   */
  public Result[] search_fields(String q,
                                Map<String, Float> fields,
                                int k) throws IOException {
    // Note that this is used for MS MARCO experiments with document expansion.
    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    return search_fields(queryGenerator, q, fields, k);
  }

  /**
   * Searches the provided fields weighted by their boosts.
   *
   * @param generator the method for generating queries
   * @param q query
   * @param fields map of fields to search with weights
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   */
  public Result[] search_fields(QueryGenerator generator,
                                String q,
                                Map<String, Float> fields,
                                int k) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    Query query = generator.buildQuery(fields, analyzer, q);
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);

    return _search(query, queryTokens, q, k);
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
   * Returns a map of collection docid to Lucene {@link Document}.
   * Batch version of {@link #doc(String)}.
   *
   * @param docids list of docids
   * @return a map of docid to corresponding Lucene {@link Document}
   */
  public Map<String, Document> batch_get_docs(List<String> docids, int threads) {
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    ConcurrentHashMap<String, Document> results = new ConcurrentHashMap<>();

    for (String docid: docids) {
      executor.execute(() -> {
        try {
          results.put(docid, IndexReaderUtils.document(reader, docid));
        } catch (Exception e) {
          // Do nothing, just eat the exception.
        }
      });
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        // Opportunity to perform status logging, but no-op here
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    return results;
  }

  /**
   * Fetches the Lucene {@link Document} based on some field other than its unique collection docid.
   * For example, scientific articles might have DOIs.
   *
   * @param field field
   * @param id unique id
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
   * The method is named to be consistent with Lucene's {@link IndexReader#document(int)}, contra Java's standard
   * method naming conventions.
   *
   * @param docid collection docid
   * @return the "raw" field the document
   */
  public String doc_raw(String docid) {
    return IndexReaderUtils.documentRaw(reader, docid);
  }
}
