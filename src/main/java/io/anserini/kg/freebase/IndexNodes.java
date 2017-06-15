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
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Builds a triples lookup index from a Freebase dump in N-Triples RDF format. Each
 * {@link FreebaseNode} object, which represents a group of triples that share the same subject,
 * is treated as a Lucene "document". This class builds an index primarily for lookup by
 * <code>mid</code>.
 */
public class IndexNodes {
  private static final Logger LOG = LogManager.getLogger(IndexNodes.class);

  public static final class Args {
    // Required arguments

    @Option(name = "-input", metaVar = "[directory]", required = true, usage = "collection directory")
    public String input;

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public String index;
  }

  /**
   * Program arguments to hold parameters and properties.
   */
  private Args args;

  private final Path indexPath;
  private final Path collectionPath;

  /**
   * Constructor
   *
   * @param args program arguments
   * @throws Exception
   */
  public IndexNodes(Args args) throws Exception {

    // Copy arguments
    this.args = args;

    // Log parameters
    LOG.info("Collection path: " + args.input);
    LOG.info("Index path: " + args.index);

    // Initialize variables
    this.indexPath = Paths.get(args.index);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }

    collectionPath = Paths.get(args.input);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath)) {
      throw new IllegalArgumentException("Document file/directory " + collectionPath.toString() +
              " does not exist or is not readable, please check the path");
    }
  }

  private void run() throws IOException, InterruptedException {
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
    new Freebase(collectionPath).stream().map(new NodeLuceneDocumentGenerator())
        .forEach(doc -> {
          try {
            writer.addDocument(doc);
            int cur = cnt.incrementAndGet();
            if (cur % 1000000 == 0) {
              LOG.debug("Number of indexed entity document: {}", cnt);
            }
          } catch (IOException e) {
            LOG.error(e);
          }
        });

    LOG.info(cnt.get() + " nodes added.");
    try {
      writer.commit();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        LOG.error(e);
      }
    }

    int numIndexed = writer.maxDoc();
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total " + numIndexed + " documents indexed in " +
            DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }

  public static void main(String[] args) throws Exception {
    Args indexRDFCollectionArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexRDFCollectionArgs,
            ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example command: "+ IndexNodes.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexNodes(indexRDFCollectionArgs).run();
  }

  public static class NodeLuceneDocumentGenerator implements Function<FreebaseNode, Document> {
    public static final String FIELD_SUBJECT = "subject";

    // RDF object predicate types
    static final String VALUE_TYPE_URI = "URI";
    static final String VALUE_TYPE_STRING = "STRING";
    static final String VALUE_TYPE_TEXT = "TEXT";
    static final String VALUE_TYPE_OTHER = "OTHER";

    public Document apply(FreebaseNode src) {
      // Convert the triple doc to lucene doc
      Document doc = new Document();

      // Index subject as a StringField to allow searching
      Field subjectField = new StringField(FIELD_SUBJECT,
          cleanUri(src.getSubject()),
          Field.Store.YES);
      doc.add(subjectField);

      // Iterate over predicates and object values
      for (Map.Entry<String, List<String>> entry : src.getPredicateValues().entrySet()) {
        String predicate = cleanUri(entry.getKey());
        List<String> values = entry.getValue();

        for (String value : values) {
          String valueType = getObjectType(value);
          value = normalizeObjectValue(value);
          // Just add the predicate as a stored field, no index on it
          doc.add(new StoredField(predicate, value));
        }
      }

      src.clear();
      return doc;
    }

    /**
     * Removes '<', '>' if they exist, lower case
     * <p>
     * TODO - replace ':' with '_' because query parser doesn't like it
     *
     * @param uri
     * @return
     */
    public static String cleanUri(String uri) {
      if (uri.charAt(0) == '<')
        return uri.substring(1, uri.length() - 1).toLowerCase();
      else
        return uri;
    }

    /**
     * Figures out the type of the value of an object in a triple
     *
     * @param objectValue object value
     * @return uri, string, text, or other
     */
    public static String getObjectType(String objectValue) {
      // Determine the type of this N-Triples `value'.
      char first = objectValue.charAt(0);
      switch (first) {
        case '<':
          return VALUE_TYPE_URI;
        case '"':
          if (objectValue.charAt(objectValue.length() - 1) == '"')
            return VALUE_TYPE_STRING;
          else
            return VALUE_TYPE_TEXT;
        default:
          return VALUE_TYPE_OTHER;
      }
    }

    /**
     * Do nothing for strings
     */
    public static String normalizeStringValue(String value) {
      return value;
    }

    /**
     * Un-escape strings
     */
    public static String normalizeTextValue(String value) {
      return NTriplesUtil.unescapeString(value);
    }

    /**
     * Normalize Object object value
     *
     * @param objectValue
     * @return
     */
    public static String normalizeObjectValue(String objectValue) {
      // Normalize a `objectValue' depending on its type.
      String type = getObjectType(objectValue);
      if (type.equals(VALUE_TYPE_URI))
        return cleanUri(objectValue);
      else if (type.equals(VALUE_TYPE_STRING))
        return normalizeStringValue(objectValue);
      else if (type.equals(VALUE_TYPE_TEXT))
        return normalizeTextValue(objectValue);
      else
        return objectValue;
    }
  }
}
