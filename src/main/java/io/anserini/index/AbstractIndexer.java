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
import org.apache.lucene.index.IndexWriter;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractIndexer implements Runnable {
  private static final Logger LOG = LogManager.getLogger(AbstractIndexer.class);

  public static class Args {
    @Option(name = "-collection", metaVar = "[class]", required = true, usage = "Collection class in io.anserini.collection.")
    public String collectionClass;

    @Option(name = "-input", metaVar = "[path]", required = true, usage = "Input collection.")
    public String input;

    @Option(name = "-generator", metaVar = "[class]", usage = "Document generator class in io.anserini.index.generator.")
    public String generatorClass = "InvertedDenseVectorDocumentGenerator";

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Index path.")
    public String index;

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

    @Option(name = "-options", usage = "Print information about options.")
    public Boolean options = false;
  }

  public static class IndexerThread extends Thread {
    private final IndexWriter writer;
    private final DocumentCollection<? extends SourceDocument> collection;
    private final Path inputFile;
    private final LuceneDocumentGenerator<SourceDocument> generator;
    private final Counters counters;

    public IndexerThread(IndexWriter writer,
                         DocumentCollection<? extends SourceDocument> collection,
                         Path inputFile,
                         LuceneDocumentGenerator<SourceDocument> generator,
                         Counters counters) {
      this.writer = writer;
      this.collection = collection;
      this.inputFile = inputFile;
      this.generator = generator;
      this.counters = counters;

      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      try(FileSegment<? extends SourceDocument> segment = collection.createFileSegment(inputFile)) {
        // We keep track of two separate counts: the total count of documents in this file segment (cnt),
        // and the number of documents in this current "batch" (batch). We update the global counter every
        // 10k documents: this is so that we get intermediate updates, which is informative if a collection
        // has only one file segment; see https://github.com/castorini/anserini/issues/683
        int cnt = 0;
        int batch = 0;

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
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e.getMessage());
      }
    }
  }

  protected Counters counters = new Counters();
  protected Path collectionPath;
  protected DocumentCollection<? extends SourceDocument> collection;
  protected Class<LuceneDocumentGenerator<? extends SourceDocument>> generatorClass;
  protected IndexWriter writer;
  protected int threads = 16;
  protected boolean optimize = false;

  @SuppressWarnings("unchecked")
  public AbstractIndexer(Args args) {
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

    LOG.info("============ Loading Index Configuration ============");
    LOG.info("AbstractIndexer settings:");
    LOG.info(" + DocumentCollection path: " + args.input);
    LOG.info(" + CollectionClass: " + args.collectionClass);
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + Index path: " + args.index);
    LOG.info(" + Threads: " + args.threads);
    LOG.info(" + Optimize (merge segments)? " + args.optimize);

    // Our documentation uses /path/to/foo as a convention: to make copy and paste of the commands work,
    // we assume collections/ as the path location.
    String pathStr = args.input;
    if (pathStr.startsWith("/path/to")) {
      pathStr = pathStr.replace("/path/to", "collections");
    }
    this.collectionPath = Paths.get(pathStr);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new IllegalArgumentException(String.format("Invalid collection path \"%s\".", collectionPath));
    }

    try {
      Class<? extends DocumentCollection<?>> collectionClass = (Class<? extends DocumentCollection<?>>)
          Class.forName("io.anserini.collection." + args.collectionClass);
      this.collection = collectionClass.getConstructor(Path.class).newInstance(collectionPath);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load collection class \"%s\".", args.collectionClass));
    }

    try {
      this.generatorClass = (Class<LuceneDocumentGenerator<? extends SourceDocument>>)
          Class.forName("io.anserini.index.generator." + args.generatorClass);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load generator class \"%s\".", args.generatorClass));
    }
  }

  @Override
  public void run() {
    LOG.info("============ Indexing Collection ============");
    final long start = System.nanoTime();

    final List<Path> segmentPaths = collection.getSegmentPaths();
    final int segmentCnt = segmentPaths.size();

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    LOG.info(String.format("Thread pool with %s threads initialized.", threads));
    LOG.info(String.format("%,d %s found in %s ", segmentCnt, (segmentCnt == 1 ? "file" : "files"), collectionPath));
    LOG.info("Starting to index...");

    segmentPaths.forEach((segmentPath) -> {
      try {
        // Each thread gets its own document generator, so we don't need to make any assumptions about its thread safety.
        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator<SourceDocument> generator = (LuceneDocumentGenerator<SourceDocument>)
            generatorClass.getDeclaredConstructor((Class<?> []) null).newInstance();

        executor.execute(new IndexerThread(writer, collection, segmentPath, generator, counters));
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new IllegalArgumentException(String.format("Unable to load LuceneDocumentGenerator \"%s\".", generatorClass.getSimpleName()));
      }
    });

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
    if (numIndexed != counters.indexed.get()) {
      LOG.warn("Unexpected difference between number of indexed documents and index maxDoc.");
    }

    // Do a final commit.
    try {
      writer.commit();
      if (optimize) {
        writer.forceMerge(1);
      }
    } catch (IOException e) {
      // It is possible that this happens... but nothing much we can do at this point,
      // so just log the error and move on.
      LOG.error(e);
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        // It is possible that this happens... but nothing much we can do at this point,
        // so just log the error and move on.
        LOG.error(e);
      }
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
  }

  public Counters getCounters() {
    return this.counters;
  }

}
