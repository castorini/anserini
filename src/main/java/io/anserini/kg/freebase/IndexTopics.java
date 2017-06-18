package io.anserini.kg.freebase;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Builds a triples lookup index from a Freebase dump in N-Triples RDF format. Each
 * {@link FreebaseNode} object, which represents a group of triples that share the same subject,
 * is treated as a Lucene "document". This class builds an index primarily for lookup by
 * <code>mid</code>.
 */
public class IndexTopics {
  private static final Logger LOG = LogManager.getLogger(IndexTopics.class);

  public static final class Args {
    // Required arguments

    @Option(name = "-input", metaVar = "[directory]", required = true, usage = "collection directory")
    public String input;

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public String index;
  }

  public final class Counters {
    public AtomicLong indexedDocuments = new AtomicLong();
  }

  /**
   * Program arguments to hold parameters and properties.
   */
  private Args args;

  private final Path indexPath;
  private final Path collectionPath;
  private final Counters counters;

  /**
   * Constructor
   *
   * @param args program arguments
   * @throws Exception
   */
  public IndexTopics(Args args) throws Exception {

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

    this.counters = new Counters();
  }

  private void run() throws IOException, InterruptedException {
    final long start = System.nanoTime();
    LOG.info("Starting indexer...");

    final Directory dir = FSDirectory.open(indexPath);
    final SimpleAnalyzer analyzer = new SimpleAnalyzer();
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setCodec(new Lucene62Codec(Lucene50StoredFieldsFormat.Mode.BEST_SPEED));
    config.setUseCompoundFile(false);

    final IndexWriter writer = new IndexWriter(dir, config);
    index(writer, collectionPath);

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

    LOG.info("Indexed documents: " + counters.indexedDocuments.get());
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total " + numIndexed + " documents indexed in " +
            DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }

  /**
   * Run the indexing process.
   *
   * @param writer index writer
   * @param inputFile which file to index from the collection
   * @throws IOException on error
   */
  private void index(IndexWriter writer, Path inputFile) throws IOException {
    TopicLuceneDocumentGenerator transformer = new TopicLuceneDocumentGenerator();
    transformer.config(args);
    transformer.setCounters(counters);

    int cnt = 0;
    Freebase freebase = new Freebase(inputFile);
    for (FreebaseNode d : freebase) {
      Document doc = transformer.createDocument(d);

      if (doc != null) {
        writer.addDocument(doc);
        cnt++;
      }

      // Display progress
      if (cnt % 100000 == 0) {
        LOG.debug("Number of indexed entity document: {}", cnt);
      }

      d = null;
      doc = null;
    }

    LOG.info(inputFile.getFileName().toString() + ": " + cnt + " docs added.");
    counters.indexedDocuments.addAndGet(cnt);
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
      System.err.println("Example command: "+ IndexTopics.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexTopics(indexRDFCollectionArgs).run();
  }

  public static class TopicLuceneDocumentGenerator {
    /**
     * FreebaseTopicDocument has four fields:
     * topicMid - the MID of the topic
     * title - the object value of the (topicMid, http://rdf.freebase.com/key/wikipedia.en_title)
     * label - the object value of the (topicMid, http://www.w3.org/2000/01/rdf-schema#label)
     * name - the object value of the (topicMid, http://rdf.freebase.com/ns/type.object.name)
     * text - all the values separated by space of the (topicMid, http://rdf.freebase.com/key/wikipedia.en)
     */
    public static final String FIELD_TOPIC_MID = "topicMid";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_LABEL = "label";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TEXT = "text";

    /**
     * Predicates for which the literals should be stored
     */
    public static final String WIKI_EN_URI = "http://rdf.freebase.com/key/wikipedia.en";
    public static final String WIKI_EN_TILE_URI = WIKI_EN_URI + "_title";
    public static final String W3_LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";
    public static final String FB_OBJECT_NAME = "http://rdf.freebase.com/ns/type.object.name";

    /**
     * Simple value factory to parse literals using Sesame library.
     */
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    protected IndexTopics.Counters counters;
    protected IndexTopics.Args args;

    public void config(IndexTopics.Args args) {
      this.args = args;
    }

    public void setCounters(IndexTopics.Counters counters) {
      this.counters = counters;
    }

    public Document createDocument(FreebaseNode src) {
      // make a Topic from the FreebaseNode
      String topicMid = src.uri();
      String title = "";
      String label = "";
      String name = "";
      String text = "";
      Map<String, List<String>> predicateValues = src.getPredicateValues();
      for(Map.Entry<String, List<String>> entry: predicateValues.entrySet()) {
        String predicate = entry.getKey();
        List<String> objects = entry.getValue();
        for (String object : objects) {
          predicate = cleanUri(predicate);
          if (predicate.startsWith(WIKI_EN_URI)) {
            if (predicate.startsWith(WIKI_EN_TILE_URI)) {
              title = removeQuotes(object);
            } else {
              // concatenate other variants with a space
              text += removeQuotes(object) + " ";
            }
          } else if (predicate.startsWith(W3_LABEL_URI)) {
            Literal parsedLiteral = NTriplesUtil.parseLiteral(object, valueFactory);
            if (parsedLiteral.getLanguage().toString().equals("Optional[en]")) {
              label = parsedLiteral.stringValue();
            }
          } else if (predicate.startsWith(FB_OBJECT_NAME)) {
            Literal parsedLiteral = NTriplesUtil.parseLiteral(object, valueFactory);
            if (parsedLiteral.getLanguage().toString().equals("Optional[en]")) {
              name = parsedLiteral.stringValue();
            }
          }
        }
      }

      // Convert the triple doc to lucene doc
      Document doc = new Document();

      // Index subject as a StringField to allow searching
      Field topicMidField = new StringField(FIELD_TOPIC_MID, cleanUri(topicMid), Field.Store.YES);
      doc.add(topicMidField);

      Field titleField = new TextField(FIELD_TITLE, title, Field.Store.YES);
      doc.add(titleField);

      Field nameField = new TextField(FIELD_NAME, name, Field.Store.YES);
      doc.add(nameField);

      Field labelField = new TextField(FIELD_LABEL, label, Field.Store.YES);
      doc.add(labelField);

      Field textField = new TextField(FIELD_TEXT, text, Field.Store.YES);
      doc.add(textField);

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
     * Removes quotes from the literal value in object field
     */
    private String removeQuotes(String literal) {
      if (literal.charAt(0) == '\"' && literal.charAt(literal.length()-1) == '\"') {
        return literal.substring(1, literal.length() - 1);
      }
      return literal;
    }
  }
}
