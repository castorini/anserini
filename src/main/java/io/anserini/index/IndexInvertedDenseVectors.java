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

package io.anserini.index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.anserini.analysis.fw.FakeWordsEncoderAnalyzer;
import io.anserini.analysis.lexlsh.LexicalLshAnalyzer;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.FileSegment;
import io.anserini.collection.SourceDocument;
import io.anserini.index.generator.EmptyDocumentException;
import io.anserini.index.generator.InvalidDocumentException;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.index.generator.SkippedDocumentException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.backward_codecs.lucene94.Lucene94Codec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;


public final class IndexInvertedDenseVectors {

  public static final String FIELD_ID = "id";
  public static final String FIELD_VECTOR = "vector";

  public static final String FW = "fw";
  public static final String LEXLSH = "lexlsh";


  public static final class Args {

    // This is the name of the field in the Lucene document where the docid is stored.
    public static final String ID = "id";

    // This is the name of the field in the Lucene document that should be searched by default.
    public static final String CONTENTS = "contents";

    // This is the name of the field in the Lucene document where the raw document is stored.
    public static final String RAW = "raw";

    @Option(name = "-input", metaVar = "[path]", required = true,
        usage = "Location of input collection.")
    public String input;

    @Option(name = "-threads", metaVar = "[num]",
        usage = "Number of indexing threads.")
    public int threads = 1;

    @Option(name = "-collection", metaVar = "[class]",
        usage = "Collection class in package 'io.anserini.collection'.")
    public String collectionClass;

    @Option(name = "-generator", metaVar = "[class]",
        usage = "Document generator class in package 'io.anserini.index.generator'.")
    public String generatorClass = "InvertedDenseVectorDocumentGenerator";

    // optional general arguments

    @Option(name = "-verbose", forbids = {"-quiet"},
        usage = "Enables verbose logging for each indexing thread; can be noisy if collection has many small file segments.")
    public boolean verbose = false;

    @Option(name = "-quiet", forbids = {"-verbose"},
        usage = "Turns off all logging.")
    public boolean quiet = false;

    // optional arguments

    @Option(name = "-index", metaVar = "[path]", usage = "Index path.", required = true)
    public String index;

    @Option(name = "-fields", handler = StringArrayOptionHandler.class,
        usage = "List of fields to index (space separated), in addition to the default 'contents' field.")
    public String[] fields = new String[]{};

    @Option(name = "-storePositions",
        usage = "Boolean switch to index store term positions; needed for phrase queries.")
    public boolean storePositions = false;

    @Option(name = "-storeDocvectors",
        usage = "Boolean switch to store document vectors; needed for (pseudo) relevance feedback.")
    public boolean storeDocvectors = false;

    @Option(name = "-storeContents",
        usage = "Boolean switch to store document contents.")
    public boolean storeContents = false;

    @Option(name = "-storeRaw",
        usage = "Boolean switch to store raw source documents.")
    public boolean storeRaw = false;

    @Option(name = "-optimize",
        usage = "Boolean switch to optimize index (i.e., force merge) into a single segment; costly for large collections.")
    public boolean optimize = false;

    @Option(name = "-uniqueDocid",
        usage = "Removes duplicate documents with the same docid during indexing. This significantly slows indexing throughput " +
            "but may be needed for tweet collections since the streaming API might deliver a tweet multiple times.")
    public boolean uniqueDocid = false;

    @Option(name = "-memorybuffer", metaVar = "[mb]",
        usage = "Memory buffer size (in MB).")
    public int memorybufferSize = 2048;

    @Option(name = "-whitelist", metaVar = "[file]",
        usage = "File containing list of docids, one per line; only these docids will be indexed.")
    public String whitelist = null;

    @Option(name = "-encoding", metaVar = "[word]", required = true, usage = "encoding must be one of {fw, lexlsh}")
    public String encoding = FW;

    @Option(name = "-stored", metaVar = "[boolean]", usage = "store vectors")
    public boolean stored = false;

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
    // Sharding options

    @Option(name = "-shard.count", metaVar = "[n]",
        usage = "Number of shards to partition the document collection into.")
    public int shardCount = -1;

    @Option(name = "-shard.current", metaVar = "[n]",
        usage = "The current shard number to generate (indexed from 0).")
    public int shardCurrent = -1;
  }

  private static final Logger LOG = LogManager.getLogger(IndexInvertedDenseVectors.class);

