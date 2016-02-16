package io.anserini.ltr;

import com.google.common.collect.Sets;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.RerankerContext;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Feature extractor class that forms the base for other feature extractors
 */
abstract public class BaseFeatureExtractor {
    private static final Logger LOG = LogManager.getLogger(BaseFeatureExtractor.class);
    private IndexReader reader;
    private Qrels qrels;
    private Map<String, String> topics;
    private Analyzer queryAnalyzer;

    abstract protected String getIdField();

    abstract protected String getTermVectorField();

    abstract protected FeatureExtractors constructExtractors();

    abstract protected Analyzer getAnalyzer();

    abstract protected Set<String> getFieldsToLoad();

    abstract protected Query parseQuery(String queryText);

    /**
     * Method used to print a line of feature vector to the output file
     * @param out           The output stream
     * @param qid           Qrel Id
     * @param qrel          The Qrel relevance value
     * @param docId         The stored Doc Id
     * @param features      The feature vector in featureNum:value form
     */
    public static void writeFeatureVector(PrintStream out, String qid, int qrel, String docId, float[] features) {
        out.print(qid);
        out.print(" ");
        out.print(qrel);
        out.print(" ");
        out.print(docId);
        for (int i = 0 ; i < features.length; i++) {
            out.print(" ");
            out.print(i);
            out.print(":");
            out.print(features[i]);
        }
        out.print("\n");
    }

    /**
     * Constructor that requires a reader to the index, the qrels and the topics
     * @param reader
     * @param qrels
     * @param topics
     */
    public BaseFeatureExtractor(IndexReader reader, Qrels qrels, Map<String,String> topics) {
        this.reader = reader;
        this.qrels = qrels;
        this.topics = topics;
        this.queryAnalyzer = getAnalyzer();
    }

    // Build all the reranker contexts because they will be reused once per query
    private Map<String, RerankerContext> buildRerankerContextMap() {
        Map<String, RerankerContext> queryContextMap = new HashMap<>();
        IndexSearcher searcher = new IndexSearcher(reader);
        for (String qid : qrels.getQids()) {
            // Construct the reranker context
            LOG.debug(String.format("Constructing context for QID: %s", qid));
            String queryText = topics.get(qid);
            Query q = null;

            // We will not be checking for nulls here because the input should be correct,
            // and if not it signals other issues
            q = parseQuery(queryText);

            // Construct the reranker context
            RerankerContext context = new RerankerContext(searcher, q,
                    qid, queryText,
                    Sets.newHashSet(AnalyzerUtils.tokenize(queryAnalyzer, queryText)),
                    null);

            queryContextMap.put(qid, context);

        }
        return queryContextMap;
    }

    private void printHeader(PrintStream out, FeatureExtractors extractors) {
        out.println("Extracting features with the following feature vector:");
        for (int i = 0; i < extractors.extractors.size(); i++) {
            out.println(String.format("%d:%s", i, extractors.extractors.get(i).getName()));
        }
    }

    /**
     * Iterates through all the documents and print the features for each of the queries
     * This way we are not iterating over the entire index for each query to save disk access
     * @param out
     * @throws IOException
     */
    public void printFeatures(PrintStream out) throws IOException {

        Map<String, RerankerContext> queryContextMap = buildRerankerContextMap();
        FeatureExtractors extractors = constructExtractors();
        Bits liveDocs = MultiFields.getLiveDocs(reader);
        Set<String> fieldsToLoad = getFieldsToLoad();

        this.printHeader(out, extractors);

        for (int docId = 0; docId < reader.maxDoc(); docId ++) {
            // Only check live docs if we have some
            if (reader.hasDeletions() && (liveDocs == null || !liveDocs.get(docId))) {
                LOG.warn(String.format("Document %d not in live docs", docId));
                continue;
            }
            Document doc = reader.document(docId, fieldsToLoad);
            String docIdString = doc.get(getIdField());
            Terms terms = reader.getTermVector(docId, getTermVectorField());

            if (terms == null) {
                continue;
            }

            for (Map.Entry<String, RerankerContext> entry : queryContextMap.entrySet()) {
                float[] featureValues = extractors.extractAll(doc, terms, entry.getValue());
                writeFeatureVector(out, entry.getKey(),qrels.getRelevanceGrade(entry.getKey(),docIdString),
                        docIdString, featureValues);
            }
            out.flush();
            LOG.debug(String.format("Completed computing feature vectors for doc %d", docId));
        }
    }
}
