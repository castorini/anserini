package io.anserini.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.BurstFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.spi.ExtendedLogger;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

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

    private final Logger TRAINING_DATA_OUTPUT_FILE;

    // Index field names
    public static final String FIELD_NAME_SUBJECT = "subject";
    public static final String FIELD_NAME_TEXT    = "text";


    public static final class Args {
        @Option(name = "-indexPath", metaVar = "[Index path]", required = true, usage = "Directory contains index files")
        String indexPath;

        @Option(name = "-outputFile", metaVar = "[Output file path]", required = false, usage = "Output file to write training data to")
        String outputFilepath;

        @Option(name = "-property", metaVar = "[Property name]", required = true, usage = "The property to generate training data for." +
                "Currently support: (birthdate)")
        String propertyName;
    }


    /**
     * Args instance
     */
    private final Args args;

    /**
     * The directory in which the RDF dataset index is stored
     */
    Directory indexDirectory = null;

    /**
     * Indexed knowledge base/RDF dataset to retreive
     * the training examples from
     */
    IndexReader indexReader = null;

    /**
     * Index searcher to search in the index
     */
    IndexSearcher indexSearcher = null;


    /**
     * Index analyzer to search
     */
    Analyzer indexAnalyzer;

    // Properties
    public static final String FIELD_NAME_BIRTHDATE = "http://rdf.freebase.com/ns/people.person.date_of_birth";

    public TrainingDataGenerator(Args args) throws Exception {
        this.args = args;



        final LoggerContext ctx = (LoggerContext) new org.apache.logging.log4j.core.LoggerContext("console" );
        final Configuration config = ((org.apache.logging.log4j.core.LoggerContext) ctx).getConfiguration();

        //ConsoleAppender console = ConsoleAppender.createAppender(PatternLayout.createDefaultLayout(), null, "SYSTEM_OUT", "console", null, null);
        FileAppender console = FileAppender.createAppender(args.outputFilepath,
                "true",
                null,
                "console",
                "true",
                null,
                null,
                null,
                PatternLayout.createDefaultLayout(),
                null,
                null,
                null,
                config
                );
        console.start();


        config.addAppender(console);
        AppenderRef[] refs = new AppenderRef[] { AppenderRef.createAppenderRef(console.getName(), null, null) };
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.ALL, LogManager.ROOT_LOGGER_NAME, "true", refs, null, config, null);
        loggerConfig.addAppender(console, null, null);
        config.addLogger(LogManager.ROOT_LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();



//        console.start();
//        config.addAppender(console);
//        AppenderRef ref = AppenderRef.createAppenderRef("console", null, null);
//        AppenderRef[] refs = new AppenderRef[] {ref};
//        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.ALL, "org.apache.logging.log4j",
//                "true", refs, null, config, null );
//        loggerConfig.addAppender(console, null, null);
//        config.addLogger("org.apache.logging.log4j", loggerConfig);
//        ((org.apache.logging.log4j.core.LoggerContext) ctx).updateLoggers();
//        ExtendedLogger logger = (ExtendedLogger) ctx.getLogger("console");
//        logger.error("abc");

        Logger logger = LogManager.getLogger("console");
        logger.trace("Hello Word!");


        logger.exit();



//        LOG.info("Initializing index from path: {}", args.indexPath);
//        initializeIndex(args.indexPath);

        TRAINING_DATA_OUTPUT_FILE = LogManager.getLogger("TrainingDataFile");

    }

    public void initializeIndex(String indexPath) throws IOException {
        if (indexDirectory == null)
            indexDirectory = FSDirectory.open(Paths.get(indexPath));
    }

    /**
     *
     * @return Lucene index reader from the path specified in
     * TrainingDataGenerator{@link #args}
     *
     * @throws IOException
     */
    public IndexReader getIndexReader() throws IOException {
        if (indexReader == null) {
            indexReader = DirectoryReader.open(indexDirectory);
        }

        return indexReader;
    }

    public IndexSearcher getIndexSearcher() throws IOException {
        if (indexSearcher == null) {
            indexSearcher = new IndexSearcher(getIndexReader());
        }

        return indexSearcher;
    }

    /**
     * Retrieve a document id given the subject URI
     * @param subjectURI the URI of the subject to retrieve
     * @return the document id, or -1 if the subject doesn't exist
     * @throws Exception
     */
    int getSubjectDocID(String subjectURI) throws IOException {
        TermQuery query = new TermQuery(new Term(FIELD_NAME_SUBJECT, subjectURI));
        TopDocs result = getIndexSearcher().search(query, 1);
        if (result.totalHits == 0)
            return -1;
        else {
            return result.scoreDocs[0].doc;
        }
    }

    /**
     * Retrieve the document identified by `subjectURI',
     * return null if nothing was found
     */
    public Document getSubjectDoc(String subjectURI) throws IOException {
        int subjectId = getSubjectDocID(subjectURI);
        return (subjectId < 0) ? null : getIndexReader().document(subjectId);
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


    public Analyzer getIndexAnalyzer() throws Exception {
        // Get the current default index analyzer or create it.
        // Use the type specified by LUCENE_INDEX_ANALYZER_DEFAULT
        // or use StandardAnalyzer as the default.
        if (indexAnalyzer == null) {
            String defaultAnalyzerName = "org.apache.lucene.analysis.standard.StandardAnalyzer";
            indexAnalyzer = (Analyzer)Class.forName(defaultAnalyzerName).newInstance();
        }
        return indexAnalyzer;
    }

    void lookup() throws IOException {
        LOG.info("Loading index...");
        getIndexReader();
        PrintWriter out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8))
        );


        String[] querySubjectIds = new String[]{
                "http://rdf.freebase.com/ns/m.02mjmr" // Barack Obama
                ,
                "http://rdf.freebase.com/ns/m.02_fj" // Frank Sinatra
        };


        for (String querySubjectId : querySubjectIds) {
            int docid = getSubjectDocID(querySubjectId);
            if (docid < 0) {
                LOG.info("Could not find document id for subject: {}", querySubjectId);
            } else {
                Document doc = getIndexReader().document(docid);
                for (IndexableField field : doc.getFields()) {
                    if (! FIELD_NAME_SUBJECT.equals(field.name()))
                        out.println("    " + field.name() + ": " + field.stringValue());
                }
            }
        }

        LOG.info("Lookup complete.");
    }

    private void generateTrainingData() throws Exception {
        switch (args.propertyName.toLowerCase()) {
            case "birthdate":
                birthdate();
                break;
            default:
                LOG.error("Cannot generate training data for property: {}", args.propertyName);
                throw new IllegalArgumentException("Cannot generate training data for property: " + args.propertyName);
        }
    }

    void birthdate() throws Exception {
        QueryParser queryParser = new QueryParser(
                FIELD_NAME_BIRTHDATE
                , getIndexAnalyzer());
        queryParser.setAllowLeadingWildcard(true);

        Query q = queryParser.parse("*");

        LOG.info("Query");
        LOG.info(q);

        getIndexSearcher().search(q, new SimpleCollector() {
            @Override
            public void collect(int docid) throws IOException {
                Document doc = getIndexReader().document(docid);

                String freebaseURI = doc.get(FIELD_NAME_SUBJECT);
                String birthdate = doc.get(FIELD_NAME_BIRTHDATE);
                String label = doc.get("http://www.w3.org/2000/01/rdf-schema#label");

                LOG.info("   Subject: {}", freebaseURI);
                LOG.info("   Birthdate: {}", birthdate);
                LOG.info("   Label: {}", label);
            }

            @Override
            public boolean needsScores() {
                return false;
            }
        });
    }

    public static void main(String[] args) throws Exception {
        Args tdArgs = new Args();
        CmdLineParser parser = new CmdLineParser(
                tdArgs,
                ParserProperties.defaults().withUsageWidth(90)
        );
        parser.parseArgument(args);

        new TrainingDataGenerator(tdArgs).generateTrainingData();
    }
}
