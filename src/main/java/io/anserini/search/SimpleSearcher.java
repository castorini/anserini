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
import io.anserini.index.IndexArgs;
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
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
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
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

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
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that exposes basic search functionality, designed specifically to provide the bridge between Java and Python
 * via pyjnius.  Note that methods are named according to Python conventions (e.g., snake case instead of camel case).
 */
public class SimpleSearcher implements Closeable {
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(IndexArgs.ID, SortField.Type.STRING_VAL));
  private static final Logger LOG = LogManager.getLogger(SimpleSearcher.class);

  public static final class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index.")
    public String index;

    @Option(name = "-topics", metaVar = "[file]", required = true, usage = "Topics file.")
    public String topics;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "Output run file.")
    public String output;

    @Option(name = "-bm25", usage = "Flag to use BM25.", forbids = {"-ql"})
    public Boolean useBM25 = true;

    @Option(name = "-bm25.k1", usage = "BM25 k1 value.", forbids = {"-ql"})
    public float bm25_k1 = 0.9f;

    @Option(name = "-bm25.b", usage = "BM25 b value.", forbids = {"-ql"})
    public float bm25_b = 0.4f;

    @Option(name = "-qld", usage = "Flag to use query-likelihood with Dirichlet smoothing.", forbids={"-bm25"})
    public Boolean useQL = false;

    @Option(name = "-qld.mu", usage = "Dirichlet smoothing parameter value for query-likelihood.", forbids={"-bm25"})
    public float ql_mu = 1000.0f;

    @Option(name = "-rm3", usage = "Flag to use RM3.")
    public Boolean useRM3 = false;

    @Option(name = "-rm3.fbTerms", usage = "RM3 parameter: number of expansion terms")
    public int rm3_fbTerms = 10;

    @Option(name = "-rm3.fbDocs", usage = "RM3 parameter: number of documents")
    public int rm3_fbDocs = 10;

    @Option(name = "-rm3.originalQueryWeight", usage = "RM3 parameter: weight to assign to the original query")
    public float rm3_originalQueryWeight = 0.5f;

    @Option(name = "-rocchio", usage = "Flag to use Rocchio.")
    public Boolean useRocchio = false;

    @Option(name = "-rocchio.topFbTerms", usage = "Rocchio parameter: number of relevant expansion terms")
    public int rocchio_topFbTerms = 10;

    @Option(name = "-rocchio.topFbDocs", usage = "Rocchio parameter: number of relevant documents")
    public int rocchio_topFbDocs = 10;

    @Option(name = "-rocchio.bottomFbTerms", usage = "Rocchio parameter: number of nonrelevant expansion terms")
    public int rocchio_bottomFbTerms = 10;

    @Option(name = "-rocchio.bottomFbDocs", usage = "Rocchio parameter: number of nonrelevant documents")
    public int rocchio_bottomFbDocs = 10;

    @Option(name = "-rocchio.alpha", usage = "Rocchio parameter: weight to assign to the original query")
    public float rocchio_alpha = 1.0f;

    @Option(name = "-rocchio.beta", usage = "Rocchio parameter: weight to assign to the relevant document vectors")
    public float rocchio_beta = 0.75f;

    @Option(name = "-rocchio.gamma", usage = "Rocchio parameter: weight to assign to the nonrelevant document vectors")
    public float rocchio_gamma = 0.0f;

    @Option(name = "-hits", metaVar = "[number]", usage = "Max number of hits to return.")
    public int hits = 1000;

    @Option(name = "-threads", metaVar = "[number]", usage = "Number of threads to use.")
    public int threads = 1;

    @Option(name = "-language", usage = "Analyzer Language")
    public String language = "en";
  }

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
  public class Result {
    public String docid;
    public int lucene_docid;
    public float score;
    public String contents;
    public String raw;
    public Document lucene_document; // Since this is for Python access, we're using Python naming conventions.

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
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    SearchArgs defaults = new SearchArgs();

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    if (reader.toString().contains("Lucene9")) {
      this.backwardsCompatibilityLucene8 = false;
    } else{
      this.backwardsCompatibilityLucene8 = true;
    }

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
   * Returns whether or not RM3 query expansion is being performed.
   *
   * @return whether or not RM3 query expansion is being performed
   */
  public boolean use_rm3() {
    return useRM3;
  }

  public boolean use_rocchio() {
    return useRocchio;
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
    SearchArgs defaults = new SearchArgs();
    set_rm3(Integer.parseInt(defaults.rm3_fbTerms[0]), Integer.parseInt(defaults.rm3_fbDocs[0]),
        Float.parseFloat(defaults.rm3_originalQueryWeight[0]));
  }

  /**
   * Enables RM3 query expansion with default parameters.
   *
   * @param fbTerms number of expansion terms
   * @param fbDocs number of expansion documents
   * @param originalQueryWeight weight to assign to the original query
   */
  public void set_rm3(int fbTerms, int fbDocs, float originalQueryWeight) {
    set_rm3(fbTerms, fbDocs, originalQueryWeight, false, true);
  }

  /**
   * Enables RM3 query expansion with default parameters.
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
    cascade.add(new Rm3Reranker(this.analyzer, IndexArgs.CONTENTS,
        fbTerms, fbDocs, originalQueryWeight, outputQuery, filterTerms));
    cascade.add(new ScoreTiesAdjusterReranker());
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
    SearchArgs defaults = new SearchArgs();
    set_rocchio(Integer.parseInt(defaults.rocchio_topFbTerms[0]), Integer.parseInt(defaults.rocchio_topFbDocs[0]),
        Integer.parseInt(defaults.rocchio_bottomFbTerms[0]), Integer.parseInt(defaults.rocchio_bottomFbDocs[0]),
        Float.parseFloat(defaults.rocchio_alpha[0]), Float.parseFloat(defaults.rocchio_beta[0]), Float.parseFloat(defaults.rocchio_gamma[0]), false, false);
  }

  /**
   * Enables Rocchio query expansion with default parameters.
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
    cascade.add(new RocchioReranker(this.analyzer, IndexArgs.CONTENTS,
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
      return;
    }
  }

  /**
   * Searches the collection using multiple threads.
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
   * Searches the collection using multiple threads.
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
    return batch_search_fields(this.generator, queries, qids, k, threads, new HashMap<>());
  }

  /**
   * Searches the provided fields weighted by their boosts, using multiple threads.
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
   * Searches the provided fields weighted by their boosts, using multiple threads.
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

    long startTime = System.nanoTime();
    AtomicLong index = new AtomicLong();
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
        // Logging to track query latency.
        // Note that this is potentially noisy because it might interfere with tqdm on the Python side; logging
        // every 500 queries seems like a reasonable comprise between offering helpful info and not being too noisy.
        Long lineNumber = index.incrementAndGet();
        if (lineNumber % 500 == 0) {
          double timePerQuery = (double) (System.nanoTime() - startTime) / (lineNumber + 1) / 1e9;
          LOG.info(String.format("Retrieving query " + lineNumber + " (%.3f s/query)", timePerQuery));
        }
      });
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        LOG.info(String.format("%.2f percent completed",
                (double) executor.getCompletedTaskCount() / queries.size() * 100.0d));
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
    Query query = new BagOfWordsQueryGenerator().buildQuery(IndexArgs.CONTENTS, analyzer, q);
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
    Query query = generator.buildQuery(IndexArgs.CONTENTS, analyzer, q);

    return _search(query, null, null, k);
  }

  // internal implementation
  protected Result[] _search(Query query, List<String> queryTokens, String queryString, int k) throws IOException {
    // Create an IndexSearch only once. Note that the object is thread safe.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    SearchArgs searchArgs = new SearchArgs();
    if (this.backwardsCompatibilityLucene8) {
      searchArgs.arbitraryScoreTieBreak = true;
    } else {
      searchArgs.arbitraryScoreTieBreak = false;
    }

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
      String docid = doc.getField(IndexArgs.ID).stringValue();

      IndexableField field;
      field = doc.getField(IndexArgs.CONTENTS);
      String contents = field == null ? null : field.stringValue();

      field = doc.getField(IndexArgs.RAW);
      String raw = field == null ? null : field.stringValue();

      results[i] = new Result(docid, hits.ids[i], hits.scores[i], contents, raw, doc);
    }

    return results;
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
   * @param ldocid internal Lucene docid
   * @return corresponding Lucene {@link Document}
   */
  public Document doc(int ldocid) {
    try {
      return reader.document(ldocid);
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Returns the Lucene {@link Document} based on a collection docid.
   * The method is named to be consistent with Lucene's {@link IndexReader#document(int)}, contra Java's standard
   * method naming conventions.
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
          Document result = IndexReaderUtils.document(reader, docid);
          results.put(docid, result);
        } catch (Exception e){}
      });
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        LOG.info(String.format("%.2f percent completed",
                (double) executor.getCompletedTaskCount() / docids.size() * 100.0d));
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
   * The method is named to be consistent with Lucene's {@link IndexReader#document(int)}, contra Java's standard
   * method naming conventions.
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
   * The method is named to be consistent with Lucene's {@link IndexReader#document(int)}, contra Java's standard
   * method naming conventions.
   *
   * @param ldocid internal Lucene docid
   * @return the "contents" field the document
   */
  public String doc_contents(int ldocid) {
    try {
      return reader.document(ldocid).get(IndexArgs.CONTENTS);
    } catch (Exception e) {
      // Eat any exceptions and just return null.
      return null;
    }
  }

  /**
   * Returns the "contents" field of a document based on a collection docid.
   * The method is named to be consistent with Lucene's {@link IndexReader#document(int)}, contra Java's standard
   * method naming conventions.
   *
   * @param docid collection docid
   * @return the "contents" field the document
   */
  public String doc_contents(String docid) {
    return IndexReaderUtils.documentContents(reader, docid);
  }

  /**
   * Returns the "raw" field of a document based on an internal Lucene docid.
   * The method is named to be consistent with Lucene's {@link IndexReader#document(int)}, contra Java's standard
   * method naming conventions.
   *
   * @param ldocid internal Lucene docid
   * @return the "raw" field the document
   */
  public String doc_raw(int ldocid) {
    try {
      return reader.document(ldocid).get(IndexArgs.RAW);
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

  // Note that this class is primarily meant to be used by automated regression scripts, not humans!
  // tl;dr - Do not use this class for running experiments. Use SearchCollection instead!
  //
  // SimpleSearcher is the main class that exposes search functionality for Pyserini (in Python).
  // As such, it has a different code path than SearchCollection, the preferred entry point for running experiments
  // from Java. The main method here exposes only barebone options, primarily designed to verify that results from
  // SimpleSearcher are *exactly* the same as SearchCollection (e.g., via automated regression scripts).
  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SimpleSearcher" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    SimpleSearcher searcher = new SimpleSearcher(searchArgs.index);
    searcher.set_language(searchArgs.language);
    SortedMap<Object, Map<String, String>> topics = TopicReader.getTopicsByFile(searchArgs.topics);

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(searchArgs.output), StandardCharsets.US_ASCII));
    List<String> argsAsList = Arrays.asList(args);

    // Test a separate code path, where we specify BM25 explicitly, which is different from not specifying it at all.
    if (argsAsList.contains("-bm25")) {
      LOG.info("Testing code path of explicitly setting BM25.");
      searcher.set_bm25(searchArgs.bm25_k1, searchArgs.bm25_b);
    } else if (searchArgs.useQL){
      LOG.info("Testing code path of explicitly setting QL.");
      searcher.set_qld(searchArgs.ql_mu);
    }

    if (searchArgs.useRM3) {
      if (argsAsList.contains("-rm3.fbTerms") || argsAsList.contains("-rm3.fbTerms") ||
          argsAsList.contains("-rm3.originalQueryWeight")) {
        LOG.info("Testing code path of explicitly setting RM3 parameters.");
        searcher.set_rm3(searchArgs.rm3_fbTerms, searchArgs.rm3_fbDocs, searchArgs.rm3_originalQueryWeight);
      } else {
        LOG.info("Testing code path of default RM3 parameters.");
        searcher.set_rm3();
      }
    } else if (searchArgs.useRocchio) {
      if (argsAsList.contains("-rocchio.topFbTerms") || argsAsList.contains("-rocchio.topFbDocs") ||
          argsAsList.contains("-rocchio.bottomFbTerms") || argsAsList.contains("-rocchio.bottomFbDocs") ||
          argsAsList.contains("-rocchio.alpha") || argsAsList.contains("-rocchio.beta") || argsAsList.contains("-rocchio.gamma")) {
        LOG.info("Testing code path of explicitly setting Rocchio parameters.");
        searcher.set_rocchio(searchArgs.rocchio_topFbTerms, searchArgs.rocchio_topFbDocs, searchArgs.rocchio_bottomFbTerms, searchArgs.rocchio_bottomFbDocs,
        searchArgs.rocchio_alpha, searchArgs.rocchio_beta, searchArgs.rocchio_gamma, false, false);
      } else {
        LOG.info("Testing code path of default Rocchio parameters.");
        searcher.set_rocchio();
      }
    }

    if (searchArgs.threads == 1) {
      for (Object id : topics.keySet()) {
        Result[] results = searcher.search(topics.get(id).get("title"), searchArgs.hits);

        for (int i = 0; i < results.length; i++) {
          out.println(String.format(Locale.US, "%s Q0 %s %d %f Anserini",
              id, results[i].docid, (i + 1), results[i].score));
        }
      }
    } else {
      List<String> qids = new ArrayList<>();
      List<String> queries = new ArrayList<>();

      for (Object id : topics.keySet()) {
        qids.add(id.toString());
        queries.add(topics.get(id).get("title"));
      }

      Map<String, Result[]> allResults = searcher.batch_search(queries, qids, searchArgs.hits, searchArgs.threads);

      // We iterate through, in natural object order.
      for (Object id : topics.keySet()) {
        Result[] results = allResults.get(id.toString());

        for (int i = 0; i < results.length; i++) {
          out.println(String.format(Locale.US, "%s Q0 %s %d %f Anserini",
              id, results[i].docid, (i + 1), results[i].score));
        }
      }
    }

    out.close();

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
