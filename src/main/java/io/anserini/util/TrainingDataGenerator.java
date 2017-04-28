package io.anserini.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

import javax.ws.rs.core.MultivaluedHashMap;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Generate training data (positive examples) of a particular
 * relationship from an indexed RDF dataset, e.g., Freebase.
 * <p>
 * The class also helps in indexing new KB RDF and entity mentions
 * TSV files.
 */
public class TrainingDataGenerator {

    private static Logger LOG = null;

    private static Logger TRAINING_DATA_OUTPUT_FILE_EXAMPLES = null;
    private static Logger TRAINING_DATA_OUTPUT_FILE_MENTIONS = null;

    // Index field names
    // Knowledge Base RDF index
    static final String FIELD_NAME_SUBJECT = "subject";
    static final String FIELD_NAME_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";

    // Properties field names
    static final String FIELD_NAME_BIRTHDATE = "http://rdf.freebase.com/ns/people.person.date_of_birth";
    static final String FIELD_NAME_SPOUSE = "http://rdf.freebase.com/ns/people.person.spouse_s";

    // Entity mentions index
    static final String FIELD_NAME_ENTITY_ID = "entityId";
    static final String FIELD_NAME_ENTITY_LABEL = "entityLabel";
    static final String FIELD_CORPUS_DOCUMENT_ID = "docId";

    // RDF object predicate types
    static final String VALUE_TYPE_URI = "URI";
    static final String VALUE_TYPE_STRING = "STRING";
    static final String VALUE_TYPE_TEXT = "TEXT";
    static final String VALUE_TYPE_OTHER = "OTHER";

    /**
     * List of commands that can be executed
     */
    static final String AVAILABLE_COMMANDS = "(index-kb, index-mentions, generate-training-data, query-kb, query-mentions)";

    /**
     * The arguments that this program accepts
     */
    public static final class Args {

        @Option(name = "-command",
                metaVar = "[Command to execute]",
                required = true,
                usage = "The command that represents the task to perform. Options: " + AVAILABLE_COMMANDS)
        String command;

        @Option(name = "-kbIndexPath",
                metaVar = "[KB Index path]",
                required = false,
                usage = "Directory contains index files for the Knowledge base")
        String kbIndexPath;

        @Option(name = "-mentionsIndexPath",
                metaVar = "[Mentions Index path]",
                required = false,
                usage = "Directory contains index files for the entity mentions")
        String mentionsIndexPath;

        @Option(name = "-outputFile",
                metaVar = "[Output file path]",
                required = false,
                usage = "Output file to write training data to")
        String outputFilePath;

        @Option(name = "-property",
                metaVar = "[Property name]",
                required = false,
                usage = "The property to generate training data for. Currently supports: (birthdate)")
        String propertyName;

        @Option(name = "-dataPath",
                metaVar = "[Data Path]",
                required = false,
                usage = "The file/folder that contains data to be indexed")
        String dataPath;

        @Option(name = "-entityIdColNum",
                metaVar = "[Entity ID Column Number]",
                required = false,
                usage = "The column number (zero-based) in the TSV file that contains the entity id")
        int entityIdColNum;

        @Option(name = "-entityLabelColNum",
                metaVar = "[Entity Label Column Number]",
                required = false,
                usage = "The column number (zero-based) in the TSV file that contains the entity label as mentioned in the text, or -1 if no label in the training file")
        int entityLabelColNum;

        @Option(name = "-docIdColNum",
                metaVar = "[Document ID Column Number]",
                required = false,
                usage = "The column number (zero-based) in the TSV file that contains the column id")
        int docIdColNum;

        @Option(name = "-predicatesToIndex",
                handler = StringArrayOptionHandler.class, // Allows for multiple args
                metaVar = "[KB Predicates to Index, separated by Space]",
                required = false,
                usage = "List of KB predicates that will be indexed")
        List<String> predicatesToIndex;

        @Option(name = "-maxNumLinesToIndex",
                metaVar = "[Maximum number of triples to index]",
                required = false,
                usage = "Integer representing the maximum number of triples to index, or -1 to index everything")
        long maxNumLinesToIndex = -1;

        // For querying commands
        @Option(name = "-query",
                metaVar = "[Search query: the ID of the document]",
                required = false,
                usage = "Can be entity id to find mentions, or KB URI to find its predicates and values")
        String query;
    }

    /**
     * Args instance
     */
    private final Args args;

    /**
     * The directory in which the RDF dataset index is stored
     */
    Directory kbIndexDirectory = null;

    /**
     * Indexed knowledge base/RDF dataset to retreive
     * the training examples from
     */
    IndexReader kbIndexReader = null;

    /**
     * Write to the KB index.
     */
    IndexWriter kbIndexWriter = null;

    /**
     * Index searcher to search in the index
     */
    IndexSearcher kbIndexSearcher = null;

    /**
     * Index analyzer to search
     */
    Analyzer kbIndexAnalyzer;

