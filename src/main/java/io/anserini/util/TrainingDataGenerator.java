package io.anserini.util;

import org.apache.commons.io.FileUtils;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Generate training data (positive examples) of a particular
 * relationship from an indexed RDF dataset, e.g., Freebase.
 *
 * The class also helps in indexing new KB RDF and entity mentions
 * TSV files.
 */
public class TrainingDataGenerator {

    private static final Logger LOG = LogManager.getLogger(TrainingDataGenerator.class);

    private static Logger TRAINING_DATA_OUTPUT_FILE = null;

    // Index field names
    // Knowledge Base RDF index
    static final String FIELD_NAME_SUBJECT = "subject";
    static final String FIELD_NAME_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";

    // Properties field names
    static final String FIELD_NAME_BIRTHDATE = "http://rdf.freebase.com/ns/people.person.date_of_birth";
    static final String FIELD_NAME_SPOUSE = "http://rdf.freebase.com/ns/people.person.spouse_s";

    // Entity mentions index
    static final String FIELD_NAME_ENTITY_ID = "entityId";

    // RDF object predicate types
    static final String VALUE_TYPE_URI    = "URI";
    static final String VALUE_TYPE_STRING = "STRING";
    static final String VALUE_TYPE_TEXT   = "TEXT";
    static final String VALUE_TYPE_OTHER  = "OTHER";

    /**
     * List of commands that can be executed
     */
    static final String AVAILABLE_COMMANDS = "(index-kb, index-mentions, generate-training-data)";

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

        @Option(name = "-entityIdColNum", metaVar = "[Entity ID Column Number]", required = false, usage = "The column number (zero-based) in the TSV file that contains the entity id")
        int entityIdColNum;

        @Option(name = "-entityLabelColNum", metaVar = "[Entity Label Column Number]", required = false, usage = "The column number (zero-based) in the TSV file that contains the entity label as mentioned in the text, or -1 if no label in the training file")
        int entityLabelColNum;

        @Option(name = "-docIdColNum", metaVar = "[Document ID Column Number]", required = false, usage = "The column number (zero-based) in the TSV file that contains the column id")
        int docIdColNum;

        @Option(name = "-predicatesToIndex",
                handler = StringArrayOptionHandler.class, // Allows for multiple args
                metaVar = "[KB Predicates to Index, separated by Space]",
                required = false,
                usage = "List of KB predicates that will be indexed")
        List<String> predicatesToIndex;

        @Option(name = "-maxNumTriplesToIndex",
                metaVar = "[Maximum number of triples to index]",
                required = false,
                usage = "Integer representing the maximum number of triples to index, or -1 to index everything")
        long maxNumTriplesToIndex = -1;
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
     * Simple value factory to parse literals
     */
    static ValueFactory valueFactory = SimpleValueFactory.getInstance();

