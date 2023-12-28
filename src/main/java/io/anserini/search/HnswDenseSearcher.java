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

import ai.onnxruntime.OrtException;
import io.anserini.encoder.dense.DenseEncoder;
import io.anserini.index.Constants;
import io.anserini.search.query.VectorQueryGenerator;
import io.anserini.util.PrebuiltIndexHandler;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnFloatVectorQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.Option;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HnswDenseSearcher<K extends Comparable<K>> extends AbstractSearcher<K> implements Closeable {
  // These are the default tie-breaking rules for documents that end up with the same score with respect to a query.
  // For most collections, docids are strings, and we break ties by lexicographic sort order.
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(Constants.ID, SortField.Type.STRING_VAL));

  private static final Logger LOG = LogManager.getLogger(HnswDenseSearcher.class);

  /**
   * This class holds arguments for configuring the HNSW searcher. Note that, explicitly, there are no arguments that
   * define queries and outputs, since this class is meant to be called interactively.
   */
  public static class Args extends BaseSearchArgs {
    @Option(name ="-encoder", metaVar = "[encoder]", usage = "Dense encoder to use.")
    public String encoder = null;

    @Option(name = "-efSearch", metaVar = "[number]", usage = "efSearch parameter for HNSW search")
    public int efSearch = 100;
  }

  private final IndexReader reader;
  private final IndexSearcher searcher;
  private final VectorQueryGenerator generator;
  private final DenseEncoder encoder;

  public HnswDenseSearcher(Args args) {
    super(args);

    // We might not be able to successfully create a reader for a variety of reasons, anything from path doesn't exist
    // to corrupt index. Gather all possible exceptions together as an unchecked exception to make initialization and
    // error reporting clearer.
    Path indexPath = Path.of(args.index);
    PrebuiltIndexHandler indexHandler = new PrebuiltIndexHandler(args.index);
    if (!Files.exists(indexPath)) {
      // it doesn't exist locally, we try to download it from remote
      try {
        indexHandler.initialize();
        indexHandler.download();
        indexPath = Path.of(indexHandler.decompressIndex());
      } catch (IOException e) {
        throw new RuntimeException("MD5 checksum does not match!");
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format("\"%s\" does not appear to be a valid index.", args.index));
      }
    } else {
      // if it exists locally, we use it
      indexPath = Paths.get(args.index);
    }

    try {
      this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format("\"%s\" does not appear to be a valid index.", args.index));
    }

    this.searcher = new IndexSearcher(this.reader);

    try {
      this.generator = (VectorQueryGenerator) Class
          .forName(String.format("io.anserini.search.query.%s", args.queryGenerator))
          .getConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load QueryGenerator \"%s\".", args.queryGenerator));
    }

    if (args.encoder != null) {
      try {
        encoder = (DenseEncoder) Class
            .forName(String.format("io.anserini.encoder.dense.%sEncoder", args.encoder))
            .getConstructor().newInstance();
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format("Unable to load Encoder \"%s\".", args.encoder));
      }
    } else {
      encoder = null;
    }
  }

  public SortedMap<K, ScoredDoc[]> batch_search(List<K> qids, List<String> queries, int hits) {
    final SortedMap<K, ScoredDoc[]> results = new ConcurrentSkipListMap<>();
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(args.threads);
    final AtomicInteger cnt = new AtomicInteger();

    final long start = System.nanoTime();
    assert qids.size() == queries.size();
    for (int i=0; i<qids.size(); i++) {
      K qid = qids.get(i);
      String queryString = queries.get(i);

      // This is the per-query execution, in parallel.
      executor.execute(() -> {
        try {
          results.put(qid, search(qid, queryString, hits));
        } catch (IOException e) {
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
        String.format(" = ~%.2f q/s", queries.size() / (durationMillis / 1000.0)));

    return results;
  }

  public ScoredDoc[] search(float[] queryFloat, int hits) throws IOException {
    return search(null, queryFloat, hits);
  }

  public ScoredDoc[] search(@Nullable K qid, float[] queryFloat, int hits) throws IOException {
    KnnFloatVectorQuery query = new KnnFloatVectorQuery(Constants.VECTOR, queryFloat, ((Args) args).efSearch);
    TopDocs topDocs = searcher.search(query, hits, BREAK_SCORE_TIES_BY_DOCID, true);

    return super.processLuceneTopDocs(this.searcher, qid, topDocs);
  }

  public ScoredDoc[] search(String queryString, int hits) throws IOException {
    return search(null, queryString, hits);
  }

  public ScoredDoc[] search(@Nullable K qid, String queryString, int hits) throws IOException {
    if (encoder != null) {
      try {
        return search(qid, encoder.encode(queryString), hits);
      } catch (OrtException e) {
        throw new RuntimeException("Error encoding query.");
      }
    }

    KnnFloatVectorQuery query = generator.buildQuery(Constants.VECTOR, queryString, ((Args) args).efSearch);
    TopDocs topDocs = searcher.search(query, hits, BREAK_SCORE_TIES_BY_DOCID, true);

    return super.processLuceneTopDocs(this.searcher, qid, topDocs);
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
}