    /**
     * Directory in which the entity mentions index is stored
     */
    Directory mentionsIndexDirectory = null;

    /**
     * Read from entity mentions index.
     */
    IndexReader mentionsIndexReader = null;

    /**
     * Used to write to an index
     */
    IndexWriter mentionsIndexWriter = null;

    /**
     * Search in mentions index
     */
    IndexSearcher mentionsIndexSearcher = null;

    /**
     * Analyzer to search in mentions
     */
    Analyzer mentionsIndexAnalyzer = null;

    /**
     * Simple value factory to parse literals using Sesame library.
     */
    static ValueFactory valueFactory = SimpleValueFactory.getInstance();

    /**
     * How to separate records in the training data files
     */
    private static final String TRAINING_DATA_SEPARATOR = "\t";

    /**
     * Creates a logger configuration with an appender that writes to a file
     *
     * @param loggerName the name of the logger to create
     * @param outputFilePath the file path to write logs to
     * @param appendToLog whether or not we append to the log file
     */
    private static void createNewLoggerConfig(String loggerName,
                                              String outputFilePath,
                                              boolean appendToLog,
                                              String patternLayout) {
        // Create a logger to write the training data
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        if (outputFilePath != null) {
            Layout layout = PatternLayout
                    .createLayout(patternLayout == null? PatternLayout.DEFAULT_CONVERSION_PATTERN : patternLayout,
                            config,
                            null,
                            StandardCharsets.UTF_8,
                            false,
                            false,
                            null,
                            null
                    );

            Appender appender = FileAppender
                    .createAppender(outputFilePath,
                            Boolean.toString(appendToLog),
                            "false",
                            loggerName,
                            "true",
                            "false",
                            "false",
                            "2000",
                            layout,
                            null,
                            "false",
                            null,
                            config);
            appender.start();

            config.addAppender(appender);

            // Adding reference to the appender
            AppenderRef ref = AppenderRef.createAppenderRef(loggerName, null, null);
            AppenderRef[] refs = new AppenderRef[]{ref};

            LoggerConfig loggerConfig = LoggerConfig.createLogger(
                    "false",
                    Level.TRACE,
                    loggerName,
                    "true",
                    refs, null,
                    config, null
            );

            // Adding appender to logger, and adding logger to context
            loggerConfig.addAppender(
                    appender,
                    null,
                    null
            );

            config.addLogger(loggerName, loggerConfig);
            ctx.updateLoggers();
        }
    }

    /**
     * Constructor.
     *
     * @param args main program arguments
     * @throws Exception on error
     */
    public TrainingDataGenerator(Args args) throws Exception {
        this.args = args;

        if (this.args.outputFilePath != null) {
            // Examples output file
            createNewLoggerConfig("prop", this.args.outputFilePath, false, null);
            TRAINING_DATA_OUTPUT_FILE_EXAMPLES = LogManager.getLogger("prop");

            // Mentions output file
            createNewLoggerConfig("men", "entity-mentions.tsv", false, null);
            TRAINING_DATA_OUTPUT_FILE_MENTIONS = LogManager.getLogger("men");
        }
    }

    /**
     * Reference index directories
     *
     * @throws IOException on error
     */
    void initializeIndexes() throws IOException {
        if (kbIndexDirectory == null && args.kbIndexPath != null) {
            LOG.info("Initializing KB index from directory: {}", args.kbIndexPath);
            kbIndexDirectory = FSDirectory.open(Paths.get(args.kbIndexPath));
        }

        if (mentionsIndexDirectory == null && args.mentionsIndexPath != null) {
            LOG.info("Initializing mentions index from directory: {}", args.mentionsIndexPath);
            mentionsIndexDirectory = FSDirectory.open(Paths.get(args.mentionsIndexPath));
        }
    }

    /**
     * @return Lucene index reader from the path specified in
     * TrainingDataGenerator{@link #args}
     * @throws IOException
     */
    IndexReader getKbIndexReader() throws IOException {
        if (kbIndexReader == null) {
            kbIndexReader = DirectoryReader.open(kbIndexDirectory);
        }

        return kbIndexReader;
    }

    IndexSearcher getKbIndexSearcher() throws IOException {
        if (kbIndexSearcher == null) {
            kbIndexSearcher = new IndexSearcher(getKbIndexReader());
        }

        return kbIndexSearcher;
    }

    IndexWriter getKbIndexWriter() throws Exception {
        if (kbIndexWriter == null || !kbIndexWriter.isOpen()) {

            if (kbIndexDirectory == null) {
                LOG.error("Missing -kbIndexDirectory parameter");
                return null;
            }

            IndexWriterConfig iwc = new IndexWriterConfig(getKbIndexAnalyzer());

            // Create a new index or append
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // We always overwrite KB index
            iwc.setCodec(new Lucene62Codec(Lucene50StoredFieldsFormat.Mode.BEST_SPEED));

            kbIndexWriter = new IndexWriter(kbIndexDirectory, iwc);
        }

        return kbIndexWriter;
    }