  // This is the default analyzer used, unless another stemming algorithm or language is specified.
  public final class Counters {

    /**
     * Counter for successfully indexed documents.
     */
    public AtomicLong indexed = new AtomicLong();

    /**
     * Counter for empty documents that are not indexed. Empty documents are not necessary errors;
     * it could be the case, for example, that a document is comprised solely of stopwords.
     */
    public AtomicLong empty = new AtomicLong();

    /**
     * Counter for unindexable documents. These are cases where {@link SourceDocument#indexable()}
     * returns false.
     */
    public AtomicLong unindexable = new AtomicLong();

    /**
     * Counter for skipped documents. These are cases documents are skipped as part of normal
     * processing logic, e.g., using a whitelist, not indexing retweets or deleted tweets.
     */
    public AtomicLong skipped = new AtomicLong();

    /**
     * Counter for unexpected errors.
     */
    public AtomicLong errors = new AtomicLong();
  }

  private final class LocalIndexerThread extends Thread {

    final private Path inputFile;
    final private IndexWriter writer;
    final private DocumentCollection collection;
    private FileSegment fileSegment;

    private LocalIndexerThread(IndexWriter writer, DocumentCollection collection, Path inputFile) {
      this.writer = writer;
      this.collection = collection;
      this.inputFile = inputFile;
      setName(inputFile.getFileName().toString());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
      try {
        LuceneDocumentGenerator generator = (LuceneDocumentGenerator)
            generatorClass.getDeclaredConstructor(Args.class).newInstance(args);

        // We keep track of two separate counts: the total count of documents in this file segment (cnt),
        // and the number of documents in this current "batch" (batch). We update the global counter every
        // 10k documents: this is so that we get intermediate updates, which is informative if a collection
        // has only one file segment; see https://github.com/castorini/anserini/issues/683
        int cnt = 0;
        int batch = 0;

        FileSegment<SourceDocument> segment = collection.createFileSegment(inputFile);
        // in order to call close() and clean up resources in case of exception
        this.fileSegment = segment;

        for (SourceDocument d : segment) {
          if (!d.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          Document doc;
          try {
            doc = generator.createDocument(d);
          } catch (EmptyDocumentException e1) {
            counters.empty.incrementAndGet();
            continue;
          } catch (SkippedDocumentException e2) {
            counters.skipped.incrementAndGet();
            continue;
          } catch (InvalidDocumentException e3) {
            counters.errors.incrementAndGet();
            continue;
          }

          if (whitelistDocids != null && !whitelistDocids.contains(d.id())) {
            counters.skipped.incrementAndGet();
            continue;
          }

          if (args.uniqueDocid) {
            writer.updateDocument(new Term("id", d.id()), doc);
          } else {
            writer.addDocument(doc);
          }
          cnt++;
          batch++;

          // And the counts from this batch, reset batch counter.
          if (batch % 10000 == 0) {
            counters.indexed.addAndGet(batch);
            batch = 0;
          }
        }

        // Add the remaining documents.
        counters.indexed.addAndGet(batch);

        int skipped = segment.getSkippedCount();
        if (skipped > 0) {
          // When indexing tweets, this is normal, because there are delete messages that are skipped over.
          counters.skipped.addAndGet(skipped);
          LOG.warn(inputFile.getParent().getFileName().toString() + File.separator +
                       inputFile.getFileName().toString() + ": " + skipped + " docs skipped.");
        }

        if (segment.getErrorStatus()) {
          counters.errors.incrementAndGet();
          LOG.error(inputFile.getParent().getFileName().toString() + File.separator +
                        inputFile.getFileName().toString() + ": error iterating through segment.");
        }

        // Log at the debug level because this can be quite noisy if there are lots of file segments.
        LOG.debug(inputFile.getParent().getFileName().toString() + File.separator +
                      inputFile.getFileName().toString() + ": " + cnt + " docs added.");
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      } finally {
        if (fileSegment != null) {
          fileSegment.close();
        }
      }
    }
  }

  private final Args args;
  private final Path collectionPath;
  private final Set whitelistDocids;
  private Class collectionClass;
  private final Class generatorClass;
  private DocumentCollection collection;
  private final Counters counters;
  private Path indexPath;

