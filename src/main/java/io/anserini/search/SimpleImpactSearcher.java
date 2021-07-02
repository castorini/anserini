package io.anserini.search;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexReaderUtils;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.QueryGenerator;
import io.anserini.search.similarity.ImpactSimilarity;
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
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
 * via pyjnius.
 */
public class SimpleImpactSearcher implements Closeable {
    public static final Sort BREAK_SCORE_TIES_BY_DOCID =
        new Sort(SortField.FIELD_SCORE, new SortField(IndexArgs.ID, SortField.Type.STRING_VAL));
    private static final Logger LOG = LogManager.getLogger(SimpleImpactSearcher.class);
  
    public static final class Args {
      @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index.")
      public String index;
  
      @Option(name = "-topics", metaVar = "[file]", required = true, usage = "Topics file.")
      public String topics;
  
      @Option(name = "-output", metaVar = "[file]", required = true, usage = "Output run file.")
      public String output;
  
      @Option(name = "-hits", metaVar = "[number]", usage = "Max number of hits to return.")
      public int hits = 1000;
  
      @Option(name = "-threads", metaVar = "[number]", usage = "Number of threads to use.")
      public int threads = 1;
  
    }
  
    protected IndexReader reader;
    protected Similarity similarity;
    protected Analyzer analyzer;
    protected RerankerCascade cascade;
  
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
     * Creates a {@code SimpleImpactSearcher} with a specified analyzer.
     *
     * @param indexDir index directory
     * @param analyzer analyzer to use
     * @throws IOException if errors encountered during initialization
     */
    public SimpleImpactSearcher(String indexDir, Analyzer analyzer) throws IOException {
      Path indexPath = Paths.get(indexDir);
  
      if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
        throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
      }
    
      this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
      // Default to using ImpactSimilarity.
      this.similarity = new ImpactSimilarity();
      this.analyzer = analyzer;
      cascade = new RerankerCascade();
      cascade.add(new ScoreTiesAdjusterReranker());
    }
  
  
    /**
     * Returns the number of documents in the index.
     *
     * @return the number of documents in the index
     */
     public int getTotalNumDocuments(){
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
    public Map<String, Result[]> batchSearch(List<String> queries, List<String> qids, int k, int threads) {
      QueryGenerator generator = new BagOfWordsQueryGenerator();
      return batchSearchFields(generator, queries, qids, k, threads, new HashMap<>());
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
    public Map<String, Result[]> batchSearch(QueryGenerator generator, List<String> queries, List<String> qids, int k, int threads) {
      return batchSearchFields(generator, queries, qids, k, threads, new HashMap<>());
    }
  
    /**
     * Searches the provided fields weighted by their boosts, using multiple threads.
     * Batch version of {@link #searchFields(String, Map, int)}.
     *
     * @param queries list of queries
     * @param qids list of unique query ids
     * @param k number of hits
     * @param threads number of threads
     * @param fields map of fields to search with weights
     * @return a map of query id to search results
     */
    public Map<String, Result[]> batchSearchFields(List<String> queries, List<String> qids, int k, int threads,
                                                   Map<String, Float> fields) {
      QueryGenerator generator = new BagOfWordsQueryGenerator();
      return batchSearchFields(generator, queries, qids, k, threads, fields);
    }
  
    /**
     * Searches the provided fields weighted by their boosts, using multiple threads.
     * Batch version of {@link #searchFields(String, Map, int)}.
     *
     * @param generator the method for generating queries
     * @param queries list of queries
     * @param qids list of unique query ids
     * @param k number of hits
     * @param threads number of threads
     * @param fields map of fields to search with weights
     * @return a map of query id to search results
     */
    public Map<String, Result[]> batchSearchFields(QueryGenerator generator, List<String> queries, List<String> qids, int k, int threads,
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
              results.put(qid, searchFields(generator, query, fields, k));
            } else {
              results.put(qid, search(generator, query, k));
            }
          } catch (IOException e) {
            throw new CompletionException(e);
          }
          // logging for speed
          Long lineNumber = index.incrementAndGet();
          if (lineNumber % 100 == 0) {
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
  
      return search(query, queryTokens, q, k);
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
      return search(query, null, null, k);
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
  
      return search(query, null, null, k);
    }
  
    // internal implementation
    protected Result[] search(Query query, List<String> queryTokens, String queryString, int k) throws IOException {
      // Create an IndexSearch only once. Note that the object is thread safe.
      if (searcher == null) {
        searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
      }
  
      SearchArgs searchArgs = new SearchArgs();
      searchArgs.arbitraryScoreTieBreak = false;
      searchArgs.hits = k;
  
      TopDocs rs;
      RerankerContext context;
      rs = searcher.search(query, k, BREAK_SCORE_TIES_BY_DOCID, true);
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
    public Result[] searchFields(String q, Map<String, Float> fields, int k) throws IOException {
      // Note that this is used for MS MARCO experiments with document expansion.
      QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
      return searchFields(queryGenerator, q, fields, k);
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
    public Result[] searchFields(QueryGenerator generator, String q, Map<String, Float> fields, int k) throws IOException {
      IndexSearcher searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
  
      Query query = generator.buildQuery(fields, analyzer, q);
      List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);
  
      return search(query, queryTokens, q, k);
    }
  
    /**
     * Fetches the Lucene {@link Document} based on an internal Lucene docid.
     * The method is named to be consistent with Lucene's {@link IndexReader#document(int)}, contra Java's standard
     * method naming conventions.
     *
     * @param ldocid internal Lucene docid
     * @return corresponding Lucene {@link Document}
     */
    public Document document(int ldocid) {
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
    public Document document(String docid) {
      return IndexReaderUtils.document(reader, docid);
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
    public Document documentByField(String field, String id) {
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
    public String documentContents(int ldocid) {
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
    public String documentContents(String docid) {
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
    public String documentRaw(int ldocid) {
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
    public String documentRaw(String docid) {
      return IndexReaderUtils.documentRaw(reader, docid);
    }
  
    // Note that this class is primarily meant to be used by automated regression scripts, not humans!
    // tl;dr - Do not use this class for running experiments. Use SearchCollection instead!
    //
    // SimpleImpactSearcher is the main class that exposes search functionality for Pyserini (in Python).
    // As such, it has a different code path than SearchCollection, the preferred entry point for running experiments
    // from Java. The main method here exposes only barebone options, primarily designed to verify that results from
    // SimpleImpactSearcher are *exactly* the same as SearchCollection (e.g., via automated regression scripts).
    public static void main(String[] args) throws Exception {
      Args searchArgs = new Args();
      CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(100));
  
      try {
        parser.parseArgument(args);
      } catch (CmdLineException e) {
        System.err.println(e.getMessage());
        parser.printUsage(System.err);
        System.err.println("Example: SimpleImpactSearcher" + parser.printExample(OptionHandlerFilter.REQUIRED));
        return;
      }
  
      final long start = System.nanoTime();
      SimpleImpactSearcher searcher = new SimpleImpactSearcher(searchArgs.index);
      SortedMap<Object, Map<String, String>> topics = TopicReader.getTopicsByFile(searchArgs.topics);
      PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(searchArgs.output), StandardCharsets.US_ASCII));
      
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
  
        Map<String, Result[]> allResults = searcher.batchSearch(queries, qids, searchArgs.hits, searchArgs.threads);
  
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
      searcher.close();
  
      final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
      LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
    }
  }
  