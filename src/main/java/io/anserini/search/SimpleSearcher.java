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

package io.anserini.search;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import io.anserini.index.IndexReaderUtils;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.PhraseQueryGenerator;
import io.anserini.search.query.QueryGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.*;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that exposes basic search functionality, designed specifically to provide the bridge between Java and Python
 * via pyjnius.
 */
public class SimpleSearcher implements Closeable {
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(IndexArgs.ID, SortField.Type.STRING_VAL));
  public static final Sort BREAK_SCORE_TIES_BY_TWEETID =
      new Sort(SortField.FIELD_SCORE,
          new SortField(TweetGenerator.TweetField.ID_LONG.name, SortField.Type.LONG, true));
  private static final Logger LOG = LogManager.getLogger(SimpleSearcher.class);

  private final IndexReader reader;
  private Similarity similarity;
  private Analyzer analyzer;
  private RerankerCascade cascade;
  private boolean searchtweets;
  private boolean isRerank;

  private IndexSearcher searcher = null;

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

  public SimpleSearcher(String indexDir) throws IOException {
    this(indexDir, IndexCollection.DEFAULT_ANALYZER);
  }

  public SimpleSearcher(String indexDir, Analyzer analyzer) throws IOException {
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    this.similarity = new BM25Similarity(0.9f, 0.4f);
    this.analyzer = analyzer;
    this.searchtweets = false;
    this.isRerank = false;
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  public void setSearchTweets(boolean flag) {
     this.searchtweets = flag;
     this.analyzer = flag? new TweetAnalyzer(true) : new EnglishAnalyzer();
  }

  public void setAnalyzer(Analyzer analyzer) {
    this.analyzer = analyzer;
  }

  public Analyzer getAnalyzer(){
    return this.analyzer;
  }

  public void setLanguage(String language) {
    if (language.equals("zh")) {
      this.analyzer = new CJKAnalyzer();
    } else if (language.equals("ar")) {
      this.analyzer = new ArabicAnalyzer();
    } else if (language.equals("fr")) {
      this.analyzer = new FrenchAnalyzer();
    } else if (language.equals("hi")) {
      this.analyzer = new HindiAnalyzer();
    } else if (language.equals("bn")) {
      this.analyzer = new BengaliAnalyzer();
    } else if (language.equals("de")) {
      this.analyzer = new GermanAnalyzer();
    } else if (language.equals("es")) {
      this.analyzer = new SpanishAnalyzer();
    }
  }

  public void unsetRM3Reranker() {
    this.isRerank = false;
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  public void setRM3Reranker() {
    setRM3Reranker(10, 10, 0.5f, false);
  }

  public void setRM3Reranker(int fbTerms, int fbDocs, float originalQueryWeight) {
    setRM3Reranker(fbTerms, fbDocs, originalQueryWeight, false);
  }

  public void setRM3Reranker(int fbTerms, int fbDocs, float originalQueryWeight, boolean rm3_outputQuery) {
    isRerank = true;
    cascade = new RerankerCascade("rm3");
    cascade.add(new Rm3Reranker(this.analyzer, IndexArgs.CONTENTS,
        fbTerms, fbDocs, originalQueryWeight, rm3_outputQuery));
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  public void setLMDirichletSimilarity(float mu) {
    this.similarity = new LMDirichletSimilarity(mu);

    // We need to re-initialize the searcher
    searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);
  }

  public void setBM25Similarity(float k1, float b) {
    this.similarity = new BM25Similarity(k1, b);

    // We need to re-initialize the searcher
    searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);
  }

  /**
   * Returns the number of documents in the provided index file. Used to expose the total number of documents in Pyserini
   *
   * @return the number of documents in total
   */

   public int getTotalNumDocuments(){
     if (searcher == null) {
       searcher = new IndexSearcher(reader);
      }
     return searcher.getIndexReader().maxDoc();
   }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public Map<String, Result[]> batchSearch(List<String> queries, List<String> qids, int k, int threads) {
    return batchSearchFields(queries, qids, k, -1, threads, new HashMap<>());
  }

  public Map<String, Result[]> batchSearch(List<String> queries, List<String> qids, int k, long t, int threads) {
    return batchSearchFields(queries, qids, k, t, threads, new HashMap<>());
  }

  public Map<String, Result[]> batchSearchFields(List<String> queries, List<String> qids, int k, int threads,
                                                 Map<String, Float> fields) {
    return batchSearchFields(queries, qids, k, -1, threads, fields);
  }

  public Map<String, Result[]> batchSearchFields(List<String> queries, List<String> qids, int k, long t, int threads,
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
            results.put(qid, searchFields(query, fields, k, t));
          } else {
            results.put(qid, search(query, k, t));
          }
        } catch (IOException e) {
          throw new CompletionException(e);
        }
        // logging for speed
        Long lineNumber = index.incrementAndGet();
        if (lineNumber % 100 == 0) {
          double timePerQuery = (double) (System.nanoTime() - startTime) / (lineNumber + 1) / 1e9;
          LOG.info(String.format("Retrieving query " + lineNumber + " (%.3f s/query)\n", timePerQuery));
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

  public Result[] search(String q) throws IOException {
    return search(q, 10);
  }

  public Result[] search(String q, int k) throws IOException {
    return search(q, k, -1);
  }

  public Result[] search(String q, int k, long t) throws IOException {
    Query query = new BagOfWordsQueryGenerator().buildQuery(IndexArgs.CONTENTS, analyzer, q);
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);

    return search(query, queryTokens, q, k, t);
  }

  public Result[] search(Query query, int k) throws IOException {
    return search(query, null, null, k, -1);
  }

  public Result[] search(QueryGenerator generator, String q, int k) throws IOException {
    Query query = generator.buildQuery(IndexArgs.CONTENTS, analyzer, q);

    return search(query, null, null, k, -1);
  }

  protected Result[] search(Query query, List<String> queryTokens, String queryString, int k,
                            long t) throws IOException {
    // Create an IndexSearch only once. Note that the object is thread safe.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    SearchArgs searchArgs = new SearchArgs();
    searchArgs.arbitraryScoreTieBreak = false;
    searchArgs.hits = k;
    searchArgs.searchtweets = searchtweets;

    TopDocs rs;
    RerankerContext context;
    if (searchtweets) {
      if (t > 0) {
        // Do not consider the tweets with tweet ids that are beyond the queryTweetTime
        // <querytweettime> tag contains the timestamp of the query in terms of the
        // chronologically nearest tweet id within the corpus
        Query filter = LongPoint.newRangeQuery(TweetGenerator.TweetField.ID_LONG.name, 0L, t);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(filter, BooleanClause.Occur.FILTER);
        builder.add(query, BooleanClause.Occur.MUST);
        Query compositeQuery = builder.build();
        rs = searcher.search(compositeQuery, isRerank ? searchArgs.rerankcutoff :
            k, BREAK_SCORE_TIES_BY_TWEETID, true);
        context = new RerankerContext<>(searcher, null, compositeQuery, null,
            queryString, queryTokens, filter, searchArgs);
      } else {
        rs = searcher.search(query,
            isRerank ? searchArgs.rerankcutoff : k, BREAK_SCORE_TIES_BY_TWEETID, true);
        context = new RerankerContext<>(searcher, null, query, null,
            queryString, queryTokens, null, searchArgs);
      }
    } else {
      rs = searcher.search(query,
          isRerank ? searchArgs.rerankcutoff : k, BREAK_SCORE_TIES_BY_DOCID, true);
      context = new RerankerContext<>(searcher, null, query, null,
          queryString, queryTokens, null, searchArgs);
    }

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

  public Result[] searchFields(String q, Map<String, Float> fields, int k) throws IOException {
    return searchFields(q, fields, k, -1);
  }

  // searching both the defaults contents fields and another field with weight boost
  // this is used for MS MARCO experiments with document expansion.
  public Result[] searchFields(String q, Map<String, Float> fields, int k, long t) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    Query queryContents = new BagOfWordsQueryGenerator().buildQuery(IndexArgs.CONTENTS, analyzer, q);
    BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder()
        .add(queryContents, BooleanClause.Occur.SHOULD);

    for (Map.Entry<String, Float> entry : fields.entrySet()) {
      Query queryField = new BagOfWordsQueryGenerator().buildQuery(entry.getKey(), analyzer, q);
      queryBuilder.add(new BoostQuery(queryField, entry.getValue()), BooleanClause.Occur.SHOULD);
    }

    BooleanQuery query = queryBuilder.build();
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);

    return search(query, queryTokens, q, k, -1);
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

}
