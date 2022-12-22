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

import io.anserini.analysis.HuggingFaceTokenizerAnalyzer;
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.FileSegment;
import io.anserini.collection.SourceDocument;
import io.anserini.index.generator.EmptyDocumentException;
import io.anserini.index.generator.InvalidDocumentException;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.index.generator.SkippedDocumentException;
import io.anserini.search.similarity.AccurateBM25Similarity;
import io.anserini.search.similarity.ImpactSimilarity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.te.TeluguAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer;
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
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class IndexCollection {
  private static final Logger LOG = LogManager.getLogger(IndexCollection.class);

  // This is the default analyzer used, unless another stemming algorithm or language is specified.
  public static final Analyzer DEFAULT_ANALYZER = DefaultEnglishAnalyzer.newDefaultInstance();

  public static class Args {

    private static final int TIMEOUT = 600 * 1000;

    // required arguments

    @Option(name = "-input", metaVar = "[path]", required = true,
        usage = "Location of input collection.")
    public String input;

    @Option(name = "-threads", metaVar = "[num]", required = true,
        usage = "Number of indexing threads.")
    public int threads;

    @Option(name = "-collection", metaVar = "[class]", required = true,
        usage = "Collection class in package 'io.anserini.collection'.")
    public String collectionClass;

    @Option(name = "-generator", metaVar = "[class]",
        usage = "Document generator class in package 'io.anserini.index.generator'.")
    public String generatorClass = "DefaultLuceneDocumentGenerator";

    // optional general arguments

    @Option(name = "-verbose", forbids = {"-quiet"},
        usage = "Enables verbose logging for each indexing thread; can be noisy if collection has many small file segments.")
    public boolean verbose = false;

    @Option(name = "-quiet", forbids = {"-verbose"},
        usage = "Turns off all logging.")
    public boolean quiet = false;

    // optional arguments

    @Option(name = "-index", metaVar = "[path]", usage = "Index path.")
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

    @Option(name = "-keepStopwords",
        usage = "Boolean switch to keep stopwords.")
    public boolean keepStopwords = false;

    @Option(name = "-stopwords", metaVar = "[file]", forbids = "-keepStopwords",
        usage = "Path to file with stopwords.")
    public String stopwords = null;

    @Option(name = "-stemmer", metaVar = "[stemmer]",
        usage = "Stemmer: one of the following {porter, krovetz, none}; defaults to 'porter'.")
    public String stemmer = "porter";

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

    @Option(name = "-impact",
        usage = "Boolean switch to store impacts (no norms).")
    public boolean impact = false;

    @Option(name = "-bm25.accurate",
        usage = "Boolean switch to use AccurateBM25Similarity (computes accurate document lengths).")
    public boolean bm25Accurate = false;

    @Option(name = "-language", metaVar = "[language]",
        usage = "Analyzer language (ISO 3166 two-letter code).")
    public String language= "en";

    @Option(name = "-pretokenized",
        usage = "index pre-tokenized collections without any additional stemming, stopword processing")
    public boolean pretokenized = false;

    @Option(name = "-analyzeWithHuggingFaceTokenizer",
        usage = "index a collection by tokenizing text with pretrained huggingface tokenizers")
    public String analyzeWithHuggingFaceTokenizer = null;

    // Tweet options

    @Option(name = "-tweet.keepRetweets",
        usage = "Boolean switch to index retweets.")
    public boolean tweetKeepRetweets = false;

    @Option(name = "-tweet.keepUrls",
        usage = "Boolean switch to keep URLs.")
    public boolean tweetKeepUrls = false;

    @Option(name = "-tweet.stemming",
        usage = "Boolean switch to apply Porter stemming while indexing tweets.")
    public boolean tweetStemming = false;

    @Option(name = "-tweet.maxId", metaVar = "[id]",
        usage = "Max tweet id to index (long); all tweets with larger tweet ids will be skipped.")
    public long tweetMaxId = Long.MAX_VALUE;

    @Option(name = "-tweet.deletedIdsFile", metaVar = "[file]",
        usage = "File that contains deleted tweet ids (longs), one per line; these tweets will be skipped during indexing.")
    public String tweetDeletedIdsFile = "";

    // Sharding options

    @Option(name = "-shard.count", metaVar = "[n]",
        usage = "Number of shards to partition the document collection into.")
    public int shardCount = -1;

    @Option(name = "-shard.current", metaVar = "[n]",
        usage = "The current shard number to generate (indexed from 0).")
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
  private final Class collectionClass;
  private final Class generatorClass;
  private final DocumentCollection collection;
  private final Counters counters;
  private Path indexPath;

  @SuppressWarnings("unchecked")
  public IndexCollection(Args args) throws Exception {
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
    LOG.info("Language: " + args.language);
    LOG.info("Stemmer: " + args.stemmer);
    LOG.info("Keep stopwords? " + args.keepStopwords);
    LOG.info("Stopwords: " + args.stopwords);
    LOG.info("Store positions? " + args.storePositions);
    LOG.info("Store docvectors? " + args.storeDocvectors);
    LOG.info("Store document \"contents\" field? " + args.storeContents);
    LOG.info("Store document \"raw\" field? " + args.storeRaw);
    LOG.info("Additional fields to index: " + Arrays.toString(args.fields));
    LOG.info("Optimize (merge segments)? " + args.optimize);
    LOG.info("Whitelist: " + args.whitelist);
    LOG.info("Pretokenized?: " + args.pretokenized);
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
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath.toString() + " does not exist or is not readable, please check the path");
    }

    this.generatorClass = Class.forName("io.anserini.index.generator." + args.generatorClass);
    this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);

    // Initialize the collection.
    collection = (DocumentCollection) this.collectionClass.getConstructor(Path.class).newInstance(collectionPath);

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

    // Used for LocalIndexThread
    if (indexPath != null) {
      final Directory dir = FSDirectory.open(indexPath);
      final CJKAnalyzer chineseAnalyzer = new CJKAnalyzer();
      final ArabicAnalyzer arabicAnalyzer = new ArabicAnalyzer();
      final BengaliAnalyzer bengaliAnalyzer = new BengaliAnalyzer();
      final DanishAnalyzer danishAnalyzer = new DanishAnalyzer();
      final DutchAnalyzer dutchAnalyzer = new DutchAnalyzer();
      final PersianAnalyzer farsiAnalyzer = new PersianAnalyzer();
      final FinnishAnalyzer finnishAnalyzer = new FinnishAnalyzer();
      final FrenchAnalyzer frenchAnalyzer = new FrenchAnalyzer();
      final GermanAnalyzer germanAnalyzer = new GermanAnalyzer();
      final HindiAnalyzer hindiAnalyzer = new HindiAnalyzer();
      final HungarianAnalyzer hungarianAnalyzer = new HungarianAnalyzer();
      final IndonesianAnalyzer indonesianAnalyzer = new IndonesianAnalyzer();
      final ItalianAnalyzer italianAnalyzer = new ItalianAnalyzer();
      final JapaneseAnalyzer japaneseAnalyzer = new JapaneseAnalyzer();
      final MorfologikAnalyzer polishAnalyzer = new MorfologikAnalyzer();
      final NorwegianAnalyzer norwegianAnalyzer = new NorwegianAnalyzer();
      final PortugueseAnalyzer portugueseAnalyzer = new PortugueseAnalyzer();
      final RussianAnalyzer russianAnalyzer = new RussianAnalyzer();
      final SpanishAnalyzer spanishAnalyzer = new SpanishAnalyzer();
      final SwedishAnalyzer swedishAnalyzer = new SwedishAnalyzer();
      final TeluguAnalyzer teluguAnalyzer = new TeluguAnalyzer();
      final ThaiAnalyzer thaiAnalyzer = new ThaiAnalyzer();
      final TurkishAnalyzer turkishAnalyzer = new TurkishAnalyzer();
      final UkrainianMorfologikAnalyzer ukrainianAnalyzer = new UkrainianMorfologikAnalyzer();
      final WhitespaceAnalyzer whitespaceAnalyzer = new WhitespaceAnalyzer();

      final DefaultEnglishAnalyzer analyzer = DefaultEnglishAnalyzer.fromArguments(
              args.stemmer, args.keepStopwords, args.stopwords);
      final TweetAnalyzer tweetAnalyzer = new TweetAnalyzer(args.tweetStemming);

      final IndexWriterConfig config;
      if (args.collectionClass.equals("TweetCollection")) {
        config = new IndexWriterConfig(tweetAnalyzer);
      } else if (args.analyzeWithHuggingFaceTokenizer!= null) {
        config = new IndexWriterConfig(new HuggingFaceTokenizerAnalyzer(args.analyzeWithHuggingFaceTokenizer));
      } else if (args.language.equals("ar")) {
        config = new IndexWriterConfig(arabicAnalyzer);
      } else if (args.language.equals("bn")) {
        config = new IndexWriterConfig(bengaliAnalyzer);
      } else if (args.language.equals("da")) {
        config = new IndexWriterConfig(danishAnalyzer);
      } else if (args.language.equals("de")) {
        config = new IndexWriterConfig(germanAnalyzer);
      } else if (args.language.equals("es")) {
        config = new IndexWriterConfig(spanishAnalyzer);
      } else if (args.language.equals("fa")) {
        config = new IndexWriterConfig(farsiAnalyzer);
      } else if (args.language.equals("fi")) {
        config = new IndexWriterConfig(finnishAnalyzer);
      } else if (args.language.equals("fr")) {
        config = new IndexWriterConfig(frenchAnalyzer);
      } else if (args.language.equals("hi")) {
        config = new IndexWriterConfig(hindiAnalyzer);
      } else if (args.language.equals("hu")) {
        config = new IndexWriterConfig(hungarianAnalyzer);
      } else if (args.language.equals("id")) {
        config = new IndexWriterConfig(indonesianAnalyzer);
      } else if (args.language.equals("it")) {
        config = new IndexWriterConfig(italianAnalyzer);
      } else if (args.language.equals("ja")) {
        config = new IndexWriterConfig(japaneseAnalyzer);
      } else if (args.language.equals("nl")) {
        config = new IndexWriterConfig(dutchAnalyzer);
      } else if (args.language.equals("no")) {
        config = new IndexWriterConfig(norwegianAnalyzer);
      } else if (args.language.equals("pl")) {
        config = new IndexWriterConfig(polishAnalyzer);
      } else if (args.language.equals("pt")) {
        config = new IndexWriterConfig(portugueseAnalyzer);
      } else if (args.language.equals("ru")) {
        config = new IndexWriterConfig(russianAnalyzer);
      } else if (args.language.equals("sv")) {
        config = new IndexWriterConfig(swedishAnalyzer);
      } else if (args.language.equals("te")) {
        config = new IndexWriterConfig(teluguAnalyzer);
      } else if (args.language.equals("th")) {
        config = new IndexWriterConfig(thaiAnalyzer);
      } else if (args.language.equals("tr")) {
        config = new IndexWriterConfig(turkishAnalyzer);
      } else if (args.language.equals("uk")) {
        config = new IndexWriterConfig(ukrainianAnalyzer);
      } else if (args.language.equals("zh") || args.language.equals("ko")) {
        config = new IndexWriterConfig(chineseAnalyzer);
      } else if (args.language.equals("sw") || args.language.equals("te")) {
        // For Mr.TyDi: sw and te do not have custom Lucene analyzers, so just use whitespace analyzer.
        config = new IndexWriterConfig(whitespaceAnalyzer);
      } else if (args.pretokenized) {
        config = new IndexWriterConfig(whitespaceAnalyzer);
      } else {
        config = new IndexWriterConfig(analyzer);
      }
      if (args.bm25Accurate) {
        config.setSimilarity(new AccurateBM25Similarity()); // necessary during indexing as the norm used in BM25 is already determined at index time.
      } if (args.impact ) {
        config.setSimilarity(new ImpactSimilarity());
      } else {
        config.setSimilarity(new BM25Similarity());
      }
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(args.memorybufferSize);
      config.setUseCompoundFile(false);
      config.setMergeScheduler(new ConcurrentMergeScheduler());

      writer = new IndexWriter(dir, config);
    }

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    LOG.info("Thread pool with " + numThreads + " threads initialized.");

    LOG.info("Initializing collection in " + collectionPath.toString());

    List<?> segmentPaths = collection.getSegmentPaths();
    // when we want sharding to be done
    if (args.shardCount > 1) {
      segmentPaths = collection.getSegmentPaths(args.shardCount, args.shardCurrent);
    }
    final int segmentCnt = segmentPaths.size();

    LOG.info(String.format("%,d %s found", segmentCnt, (segmentCnt == 1 ? "file" : "files" )));
    LOG.info("Starting to index...");

    for (int i = 0; i < segmentCnt; i++) {
      executor.execute(new LocalIndexerThread(writer, collection, (Path) segmentPaths.get(i)));
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
      System.err.println("Example: " + IndexCollection.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexCollection(indexCollectionArgs).run();
  }
}
