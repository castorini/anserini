/**
 * Anserini: An information retrieval toolkit built on Lucene
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

import io.anserini.collection.BaseFileSegment;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.SegmentProvider;
import io.anserini.collection.SourceDocument;

import io.anserini.util.mapper.DocumentMapper;
import io.anserini.util.mapper.DocumentMapperContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class MapCollections {
  private static final Logger LOG = LogManager.getLogger(MapCollections.class);

  public static final class Args {

    // required arguments

    @Option(name = "-input", metaVar = "[Directory]", required = true, usage = "collection directory")
    public String input;

    @Option(name = "-threads", metaVar = "[Number]", required = true, usage = "Number of Threads")
    public int threads;

    @Option(name = "-collection", required = true, usage = "collection class in io.anserini.collection")
    public String collectionClass;

    @Option(name = "-mapper", required = true, usage = "mapper class in io.anserini.util.mapper")
    public String mapperClass;

    @Option(name = "-context", required = true, usage = "context class in io.anserini.util.mapper")
    public String contextClass;

    // optional arguments

    @Option(name = "-output", metaVar = "[Path]", usage = "output path")
    public String output;

    @Option(name = "-whitelist", usage = "file containing docids, one per line; only specified docids will be indexed.")
    public String whitelist = null;

    @Option(name = "-tweet.keepRetweets", usage = "boolean switch to keep retweets while indexing")
    public boolean tweetKeepRetweets = false;

    @Option(name = "-tweet.keepUrls", usage = "boolean switch to keep URLs while indexing tweets")
    public boolean tweetKeepUrls = false;

    @Option(name = "-tweet.maxId", usage = "the max tweet Id for indexing. Tweet Ids that are larger " +
            " (when being parsed to Long type) than this value will NOT be indexed")
    public long tweetMaxId = Long.MAX_VALUE;

    @Option(name = "-tweet.deletedIdsFile", metaVar = "[Path]",
            usage = "a file that contains deleted tweetIds, one per line. these tweeets won't be indexed")
    public String tweetDeletedIdsFile = "";
  }

  private final class MapThread extends Thread {
    final private Path inputFile;
    final private DocumentCollection collection;

    private MapThread(DocumentCollection collection, Path inputFile) {
      this.collection = collection;
      this.inputFile = inputFile;

      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      try {
        @SuppressWarnings("unchecked")
        BaseFileSegment<SourceDocument> iter =
          (BaseFileSegment) ((SegmentProvider) collection).createFileSegment(inputFile);

        // We're calling these records because the documents may not in indexable.
        AtomicInteger records = new AtomicInteger();
        iter.forEachRemaining(d -> {
          mapper.process(d, context);
          records.incrementAndGet();
        });

        iter.close();
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
  private final Class mapperClass;
  private final Class contextClass;
  private final DocumentCollection collection;
  private final DocumentMapper mapper;
  private final DocumentMapperContext context;

  @SuppressWarnings("unchecked")
  public MapCollections(Args args) throws Exception {
    this.args = args;

    LOG.info("DocumentCollection path: " + args.input);
    LOG.info("CollectionClass: " + args.collectionClass);
    LOG.info("Mapper: " + args.mapperClass);
    LOG.info("Context: " + args.contextClass);
    LOG.info("Threads: " + args.threads);
    LOG.info("Output: " + args.output);
    LOG.info("Whitelist: " + args.whitelist);

    collectionPath = Paths.get(args.input);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath.toString() +
              " does not exist or is not readable, please check the path");
    }

    this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);
    this.mapperClass = Class.forName("io.anserini.util.mapper." + args.mapperClass);
    this.contextClass = Class.forName("io.anserini.util.mapper." + args.contextClass);

    collection = (DocumentCollection) collectionClass.newInstance();
    collection.setCollectionPath(collectionPath);

    context = (DocumentMapperContext) contextClass.newInstance();
    mapper = (DocumentMapper) mapperClass.getDeclaredConstructor(Args.class).newInstance(args);
    mapper.setContext(context);
  }

  public void run() {
    final long start = System.nanoTime();
    LOG.info("Starting MapCollections...");

    int numThreads = args.threads;
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    final List segmentPaths = ((SegmentProvider) collection).getFileSegmentPaths();

    final int segmentCnt = segmentPaths.size();
    LOG.info(segmentCnt + " files found in " + collectionPath.toString());
    for (int i = 0; i < segmentCnt; i++) {
      executor.execute(new MapCollections.MapThread(collection, (Path) segmentPaths.get(i)));
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

    mapper.printResult(durationMillis);
  }

  public static void main(String[] args) throws Exception {
    Args mapCollectionArgs = new Args();
    CmdLineParser parser = new CmdLineParser(mapCollectionArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ MapCollections.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new MapCollections(mapCollectionArgs).run();
  }
}

