package io.anserini.search;

import ai.onnxruntime.OrtException;
import io.anserini.encoder.dense.DenseEncoder;
import io.anserini.index.Constants;
import io.anserini.search.query.VectorQueryGenerator;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnFloatVectorQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleHnswSearcher<K> {
  // These are the default tie-breaking rules for documents that end up with the same score with respect to a query.
  // For most collections, docids are strings, and we break ties by lexicographic sort order.
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(Constants.ID, SortField.Type.STRING_VAL));

  private static final Logger LOG = LogManager.getLogger(SimpleHnswSearcher.class);

  private IndexSearcher searcher;
  private final VectorQueryGenerator generator;
  private final DenseEncoder encoder;

  private int efSearch;
  private boolean removeQuery;
  private boolean removedups;
  private boolean selectMaxPassage;
  private String selectMaxPassage_delimiter;
  private int selectMaxPassage_hits;
  private int threads;

  public SimpleHnswSearcher(IndexSearcher searcher, VectorQueryGenerator generator, DenseEncoder encoder, SearchHnswDenseVectors.Args args) {
    this.searcher = searcher;
    this.generator = generator;
    this.encoder = encoder;

    this.efSearch = args.efSearch;
    this.removedups = args.removedups;
    this.removeQuery = args.removeQuery;
    this.selectMaxPassage = args.selectMaxPassage;
    this.selectMaxPassage_delimiter = args.selectMaxPassage_delimiter;
    this.selectMaxPassage_hits = args.selectMaxPassage_hits;
    this.threads = args.threads;
  }

  public SortedMap<K, Result[]> batch_search(Map<K, String> queries, int hits) {
    final SortedMap<K, Result[]> results = new ConcurrentSkipListMap<>();
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    final AtomicInteger cnt = new AtomicInteger();

    final long start = System.nanoTime();
    for (Map.Entry<K, String> entry : queries.entrySet()) {
      K qid = entry.getKey();

      // This is the per-query execution, in parallel.
      executor.execute(() -> {
        String queryString = entry.getValue();
        TopDocs docs;

        try {
          Result[] rs = encoder != null ?
              search(qid, encoder.encode(queryString), hits) :
              search(qid, queryString, hits);

          results.put(qid, rs);
        } catch (IOException| OrtException e) {
          throw new CompletionException(e);
        }

        int n = cnt.incrementAndGet();
        if (n % 100 == 0) {
          LOG.info(String.format("%d queries processed", n));
        }
      });
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate.
      while (!executor.awaitTermination(1, TimeUnit.MINUTES));
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted.
      executor.shutdownNow();
      // Preserve interrupt status.
      Thread.currentThread().interrupt();
    }
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);

    LOG.info(queries.size() + " queries processed in " +
        DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss") +
        String.format(" = ~%.2f q/s", queries.size()/(durationMillis/1000.0)));

    return results;
  }

  public Result[] search(K qid, float[] queryFloat, int hits) throws IOException {
    KnnFloatVectorQuery query = new KnnFloatVectorQuery(Constants.VECTOR, queryFloat, efSearch);
    TopDocs topDocs = searcher.search(query, hits, BREAK_SCORE_TIES_BY_DOCID, true);

    return RunOutputWriter.generateRunOutput(this.searcher, topDocs, qid, removedups,
        removeQuery, selectMaxPassage, selectMaxPassage_delimiter, selectMaxPassage_hits);
  }

  public Result[] search(K qid, String queryString, int hits) throws IOException {
    KnnFloatVectorQuery query = generator.buildQuery(Constants.VECTOR, queryString, efSearch);
    TopDocs topDocs = searcher.search(query, hits, BREAK_SCORE_TIES_BY_DOCID, true);

    return RunOutputWriter.generateRunOutput(this.searcher, topDocs, qid, removedups,
        removeQuery, selectMaxPassage, selectMaxPassage_delimiter, selectMaxPassage_hits);
  }

}
