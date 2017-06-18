package io.anserini.kg.freebase;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.CheckHits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Generate training data (positive examples) of a particular
 * relationship from an indexed RDF dataset, e.g., Freebase.
 */
public class TrainingDataGenerator {

  private static final Logger LOG = LogManager.getLogger(TrainingDataGenerator.class);
  private final Logger TRAINING_DATA_OUTPUT_FILE_EXAMPLES;

  private static final String[] supportedProperties = new String[] {
          "birthdate"
  };

  private static final String supportedPropertiesStr = String.join(", ", supportedProperties);

  // Field names of the predicates in the lucene documents
  static final String FIELD_BIRTHDATE = "http://rdf.freebase.com/ns/people.person.date_of_birth";
  static final String FIELD_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";


  /**
   * The arguments that this program accepts
   */
  public static class Args {

    // Required arguments
    @Option(name = "-index", metaVar = "[Knoweldge base Index path]", required = true, usage = "Directory contains index files for the Knowledge base")
    String index;

    @Option(name = "-output", metaVar = "[Output file path]", required = true, usage = "Output file to write training data to")
    String output;

    @Option(name = "-property", metaVar = "[Property name]", required = true, usage = "The property to generate training data for.")
    String property;
  }

  /**
   * Args instance
   */
  private Args args;

  /**
   * The directory in which the RDF dataset index is stored
   */
  private Directory kbIndexDirectory;

  /**
   * Indexed knowledge base/RDF dataset to retrieve
   * the training examples from
   */
  private IndexReader kbIndexReader;

  /**
   * Index searcher to search in the index
   */
  private IndexSearcher kbIndexSearcher;

  /**
   * Index analyzer to search
   */
  private Analyzer kbIndexAnalyzer;

  /**
   * Simple value factory to parse literals using Sesame library.
   */
  private ValueFactory valueFactory;

  /**
   * How to separate records in the training data files
   */
  private static final String TRAINING_DATA_SEPARATOR = "\t";

  /**
   * Dynamically creates a logger configuration with an appender that writes to a file.
   * This logger is used to write training data.
   *
   * @param loggerName     the name of the logger to create
   * @param outputFilePath the file path to write logs to
   * @param patternLayout  layout for the logger, if null just display
   *                       the message using DEFAULT_CONVERSION_PATTERN
   */
  private static void createNewLoggerConfig(String loggerName,
                                            String outputFilePath,
                                            String patternLayout) {

    // Ignore null output files
    if (outputFilePath == null)
      return;

    // Create a logger to write the training data
    final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    final Configuration config = ctx.getConfiguration();

    Layout layout = PatternLayout.createLayout(patternLayout == null ? PatternLayout.DEFAULT_CONVERSION_PATTERN : patternLayout,
            config, null, StandardCharsets.UTF_8, false,
            false, null, null);

    Appender appender = FileAppender.createAppender(outputFilePath,
            "false", "false", loggerName, "true",
            "false", "false", "2000",
            layout, null, "false", null, config);
    appender.start();

    config.addAppender(appender);

    // Adding reference to the appender
    AppenderRef ref = AppenderRef.createAppenderRef(loggerName, null, null);
    AppenderRef[] refs = new AppenderRef[]{ref};

    LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.TRACE,
            loggerName, "true", refs, null, config, null);

    // Adding appender to logger, and adding logger to context
    loggerConfig.addAppender(appender, null, null);

