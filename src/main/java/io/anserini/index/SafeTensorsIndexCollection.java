package io.anserini.index;

import io.anserini.collection.SourceDocument;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.search.similarity.AccurateBM25Similarity;
import io.anserini.search.similarity.ImpactSimilarity;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public final class SafeTensorsIndexCollection extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsIndexCollection.class);

  // This is the default analyzer used, unless another stemming algorithm or language is specified.
  public static final Analyzer DEFAULT_ANALYZER = new WhitespaceAnalyzer();

  public static class Args extends AbstractIndexer.Args {
    @Option(name = "-append", usage = "Append documents.")
    public boolean append = false;

    @Option(name = "-generator", metaVar = "[class]",
        usage = "Document generator class in package 'io.anserini.index.generator'.")
    public String generatorClass = "HnswSafetensorsDenseVectorDocumentGenerator";

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

    @Option(name = "-keepStopwords",
        usage = "Boolean switch to keep stopwords.")
    public boolean keepStopwords = false;

    @Option(name = "-stopwords", metaVar = "[file]", forbids = "-keepStopwords",
        usage = "Path to file with stopwords.")
    public String stopwords = null;

    @Option(name = "-impact",
        usage = "Boolean switch to store impacts (no norms).")
    public boolean impact = false;

    @Option(name = "-bm25.accurate",
        usage = "Boolean switch to use AccurateBM25Similarity (computes accurate document lengths).")
    public boolean bm25Accurate = false;

    @Option(name = "-language", metaVar = "[language]",
        usage = "Analyzer language (ISO 3166 two-letter code).")
    public String language = "en";

    @Option(name = "-pretokenized",
        usage = "Index pre-tokenized collections without any additional stemming, stopword processing.")
    public boolean pretokenized = false;

    @Option(name = "-whitelist", metaVar = "[file]",
        usage = "File containing list of docids, one per line; only these docids will be indexed.")
    public String whitelist = null;

    public URI docidsPath;

    public URI vectorsPath;

    public URI docidToIdxPath;
  }

  private final Set<String> whitelistDocids;

  @SuppressWarnings("unchecked")
  public SafeTensorsIndexCollection(Args args) throws Exception {
    super(args);

    try {
      super.generatorClass = (Class<LuceneDocumentGenerator<? extends SourceDocument>>)
          Class.forName("io.anserini.index.generator." + args.generatorClass);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load generator class \"%s\".", args.generatorClass));
    }

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }

    final Directory dir = FSDirectory.open(Paths.get(args.index));
    final IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());

    if (args.bm25Accurate) {
      // Necessary during indexing as the norm used in BM25 is already determined at index time.
      config.setSimilarity(new AccurateBM25Similarity());
    } if (args.impact) {
      config.setSimilarity(new ImpactSimilarity());
    } else {
      config.setSimilarity(new BM25Similarity());
    }
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(args.memoryBuffer);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    super.writer = new IndexWriter(dir, config);

    LOG.info("SafeTensorsIndexCollection settings:");
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + Language: " + args.language);
    LOG.info(" + Keep stopwords? " + args.keepStopwords);
    LOG.info(" + Stopwords: " + args.stopwords);
    LOG.info(" + Store positions? " + args.storePositions);
    LOG.info(" + Store docvectors? " + args.storeDocvectors);
    LOG.info(" + Store document \"contents\" field? " + args.storeContents);
    LOG.info(" + Store document \"raw\" field? " + args.storeRaw);
    LOG.info(" + Additional fields to index: " + Arrays.toString(args.fields));
    LOG.info(" + Whitelist: " + args.whitelist);
    LOG.info(" + Pretokenized?: " + args.pretokenized);
  }

  private Analyzer getAnalyzer() {
    return DEFAULT_ANALYZER;
  }

  protected void processSegments(ThreadPoolExecutor executor, List<Path> segmentPaths) {
    segmentPaths.forEach((segmentPath) -> {
      try {
        // Each thread gets its own document generator, so we don't need to make any assumptions about its thread safety.
        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator<SourceDocument> generator = (LuceneDocumentGenerator<SourceDocument>)
                generatorClass.getDeclaredConstructor(Args.class).newInstance(this.args);

        executor.execute(new AbstractIndexer.IndexerThread(segmentPath, generator, whitelistDocids));
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new IllegalArgumentException(String.format("Unable to load LuceneDocumentGenerator \"%s\".", generatorClass.getSimpleName()));
      }
    });
  }

  public static void main(String[] args) throws Exception {
    Args indexArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (indexArgs.options) {
        System.err.printf("Options for %s:\n\n", SafeTensorsIndexCollection.class.getSimpleName());
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

    new SafeTensorsIndexCollection(indexArgs).run();
  }
}
