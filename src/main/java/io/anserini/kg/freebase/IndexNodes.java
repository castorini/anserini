package io.anserini.kg.freebase;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Builds a triples lookup index from a Freebase dump in N-Triples RDF format. Each
 * {@link FreebaseNode} object, which represents a group of triples that share the same subject
 * ({@code mid}), is treated as a Lucene "document". This class builds an index for lookup based
 * on {@code mid}.
 */
public class IndexNodes {
  private static final Logger LOG = LogManager.getLogger(IndexNodes.class);

  public static final class Args {
    @Option(name = "-input", metaVar = "[file]", required = true, usage = "Freebase dump file")
    public Path input;

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public Path index;
  }

  public static final String FIELD_ID = "id";

  private final Path indexPath;
  private final Path inputPath;

  public IndexNodes(Path inputPath, Path indexPath) throws Exception {
    this.inputPath = inputPath;
    this.indexPath = indexPath;

    LOG.info("Input path: " + this.inputPath);
    LOG.info("Index path: " + this.indexPath);

    if (!Files.exists(inputPath) || !Files.isReadable(inputPath)) {
      throw new IllegalArgumentException("Input " + inputPath.toString() +
              " does not exist or is not readable.");
    }
  }

  public void run() throws IOException, InterruptedException {
    final long start = System.nanoTime();
    LOG.info("Starting indexer...");

    final Directory dir = FSDirectory.open(indexPath);
    final EnglishAnalyzer analyzer = new EnglishAnalyzer();
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setCodec(new Lucene62Codec(Lucene50StoredFieldsFormat.Mode.BEST_SPEED));
    config.setUseCompoundFile(false);

    final IndexWriter writer = new IndexWriter(dir, config);

    final AtomicInteger cnt = new AtomicInteger();
    new Freebase(inputPath).stream().map(new LuceneDocumentGenerator())
        .forEach(doc -> {
          try {
            writer.addDocument(doc);
            int cur = cnt.incrementAndGet();
            if (cur % 10000000 == 0) {
              LOG.info(cnt + " nodes added.");
            }
          } catch (IOException e) {
            LOG.error(e);
          }
        });

    LOG.info(cnt.get() + " nodes added.");
    int numIndexed = writer.maxDoc();

    try {
      writer.commit();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        LOG.error(e);
      }
    }

    long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total " + numIndexed + " documents indexed in " +
            DurationFormatUtils.formatDuration(duration, "HH:mm:ss"));
  }

  public static void main(String[] args) throws Exception {
    Args indexArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ IndexNodes.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexNodes(indexArgs.input, indexArgs.index).run();
  }

  private static class LuceneDocumentGenerator implements Function<FreebaseNode, Document> {
    public Document apply(FreebaseNode src) {
      Document doc = new Document();
      doc.add(new StringField(FIELD_ID, FreebaseNode.cleanUri(src.uri()), Field.Store.YES));

      // Iterate over predicates and object values
      for (Map.Entry<String, List<String>> entry : src.getPredicateValues().entrySet()) {
        final String predicate = FreebaseNode.cleanUri(entry.getKey());
        entry.getValue().forEach(value ->
            doc.add(new StoredField(predicate, FreebaseNode.normalizeObjectValue(value))));
      }
      return doc;
    }
  }
}
