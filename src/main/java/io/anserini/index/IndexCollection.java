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

package io.anserini.index;

import io.anserini.analysis.TweetAnalyzer;
import io.anserini.collection.Collection;
import io.anserini.document.SourceDocument;
import io.anserini.index.generator.LuceneDocumentGenerator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class IndexCollection {
  private static final Logger LOG = LogManager.getLogger(IndexCollection.class);

  public static final class Args {

    // required arguments

    @Option(name = "-input", metaVar = "[Directory]", required = true, usage = "collection directory")
    public String input;

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;

    @Option(name = "-threads", metaVar = "[Number]", required = true, usage = "Number of Threads")
    public int threads;

    @Option(name = "-collection", required = true, usage = "collection class in io.anserini.collection")
    public String collectionClass;

    @Option(name = "-generator", required = true, usage = "document generator in io.anserini.index.generator")
    public String generatorClass;

    // optional arguments
    @Option(name = "-storePositions", usage = "boolean switch to index storePositions")
    public boolean storePositions = false;

    @Option(name = "-storeDocvectors", usage = "boolean switch to store document vectors")
    public boolean storeDocvectors = false;

    @Option(name = "-storeTransformedDocs", usage = "boolean switch to store transformed document text")
    public boolean storeTransformedDocs = false;

    @Option(name = "-storeRawDocs", usage = "boolean switch to store raw document text")
    public boolean storeRawDocs = false;

    @Option(name = "-optimize", usage = "boolean switch to optimize index (force merge)")
    public boolean optimize = false;

    @Option(name = "-keepStopwords", usage = "boolean switch to keep stopwords")
    public boolean keepStopwords = false;

    @Option(name = "-uniqueDocid", usage = "remove duplicated documents with the same doc id when indexing. " +
      "please note that this option may slow the indexing a lot and if you are sure there is no " +
      "duplicated document ids in the corpus you shouldn't use this option.")
    public boolean uniqueDocid = false;

    @Option(name = "-memorybuffer", usage = "memory buffer size")
    public int memorybufferSize = 2048;

    @Option(name = "-whitelist", usage = "file containing docids, one per line; only specified docids will be indexed.")
    public String whitelist = null;

    @Option(name = "-tweet.keepRetweets", usage = "boolean switch to keep retweets while indexing")
    public boolean tweetKeepRetweets = false;
    @Option(name = "-tweet.keepUrls", usage = "boolean switch to keep URLs while indexing tweets")
    public boolean tweetKeepUrls = false;
    @Option(name = "-tweet.stemming", usage = "boolean switch to apply Porter stemming while indexing tweets")
    public boolean tweetStemming = false;
    @Option(name = "-tweet.maxId", usage = "the max tweet Id for indexing. Tweet Ids that are larger " +
        " (when being parsed to Long type) than this value will NOT be indexed")
    public long tweetMaxId = Long.MAX_VALUE;
    @Option(name = "-tweet.deletedIdsFile", metaVar = "[Path]",
        usage = "a file that contains deleted tweetIds, one per line. these tweeets won't be indexed")
    public String tweetDeletedIdsFile = "";
  }

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
     * Counter for unindexed documents. These are cases where the {@link SourceDocument} returned
     * by {@link Collection.FileSegment} is {@code null} or the {@link LuceneDocumentGenerator}
     * returned {@code null}. These are not necessarily errors.
     */
    public AtomicLong unindexed = new AtomicLong();

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

  private final class IndexerThread extends Thread {
    final private Path inputFile;
    final private IndexWriter writer;
    final private Collection collection;

    private IndexerThread(IndexWriter writer, Collection collection, Path inputFile) throws IOException {
      this.writer = writer;
      this.collection = collection;
      this.inputFile = inputFile;
      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      try {
        LuceneDocumentGenerator generator =
            (LuceneDocumentGenerator) generatorClass
                .getDeclaredConstructor(Args.class, Counters.class)
                .newInstance(args, counters);

        int cnt = 0;
        Collection.FileSegment iter = collection.createFileSegment(inputFile);
        while (iter.hasNext()) {
          SourceDocument d = iter.next();
          if (d == null) {
            // Current implementation can't distinguish between end-of-iterator vs. actual error,
            // so don't update counters.
            continue;
          }
          if (!d.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          @SuppressWarnings("unchecked") // Yes, we know what we're doing here.
          Document doc = generator.createDocument(d);
          if (doc == null) {
            counters.unindexed.incrementAndGet();
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
        }
        iter.close();
        LOG.info(inputFile.getParent().getFileName().toString() + File.separator +
            inputFile.getFileName().toString() + ": " + cnt + " docs added.");
        counters.indexed.addAndGet(cnt);
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      }
    }
  }

  private final IndexCollection.Args args;
  private final Path indexPath;
  private final Path collectionPath;
  private final Set whitelistDocids;
  private final Class collectionClass;
  private final Class generatorClass;
  private final Collection collection;
  private final Counters counters;

  public IndexCollection(IndexCollection.Args args) throws Exception {
    this.args = args;

    LOG.info("Collection path: " + args.input);
    LOG.info("Index path: " + args.index);
    LOG.info("CollectionClass: " + args.collectionClass);
    LOG.info("Generator: " + args.generatorClass);
    LOG.info("Threads: " + args.threads);
    LOG.info("Keep stopwords? " + args.keepStopwords);
    LOG.info("Store positions? " + args.storePositions);
    LOG.info("Store docvectors? " + args.storeDocvectors);
    LOG.info("Store transformed docs? " + args.storeTransformedDocs);
    LOG.info("Store raw docs? " + args.storeRawDocs);
    LOG.info("Optimize (merge segments)? " + args.optimize);
    LOG.info("Whitelist: " + args.whitelist);

    this.indexPath = Paths.get(args.index);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }

    collectionPath = Paths.get(args.input);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath.toString() +
          " does not exist or is not readable, please check the path");
    }

    this.generatorClass = Class.forName("io.anserini.index.generator." + args.generatorClass);
    this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);

    collection = (Collection) this.collectionClass.newInstance();
    collection.setCollectionPath(collectionPath);

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }

    this.counters = new Counters();
  }

  public void run() throws IOException, InterruptedException {
    final long start = System.nanoTime();
    LOG.info("Starting indexer...");

    int numThreads = args.threads;

    final Directory dir = FSDirectory.open(indexPath);
    final EnglishAnalyzer englishAnalyzer= args.keepStopwords ?
        new EnglishAnalyzer(CharArraySet.EMPTY_SET) : new EnglishAnalyzer();
    final TweetAnalyzer tweetAnalyzer = new TweetAnalyzer(args.tweetStemming);
    final IndexWriterConfig config = args.collectionClass.equals("TweetCollection") ?
        new IndexWriterConfig(tweetAnalyzer) : new IndexWriterConfig(englishAnalyzer);
    config.setSimilarity(new BM25Similarity());
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(args.memorybufferSize);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    final IndexWriter writer = new IndexWriter(dir, config);

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    final List segmentPaths = collection.getFileSegmentPaths();

    final int segmentCnt = segmentPaths.size();
    LOG.info(segmentCnt + " files found in " + collectionPath.toString());
    for (int i = 0; i < segmentCnt; i++) {
      executor.execute(new IndexerThread(writer, collection, (Path) segmentPaths.get(i)));
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

    int numIndexed = writer.maxDoc();

    try {
      writer.commit();
      if (args.optimize)
        writer.forceMerge(1);
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

    LOG.info("# Final Counter Values");
    LOG.info(String.format("indexed:     %,12d", counters.indexed.get()));
    LOG.info(String.format("empty:       %,12d", counters.empty.get()));
    LOG.info(String.format("unindexed:   %,12d", counters.unindexed.get()));
    LOG.info(String.format("unindexable: %,12d", counters.unindexable.get()));
    LOG.info(String.format("skipped:     %,12d", counters.errors.get()));
    LOG.info(String.format("errors:      %,12d", counters.errors.get()));

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info(String.format("Total %,d documents indexed in %s", numIndexed,
        DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));
  }

  public static void main(String[] args) throws Exception {
    IndexCollection.Args indexCollectionArgs = new IndexCollection.Args();
    CmdLineParser parser = new CmdLineParser(indexCollectionArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ IndexCollection.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexCollection(indexCollectionArgs).run();
  }
}