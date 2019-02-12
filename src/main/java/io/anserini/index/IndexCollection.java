/**
 * Anserini: A toolkit for reproducible information retrieval research built on Lucene
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

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.hash.Hashing;
import io.anserini.analysis.EnglishStemmingAnalyzer;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.collection.*;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.kohsuke.args4j.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    @Option(name = "-threads", metaVar = "[Number]", required = true, usage = "Number of Threads")
    public int threads;

    @Option(name = "-collection", required = true, usage = "collection class in io.anserini.collection")
    public String collectionClass;

    @Option(name = "-generator", required = true, usage = "document generator in io.anserini.index.generator")
    public String generatorClass;

    // optional arguments

    @Option(name = "-index", metaVar = "[Path]", forbids = {"-solr"}, usage = "index path")
    public String index;

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

    @Option(name = "-stemmer", usage = "Stemmer: one of the following porter,krovetz,none. Default porter")
    public String stemmer = "porter";

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

    @Option(name = "-solr", forbids = {"-index"}, usage = "boolean switch to determine if we should index into Solr")
    public boolean solr = false;

    @Option(name = "-solr.batch", usage = "the batch size for submitting documents to Solr")
    public int solrBatch = 1000;

    @Option(name = "-solr.cloud", usage = "boolean switch to determine if we're running in SolrCloud mode")
    public boolean solrCloud = false;

    @Option(name = "-solr.commitWithin", usage = "the number of seconds to commitWithin")
    public int solrCommitWithin = 60;

    @Option(name = "-solr.index", usage = "the name of the index")
    public String solrIndex = null;

    @Option(name = "-solr.url", usage = "the URL of Solr (standalone) or ZooKeeper (cloud, possibly comma-separated) servers")
    public String solrUrl = null;

    @Option(name = "-solr.zkChroot", usage = "the ZooKeeper chroot if using SolrCloud")
    public String solrZkChroot = "/";

    /**
     * The number of {@link SolrClient#add(Collection) add} calls to buffer before draining pending update queue.
     */
    @Option(name = "-solr.clientQueueSize", usage = "the number of update requests to buffer before draining")
    public int solrClientQueueSize = 16;

    /**
     * The number threads used to drain the ConcurrentUpdateSolrClient queue
     */
    @Option(name = "-solr.clientThreads", metaVar = "[NUMBER]", usage = "the number of threads used to drain the ConcurrentUpdateSolrClient queue")
    public int solrClientThreads = 1;

    /**
     * The number of SolrClients to keep in the object pool.
     */
    @Option(name = "-solr.pool.size", metaVar = "[NUMBER]", usage = "the number of clients to keep in the pool")
    public int solrPoolSize = 16;

    @Option(name = "-shard.count", usage = "the number of shards for the index")
    public int shardCount = -1;

    @Option(name = "-shard.current", usage = "the current shard number to produce (indexed from 0)")
    public int shardCurrent = -1;
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
     * by {@link Segment} is {@code null} or the {@link LuceneDocumentGenerator}
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

  private final class LocalIndexerThread extends Thread {
    final private Path inputFile;
    final private IndexWriter writer;
    final private DocumentCollection collection;

    private LocalIndexerThread(IndexWriter writer, DocumentCollection collection, Path inputFile) throws IOException {
      this.writer = writer;
      this.collection = collection;
      this.inputFile = inputFile;
      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      try {
        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator generator =
            (LuceneDocumentGenerator) generatorClass
                .getDeclaredConstructor(Args.class, Counters.class)
                .newInstance(args, counters);

        int cnt = 0;

        @SuppressWarnings("unchecked")
        BaseFileSegment<SourceDocument> iter =
            (BaseFileSegment) ((SegmentProvider) collection).createFileSegment(inputFile);

        while (iter.hasNext()) {
          SourceDocument d;
          try {
            d = iter.next();
          } catch (RuntimeException e) {
            counters.skipped.incrementAndGet();
            continue;
          }

          if (!d.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          // Used for indexing distinct shardCount of a collection
          if (args.shardCount > 1) {
            int hash = Hashing.sha1().hashString(d.id(), Charsets.UTF_8).asInt() % args.shardCount;
            if (hash != args.shardCurrent) {
              counters.skipped.incrementAndGet();
              continue;
            }
          }

          // Yes, we know what we're doing here.
          @SuppressWarnings("unchecked")
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

        if (iter.getNextRecordStatus() == BaseFileSegment.Status.ERROR) {
          counters.errors.incrementAndGet();
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

  private final class SolrIndexerThread implements Runnable {

    private final Path input;
    private final DocumentCollection collection;
    private final List<SolrInputDocument> buffer = new ArrayList(args.solrBatch);

    private SolrIndexerThread(DocumentCollection collection, Path input) {
      this.input = input;
      this.collection = collection;
    }

    @Override
    public void run() {
      try {

        LuceneDocumentGenerator generator = (LuceneDocumentGenerator) generatorClass.getDeclaredConstructor(Args.class, Counters.class).newInstance(args, counters);
        BaseFileSegment<SourceDocument> iter = (BaseFileSegment) ((SegmentProvider) collection).createFileSegment(input);

        int cnt = 0;
        while (iter.hasNext()) {
          SourceDocument sourceDocument;
          try {
            sourceDocument = iter.next();
          } catch (RuntimeException e) {
            counters.skipped.incrementAndGet();
            continue;
          }

          if (!sourceDocument.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          // Used for indexing distinct shardCount of a collection
          if (args.shardCount > 1) {
            int hash = Hashing.sha1().hashString(sourceDocument.id(), Charsets.UTF_8).asInt() % args.shardCount;
            if (hash != args.shardCurrent) {
              counters.skipped.incrementAndGet();
              continue;
            }
          }

          Document document = generator.createDocument(sourceDocument);
          if (document == null) {
            counters.unindexed.incrementAndGet();
            continue;
          }
          if (whitelistDocids != null && !whitelistDocids.contains(sourceDocument.id())) {
            counters.skipped.incrementAndGet();
            continue;
          }

          SolrInputDocument solrDocument = new SolrInputDocument();

          // Copy all Lucene Document fields to Solr document
          for (IndexableField field : document.getFields()) {
            if (field.stringValue() != null) { // For some reason, id is multi-valued with null as one of the values
              solrDocument.addField(field.name(), field.stringValue());
            }
          }

          buffer.add(solrDocument);
          if (buffer.size() == args.solrBatch) {
            flush();
          }

          cnt++;
        }

        // If we have docs in the buffer, flush them.
        if (!buffer.isEmpty()) {
          flush();
        }

        if (iter.getNextRecordStatus() == BaseFileSegment.Status.ERROR) {
          counters.errors.incrementAndGet();
        }

        iter.close();
        LOG.info(input.getParent().getFileName().toString() + File.separator + input.getFileName().toString() + ": " + cnt + " docs added.");
        counters.indexed.addAndGet(cnt);
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      }

    }

    private void flush() {
      if (!buffer.isEmpty()) {
        try {
          SolrClient solrClient = solrPool.borrowObject();
          solrClient.add(args.solrIndex, buffer, args.solrCommitWithin * 1000);
          solrPool.returnObject(solrClient);
          buffer.clear();
        } catch (Exception e) {
          LOG.error("Error flushing documents to Solr", e);
        }
      }
    }

  }

  private final IndexCollection.Args args;
  private final Path collectionPath;
  private final Set whitelistDocids;
  private final Class collectionClass;
  private final Class generatorClass;
  private final DocumentCollection collection;
  private final Counters counters;
  private Path indexPath;
  private ObjectPool<SolrClient> solrPool;

  public IndexCollection(IndexCollection.Args args) throws Exception {
    this.args = args;

    LOG.info("DocumentCollection path: " + args.input);
    LOG.info("Index path: " + args.index);
    LOG.info("CollectionClass: " + args.collectionClass);
    LOG.info("Generator: " + args.generatorClass);
    LOG.info("Threads: " + args.threads);
    LOG.info("Stemmer: " + args.stemmer);
    LOG.info("Keep stopwords? " + args.keepStopwords);
    LOG.info("Store positions? " + args.storePositions);
    LOG.info("Store docvectors? " + args.storeDocvectors);
    LOG.info("Store transformed docs? " + args.storeTransformedDocs);
    LOG.info("Store raw docs? " + args.storeRawDocs);
    LOG.info("Optimize (merge segments)? " + args.optimize);
    LOG.info("Whitelist: " + args.whitelist);
    LOG.info("Solr? " + args.solr);
    if (args.solr) {
      LOG.info("Solr batch size: " + args.solrBatch);
      LOG.info("SolrCloud? " + args.solrCloud + " (zkChroot = " + args.solrZkChroot + ")");
      LOG.info("Solr commitWithin: " + args.solrCommitWithin);
      LOG.info("Solr index: " + args.solrIndex);
      LOG.info("Solr URL: " + args.solrUrl);
      LOG.info("SolrClient thread count: " + args.solrClientThreads);
      LOG.info("SolrClient queue size: " + args.solrClientQueueSize);
      LOG.info("SolrClient pool size: " + args.solrPoolSize);
    }

    if (args.index == null && !args.solr) {
      throw new IllegalArgumentException("Must specify one of -index or -solr");
    }

    if (args.index != null) {
      this.indexPath = Paths.get(args.index);
      if (!Files.exists(this.indexPath)) {
        Files.createDirectories(this.indexPath);
      }
    }

    collectionPath = Paths.get(args.input);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath.toString() + " does not exist or is not readable, please check the path");
    }

    this.generatorClass = Class.forName("io.anserini.index.generator." + args.generatorClass);
    this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);

    collection = (DocumentCollection) this.collectionClass.newInstance();
    collection.setCollectionPath(collectionPath);

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }

    if (args.solr) {
      GenericObjectPoolConfig<SolrClient> config = new GenericObjectPoolConfig<>();
      config.setMaxTotal(args.solrPoolSize);
      config.setMinIdle(args.solrPoolSize); // To guard against premature discarding of solrClients
      this.solrPool = new GenericObjectPool(new SolrClientFactory(), config);
    }

    this.counters = new Counters();
  }

  private class SolrClientFactory extends BasePooledObjectFactory<SolrClient> {

    @Override
    public SolrClient create() {

      if (args.solrCloud) {
        List<String> urls = Splitter.on(',').splitToList(args.solrUrl);
        return new CloudSolrClient.Builder(urls, Optional.of(args.solrZkChroot)).build();
      }

      ConcurrentUpdateSolrClient.Builder builder = new ConcurrentUpdateSolrClient.Builder(args.solrUrl)
          .withQueueSize(args.solrClientQueueSize)
          .withThreadCount(args.solrClientThreads);

      return new ExceptionHandlingSolrClient(builder);
    }

    @Override
    public PooledObject<SolrClient> wrap(SolrClient solrClient) {
      return new DefaultPooledObject(solrClient);
    }

    @Override
    public void destroyObject(PooledObject<SolrClient> pooled) throws Exception {
      pooled.getObject().close();
    }

  }

  private class ExceptionHandlingSolrClient extends ConcurrentUpdateSolrClient {
    ExceptionHandlingSolrClient(ConcurrentUpdateSolrClient.Builder builder) {
      super(builder);
    }

    @Override
    public void handleError(Throwable ex) {
      LOG.warn("Solr: Exception delivering documents", ex);
    }
  }

  public void run() throws IOException {
    final long start = System.nanoTime();
    LOG.info("Starting indexer...");

    int numThreads = args.threads;

    IndexWriter writer = null;

    // Used for LocalIndexThread
    if (indexPath != null) {

      final Directory dir = FSDirectory.open(indexPath);
      final EnglishStemmingAnalyzer analyzer = args.keepStopwords ?
          new EnglishStemmingAnalyzer(args.stemmer, CharArraySet.EMPTY_SET) : new EnglishStemmingAnalyzer(args.stemmer);
      final TweetAnalyzer tweetAnalyzer = new TweetAnalyzer(args.tweetStemming);
      final IndexWriterConfig config = args.collectionClass.equals("TweetCollection") ? new IndexWriterConfig(tweetAnalyzer) : new IndexWriterConfig(analyzer);
      config.setSimilarity(new BM25Similarity());
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(args.memorybufferSize);
      config.setUseCompoundFile(false);
      config.setMergeScheduler(new ConcurrentMergeScheduler());

      writer = new IndexWriter(dir, config);
    }

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    final List segmentPaths = ((SegmentProvider) collection).getFileSegmentPaths();

    final int segmentCnt = segmentPaths.size();
    LOG.info(segmentCnt + " files found in " + collectionPath.toString());
    for (int i = 0; i < segmentCnt; i++) {
      if (args.solr) {
        executor.execute(new SolrIndexerThread(collection, (Path) segmentPaths.get(i)));
      } else {
        executor.execute(new LocalIndexerThread(writer, collection, (Path) segmentPaths.get(i)));
      }
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

    long numIndexed;

    if (args.solr) {
      numIndexed = counters.indexed.get();
    } else {
      numIndexed = writer.maxDoc();
    }

    // Do a final commit
    if (args.solr) {
      try {
        SolrClient client = solrPool.borrowObject();
        client.commit(args.solrIndex);
        // Needed for orderly shutdown so the SolrClient executor does not delay main thread exit
        solrPool.returnObject(client);
        solrPool.close();
      } catch (Exception e) {
        LOG.error("Exception during final Solr commit: ", e);
      }
    }

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

    LOG.info("# Final Counter Values");
    LOG.info(String.format("indexed:     %,12d", counters.indexed.get()));
    LOG.info(String.format("empty:       %,12d", counters.empty.get()));
    LOG.info(String.format("unindexed:   %,12d", counters.unindexed.get()));
    LOG.info(String.format("unindexable: %,12d", counters.unindexable.get()));
    LOG.info(String.format("skipped:     %,12d", counters.skipped.get()));
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
      System.err.println("Example: " + IndexCollection.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexCollection(indexCollectionArgs).run();
  }
}