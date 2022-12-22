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
import io.anserini.index.IndexReaderUtils;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
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

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
  protected RerankerCascade cascade;
  protected IndexSearcher searcher = null;
  protected boolean backwardsCompatibilityLucene8;

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
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));

    // Fix for index compatibility issue between Lucene 8 and 9: https://github.com/castorini/anserini/issues/1952
    // If we detect an older index version, we turn off consistent tie-breaking, which avoids accessing docvalues,
    // which is the source of the incompatibility.
    this.backwardsCompatibilityLucene8 = !reader.toString().contains("lucene.version=9");

    // Default to using ImpactSimilarity.
    this.similarity = new ImpactSimilarity();
    this.generator = new BagOfWordsQueryGenerator();
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
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
   * @param queries list of queries
   * @param qids    list of unique query ids
   * @param k       number of hits
   * @param threads number of threads
   * @return a map of query id to search results
   */
  public Map<String, Result[]> batch_search(List<Map<String, Float>> queries,
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
      Map<String, Float> query = queries.get(q);
      String qid = qids.get(q);
      executor.execute(() -> {
        try {
          results.put(qid, search(query, k));
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
  public Result[] search(Map<String, Float> q) throws IOException {
    return search(q, 10);
  }

  /**
   * Searches the collection.
   *
   * @param q query
   * @param k number of hits
   * @return array of search results
   * @throws IOException if error encountered during search
   */
  public Result[] search(Map<String, Float> q, int k) throws IOException {
    Query query = generator.buildQuery(Constants.CONTENTS, q);

    return _search(query, k);
  }

  // internal implementation
  protected Result[] _search(Query query, int k) throws IOException {
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
      rs = searcher.search(query, k);
    } else {
      rs = searcher.search(query, k, BREAK_SCORE_TIES_BY_DOCID, true);
    }
    context = new RerankerContext<>(searcher, null, query, null,
        null, null, null, searchArgs);

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
  