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

import io.anserini.analysis.fw.FakeWordsEncoderAnalyzer;
import io.anserini.analysis.lexlsh.LexicalLshAnalyzer;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.FileSegment;
import io.anserini.collection.SourceDocument;
import io.anserini.index.generator.EmptyDocumentException;
import io.anserini.index.generator.InvalidDocumentException;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.index.generator.SkippedDocumentException;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.lucene95.Lucene95Codec;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public final class IndexInvertedDenseVectors {
  private static final Logger LOG = LogManager.getLogger(IndexInvertedDenseVectors.class);

  public static final String FW = "fw";
  public static final String LEXLSH = "lexlsh";

  public static final class Args {
    @Option(name = "-collection", metaVar = "[class]", required = true, usage = "Collection class in io.anserini.collection.")
    public String collectionClass;

    @Option(name = "-input", metaVar = "[path]", required = true, usage = "Input collection.")
    public String input;

    @Option(name = "-generator", metaVar = "[class]", usage = "Document generator class in io.anserini.index.generator.")
    public String generatorClass = "InvertedDenseVectorDocumentGenerator";

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Index path.")
    public String index;

    @Option(name = "-encoding", metaVar = "[word]", usage = "Encoding method: {'fw', 'lexlsh'}.")
    public String encoding = FW;

    @Option(name = "-fw.q", metaVar = "[int]", usage = "Fake Words encoding: quantization factor.")
    public int q = FakeWordsEncoderAnalyzer.DEFAULT_Q;

    @Option(name = "-lexlsh.n", metaVar = "[int]", usage = "LexLSH encoding: n-gram size.")
    public int ngrams = 2;

    @Option(name = "-lexlsh.d", metaVar = "[int]", usage = "LexLSH encoding: decimal digits.")
    public int decimals = 1;

    @Option(name = "-lexlsh.hsize", metaVar = "[int]", usage = "LexLSH encoding: hash set size.")
    public int hashSetSize = 1;

    @Option(name = "-lexlsh.h", metaVar = "[int]", usage = "LexLSH encoding: hash count.")
    public int hashCount = 1;

    @Option(name = "-lexlsh.b", metaVar = "[int]", usage = "LexLSH encoding: bucket count.")
    public int bucketCount = 300;

    @Option(name = "-optimize", usage = "Optimizes index by merging into a single index segment.")
    public boolean optimize = false;

    @Option(name = "-memorybuffer", metaVar = "[mb]", usage = "Memory buffer size in MB.")
    public int memorybufferSize = 4096;

    @Option(name = "-threads", metaVar = "[num]", usage = "Number of indexing threads.")
    public int threads = 4;

    @Option(name = "-verbose", forbids = {"-quiet"}, usage = "Enables verbose logging for each indexing thread.")
    public boolean verbose = false;

    @Option(name = "-quiet", forbids = {"-verbose"}, usage = "Turns off all logging.")
    public boolean quiet = false;
  }

  private final class LocalIndexerThread extends Thread {
    final private Path inputFile;
    final private IndexWriter writer;
    final private DocumentCollection<? extends SourceDocument> collection;

    private LocalIndexerThread(IndexWriter writer, DocumentCollection<? extends SourceDocument> collection, Path inputFile) {
      this.writer = writer;
      this.collection = collection;
      this.inputFile = inputFile;
      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      FileSegment<? extends SourceDocument> segment = null;

      try {
        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator<SourceDocument> generator = (LuceneDocumentGenerator<SourceDocument>)
            generatorClass.getDeclaredConstructor(Args.class).newInstance(args);

        // We keep track of two separate counts: the total count of documents in this file segment (cnt),
        // and the number of documents in this current "batch" (batch). We update the global counter every
        // 10k documents: this is so that we get intermediate updates, which is informative if a collection
        // has only one file segment; see https://github.com/castorini/anserini/issues/683
        int cnt = 0;
        int batch = 0;

        segment = collection.createFileSegment(inputFile);

        for (SourceDocument d : segment) {
          if (!d.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          try {
            writer.addDocument(generator.createDocument(d));

            cnt++;
            batch++;
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

          // Add the counts from this batch, reset batch counter.
          if (batch % 10000 == 0) {
            counters.indexed.addAndGet(batch);
            batch = 0;
          }
        }

        // Add the remaining documents.
        counters.indexed.addAndGet(batch);

        int skipped = segment.getSkippedCount();
        if (skipped > 0) {
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
        segment.close();
      }
    }
  }

  private final Args args;
  private final Path collectionPath;
  private final Class<LuceneDocumentGenerator<? extends SourceDocument>> generatorClass;
  private final DocumentCollection<? extends SourceDocument> collection;
  private final Counters counters;
  private final Path indexPath;

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
    LOG.info("Collection class: " + args.collectionClass);
    LOG.info("Collection path: " + args.input);
    LOG.info("Generator: " + args.generatorClass);
    LOG.info("Index path: " + args.index);
    LOG.info("Encoding: " + args.encoding);
    LOG.info("Threads: " + args.threads);
    LOG.info("Optimize? " + args.optimize);

    this.indexPath = Paths.get(args.index);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }

    // Our documentation uses /path/to/foo as a convention: to make copy and paste of the commands work,
    // we assume collections/ as the path location.
    String pathStr = args.input;
    if (pathStr.startsWith("/path/to")) {
      pathStr = pathStr.replace("/path/to", "collections");
    }
    this.collectionPath = Paths.get(pathStr);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Invalid collection path " + collectionPath + "!");
    }

    Class<? extends DocumentCollection<?>> collectionClass = (Class<? extends DocumentCollection<?>>)
        Class.forName("io.anserini.collection." + args.collectionClass);
    this.collection = collectionClass.getConstructor(Path.class).newInstance(collectionPath);

    this.generatorClass = (Class<LuceneDocumentGenerator<? extends SourceDocument>>)
        Class.forName("io.anserini.index.generator." + args.generatorClass);

    this.counters = new Counters();
  }

  public Counters run() throws IOException {
    LOG.info("============ Indexing Collection ============");
    final long start = System.nanoTime();

    Analyzer vectorAnalyzer;
    if (args.encoding.equalsIgnoreCase(FW)) {
      vectorAnalyzer = new FakeWordsEncoderAnalyzer(args.q);
    } else if (args.encoding.equalsIgnoreCase(LEXLSH)) {
      vectorAnalyzer = new LexicalLshAnalyzer(args.decimals, args.ngrams, args.hashCount, args.bucketCount, args.hashSetSize);
    } else {
      throw new RuntimeException("Invalid encoding scheme!");
    }

    Map<String, Analyzer> map = new HashMap<>();
    map.put(Constants.VECTOR, vectorAnalyzer);
    Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), map);

    final Directory dir = FSDirectory.open(indexPath);
    final IndexWriterConfig config = new IndexWriterConfig(analyzer).setCodec(new Lucene95Codec());
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(args.memorybufferSize);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());
    IndexWriter writer = new IndexWriter(dir, config);

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(args.threads);
    LOG.info("Thread pool with " + args.threads + " threads initialized.");
    LOG.info("Initializing collection in " + collectionPath);

    List<Path> segmentPaths = collection.getSegmentPaths();
    final int segmentCnt = segmentPaths.size();

    LOG.info(String.format("%,d %s found", segmentCnt, (segmentCnt == 1 ? "file" : "files")));
    LOG.info("Starting to index...");

    segmentPaths.forEach((segmentPath) -> executor.execute(new LocalIndexerThread(writer, collection, segmentPath)));

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate.
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        if (segmentCnt == 1) {
          LOG.info(String.format("%,d documents indexed", counters.indexed.get()));
        } else {
          LOG.info(String.format("%.2f%% of files completed, %,d documents indexed",
              (double) executor.getCompletedTaskCount() / segmentCnt * 100.0d, counters.indexed.get()));
        }
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted.
      executor.shutdownNow();
      // Preserve interrupt status.
      Thread.currentThread().interrupt();
    }

    if (segmentCnt != executor.getCompletedTaskCount()) {
      throw new RuntimeException("totalFiles = " + segmentCnt +
          " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());
    }

    long numIndexed = writer.getDocStats().maxDoc;

    // Do a final commit.
    try {
      writer.commit();
      if (args.optimize) {
        writer.forceMerge(1);
      }
    } finally {
      try {
        writer.close();
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

    return counters;
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