    /**
     * Get the current default index analyzer or create it.
     * Use StandardAnalyzer as the default.
     *
     * @return kb index analyzer
     * @throws Exception
     */
    Analyzer getKbIndexAnalyzer() throws Exception {
        if (kbIndexAnalyzer == null) {
            String defaultAnalyzerName = "org.apache.lucene.analysis.standard.StandardAnalyzer";
            kbIndexAnalyzer = (Analyzer) Class.forName(defaultAnalyzerName).newInstance();
        }
        return kbIndexAnalyzer;
    }

    /**
     * @return Entity mentions index reader
     * @throws IOException on error
     */
    IndexReader getMentionsIndexReader() throws IOException {
        if (mentionsIndexReader == null) {
            mentionsIndexReader = DirectoryReader.open(mentionsIndexDirectory);
        }

        return mentionsIndexReader;
    }

    /**
     * @return entity mentions index searcher
     * @throws IOException on error
     */
    IndexSearcher getMentionsIndexSearcher() throws IOException {
        if (mentionsIndexSearcher == null) {
            mentionsIndexSearcher = new IndexSearcher(getMentionsIndexReader());
        }

        return mentionsIndexSearcher;
    }

    Analyzer getMentionsIndexAnalyzer() throws Exception {
        if (mentionsIndexAnalyzer == null) {
            String defaultAnalyzerName = "org.apache.lucene.analysis.standard.StandardAnalyzer";
            mentionsIndexAnalyzer = (Analyzer) Class.forName(defaultAnalyzerName).newInstance();
        }

        return mentionsIndexAnalyzer;
    }

    /**
     * @return Index writer for entity mentions index.
     * @throws Exception on error
     */
    IndexWriter getMentionsIndexWriter() throws Exception {
        if (mentionsIndexWriter == null || !mentionsIndexWriter.isOpen()) {

            if (mentionsIndexDirectory == null) {
                LOG.error("Missing -mentionsIndexDirectory parameter");
                return null;
            }

            IndexWriterConfig iwc = new IndexWriterConfig(getMentionsIndexAnalyzer());

            // Create a new index or append
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            iwc.setCodec(new Lucene62Codec(Lucene50StoredFieldsFormat.Mode.BEST_SPEED));

            mentionsIndexWriter = new IndexWriter(mentionsIndexDirectory, iwc);
        }

        return mentionsIndexWriter;
    }

    /**
     * Retrieve a document id given the subject URI
     *
     * @param subjectURI the URI of the subject to retrieve
     * @return the document id, or -1 if the subject doesn't exist
     * @throws IOException on error
     */
    int getSubjectDocID(String subjectURI) throws IOException {
        TermQuery query = new TermQuery(new Term(FIELD_NAME_SUBJECT, subjectURI));
        TopDocs result = getKbIndexSearcher().search(query, 1);
        if (result.totalHits == 0)
            return -1;
        else {
            return result.scoreDocs[0].doc;
        }
    }

    /**
     * Given an entity freebase ID, find all text documents that this entity
     * is mentioned in (using the mentionsIndex)
     *
     * @param entityId the entity freebase id
     * @return a map that has clueweb_document_id -> [list of labels in this document]
     */
    private MultivaluedHashMap<String, String> getEntityDocumentsAndLabels(String entityId) throws IOException {

        MultivaluedHashMap<String, String> ret = new MultivaluedHashMap<>();

        // Search query
        TermQuery query = new TermQuery(new Term(FIELD_NAME_ENTITY_ID, entityId));

        // Collect all matching documents in a set of matching doc ids
        Set<Integer> matchingDocIds = new HashSet<>(5);
        getMentionsIndexSearcher().search(query, new CheckHits.SetCollector(matchingDocIds));

        // Retrieve fields stored in each document, the fields represent the doc corpus id
        // and the label that the entity appears as
        matchingDocIds.forEach(docId -> {
            try {
                Document doc = getMentionsIndexReader().document(docId.intValue());

                String docEntityId = doc.get(FIELD_NAME_ENTITY_ID);
                if (docEntityId.equals(entityId)) {
                    String entityLabel = doc.get(FIELD_NAME_ENTITY_LABEL);
                    String corpusDocId = doc.get(FIELD_CORPUS_DOCUMENT_ID);
                    ret.add(corpusDocId, entityLabel);
                }
            } catch (IOException e) {
                LOG.warn("Error reading document with id: {}", docId);
            }
        });

        return ret;
    }

    /**
     * Helper function.
     * <p>
     * Converts freebase URI to freebase mention id
     *
     * @param freebaseUri freebase uri, similar to
     * @return freebase mention id
     */
    static String freebaseUriToFreebaseId(String freebaseUri) {
        return freebaseUri.substring(freebaseUri.lastIndexOf('/')).replace('.', '/');
    }

