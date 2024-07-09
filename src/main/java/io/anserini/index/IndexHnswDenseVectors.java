package io.anserini.index;

import io.anserini.collection.SourceDocument;
import io.anserini.index.generator.HnswJsonWithSafeTensorsDenseVectorDocumentGenerator;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.search.similarity.AccurateBM25Similarity;
import io.anserini.search.similarity.ImpactSimilarity;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.codecs.lucene99.Lucene99Codec;
import org.apache.lucene.codecs.lucene99.Lucene99HnswScalarQuantizedVectorsFormat;
import org.apache.lucene.codecs.lucene99.Lucene99HnswVectorsFormat;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.TieredMergePolicy;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public final class IndexHnswDenseVectors extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(IndexHnswDenseVectors.class);

  // This is the default analyzer used, unless another stemming algorithm or language is specified.
  public static final Analyzer DEFAULT_ANALYZER = new WhitespaceAnalyzer();

  public static class Args extends AbstractIndexer.Args {
    @Option(name = "-append", usage = "Append documents.")
    public boolean append = false;

    @Option(name = "-generator", metaVar = "[class]",
        usage = "Document generator class in package 'io.anserini.index.generator'.")
    public String generatorClass = "HnswJsonWithSafeTensorsDenseVectorDocumentGenerator";

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

    @Option(name = "-outputDirectory", usage = "Directory containing aligned SafeTensors files.")
    public String outputDirectory;

    public String currentJsonlFile;
  }

  private final Set<String> whitelistDocids;

  @SuppressWarnings("unchecked")
  public IndexHnswDenseVectors(Args args) throws Exception {
    super(args);

    try {
        super.generatorClass = (Class<LuceneDocumentGenerator<? extends SourceDocument>>)
            Class.forName("io.anserini.index.generator." + args.generatorClass);
    } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(String.format("Unable to load generator class \"%s\". Class not found.", args.generatorClass));
    } catch (Exception e) {
        throw new IllegalArgumentException(String.format("Unable to load generator class \"%s\". Error: %s", args.generatorClass, e.getMessage()));
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
    config.setRAMPerThreadHardLimitMB(args.maxThreadMemoryBeforeFlush);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    if (args.noMerge) {
      config.setMergePolicy(NoMergePolicy.INSTANCE);
    } else {
      TieredMergePolicy mergePolicy = new TieredMergePolicy();
      if (args.optimize) {
        // If we're going to merge down into a single segment at the end, skip intermediate merges,
        // since they are a waste of time.
        mergePolicy.setMaxMergeAtOnce(256);
        mergePolicy.setSegmentsPerTier(256);
      } else {
        mergePolicy.setFloorSegmentMB(1024);
        mergePolicy.setMaxMergedSegmentMB(args.maxMergedSegmentSize);
        mergePolicy.setSegmentsPerTier(args.segmentsPerTier);
        mergePolicy.setMaxMergeAtOnce(args.maxMergeAtOnce);
      }
      config.setMergePolicy(mergePolicy);
    }

    this.writer = new IndexWriter(dir, config);

    LOG.info("HnswIndexer settings:");
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + M: " + args.M);
    LOG.info(" + efC: " + args.efC);
    LOG.info(" + Store document vectors? " + args.storeVectors);
    LOG.info(" + Int8 quantization? " + args.quantizeInt8);
    LOG.info(" + Codec: " + this.writer.getConfig().getCodec());
    LOG.info(" + MemoryBuffer: " + args.memoryBuffer);
    LOG.info(" + MaxThreadMemoryBeforeFlush: " + args.maxThreadMemoryBeforeFlush);

    if (args.noMerge) {
      LOG.info(" + MergePolicy: NoMerge");
    } else if (args.optimize) {
      LOG.info(" + MergePolicy: TieredMergePolicy (force merge into a single index segment)");
    } else {
      LOG.info(" + MergePolicy: TieredMergePolicy");
      LOG.info(" + MaxMergedSegmentSize: " + args.maxMergedSegmentSize);
      LOG.info(" + SegmentsPerTier: " + args.segmentsPerTier);
      LOG.info(" + MaxMergeAtOnce: " + args.maxMergeAtOnce);
    }
  }

  // Solution provided by Solr, see https://www.mail-archive.com/java-user@lucene.apache.org/msg52149.html
  // This class exists because Lucene95HnswVectorsFormat's getMaxDimensions method is final and we
  // need to workaround that constraint to allow more than the default number of dimensions.
  private static final class DelegatingKnnVectorsFormat extends KnnVectorsFormat {
    private final KnnVectorsFormat delegate;
    private final int maxDimensions;

    public DelegatingKnnVectorsFormat(KnnVectorsFormat delegate, int maxDimensions) {
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

  protected void processSegments(ThreadPoolExecutor executor, List<Path> segmentPaths) {
    segmentPaths.forEach((segmentPath) -> {
      try {
        // Update the current JSONL file
        String currentJsonlFile = segmentPath.getFileName().toString();

        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator<SourceDocument> generator = (LuceneDocumentGenerator<SourceDocument>)
                generatorClass.getDeclaredConstructor(Args.class).newInstance(this.args);

        ((HnswJsonWithSafeTensorsDenseVectorDocumentGenerator) generator).setCurrentJsonlFile(currentJsonlFile);

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
        System.err.printf("Options for %s:\n\n", IndexHnswDenseVectors.class.getSimpleName());
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

    new IndexHnswDenseVectors(indexArgs).run();
  }
}
