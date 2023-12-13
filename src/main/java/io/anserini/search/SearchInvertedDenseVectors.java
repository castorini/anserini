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

import io.anserini.analysis.fw.FakeWordsEncoderAnalyzer;
import io.anserini.index.Constants;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.search.query.InvertedDenseVectorQueryGenerator;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.util.PrebuiltIndexHandler;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.anserini.index.IndexInvertedDenseVectors.FW;

/**
 * Main entry point for inverted dense vector search.
 */
public final class SearchInvertedDenseVectors<K> implements Runnable, Closeable {
  // These are the default tie-breaking rules for documents that end up with the same score with respect to a query.
  // For most collections, docids are strings, and we break ties by lexicographic sort order.
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(Constants.ID, SortField.Type.STRING_VAL));

  private static final Logger LOG = LogManager.getLogger(SearchInvertedDenseVectors.class);

  public static class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index")
    public String index;

    @Option(name = "-topics", metaVar = "[file]", required = true, handler = StringArrayOptionHandler.class, usage = "topics file")
    public String[] topics;

    @Option(name = "-output", metaVar = "[file]", usage = "output file")
    public String output;

    @Option(name = "-topicReader", usage = "TopicReader to use.")
    public String topicReader;

    @Option(name = "-topicField", usage = "Topic field that should be used as the query.")
    public String topicField = "title";

    @Option(name = "-encoding", metaVar = "[word]", required = true, usage = "Encoding, must be one of {fw, lexlsh}")
    public String encoding;

    @Option(name = "-lexlsh.n", metaVar = "[int]", usage = "ngrams")
    public int ngrams = 2;

    @Option(name = "-lexlsh.d", metaVar = "[int]", usage = "decimals")
    public int decimals = 1;

    @Option(name = "-lexlsh.hsize", metaVar = "[int]", usage = "hash set size")
    public int hashSetSize = 1;

    @Option(name = "-lexlsh.h", metaVar = "[int]", usage = "hash count")
    public int hashCount = 1;

    @Option(name = "-lexlsh.b", metaVar = "[int]", usage = "bucket count")
    public int bucketCount = 300;

    @Option(name = "-fw.q", metaVar = "[int]", usage = "quantization factor")
    public int q = FakeWordsEncoderAnalyzer.DEFAULT_Q;

    @Option(name = "-threads", metaVar = "[int]", usage = "Number of threads for running queries in parallel.")
    public int threads = 4;

    @Option(name = "-removeQuery", usage = "Remove docids that have the query id when writing final run output.")
    public Boolean removeQuery = false;

    // Note that this option is set to false by default because duplicate documents usually indicate some underlying
    // indexing issues, and we don't want to just eat errors silently.
    @Option(name = "-removedups", usage = "Remove duplicate docids when writing final run output.")
    public Boolean removedups = false;

    @Option(name = "-hits", metaVar = "[number]", usage = "max number of hits to return")
    public int hits = 1000;

    @Option(name = "-runtag", metaVar = "[tag]", usage = "runtag")
    public String runtag = "Anserini";

    @Option(name = "-format", metaVar = "[output format]", usage = "Output format, default \"trec\", alternative \"msmarco\".")
    public String format = "trec";

    @Option(name = "-options", usage = "Print information about options.")
    public Boolean options = false;

    // ---------------------------------------------
    // Simple built-in support for passage retrieval
    // ---------------------------------------------

    // A simple approach to passage retrieval is to pre-segment documents in the corpus into passages and index those
    // passages. At retrieval time, we retain only the max scoring passage from each document; this is often called MaxP,
    // from Dai and Callan (SIGIR 2019) in the context of BERT, although the general approach dates back to Callan
    // (SIGIR 1994), Hearst and Plaunt (SIGIR 1993), and lots of other papers from the 1990s and even earlier.
    //
    // One common convention is to label the passages of a docid as "docid.00000", "docid.00001", "docid.00002", ...
    // We use this convention in CORD-19. Alternatively, in document expansion for the MS MARCO document corpus, we use
    // '#' as the delimiter.
    //
    // The options below control various aspects of this behavior.

    @Option(name = "-selectMaxPassage", usage = "Select and retain only the max scoring segment from each document.")
    public Boolean selectMaxPassage = false;

    @Option(name = "-selectMaxPassage.delimiter", metaVar = "[regexp]",
        usage = "The delimiter (as a regular regression) for splitting the segment id from the doc id.")
    public String selectMaxPassage_delimiter = "\\.";

    @Option(name = "-selectMaxPassage.hits", metaVar = "[int]",
        usage = "Maximum number of hits to return per topic after segment id removal. " +
            "Note that this is different from '-hits', which specifies the number of hits including the segment id.")
    public int selectMaxPassage_hits = Integer.MAX_VALUE;
  }

  private final Args args;
  private final IndexReader reader;
  private final IndexSearcher searcher;
  private final InvertedDenseVectorQueryGenerator generator;
  private final SortedMap<K, String> queries = new TreeMap<>();
  private final ConcurrentSkipListMap<K, String> results = new ConcurrentSkipListMap<>();

  public SearchInvertedDenseVectors(Args args) {
    this.args = args;

    LOG.info("============ Initializing InvertedDenseVector Searcher ============");
    LOG.info("Index: " + args.index);
    LOG.info("Topics: " + Arrays.toString(args.topics));
    LOG.info("Encoding: " + args.encoding);
    LOG.info("Threads: " + args.threads);

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
    }

    try {
      this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format("\"%s\" does not appear to be a valid index.", args.index));
    }

    this.searcher = new IndexSearcher(this.reader);
    if (args.encoding.equalsIgnoreCase(FW)) {
      searcher.setSimilarity(new ClassicSimilarity());
    }

    this.generator = new InvertedDenseVectorQueryGenerator(args, true);

    // Same as above: we might not be able to successfully read topics for a variety of reasons. Gather all possible
    // exceptions together as an unchecked exception to make initialization and error reporting clearer.
    SortedMap<K, Map<String, String>> topics = new TreeMap<>();
    for (String singleTopicsFile : args.topics) {
      Path topicsFilePath = Paths.get(singleTopicsFile);
      if (!Files.exists(topicsFilePath) || !Files.isRegularFile(topicsFilePath) || !Files.isReadable(topicsFilePath)) {
        throw new IllegalArgumentException(String.format("\"%s\" does not appear to be a valid topics file.", topicsFilePath));
      }
      try {
        @SuppressWarnings("unchecked")
        TopicReader<K> tr = (TopicReader<K>) Class
            .forName(String.format("io.anserini.search.topicreader.%sTopicReader", args.topicReader))
            .getConstructor(Path.class).newInstance(topicsFilePath);

        topics.putAll(tr.read());
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format("Unable to load topic reader \"%s\".", args.topicReader));
      }
    }

    // Now iterate through all the topics to pick out the right field with proper exception handling.
    try {
      for (Map.Entry<K, Map<String, String>> entry : topics.entrySet()) {
        K qid = entry.getKey();
        String query = entry.getValue().get(args.topicField);
        assert query != null;

        this.queries.put(qid, query);
      }
    } catch (AssertionError|Exception e) {
      throw new IllegalArgumentException(String.format("Unable to read topic field \"%s\".", args.topicField));
    }
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public void run() {
    LOG.info("============ Launching Search Threads ============");
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(args.threads);
    final AtomicInteger cnt = new AtomicInteger();

    final long start = System.nanoTime();
    for (Map.Entry<K, String> entry : queries.entrySet()) {
      K qid = entry.getKey();

      // This is the per-query execution, in parallel.
      executor.execute(() -> {
        ScoredDocuments docs;
        try {
          docs = search(this.searcher, entry.getValue());
        } catch (IOException e) {
          throw new CompletionException(e);
        }

        String runOutput = SearchCollection.generateRunOutput(docs, qid, args.format, args.runtag, args.removedups,
            args.removeQuery, args.selectMaxPassage, args.selectMaxPassage_delimiter, args.selectMaxPassage_hits);

        results.put(qid, runOutput);
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

    // Now we write the results to a run file.
    try {
      PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(args.output), StandardCharsets.UTF_8));

      // This is the default case: just dump out the qids by their natural order.
      for (K qid : results.keySet()) {
        out.print(results.get(qid));
      }

      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ScoredDocuments search(IndexSearcher searcher, String queryString) throws IOException {
    Query query = generator.buildQuery(queryString);
    TopDocs results = searcher.search(query, args.hits, BREAK_SCORE_TIES_BY_DOCID, true);

    return ScoredDocuments.fromTopDocs(results, searcher);
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (searchArgs.options) {
        System.err.printf("Options for %s:\n\n", SearchInvertedDenseVectors.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("\nRequired options are %s\n", required);
      } else {
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n", e.getMessage());
      }

      return;
    }

    final long start = System.nanoTime();

    // We're at top-level already inside a main; makes no sense to propagate exceptions further, so reformat the
    // exception messages and display on console.
    try {
      SearchInvertedDenseVectors searcher = new SearchInvertedDenseVectors(searchArgs);
      searcher.run();
      searcher.close();
    } catch (IllegalArgumentException e) {
      System.err.printf("Error: %s\n", e.getMessage());
      return;
    }

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}