    /**
     * Helper function.
     * <p>
     * Extracts value from literal that has a type (whether the type is
     * a language for a string literal or a basic data type like date, int, etc.)
     *
     * @param literalString the string representation of the literal, including its type
     * @return value of the literal
     */
    static String extractValueFromTypedLiteralString(String literalString) {
        return NTriplesUtil.parseLiteral(literalString, valueFactory).stringValue();
    }

    /**
     * Determine which properties to retrieve and runs the training data retrieval pipeline.
     *
     * @throws Exception on error
     */
    private void generateTrainingData() throws Exception {

        if (args.kbIndexPath == null) {
            Exception e = new IllegalArgumentException("Missing -kbIndexPath argument to specify the location of the KB index");
            LOG.error(e);
            throw e;
        }

        if (args.mentionsIndexPath == null) {
            Exception e = new IllegalArgumentException("Missing -mentionsIndexPath argument to specify the location of the mentions index");
            LOG.error(e);
            throw e;
        }

        if (args.propertyName == null) {
            Exception e = new IllegalArgumentException("Missing -property argument to specify which property to extract");
            LOG.error(e);
            throw e;
        }

        // Initialize both KB and Mentions indexes
        initializeIndexes();

        switch (args.propertyName.toLowerCase()) {
            case "birthdate":
                birthdate();
                break;

            default:
                LOG.error("Cannot generate training data for property: {}", args.propertyName);
                throw new IllegalArgumentException("Cannot generate training data for property: " + args.propertyName);
        }
    }

    /**
     * Generate training data for property birth date
     *
     * TODO - This might need refactoring when we add other properties
     */
    void birthdate() throws Exception {
        QueryParser queryParser = new QueryParser(
                FIELD_NAME_BIRTHDATE
                , getKbIndexAnalyzer());
        queryParser.setAllowLeadingWildcard(true);

        Query q = queryParser.parse("*");

        LOG.info("Query: {}", q.toString());

        LOG.info("Searching...");

        // Collect all matching documents in a set of matching doc ids
        Set<Integer> matchingDocIds = new HashSet<>(5);
        getKbIndexSearcher().search(q, new CheckHits.SetCollector(matchingDocIds));

        // Process the retrieved documents
        matchingDocIds.forEach((Integer docId) -> {

            Document doc = null;
            try {
                doc = getKbIndexReader().document(docId);
            } catch (IOException e) {
                LOG.warn("Error reading document with id: {}", docId);
                return;
            }

            String freebaseURI = doc.get(FIELD_NAME_SUBJECT);
            String[] birthdates = doc.getValues(FIELD_NAME_BIRTHDATE);
            String label = doc.get(FIELD_NAME_LABEL);

            // Basically make sure label is not null, for some entities in freebase
            if (label == null || freebaseURI == null || birthdates == null || birthdates.length == 0)
                return; // Ignore this search

            String freebaseId = freebaseUriToFreebaseId(freebaseURI);

            String labelVal = extractValueFromTypedLiteralString(label);

            for (String birthdate : birthdates) {
                // Get string value
                String birthdateVal = extractValueFromTypedLiteralString(birthdate);

                // Write property value as training data
                writeToTrainingFiles(TRAINING_DATA_OUTPUT_FILE_EXAMPLES,
                        freebaseId,
                        labelVal,
                        birthdateVal);
            }

            // Get the mentions of this freebase entity and write them in the mentions file
            MultivaluedHashMap<String, String> documentsAndLabels = null;
            try {
                documentsAndLabels = getEntityDocumentsAndLabels(freebaseId);
            } catch (IOException e) {
                LOG.warn("Error retrieving mentions for freebase entity id: {}", freebaseId);
                return;
            }

            for (String corpusDocId : documentsAndLabels.keySet()) {
                List<String> labels = documentsAndLabels.get(corpusDocId);
                for (String labelInDoc : labels) {
                    writeToTrainingFiles(TRAINING_DATA_OUTPUT_FILE_MENTIONS,
                            freebaseId,
                            corpusDocId,
                            labelInDoc);
                }
            }
        });

        LOG.info("Training data retrieved.");
    }

