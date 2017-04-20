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
import org.apache.lucene.codecs.lucene60.Lucene60Codec;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Generate training data (positive examples) of a particular
 * relationship from an indexed RDF dataset, e.g., Freebase.
 *
 * This class relies on an index that should be prebuilt
 * using
 *
 *
 */
public class TrainingDataGenerator {

    private static final Logger LOG = LogManager.getLogger(TrainingDataGenerator.class);

    private static Logger TRAINING_DATA_OUTPUT_FILE = null;

    // Index field names
    // Knowledge Base RDF index
    static final String FIELD_NAME_SUBJECT = "subject";
    static final String FIELD_NAME_TEXT    = "text";

    // Properties field names
    static final String FIELD_NAME_BIRTHDATE = "http://rdf.freebase.com/ns/people.person.date_of_birth";

    // Entity mentions index
    static final String FIELD_NAME_ENTITY_ID = "entityId";

    /**
     * The arguments that this program accepts
     */
    public static final class Args {
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
            mentionsIndexReader = DirectoryReader.open(mentionsIndexDirectory);
        }

        return mentionsIndexReader;
    }

    /**
     *
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
     *
     * @return Index writer for entity mentions index.
     * @throws Exception on error
     */
    IndexWriter getMentionsIndexWriter() throws Exception {
        if (mentionsIndexWriter == null) {
            IndexWriterConfig iwc = new IndexWriterConfig(getMentionsIndexAnalyzer());

            // Create a new index or append
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//            iwc.setCodec(new Lucene60Codec());

            mentionsIndexWriter = new IndexWriter(mentionsIndexDirectory, iwc);
        } else if (!mentionsIndexWriter.isOpen()) {
            IndexWriterConfig iwc = new IndexWriterConfig(getMentionsIndexAnalyzer());

            // Create a new index or append
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            iwc.setCodec(new Lucene60Codec());
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
        return literalString.substring(literalString.indexOf('\"') + 1, literalString.lastIndexOf("\""));
    }

    /**
     * Determine which properties to retrieve and runs the training data retrieval pipeline.
     *
     * @throws Exception on error
     */
    private void generateTrainingData() throws Exception {
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

        LOG.info("Query");
        LOG.info(q);

        LOG.info("Searching...");
        getKbIndexSearcher().search(q, new SimpleCollector() {
            @Override
            public void collect(int docid) throws IOException {
                Document doc = getKbIndexReader().document(docid);

                String freebaseURI = doc.get(FIELD_NAME_SUBJECT);
                String birthdate = doc.get(FIELD_NAME_BIRTHDATE);
                String label = doc.get("http://www.w3.org/2000/01/rdf-schema#label");

                // Basically make sure label is not null, for some entities in freebase
                if (label == null || freebaseURI == null || birthdate == null)
                    return;

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
    void indexRdfDataset() {
        throw new NotImplementedException();
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
        initializeIndexes();
        getMentionsIndexWriter();
        getMentionsIndexWriter().commit();
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
                    String[] linePieces = line.split("\t");

                    // Extract different information from
                    String entityId = linePieces[args.entityIdColNum];
                    String docId = linePieces[args.docIdColNum];
                    String entityLabel = args.entityLabelColNum == -1? null : linePieces[args.entityLabelColNum];

                    try {
                        indexEntityMention(entityId, entityLabel, docId);
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
    void indexEntityMention(String entityId, String entityLabel, String docId) throws Exception {
        Document indexedDocument = getEntityMentionByEntityId(entityId);

        String labelValue = (entityLabel == null?  "null" : entityLabel);
        Field entityDocAndLabelField = new StringField(docId, labelValue, Field.Store.YES);

        if (indexedDocument == null) {
            // Create a new document to add to the index
            Document doc = new Document();

            Field entityIdField = new StringField(FIELD_NAME_ENTITY_ID, entityId, Field.Store.YES);

            doc.add(entityIdField);
            doc.add(entityDocAndLabelField);

            getMentionsIndexWriter().addDocument(doc);
        } else {
            String labelForDoc = indexedDocument.get(docId);
            if (labelForDoc == null) {
                // First time this entity appears in this docId
                getMentionsIndexWriter().updateDocValues(new Term(FIELD_NAME_ENTITY_ID, entityId), entityDocAndLabelField);
            } else if (entityLabel != null && !labelForDoc.equals(labelValue)) {
                // Entity already indexed with the same document but with a different label, we still add it
                getMentionsIndexWriter().updateDocValues(new Term(FIELD_NAME_ENTITY_ID, entityId), entityDocAndLabelField);
            } else if (labelForDoc.equals("null") && entityLabel == null) {
                // Entity already recorded to appear in the document with no label information
                // Do nothing.
            }
        }

        // Order of operations matters, probably
        getMentionsIndexWriter().flush();
        getMentionsIndexWriter().commit();
    }

    public static void main(String[] args) throws Exception {
        args = new String[] {
                "-mentionsIndexPath", "entity-mentions.index",
                "-dataPath", "entity-mentions",
                "-entityIdColNum", "7",
                "-entityLabelColNum", "2",
                "-docIdColNum", "0"
        };

        Args tdArgs = new Args();
        CmdLineParser parser = new CmdLineParser(
                tdArgs,
                ParserProperties.defaults().withUsageWidth(90)
        );
        parser.parseArgument(args);

        new TrainingDataGenerator(tdArgs).indexEntityMentions();
    }
}