    public TrainingDataGenerator(Args args) throws Exception {
        this.args = args;

        // Create a logger to write the training data
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        if (args.outputFilePath != null) {
            Layout layout = PatternLayout
                    .createLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN,
                            config,
                            null,
                            StandardCharsets.UTF_8,
                            false,
                            false,
                            null,
                            null
                    );

            Appender appender = FileAppender
                    .createAppender(args.outputFilePath,
                            "false",
                            "false",
                            "prop",
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
            AppenderRef ref = AppenderRef.createAppenderRef("prop", null, null);
            AppenderRef[] refs = new AppenderRef[] {ref};
            LoggerConfig loggerConfig = LoggerConfig.createLogger(
                    "false",
                    Level.TRACE,
                    "prop",
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
            config.addLogger("prop", loggerConfig);
            ctx.updateLoggers();

            TRAINING_DATA_OUTPUT_FILE = LogManager.getLogger("prop");
        }
    }

    /**
     * Reference index directories
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
     *
     * @return Lucene index reader from the path specified in
     * TrainingDataGenerator{@link #args}
     *
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
            IndexWriterConfig iwc = new IndexWriterConfig(getKbIndexAnalyzer());

            // Create a new index or append
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // We always overwrite KB index

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
            kbIndexAnalyzer = (Analyzer)Class.forName(defaultAnalyzerName).newInstance();
        }
        return kbIndexAnalyzer;
    }

    /**
     *
     * @return Entity mentions index reader
     * @throws IOException on error
     */
    IndexReader getMentionsIndexReader() throws IOException {
        if (mentionsIndexReader == null) {
//            if (mentionsIndexWriter != null) {
//                // If someone is writing to the index, we need to keep track
//                // of the committed changes, so we set mentionsIndexReader to reference
//                // the index writer, so that changes are reflected
//                // See {@link https://lucene.apache.org/core/6_4_1/core/org/apache/lucene/index/IndexWriter.html#commit--}
//                mentionsIndexReader = DirectoryReader.open(mentionsIndexWriter, true, true);
//            } else {
                mentionsIndexReader = DirectoryReader.open(mentionsIndexDirectory);
//            }
        }

        return mentionsIndexReader;
    }

    /**
     *
     * @return entity mentions index searcher
     * @throws IOException on error
     */
    IndexSearcher getMentionsIndexSearcher() throws IOException {

        // If we made some changes in the index, we need to re-open the index
        if (mentionsIndexWriter != null && mentionsIndexWriter.hasUncommittedChanges()) {
            mentionsIndexWriter.commit();
            mentionsIndexWriter.flush();
            mentionsIndexReader = DirectoryReader.open(mentionsIndexWriter);
            mentionsIndexSearcher = new IndexSearcher(mentionsIndexReader);
        }

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
     *
     * @return Index writer for entity mentions index.
     * @throws Exception on error
     */
    IndexWriter getMentionsIndexWriter() throws Exception {
        if (mentionsIndexWriter == null || !mentionsIndexWriter.isOpen()) {
            IndexWriterConfig iwc = new IndexWriterConfig(getMentionsIndexAnalyzer());

            // Create a new index or append
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            mentionsIndexWriter = new IndexWriter(mentionsIndexDirectory, iwc);
        }

        return mentionsIndexWriter;
    }

    /**
     * Retrieve a document id given the subject URI
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
     * Retrieve an entity mention document id given the entity Id.
     *
     * @param entityId the entity id to retrieve
     * @return the document id, or -1 if the subject doesn't exist
     * @throws IOException on error
     */
    int getEntityMentionDocID(String entityId) throws IOException {
        TermQuery query = new TermQuery(new Term(FIELD_NAME_ENTITY_ID, entityId));
        TopDocs result = getMentionsIndexSearcher().search(query, 1);
        if (result.totalHits == 0)
            return -1;
        else
            return result.scoreDocs[0].doc;
    }

    /**
     * Retrieve the document identified by `subjectURI',
     * return null if nothing was found
     */
    public Document getSubjectDoc(String subjectURI) throws IOException {
        int subjectId = getSubjectDocID(subjectURI);
        return (subjectId < 0) ? null : getKbIndexReader().document(subjectId);
    }

    /**
     * Return the value of predicate `predName' on `subjectDoc'.  If there are muliple values,
     * return the first one indexed, if there are none, return null.
     */
    public String getSubjectPredicateValue(Document subjectDoc, String predName) throws IOException {
        return subjectDoc.get(predName);
    }

    /**
     * Return the values of predicate `predName' on `subjectDoc' in the order they were indexed.
     * If there are none, return an empty array.
     */
    public String[] getSubjectPredicateValues(Document subjectDoc, String predName) throws IOException {
        return subjectDoc.getValues(predName);
    }

    /**
     * Looks up a particular freebase URI
     * @param freebaseUri the freebase URI that represents the subject id
     * @return the document that matches the freebaseUri, or null if it doesn't exist
     * @throws IOException on error
     */
    Document getDocBySubjectId(String freebaseUri) throws IOException {
        Document ret = null;

        int docid = getSubjectDocID(freebaseUri);
        if (docid < 0) {
            LOG.debug("Could not find document id for subject: {}", freebaseUri);
        } else {
            Document doc = getKbIndexReader().document(docid);
            ret = doc;
//            for (IndexableField field : doc.getFields())
        }

        return ret;
    }

    /**
     * Looks up a particular entity id
     * @param entityId entity id that we want to retrieve the mentions for
     * @return the document that matches the entity id
     * @throws IOException on error
     */
    Document getEntityMentionByEntityId(String entityId) throws IOException {
        Document ret = null;

        int docId = getEntityMentionDocID(entityId);
        if (docId < 0) {
            LOG.debug("Couldn't find entity mention document for entity: {}", entityId);
        } else {
            Document doc = getMentionsIndexReader().document(docId);
            ret = doc;
        }

        return ret;
    }

    /**
     * Helper function.
     *
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
     *
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
     */
    void birthdate() throws Exception {
        QueryParser queryParser = new QueryParser(
                FIELD_NAME_BIRTHDATE
                , getKbIndexAnalyzer());
        queryParser.setAllowLeadingWildcard(true);

        Query q = queryParser.parse("*");

        LOG.info("Query: {}", q.toString());

        LOG.info("Searching...");
        getKbIndexSearcher().search(q, new SimpleCollector() {
            @Override
            public void collect(int docid) throws IOException {
                Document doc = getKbIndexReader().document(docid);

                String freebaseURI = doc.get(FIELD_NAME_SUBJECT);
                String birthdate = doc.get(FIELD_NAME_BIRTHDATE);
                String label = doc.get(FIELD_NAME_LABEL);

                // Basically make sure label is not null, for some entities in freebase
                if (label == null || freebaseURI == null || birthdate == null)
                    return; // Ignore this search

                String freebaseId = freebaseUriToFreebaseId(freebaseURI);

                String labelVal = extractValueFromTypedLiteralString(label);
                String birthdateVal = extractValueFromTypedLiteralString(birthdate);

                writeToTrainingFile(freebaseId, labelVal, birthdateVal);
            }

            @Override
            public boolean needsScores() {
                return false;
            }
        });
    }

    private static final String TRAINING_DATA_SEPARATOR = "\t";

    /**
     * Writes an array as CSV line separated by TRAINING_DATA_SEPARATOR
     * @param data array of data
     */
    void writeToTrainingFile(String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (i>0)
                sb.append(TRAINING_DATA_SEPARATOR);

            if (data[i].contains(TRAINING_DATA_SEPARATOR))
                sb.append("\"").append(data[i]).append("\"");
            else
                sb.append(data[i]);
        }

        TRAINING_DATA_OUTPUT_FILE.info(sb.toString());
    }

    /**
     * Given an RDF dataset, this function builds a Lucene index on the RDF triples,
     * storing each subject as a document id, predicates as (indexable) fields,
     * and predicate values as values of the fields.
     *
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

        initializeIndexes();
        getKbIndexWriter();
        getKbIndexWriter().commit();
        getKbIndexReader();

        // Temp Map to hold the predicate values for a single subject
        Map<String, List<String>> predValues = new TreeMap<String, List<String>>();
        int count = 0;
        String currentSubject = "";

        File dataPathDirectory = new File(args.dataPath);
        Iterator<File> filesIterator = null;
        // If the provided path was a directory, iterate over all of its files and sub-directories,
        // otherwise, use the single file
        if (dataPathDirectory.isDirectory()) {
            filesIterator = FileUtils.iterateFiles(dataPathDirectory, null, true);
        } else {
            filesIterator = Arrays.asList(new File[]{dataPathDirectory}).iterator();
        }

        boolean limitReached = false;
        while (filesIterator.hasNext()) {

            if (limitReached)
                break;

            File triplesFile = filesIterator.next();
            InputStream is = null;
            Scanner triplesScanner = null;

            try {
                is = new FileInputStream(triplesFile);
                if (triplesFile.getAbsolutePath().toLowerCase().endsWith(".gz"))
                    is = new GZIPInputStream(is);
                triplesScanner = new Scanner(is);
                while (triplesScanner.hasNextLine()) {
                    String line = triplesScanner.nextLine();
                    count++;

                    // Show progress
                    if (count % 100000 == 0) {
                        LOG.info("Processed {} lines", count);
                    }

                    if (line.startsWith("#")) // Ignore comments
                        continue;

                    // Check line limit
                    if (args.maxNumTriplesToIndex > 0 && count >= args.maxNumTriplesToIndex) {
                        LOG.info("Reached maximum limit of triples to index... Writing current data and stopping the index process");
                        limitReached = true;
                    }

                    // Tokenize line pieces using tabs
                    String[] triple = line.split("\t");
                    if (triple.length != 4) {
                        LOG.warn("Ignoring invalid NT triple line: {}", line);
                        continue;
                    }

                    if (!triple[0].equals(currentSubject) || limitReached) {
                        if (!"".equals(currentSubject)) {
                            // New subject found, index the previous subject and its value
                            indexSubjectValues(currentSubject, predValues);
                        }

                        if (limitReached)
                            throw new RuntimeException("Processing limit reached, stopping");

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

                if (triplesScanner != null) {
                    triplesScanner.close();
                }

                LOG.info("Indexing process completed.");
            }
        }
    }

    void indexSubjectValues(String subject, Map<String, List<String>> predicatesAndValues) throws Exception {
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
                    if (valueType.equals(VALUE_TYPE_URI))
                        // Always index URIs
                        doc.add(new StringField(predicate, value, Field.Store.YES));
                    else
                        doc.add(new TextField(predicate, value, Field.Store.YES));
                }
                else {
                    // Just add the predicate as a stored field, no index on it
                    doc.add(new StoredField(predicate, value));
                }
            }
        }

        getKbIndexWriter().addDocument(doc);
    }

    /**
     * Check if the predicate should be indexed
     * @param predicate the predicate to check
     * @return true if the user specified it to be indexed, false otherwise.
     */
    boolean isIndexedPredicate(String predicate) {
        return args.predicatesToIndex != null && args.predicatesToIndex.contains(predicate);
    }

    String getValueType(String value) {
        // Determine the type of this N-Triples `value'.
        char first = value.charAt(0);
        switch (first) {
            case '<': return VALUE_TYPE_URI;
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
     * Unescape strings
     */
    public String normalizeTextValue(String value) {
        return NTriplesUtil.unescapeString(value);
    }

    public String normalizeValue(String value) {
        // Normalize a `value' depending on its type.
        String type = getValueType(value);
        if (type == VALUE_TYPE_URI)
            return cleanUri(value);
        else if (type == VALUE_TYPE_STRING)
            return normalizeStringValue(value);
        else if (type == VALUE_TYPE_TEXT)
            return normalizeTextValue(value);
        else
            return value;
    }

    /**
     *
     * Removes '<', '>' if they exist, lower case
     *
     * TODO - replace ':' with '_' because query parser doesn't like it
     *
     * @param uri
     * @return
     */
    public String cleanUri(String uri) {
        // We want lower-case namespace IDs and no `:'s, since those make the query field parser unhappy.
        // That way we can freely mix text queries with exact field restrictions such as type names.
        // This is now done mostly by the preprocessing steps, and we only have to strip off angle brackets.
        if (uri.charAt(0) == '<')
            return uri.substring(1, uri.length() - 1).toLowerCase();
        else
            return uri;
    }

    /**
     * This function indexes entity mentions file.
     * The entity mentions file contains mentions of each entity id and the documents
     * in which they appear.
     * This is similar to ClueWeb09FACC.
     *
     * The file is expected to be a TSV file.
     */
    void indexEntityMentions() throws Exception {

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
        getMentionsIndexWriter().commit(); // need to commit in case the index was just created
        getMentionsIndexReader();

        // Get the files in the directory that contains the mentions files
        File dataPathDirectory = new File(args.dataPath);
        Iterator<File> filesIterator = FileUtils.iterateFiles(dataPathDirectory, new String[]{"tsv", "TSV"}, true);
        while (filesIterator.hasNext()) {
            File mentionsFile = filesIterator.next();
            try {
                // Use a scanner in case the files are large
                Scanner mentionsScanner = new Scanner(mentionsFile);
                while (mentionsScanner.hasNextLine()) {
                    // Split line into pieces
                    String line = mentionsScanner.nextLine();

                    if (line.startsWith("#"))
                        continue;

                    String[] linePieces = line.split("\t");

                    // Extract different information from
                    String entityId = linePieces[args.entityIdColNum];
                    String docId = linePieces[args.docIdColNum];
                    String entityLabel = args.entityLabelColNum == -1? null : linePieces[args.entityLabelColNum];

                    try {
                        indexEntityMentionRecord(entityId, entityLabel, docId);
                    } catch (Exception e) {
                        LOG.error("Error indexing entity mention for entity: ({}) in document: ({})",
                                entityId,
                                docId,
                                e);
                    }
                }
            } catch (FileNotFoundException e) {
                LOG.error("Error reading mentions file: {}", mentionsFile, e);
            }
        }

        cleanup();
    }

    /**
     * Adds an entity mention to the entity mention index.
     * 1. Checks if there is a document in the index with docid = entityId
     * 2. If not, add a new document to the index with the following fields:
     *      - Document id: entityId
     *      - Stored Field 1: docId
     *      - Field value: entityLabel (if not null)
     *
     * 3. If a document exists, incrementally index another field with the new doc id,
     *    even if it is mentioned in the same document multiple times,
     *    unless it is mentioned with the same label.
     *
     * @param entityId the Id of the entity to index
     * @param entityLabel the label of the entity, how it appears in THIS document
     * @param docId the id of the text document (e.g., ClueWeb) in which it appears
     */
    void indexEntityMentionRecord(String entityId, String entityLabel, String docId) throws Exception {
        Document indexedDocument = getEntityMentionByEntityId(entityId);

        String newLabelValue = (entityLabel == null?  "null" : entityLabel);
        boolean shouldAddLabel = (indexedDocument == null || Arrays.asList(indexedDocument.getValues(docId)).indexOf(newLabelValue) == -1);
        if (!shouldAddLabel)
            return;

        // Create a document to add to the index as a new document or updating an older document
        Document newDoc = new Document();

        // Document fields. entityId is StringField to be searchable
        Field entityIdField = new StringField(FIELD_NAME_ENTITY_ID, entityId, Field.Store.YES);

        // The new entity label to be added
        Field entityDocAndLabelField = new StoredField(docId, newLabelValue);

        newDoc.add(entityIdField);
        newDoc.add(entityDocAndLabelField);

        // Add a new document or update the existing one
        if (indexedDocument == null) {
            getMentionsIndexWriter().addDocument(newDoc);
        } else {

            // Add fields from the other indexed document (the fields correspond to other mentions)
            for (IndexableField indexableField : indexedDocument.getFields()) {
                // Ignore entity Id field
                if (indexableField.name().equals(FIELD_NAME_ENTITY_ID))
                    continue;

                newDoc.add(indexableField);
            }

            // Update the document
            getMentionsIndexWriter().updateDocument(new Term(FIELD_NAME_ENTITY_ID, entityId), newDoc);
        }
    }

    void cleanup() throws Exception {
        if (mentionsIndexWriter != null && mentionsIndexWriter.isOpen()) {
            mentionsIndexWriter.close();
            mentionsIndexWriter = null;
        }

        if (kbIndexWriter != null && kbIndexWriter.isOpen()) {
            kbIndexWriter.close();
            kbIndexWriter = null;
        }
    }

    public static void main(String[] args) throws Exception {
        // For testing
        if (args == null || args.length == 0)
            args = new String[] {
                    "-command", "index-mentions",
                    "-mentionsIndexPath", "entity-mentions.index",
                    "-dataPath", "entity-mentions",
                    "-entityIdColNum", "7",
                    "-entityLabelColNum", "2",
                    "-docIdColNum", "0"
            };

            /*
            args = new String[] {
                    "-command", "index-kb",
                    "-kbIndexPath", "/tmp/tmp.index.1",
                    "-dataPath", "/Users/mfathy/Downloads/freebase-rdf-latest.gz",
                    "-predicatesToIndex", "http://rdf.freebase.com/ns/people.person.date_of_birth"
                    ,"-maxNumTriplesToIndex", "20000000"
            };
            */

            /*
            args = new String[] {
                    "-command", "generate-training-data",
                    "-kbIndexPath", "/tmp/tmp.index.1",
                    "-mentionsIndexPath", "entity-mentions.index",
                    "-property", "http://rdf.freebase.com/ns/people.person.date_of_birth"
            };
            */

        Args tdArgs = new Args();
        CmdLineParser parser = new CmdLineParser(
                tdArgs,
                ParserProperties.defaults().withUsageWidth(90)
        );
        parser.parseArgument(args);

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

            default:
                LOG.error("Invalid command: ({}). Available commands: {}", tdArgs.command, AVAILABLE_COMMANDS);
                throw new IllegalArgumentException("Invalid command: (" + tdArgs.command + "). Available commands: " + AVAILABLE_COMMANDS);
        }
    }
}