    /**
     * Writes an array as CSV line separated by TRAINING_DATA_SEPARATOR
     *
     * @param l the logger to write to (the training data file)
     * @param data array of data
     */
    void writeToTrainingFiles(Logger l, String... data) {
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
     * Given an RDF dataset, this function builds a Lucene index on the RDF triples,
     * storing each subject as a document id, predicates as (indexable) fields,
     * and predicate values as values of the fields.
     * <p>
     * Check original Freebase-Tools for sample code.
     */
    void indexRdfDataset() throws Exception {

        // Make sure we have all required parameters/arguments
        if (args.dataPath == null) {
            Exception e = new IllegalArgumentException("Missing -dataPath argument to specify which data to index");
            LOG.error(e);
            throw e;
        }

        if (args.kbIndexPath == null) {
            Exception e = new IllegalArgumentException("Missing -kbIndexPath argument to specify where to create the index");
            LOG.error(e);
            throw e;
        }

        if (args.predicatesToIndex == null || args.predicatesToIndex.size() == 0) {
            LOG.warn("No predicates to index, all predicates will be stored only");
        }

        // Initialize and create indexes
        initializeIndexes();
        getKbIndexWriter();
        getKbIndexWriter().commit();

        // Get the files in the directory that contains the RDF files
        File dataPathDirectory = new File(args.dataPath);
        Iterator<File> filesIterator = null;
        // If the provided path was a directory, iterate over all of its files and sub-directories,
        // otherwise, use the single file
        if (dataPathDirectory.isDirectory()) {
            filesIterator = FileUtils.iterateFiles(dataPathDirectory, null, true);
        } else {
            filesIterator = Arrays.asList(new File[]{dataPathDirectory}).iterator();
        }

        // Temp Map to hold the predicate values for a single subject
        Map<String, List<String>> predValues = new TreeMap<String, List<String>>();
        long count = 0;
        String currentSubject = "";
        boolean limitReached = false;

        LOG.info("Started indexing process...");
        while (filesIterator.hasNext()) {

            // Check if we already reached the limit by processing one of the files
            if (limitReached)
                break;

            // Read next file to index it
            File triplesFile = filesIterator.next();

            LOG.info("Indexing from file: {}", triplesFile.getAbsolutePath());
            InputStream is = null;
            BufferedReader tripleBR = null;

            try {
                // Open file stream, read it line by line
                is = new FileInputStream(triplesFile);
                if (triplesFile.getAbsolutePath().toLowerCase().endsWith(".gz"))
                    is = new GZIPInputStream(is);
                tripleBR = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                while (true) {
                    String line = tripleBR.readLine();
                    count++;

                    // if file doesn't have any more triples, go to next file
                    if (line == null) {
                        indexSubjectPredicatesAndValues(currentSubject, predValues);
                        LOG.info("Finished processing file: {}", triplesFile.getAbsolutePath());
                        break;
                    }

                    // If processing limit exceeded, exit the process,
                    // setting limitReached to true to avoid processing other files
                    if (args.maxNumLinesToIndex > 0 && count >= args.maxNumLinesToIndex) {
                        indexSubjectPredicatesAndValues(currentSubject, predValues);
                        LOG.info("Limit of triples to prcess reached... Stopping indexing process.");
                        limitReached = true;
                        break;
                    }

                    // Show progress
                    if (count % 100000 == 0) {
                        LOG.info("Processed {} lines", count);
                    }

                    if (line.startsWith("#")) // Ignore comments
                        continue;

                    // Tokenize line pieces using tabs
                    String[] triple = line.split("\t");
                    if (triple.length != 4) {
                        LOG.warn("Ignoring invalid NT triple line: {}", line);
                        continue;
                    }

                    if (!triple[0].equals(currentSubject) || limitReached) {
                        if (!"".equals(currentSubject)) {
                            // New subject found, index the previous subject and its value
                            indexSubjectPredicatesAndValues(currentSubject, predValues);
                        }

                        // Change the current subject to start processing its predicates and values
                        currentSubject = triple[0];
                        predValues.clear();
                    }

                    // record this predicate and value for the current subject:
                    String predicate = triple[1];
                    String value = triple[2];

                    List<String> values = predValues.get(predicate);
                    if (values == null) {
                        values = new ArrayList<String>(5);
                        predValues.put(predicate, values);
                    }

                    values.add(value);
                }

            } catch (Exception e) {
                LOG.error("Error while processing triples file: {}", triplesFile.getAbsolutePath(), e);
            } finally {
                // Always close streams
                if (is != null) {
                    is.close();
                }

                if (tripleBR != null) {
                    tripleBR.close();
                }
            }
        }

        LOG.info("Indexed {} lines", count);
        LOG.info("Indexing process completed.");
        cleanup();
    }

    /**
     * This function adds a new document for the KB entity to the KB index.
     * It checks if each of the predicates is indexed, and add appropriate fields.
     *
     * @param subject the main subject (URI) of the entity
     * @param predicatesAndValues a map that has predicates and their values
     * @throws Exception on error
     */
    void indexSubjectPredicatesAndValues(String subject, Map<String, List<String>> predicatesAndValues) throws Exception {
        Document doc = new Document();

        // Index as a StringField to allow searching
        Field subjectField = new StringField(FIELD_NAME_SUBJECT, cleanUri(subject), Field.Store.YES);

        doc.add(subjectField);

        for (Map.Entry<String, List<String>> entry : predicatesAndValues.entrySet()) {
            String predicate = cleanUri(entry.getKey());
            List<String> values = entry.getValue();

            for (String value : values) {
                String valueType = getValueType(value);
                value = normalizeValue(value);
                if (isIndexedPredicate(predicate)) {
                    if (valueType.equals(VALUE_TYPE_URI)) {
                        // Always index URIs using StringField
                        doc.add(new StringField(predicate, value, Field.Store.YES));
                    } else {
                        // Just store the predicate in a stored field, no index
                        doc.add(new TextField(predicate, value, Field.Store.YES));
                    }
                } else {
                    // Just add the predicate as a stored field, no index on it
                    doc.add(new StoredField(predicate, value));
                }
            }
        }

        if (cleanUri(subject).equals("http://rdf.freebase.com/ns/m.02mjmr")) {
            LOG.info("OBAMA FOUND");
            System.out.println(doc);
            LOG.info(doc.toString());
        }

        getKbIndexWriter().addDocument(doc);
    }

    /**
     * Check if the predicate should be indexed
     *
     * @param predicate the predicate to check
     * @return true if the user specified it to be indexed, false otherwise.
     */
    boolean isIndexedPredicate(String predicate) {
        return args.predicatesToIndex != null && args.predicatesToIndex.contains(predicate);
    }

    /**
     * Figures out the type of the value of an object in a triple
     *
     * @param value object value
     * @return uri, string, text, or other
     */
    String getValueType(String value) {
        // Determine the type of this N-Triples `value'.
        char first = value.charAt(0);
        switch (first) {
            case '<':
                return VALUE_TYPE_URI;
            case '"':
                if (value.charAt(value.length() - 1) == '"')
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
    public String normalizeStringValue(String value) {
        return value;
    }

    /**
     * Un-escape strings
     */
    public String normalizeTextValue(String value) {
        return NTriplesUtil.unescapeString(value);
    }

    public String normalizeValue(String value) {
        // Normalize a `value' depending on its type.
        String type = getValueType(value);
        if (type.equals(VALUE_TYPE_URI))
            return cleanUri(value);
        else if (type.equals(VALUE_TYPE_STRING))
            return normalizeStringValue(value);
        else if (type.equals(VALUE_TYPE_TEXT))
            return normalizeTextValue(value);
        else
            return value;
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
     * This function indexes entity mentions file.
     * The entity mentions file contains mentions of each entity id and the documents
     * in which they appear.  This is similar to ClueWeb09FACC.
     * The mentions file is expected to be a TSV file.
     */
    void indexEntityMentions() throws Exception {

        // Make sure we have all required parameters/arguments
        if (args.dataPath == null) {
            Exception e = new IllegalArgumentException("Missing -dataPath argument to specify which data to index");
            LOG.error(e);
            throw e;
        }

        if (args.mentionsIndexPath == null) {
            Exception e = new IllegalArgumentException("Missing -mentionsIndexPath argument to specify where to create/append the index");
            LOG.error(e);
            throw e;
        }

        initializeIndexes();
        getMentionsIndexWriter();
        getMentionsIndexWriter().commit();

        // Get the files in the directory that contains the mentions files
        File dataPathDirectory = new File(args.dataPath);
        Iterator<File> filesIterator = null;
        // If the provided path was a directory, iterate over all of its files and sub-directories,
        // otherwise, use the single file
        if (dataPathDirectory.isDirectory()) {
            filesIterator = FileUtils.iterateFiles(dataPathDirectory, new String[]{"tsv", "TSV"}, true);
        } else {
            filesIterator = Arrays.asList(new File[]{dataPathDirectory}).iterator();
        }

        // Temp map to hold entity mentions that appear in a document
        Map<String, Set<String>> docEntities = new HashMap<>();
        String currentDocument = "";
        long count = 0;
        boolean limitReached = false;

        // Highest index
        int maxColIndex = NumberUtils.max(new int[]{args.docIdColNum, args.entityIdColNum, args.entityLabelColNum});


        LOG.info("Started indexing process...");
        while (filesIterator.hasNext()) {

            if (limitReached)
                break;

            File mentionsFile = filesIterator.next();
            LOG.info("Indexing from file: {}", mentionsFile.getAbsolutePath());

            try {
                // Use a scanner in case the files are large
                Scanner mentionsScanner = new Scanner(mentionsFile);
                while (true) {

                    // No more lines to read
                    if (!mentionsScanner.hasNextLine()) {
                        indexDocumentMentions(currentDocument, docEntities);
                        LOG.info("Finished processing file: {}", mentionsFile.getAbsolutePath());
                        break;
                    }

                    // Read next line
                    String line = mentionsScanner.nextLine();
                    count++;

                    // Show progress
                    if (count % 100000 == 0) {
                        LOG.info("Processed {} lines", count);
                    }

                    // Check line limit, set limitReached to true to avoid processing other files
                    if (args.maxNumLinesToIndex > 0 && count >= args.maxNumLinesToIndex) {
                        LOG.info("Reached maximum limit of lines to index... Writing current data and stopping the index process");
                        limitReached = true;
                        break;
                    }

                    // Tokenize the TSV file
                    String[] linePieces = line.split("\t");

                    // Check if there is enough column pieces in the split line
                    if (maxColIndex >= linePieces.length) {
                        LOG.warn("Ignoring invalid line, not enough columns: {}", line);
                        continue;
                    }

                    // Extract different information from
                    String entityId = linePieces[args.entityIdColNum];
                    String docId = linePieces[args.docIdColNum];
                    String entityLabel = args.entityLabelColNum == -1 ? null : linePieces[args.entityLabelColNum];

                    if (!docId.equals(currentDocument)) {
                        if (!"".equals(currentDocument)) {
                            // New document found, index previous document
                            indexDocumentMentions(currentDocument, docEntities);
                        }

                        // Change the current document to start collecting its entity mentions
                        currentDocument = docId;
                        docEntities.clear();
                    }

                    Set<String> entityMentions = docEntities.get(entityId);
                    if (entityMentions == null) {
                        entityMentions = new HashSet<>(5);
                        docEntities.put(entityId, entityMentions);
                    }

                    entityMentions.add(entityLabel);
                }
            } catch (Exception e) {
                LOG.error("Error reading mentions file: {}", mentionsFile, e);
            }
        }

        LOG.info("Indexed {} lines", count);
        LOG.info("Indexing process completed.");
        cleanup();
    }

    private void indexDocumentMentions(String docId, Map<String, Set<String>> docEntities) {
        for (String entityId : docEntities.keySet()) {
            Set<String> entityLabels = docEntities.get(entityId);
            for (String entityLabel: entityLabels) {
                try {
                    indexEntityMentionRecord(entityId, entityLabel, docId);
                } catch (Exception e) {
                    LOG.error("Error indexing entity mention for entity: ({}) in document: ({})",
                            entityId,
                            docId,
                            e);
                }
            }
        }
    }

    /**
     * Adds an entity mention to the entity mention index.
     * The added document will have 3 fields:
     * 1- the entity id
     * 2- the document id (corpus id)
     * 3- the label
     *
     * @param entityId id of the entity to index
     * @param entityLabel the label that this entity appears in
     * @param docId the document in which this entity appears with label entityLabel
     * @throws Exception
     */
    void indexEntityMentionRecord(String entityId, String entityLabel, String docId) throws Exception {
        Document doc = new Document();

        // Document fields. entityId is StringField to be searchable
        Field entityIdField = new StringField(FIELD_NAME_ENTITY_ID,
                entityId,
                Field.Store.YES);

        // Field document
        Field docField = new TextField(FIELD_CORPUS_DOCUMENT_ID,
                docId,
                Field.Store.YES);

        // Label
        Field labelField = new TextField(FIELD_NAME_ENTITY_LABEL,
                entityLabel,
                Field.Store.YES);

        // Add fields to the document
        doc.add(entityIdField);
        doc.add(docField);
        doc.add(labelField);

        // Index the new document
        getMentionsIndexWriter().addDocument(doc);
    }

    /**
     * Close index writers, commits and flushes any uncommitted changes
     * @param iw writer to be closed
     *
     * @throws Exception on error
     */
    private void closeIndex(IndexWriter iw) throws Exception {
        if (iw != null && iw.isOpen()) {
            iw.commit();
            iw.flush();
            iw.close();
        }
    }

    /**
     * Query the entity mentions index to find mentions of an entity
     * in the document, along with the labels that are mentioned with
     * this entity.
     *
     * @throws Exception on error
     */
    private void queryMentions() throws Exception {

        // Make sure we have all required parameters
        if (args.mentionsIndexPath == null) {
            Exception e = new IllegalArgumentException("Missing -mentionsIndexPath argument to read mentions index from");
            LOG.error(e);
            throw e;
        }

        if (args.query == null) {
            Exception e = new IllegalArgumentException("Missing -query argument. This should be the freebase entity ID");
            LOG.error(e);
            throw e;
        }


        // Initialize index readers
        initializeIndexes();
        getMentionsIndexReader();

        String entityId = args.query;

        LOG.info("Querying started...");
        LOG.info("Query entity id: {}", entityId);

        // Retrieve docs and labels
        MultivaluedHashMap<String, String> docsAndLabels = getEntityDocumentsAndLabels(entityId);

        if (docsAndLabels.isEmpty()) {
            String msg = "Cannot find mentions of entity with id: " + entityId;
            LOG.warn(msg);
            System.err.println(msg);
        } else {
            for (String docId : docsAndLabels.keySet()) {
                List<String> labels = docsAndLabels.get(docId);
                labels.forEach(label -> {
                    String msg = docId + " \t " + label;
                    System.out.println(msg);
                    LOG.info(msg);
                });
            }
        }

        LOG.info("Querying completed.");
        cleanup();
    }

    /**
     * Query the KB RDF index to find predicates/properties
     * of a particular entity URI.
     *
     * @throws Exception on error
     */
    private void queryKB() throws Exception {

        // Make sure we have all required parameters
        if (args.kbIndexPath == null) {
            Exception e = new IllegalArgumentException("Missing -kbIndexPath argument to read KB index from");
            LOG.error(e);
            throw e;
        }

        if (args.query == null) {
            Exception e = new IllegalArgumentException("Missing -query argument. This should be the freebase entity URI");
            LOG.error(e);
            throw e;
        }

        String query = args.query;

        // Get property if it exists
        final String property = args.propertyName;

        LOG.info("Querying started...");
        // Initialize index readers
        initializeIndexes();
        getKbIndexReader();

        int subjectDocId = getSubjectDocID(query);

        if (subjectDocId < 0) {
            String msg = "Cannot find subject: " + query;
            LOG.warn(msg);
            System.err.println(msg);
        } else {
            Document subjectDoc = getKbIndexReader().document(subjectDocId);
            subjectDoc.iterator().forEachRemaining(field -> {
                if (property == null || field.name().equals(property)) {
                    String fieldMessage = field.name() + ":\t " + field.stringValue();
                    LOG.info(fieldMessage);
                    System.out.println(fieldMessage);
                }
            });
        }

        LOG.info("Querying completed.");
        cleanup();
    }

    /**
     * Close indexes and clean up
     *
     * @throws Exception on error
     */
    void cleanup() throws Exception {
        LOG.info("Cleaning up, closing indexes...");

        if (mentionsIndexWriter != null && mentionsIndexWriter.isOpen())
            closeIndex(mentionsIndexWriter);

        if (kbIndexWriter != null && kbIndexWriter.isOpen())
            closeIndex(kbIndexWriter);

        LOG.info("Cleanup complete.");
    }

    public static void main(String[] args) throws Exception {

        // For testing if no arguments were provided
        String[] argsIndexKB = new String[] {
                "-command", "index-kb",
                "-kbIndexPath", "/tmp/freebase.index",
                "-dataPath", "/Users/mfathy/Downloads/freebase-rdf-latest.gz",
                "-predicatesToIndex", "http://rdf.freebase.com/ns/people.person.date_of_birth",
                "-maxNumLinesToIndex", "10000000"
        };

        String[] argsIndexMentions = new String[]{
                "-command", "index-mentions",
                "-mentionsIndexPath", "entity-mentions.index",
                "-dataPath", "entity-mentions",
                "-entityIdColNum", "7",
                "-entityLabelColNum", "2",
                "-docIdColNum", "0"
        };

        String[] argsGenerateTrainingData = new String[] {
                "-command", "generate-training-data",
                "-kbIndexPath", "/tmp/tmp.index.1",
                "-mentionsIndexPath", "entity-mentions.index",
                "-property", "http://rdf.freebase.com/ns/people.person.date_of_birth"
        };

        String[] argsQueryMentions = new String[]{
                "-command", "query-mentions",
                "-mentionsIndexPath", "/tmp/entity-mentions.index.3",
                "-query", "/m/011_3d"
        };

        String[] argsQueryKB = new String[]{
                "-command", "query-kb",
                "-kbIndexPath", "/tmp/freebase.index",
                "-query", "http://rdf.freebase.com/ns/m.02mjmr"
        };

        String[] argsQueryKB2 = new String[]{
                "-command", "query-kb",
                "-kbIndexPath", "/tmp/freebase.index",
                "-query", "http://rdf.freebase.com/ns/m.02mjmr",
                "-property", "http://rdf.freebase.com/ns/people.person.date_of_birth"
//                "-property", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
        };


        if (args == null || args.length == 0)
//            args = argsIndexKB;
//            args = argsQueryKB;
            args = argsQueryMentions;

        // Create args
        Args tdArgs = new Args();
        CmdLineParser parser = new CmdLineParser(
                tdArgs,
                ParserProperties.defaults().withUsageWidth(90)
        );
        parser.parseArgument(args);

        // Initialize log with simple conversion pattern
        String logFileName = tdArgs.command + ".log";
        createNewLoggerConfig(TrainingDataGenerator.class.getName(), logFileName, false, PatternLayout.SIMPLE_CONVERSION_PATTERN);
        LOG = LogManager.getLogger(TrainingDataGenerator.class.getName());

        LOG.info("Training Data Generator - Started");

        TrainingDataGenerator tdg = new TrainingDataGenerator(tdArgs);

        // Select an action to perform according to the given command
        switch (tdArgs.command.toLowerCase()) {
            case "index-kb":
                tdg.indexRdfDataset();
                break;

            case "index-mentions":
                tdg.indexEntityMentions();
                break;

            case "generate-training-data":
                tdg.generateTrainingData();
                break;

            case "query-mentions":
                tdg.queryMentions();
                break;

            case "query-kb":
                tdg.queryKB();
                break;

            default:
                LOG.error("Invalid command: ({}). Available commands: {}", tdArgs.command, AVAILABLE_COMMANDS);
                throw new IllegalArgumentException("Invalid command: (" + tdArgs.command + "). Available commands: " + AVAILABLE_COMMANDS);
        }

        LOG.info("Training Data Generator - Finished");
    }
}