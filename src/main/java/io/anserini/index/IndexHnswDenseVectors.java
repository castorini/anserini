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
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.codecs.lucene95.Lucene95Codec;
import org.apache.lucene.codecs.lucene95.Lucene95HnswVectorsFormat;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class IndexHnswDenseVectors {
  private static final Logger LOG = LogManager.getLogger(IndexHnswDenseVectors.class);

  public static final class Args {
    @Option(name = "-collection", metaVar = "[class]", required = true, usage = "Collection class in io.anserini.collection.")
    public String collectionClass;

    @Option(name = "-input", metaVar = "[path]", required = true, usage = "Input collection.")
    public String input;

    @Option(name = "-generator", metaVar = "[class]", usage = "Document generator class in io.anserini.index.generator.")
    public String generatorClass = "HnswDenseVectorDocumentGenerator";

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Index path.")
    public String index;

    @Option(name = "-M", metaVar = "[num]", usage = "HNSW parameters M")
    public int M = 16;
  
    @Option(name = "-efC", metaVar = "[num]", usage = "HNSW parameters ef Construction")
    public int efC = 100;

    @Option(name = "-optimize", usage = "Optimizes index by merging into a single index segment.")
    public boolean optimize = false;

    @Option(name = "-memorybuffer", metaVar = "[mb]", usage = "Memory buffer size in MB.")
    public int memorybufferSize = 4096;

    @Option(name = "-storeVectors", usage = "Boolean switch to store raw raw vectors.")
    public boolean storeVectors = false;

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
  public IndexHnswDenseVectors(Args args) throws Exception {
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
    LOG.info("Store document vectors? " + args.storeVectors);
    LOG.info("Optimize (merge segments)? " + args.optimize);
    LOG.info("Index path: " + args.index);

    this.indexPath = Paths.get(args.index);
    if (!Files.exists(this.indexPath)) {
        Files.createDirectories(this.indexPath);
    }

    // Our documentation uses /path/to/foo as a convention: to make copy and paste of the commands work, we assume
    // collections/ as the path location.
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

  // Solution provided by Solr, see https://www.mail-archive.com/java-user@lucene.apache.org/msg52149.html
  // This class exists because Lucene95HnswVectorsFormat's getMaxDimensions method is final and we
  // need to workaround that constraint to allow more than the default number of dimensions
  private static final class OpenAiDelegatingKnnVectorsFormat extends KnnVectorsFormat {
    private final KnnVectorsFormat delegate;
    private final int maxDimensions;

    public OpenAiDelegatingKnnVectorsFormat(KnnVectorsFormat delegate, int maxDimensions) {
      super(delegate.getName());
      this.delegate = delegate;
      this.maxDimensions = maxDimensions;
    }

    @Override
    public KnnVectorsWriter fieldsWriter(SegmentWriteState state) throws IOException {
      return delegate.fieldsWriter(state);
    }

    @Override
    public KnnVectorsReader fieldsReader(SegmentReadState state) throws IOException {
      return delegate.fieldsReader(state);
    }

    @Override
    public int getMaxDimensions(String fieldName) {
      return maxDimensions;
    }
  }

  public Counters run() throws IOException {
    final long start = System.nanoTime();
    LOG.info("============ Indexing Collection ============");

    final Directory dir = FSDirectory.open(indexPath);
    final IndexWriterConfig config = new IndexWriterConfig().setCodec(
        new Lucene95Codec() {
          @Override
          public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
            return new OpenAiDelegatingKnnVectorsFormat(
                new Lucene95HnswVectorsFormat(args.M, args.efC), 4096);
          }
        });
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(args.memorybufferSize);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());
    IndexWriter writer = new IndexWriter(dir, config);

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(args.threads);
    LOG.info("Thread pool with " + args.threads + " threads initialized.");
    LOG.info("Initializing collection in " + collectionPath.toString());

    List<Path> segmentPaths = collection.getSegmentPaths();
    final int segmentCnt = segmentPaths.size();

    LOG.info(String.format("%,d %s found", segmentCnt, (segmentCnt == 1 ? "file" : "files" )));
    LOG.info("Starting to index...");

    segmentPaths.forEach((segmentPath) -> executor.execute(new LocalIndexerThread(writer, collection, segmentPath)));

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
      System.err.println("Example: " + IndexHnswDenseVectors.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexHnswDenseVectors(indexCollectionArgs).run();
  }
}