  @SuppressWarnings("unchecked")
  public IndexInvertedDenseVectors(Args args) throws Exception {
    this.args = args;

    if (args.verbose) {
      // If verbose logging enabled, changed default log level to DEBUG so we get per-thread logging messages.
      Configurator.setRootLevel(Level.DEBUG);
      LOG.info("Setting log level to " + Level.DEBUG);
    } else if (args.quiet) {
      // If quiet mode enabled, only report warnings and above.
      Configurator.setRootLevel(Level.WARN);
    } else {
      // Otherwise, we get the standard set of log messages.
      Configurator.setRootLevel(Level.INFO);
      LOG.info("Setting log level to " + Level.INFO);
    }

    LOG.info("Starting indexer...");
    LOG.info("============ Loading Parameters ============");
    LOG.info("DocumentCollection path: " + args.input);
    LOG.info("CollectionClass: " + args.collectionClass);
    LOG.info("Generator: " + args.generatorClass);
    LOG.info("Threads: " + args.threads);
    LOG.info("Store document \"contents\" field? " + args.storeContents);
    LOG.info("Store document \"raw\" field? " + args.storeRaw);
    LOG.info("Optimize (merge segments)? " + args.optimize);
    LOG.info("Whitelist: " + args.whitelist);
    LOG.info("Index path: " + args.index);

    if (args.index != null) {
      this.indexPath = Paths.get(args.index);
      if (!Files.exists(this.indexPath)) {
        Files.createDirectories(this.indexPath);
      }
    }

    // Our documentation uses /path/to/foo as a convention: to make copy and paste of the commands work, we assume
    // collections/ as the path location.
    String pathStr = args.input;
    if (pathStr.startsWith("/path/to")) {
      pathStr = pathStr.replace("/path/to", "collections");
    }
    collectionPath = Paths.get(pathStr);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath + " does not exist or is not readable, please check the path");
    }

    if (Files.isDirectory(collectionPath) && args.collectionClass == null) {
      throw new RuntimeException("Collection class must be defined, got `null` instead");
    }