    config.addLogger(loggerName, loggerConfig);
    ctx.updateLoggers();
  }

  private void initializeIndex() throws IOException {
    if (kbIndexDirectory == null && args.index != null) {
      LOG.info("Initializing KB index from directory: {}", args.index);
      kbIndexDirectory = FSDirectory.open(Paths.get(args.index));
    }
  }

  /**
   * Gets index reader from the directory specified in the user parameters
   *
   * @return KB index reader
   * @throws IOException on error
   */
  private IndexReader getKbIndexReader() throws IOException {
    if (kbIndexReader == null) {
      initializeIndex();
      kbIndexReader = DirectoryReader.open(kbIndexDirectory);
    }

    return kbIndexReader;
  }

  /**
   * @return index searcher
   * @throws IOException on error
   */
  IndexSearcher getKbIndexSearcher() throws IOException {
    if (kbIndexSearcher == null) {
      kbIndexSearcher = new IndexSearcher(getKbIndexReader());
    }

    return kbIndexSearcher;
  }

  /**
   * Get the current default index analyzer or create it.
   * Use EnglishAnalyzer as the default.
   *
   * @return kb index analyzer
   */
  Analyzer getKbIndexAnalyzer() {
    if (kbIndexAnalyzer == null) {
      kbIndexAnalyzer = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    }

    return kbIndexAnalyzer;
  }

  /**
   * Helper function. Extracts value from literal that has a type
   * (whether the type is a language for a string literal or a basic data type
   * like date, int, etc.)
   *
   * @param literalString the string representation of the literal, including its type
   * @return value of the literal
   */
  String extractValueFromTypedLiteralString(String literalString) {
    return NTriplesUtil.parseLiteral(literalString, valueFactory).stringValue();
  }

  /**
   * Helper function. Converts freebase URI to freebase mention id
   *
   * @param freebaseUri freebase uri, similar to
   * @return freebase mention id
   */
  static String freebaseUriToFreebaseId(String freebaseUri) {
    return freebaseUri.substring(freebaseUri.lastIndexOf('/')).replace('.', '/');
  }

  /**
   * Writes an array as a line separated by TRAINING_DATA_SEPARATOR.
   *
   * @param l    the logger to write to (the training data file)
   * @param data array of data
   */
  private void writeToTrainingFile(Logger l, String... data) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      if (i > 0)
        sb.append(TRAINING_DATA_SEPARATOR);

      if (data[i].contains(TRAINING_DATA_SEPARATOR))
        sb.append("\"").append(data[i]).append("\"");
      else
        sb.append(data[i]);
    }

    l.info(sb.toString());
  }

  /**
   * Determine which properties to retrieve and runs the training data retrieval pipeline.
   *
   * @throws Exception on error
   */
  private void generateTrainingData() throws IOException, ParseException, NoSuchMethodException {
    Method propertyFunction = null;
    try {
      propertyFunction = this.getClass().getDeclaredMethod(args.property);
      propertyFunction.invoke(this);
    } catch (NoSuchMethodException e) {
      String msg = "Cannot generate training data for property: " + args.property
              + ". Supported properties are: " + supportedPropertiesStr;
      LOG.error(msg);
      throw new IllegalArgumentException(msg);
    } catch (IllegalAccessException | InvocationTargetException e) {
      String msg = "Unable to call method: " + propertyFunction.getName();
      LOG.error(msg, e);
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Generate training data for property birth date
   * <p>
   * Note: this function might need some refactoring when we add more properties
   */
  void birthdate() throws ParseException, IOException {
    QueryParser queryParser = new QueryParser(FIELD_BIRTHDATE, getKbIndexAnalyzer());
    queryParser.setAllowLeadingWildcard(true);

    Query q = queryParser.parse("*");

    LOG.info("Starting the search using query: {}", q.toString());

    // Collect all matching documents in a set of matching doc ids
    Set<Integer> matchingDocIds = new HashSet<>();
    getKbIndexSearcher().search(q, new CheckHits.SetCollector(matchingDocIds));

    LOG.info("Found {} matching documents, retrieving...", matchingDocIds.size());

    // Process the retrieved document ids
    matchingDocIds.forEach((Integer docId) -> {

      Document doc = null;

      try {
        doc = getKbIndexReader().document(docId);
      } catch (IOException e) {
        LOG.warn("Error retrieving document with id: {}. Ignoring.", docId);
        return;
      }

      String freebaseURI = doc.get(IndexNodes.FIELD_ID);

      // We might have multiple values for the field
      String[] birthdates = doc.getValues(FIELD_BIRTHDATE);

      // Get the freebase English label of this entity
      String[] labels = doc.getValues(FIELD_LABEL);

      String englishLabel = null;
      for (String label : labels) {
        Literal literal = NTriplesUtil.parseLiteral(label, valueFactory);
        if (literal.getLanguage().orElse("N/A").toLowerCase().equals("en")) {
          englishLabel = literal.stringValue();
          break;
        }
      }

      // Basically make sure label is not null, for some entities in freebase
      if (englishLabel == null || freebaseURI == null || birthdates == null || birthdates.length == 0)
        return; // Ignore this search

      String freebaseId = freebaseUriToFreebaseId(freebaseURI);

      for (String birthdate : birthdates) {
        // Get string value
        String birthdateVal = extractValueFromTypedLiteralString(birthdate);

        // Write property value as training data
        writeToTrainingFile(TRAINING_DATA_OUTPUT_FILE_EXAMPLES, freebaseId, englishLabel, birthdateVal);
      }

      // TODO - After building an index for the mentions of Freebase entities in ClueWeb,
      // we need to get the ClueWeb mentions of this freebase entity and write them to a separate file
    });

  }

  public TrainingDataGenerator(Args args) {
    this.args = args;

    // Create logger to write training data to
    createNewLoggerConfig("prop", this.args.output, null);
    TRAINING_DATA_OUTPUT_FILE_EXAMPLES = LogManager.getLogger("prop");

    valueFactory = SimpleValueFactory.getInstance();
  }

  public static void main(String[] args) throws Exception {
    Args trainingDataGeneratorArgs = new Args();
    CmdLineParser parser = new CmdLineParser(trainingDataGeneratorArgs,
            ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example command: " + TrainingDataGenerator.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    LOG.info("Generating training data started...");

    new TrainingDataGenerator(trainingDataGeneratorArgs).generateTrainingData();

    LOG.info("Generating training data completed.");
  }
}
