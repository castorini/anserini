/**
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

package io.anserini.util;

import io.anserini.collection.FileSegment;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.SourceDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Simple program to benchmark IO performance, reading collections from disk.
public final class BenchmarkCollectionReader {
  private static final Logger LOG = LogManager.getLogger(BenchmarkCollectionReader.class);

  public static final class Args {
    @Option(name = "-input", metaVar = "[Directory]", required = true, usage = "collection directory")
    public String input;

    @Option(name = "-threads", metaVar = "[Number]", required = true, usage = "Number of Threads")
    public int threads;

    @Option(name = "-collection", required = true, usage = "collection class in io.anserini.collection")
    public String collectionClass;
  }

  private final class ReaderThread extends Thread {
    final private Path inputFile;
    final private DocumentCollection collection;

    private ReaderThread(DocumentCollection collection, Path inputFile) {
      this.collection = collection;
      this.inputFile = inputFile;

      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      try {
        @SuppressWarnings("unchecked")
        FileSegment<SourceDocument> segment = (FileSegment) collection.createFileSegment(inputFile);

        // We're calling these records because the documents may not an indexable.
        AtomicInteger records = new AtomicInteger();
        segment.iterator().forEachRemaining(d -> {
          records.incrementAndGet();
        });

        segment.close();
        LOG.info(inputFile.getParent().getFileName().toString() + File.separator +
            inputFile.getFileName().toString() + ": " + records.incrementAndGet() + " records processed.");
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      }
    }
  }

  private final Args args;
  private final Path collectionPath;
  private final Class collectionClass;
  private final DocumentCollection collection;

  @SuppressWarnings("unchecked")
  public BenchmarkCollectionReader(Args args) throws Exception {
    this.args = args;

    LOG.info("DocumentCollection path: " + args.input);
    LOG.info("CollectionClass: " + args.collectionClass);
    LOG.info("Threads: " + args.threads);

    collectionPath = Paths.get(args.input);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath.toString() +
          " does not exist or is not readable, please check the path");
    }

    this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);

    // Initialize the collection.
    collection = (DocumentCollection) this.collectionClass.getConstructor(Path.class).newInstance(collectionPath);
  }

  public void run() {
    final long start = System.nanoTime();
    LOG.info("Starting MapCollections...");

    int numThreads = args.threads;
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    final List segmentPaths = collection.getSegmentPaths();

    final int segmentCnt = segmentPaths.size();
    LOG.info(segmentCnt + " files found in " + collectionPath.toString());
    for (int i = 0; i < segmentCnt; i++) {
      executor.execute(new ReaderThread(collection, (Path) segmentPaths.get(i)));
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        LOG.info(String.format("%.2f percent completed",
            (double) executor.getCompletedTaskCount() / segmentCnt * 100.0d));
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

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    System.out.println("Total running time: " + durationMillis + "ms");
  }

  public static void main(String[] args) throws Exception {
    Args mapCollectionArgs = new Args();
    CmdLineParser parser = new CmdLineParser(mapCollectionArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ BenchmarkCollectionReader.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new BenchmarkCollectionReader(mapCollectionArgs).run();
  }
}