    this.generatorClass = Class.forName("io.anserini.index.generator." + args.generatorClass);
    if (args.collectionClass != null) {
      this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);
      // Initialize the collection.
      collection = (DocumentCollection) this.collectionClass.getConstructor(Path.class).newInstance(collectionPath);
    }

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }

    this.counters = new Counters();
  }

  public Counters run() throws IOException {
    final long start = System.nanoTime();

    LOG.info("============ Indexing Collection ============");

    int numThreads = args.threads;
    IndexWriter writer = null;
    Analyzer vectorAnalyzer;
    if (args.encoding.equalsIgnoreCase(FW)) {
      vectorAnalyzer = new FakeWordsEncoderAnalyzer(args.q);
    } else if (args.encoding.equalsIgnoreCase(LEXLSH)) {
      vectorAnalyzer = new LexicalLshAnalyzer(args.decimals, args.ngrams, args.hashCount,
                                              args.bucketCount, args.hashSetSize);
    } else {
      vectorAnalyzer = null;
      System.err.println("error!");
    }
    Map<String, Analyzer> map = new HashMap<>();
    map.put(FIELD_VECTOR, vectorAnalyzer);
    Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), map);

    // Used for LocalIndexThread
    if (indexPath != null) {
      final Directory dir = FSDirectory.open(indexPath);
      final IndexWriterConfig config = new IndexWriterConfig(analyzer).setCodec(new Lucene94Codec());
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(args.memorybufferSize);
      config.setUseCompoundFile(false);
      config.setMergeScheduler(new ConcurrentMergeScheduler());
      writer = new IndexWriter(dir, config);
    }

    if (Files.isDirectory(collectionPath)) {
      final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
      LOG.info("Thread pool with " + numThreads + " threads initialized.");

      LOG.info("Initializing collection in " + collectionPath);

      List<?> segmentPaths = collection.getSegmentPaths();
      // when we want sharding to be done
      if (args.shardCount > 1) {
        segmentPaths = collection.getSegmentPaths(args.shardCount, args.shardCurrent);
      }
      final int segmentCnt = segmentPaths.size();

      LOG.info(String.format("%,d %s found", segmentCnt, (segmentCnt == 1 ? "file" : "files")));
      LOG.info("Starting to index...");

      for (Object segmentPath : segmentPaths) {
        executor.execute(new LocalIndexerThread(writer, collection, (Path) segmentPath));
      }

      executor.shutdown();

      try {
        // Wait for existing tasks to terminate
        while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
          if (segmentCnt == 1) {
            LOG.info(String.format("%,d documents indexed", counters.indexed.get()));
          } else {
            LOG.info(String.format("%.2f%% of files completed, %,d documents indexed",
                                   (double) executor.getCompletedTaskCount() / segmentCnt * 100.0d, counters.indexed.get()));
          }
        }
      } catch (InterruptedException ie) {
        // (Re-)Cancel if current thread also interrupted
        executor.shutdownNow();
        // Preserve interrupt status
        Thread.currentThread().interrupt();
      }

      if (segmentCnt != executor.getCompletedTaskCount()) {
        throw new RuntimeException("totalFiles = " + segmentCnt +
                                       " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());
      }

      long numIndexed = writer.getDocStats().maxDoc;

      // Do a final commit
      try {
        if (writer != null) {
          writer.commit();
          if (args.optimize) {
            writer.forceMerge(1);
          }
        }
      } finally {
        try {
          if (writer != null) {
            writer.close();
          }
        } catch (IOException e) {
          // It is possible that this happens... but nothing much we can do at this point,
          // so just log the error and move on.
          LOG.error(e);
        }
      }

      if (numIndexed != counters.indexed.get()) {
        LOG.warn("Unexpected difference between number of indexed documents and index maxDoc.");
      }

      LOG.info(String.format("Indexing Complete! %,d documents indexed", numIndexed));
      LOG.info("============ Final Counter Values ============");
      LOG.info(String.format("indexed:     %,12d", counters.indexed.get()));
      LOG.info(String.format("unindexable: %,12d", counters.unindexable.get()));
      LOG.info(String.format("empty:       %,12d", counters.empty.get()));
      LOG.info(String.format("skipped:     %,12d", counters.skipped.get()));
      LOG.info(String.format("errors:      %,12d", counters.errors.get()));

      final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
      LOG.info(String.format("Total %,d documents indexed in %s", numIndexed,
                             DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));
    } else {
      Map<String, List<float[]>> vectors = readGloVe(new File(args.input));
      for (Map.Entry<String, List<float[]>> entry : vectors.entrySet()) {
        for (float[] vector: entry.getValue()) {
          Document doc = new Document();
          doc.add(new StringField(FIELD_ID, entry.getKey(), Field.Store.YES));
          StringBuilder sb = new StringBuilder();
          for (double fv : vector) {
            if (sb.length() > 0) {
              sb.append(' ');
            }
            sb.append(fv);
          }
          doc.add(new TextField(FIELD_VECTOR, sb.toString(), args.stored ? Field.Store.YES : Field.Store.NO));
          try {
            writer.addDocument(doc);
            long cur = counters.indexed.incrementAndGet();
            if (cur % 100000 == 0) {
              System.out.println(String.format("%s docs added", counters.indexed.get()));
            }
          } catch (IOException e) {
            System.err.println("Error while indexing: " + e.getLocalizedMessage());
            counters.errors.incrementAndGet();
          }
        }
      }

      writer.commit();
      writer.close();

      LOG.info(String.format("Indexing Complete! %,d documents indexed", counters.indexed.get()));

      final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
      LOG.info(String.format("Total %,d documents indexed in %s", counters.indexed.get(),
          DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));
    }

    return counters;
  }

  public static Map<String, List<float[]>> readGloVe(File input) throws IOException {
    Map<String, List<float[]>> vectors = new HashMap<>();
    for (String line : org.apache.commons.io.IOUtils.readLines(new FileReader(input))) {
      String[] s = line.split("\\s+");
      if (s.length > 2) {
        String key = s[0];
        float[] vector = new float[s.length - 1];
        float norm = 0f;
        for (int i = 1; i < s.length; i++) {
          float f = Float.parseFloat(s[i]);
          vector[i - 1] = f;
          norm += Math.pow(f, 2);
        }
        norm = (float) Math.sqrt(norm);
        for (int i = 0; i < vector.length; i++) {
          vector[i] = vector[i] / norm;
        }
        if (vectors.containsKey(key)) {
          List<float[]> floats = new LinkedList<>(vectors.get(key));
          floats.add(vector);
          vectors.put(key, floats);
        } else {
          vectors.put(key, List.of(vector));
        }
      }
    }
    return vectors;
  }

  public static void main(String[] args) throws Exception {
    Args indexCollectionArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexCollectionArgs, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: " + IndexInvertedDenseVectors.class.getSimpleName() +
                             parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexInvertedDenseVectors(indexCollectionArgs).run();
  }
}