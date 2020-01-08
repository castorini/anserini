/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.ltr;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.RerankerContext;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiBits;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Feature extractor class that forms the base for other feature extractors
 */
abstract public class BaseFeatureExtractor<K> {
    private static final Logger LOG = LogManager.getLogger(BaseFeatureExtractor.class);
    private IndexReader reader;
    private Qrels qrels;
    private Map<K, Map<String, String>> topics;
    private Analyzer queryAnalyzer;
    private final FeatureExtractors customFeatureExtractors;

    abstract protected String getIdField();

    abstract protected String getTermVectorField();

    protected FeatureExtractors getExtractors() {
      return this.customFeatureExtractors;
    }

    abstract protected Analyzer getAnalyzer();

    abstract protected Set<String> getFieldsToLoad();

    abstract protected Query parseQuery(String queryText);

    abstract protected Query docIdQuery(String docId);

    public static<K> String constructOutputString(K qid, int qrel, String docId, float[] features) {
      StringBuilder sb = new StringBuilder();
      sb.append(qrel);
      sb.append(" ");
      sb.append("qid:");
      sb.append(qid);
      for (int i = 0 ; i < features.length; i++) {
        sb.append(" ");
        sb.append(i+1);
        sb.append(":");
        sb.append(features[i]);
      }
      sb.append(" # ");
      sb.append(docId);
      return sb.toString();
    }
    /**
     * Method used to print a line of feature vector to the output file
     * @param out           The output stream
     * @param qid           Qrel Id
     * @param qrel          The Qrel relevance value
     * @param docId         The stored Doc Id
     * @param features      The feature vector in featureNum:value form
     */
    public static<K> void writeFeatureVector(PrintStream out, K qid, int qrel, String docId, float[] features) {
        out.print(constructOutputString(qid, qrel, docId,features));
        out.print("\n");
    }

  /**
   * Factory method that will take the usual parameters for making a Web or Twitter feature extractor
   * and a definition file. Will parse the definition file to build the FeatureExtractor chain
   * @param reader
   * @param qrels
   * @param topics
   * @param definitionFile
   * @return
   */
    static BaseFeatureExtractor parseExtractorsFromFile(IndexReader reader, Qrels qrels,
                                Map<String, Map<String, String>> topics, String definitionFile) {

      return null;
    }

    /**
     * Constructor that requires a reader to the index, the qrels and the topics
     * @param reader
     * @param qrels
     * @param topics
     */
    public BaseFeatureExtractor(IndexReader reader, Qrels qrels, Map<K, Map<String,String>> topics,
                                FeatureExtractors extractors) {
        this.reader = reader;
        this.qrels = qrels;
        this.topics = topics;
        this.queryAnalyzer = getAnalyzer();
        this.customFeatureExtractors = extractors;
    }

    // Build all the reranker contexts because they will be reused once per query
    @SuppressWarnings("unchecked")
    private Map<String, RerankerContext<K>> buildRerankerContextMap() throws IOException {
        Map<String, RerankerContext<K>> queryContextMap = new HashMap<>();
        IndexSearcher searcher = new IndexSearcher(reader);

        for (String qid : qrels.getQids()) {
            // Construct the reranker context
            LOG.debug(String.format("Constructing context for QID: %s", qid));
            String queryText = topics.get(Integer.parseInt(qid)).get("title");
            Query q = null;

            // We will not be checking for nulls here because the input should be correct,
            // and if not it signals other issues
            q = parseQuery(queryText);
            List<String> queryTokens = AnalyzerUtils.tokenize(queryAnalyzer, queryText);
            // Construct the reranker context
            RerankerContext<K> context = new RerankerContext<>(searcher, (K)qid,
                    q, null, queryText,
                    queryTokens,
                    null, null);

            queryContextMap.put(qid, context);

        }
        LOG.debug("Completed constructing context for all qrels");
        return queryContextMap;
    }

    private void printHeader(PrintStream out, FeatureExtractors extractors) {
        out.println("#Extracting features with the following feature vector:");
        for (int i = 0; i < extractors.extractors.size(); i++) {
            out.println(String.format("#%d:%s", i +1, extractors.extractors.get(i).getName()));
        }
    }

   /**
   * Iterates through all the documents and print the features for each of the queries
   * This way we are not iterating over the entire index for each query to save disk access
   * @param out
   * @throws IOException
   */
    public void printFeatureForAllDocs(PrintStream out) throws IOException {
      Map<String, RerankerContext<K>> queryContextMap = buildRerankerContextMap();
      FeatureExtractors extractors = getExtractors();
      Bits liveDocs = MultiBits.getLiveDocs(reader);
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
        // NOTE doc frequencies should not be retrieved from here, term vector returned is as if on single document
        // index
        Terms terms = MultiTerms.getTerms(reader, getTermVectorField());//reader.getTermVector(docId, getTermVectorField());

        if (terms == null) {
          continue;
        }

        for (Map.Entry<String, RerankerContext<K>> entry : queryContextMap.entrySet()) {
          float[] featureValues = extractors.extractAll(doc, terms, entry.getValue());
          writeFeatureVector(out, entry.getKey(),qrels.getRelevanceGrade(entry.getKey(),docIdString),
                  docIdString, featureValues);
        }
        out.flush();
        LOG.debug(String.format("Completed computing feature vectors for doc %d", docId));
      }
    }

    /**
     * Prints feature vectors wrt to the qrels, one vector per qrel
     * @param out
     * @throws IOException
     */
    public void printFeatures(PrintStream out) throws IOException {
      Map<String, RerankerContext<K>> queryContextMap = buildRerankerContextMap();
      FeatureExtractors extractors = getExtractors();
      Bits liveDocs = MultiBits.getLiveDocs(reader);
      Set<String> fieldsToLoad = getFieldsToLoad();

      // We need to open a searcher
      IndexSearcher searcher = new IndexSearcher(reader);

      this.printHeader(out, extractors);
      // Iterate through all the qrels and for each document id we have for them
      LOG.debug("Processing queries");

      for (String qid : this.qrels.getQids()) {
        LOG.debug(String.format("Processing qid: %s", qid));
        // Get the map of documents
        RerankerContext context = queryContextMap.get(qid);

        for (Map.Entry<String, Integer> entry : this.qrels.getDocMap(qid).entrySet()) {
          String docId = entry.getKey();
          int qrelScore = entry.getValue();
          // We issue a specific query
          TopDocs topDocs = searcher.search(docIdQuery(docId), 1);
          if (topDocs.totalHits.value == 0) {
            LOG.warn(String.format("Document Id %s expected but not found in index, skipping...", docId));
            continue;
          }

          ScoreDoc hit = topDocs.scoreDocs[0];
          Document doc = reader.document(hit.doc, fieldsToLoad);

          //TODO factor for test
          Terms terms = reader.getTermVector(hit.doc, getTermVectorField());

          if (terms == null) {
            LOG.debug(String.format("No term vectors found for doc %s, qid %s", docId, qid));
            continue;
          }
          float[] featureValues = extractors.extractAll(doc, terms, context);
          writeFeatureVector(out, qid ,qrelScore,
                  docId, featureValues);
        }
        LOG.debug(String.format("Finished processing for qid: %s", qid));
        out.flush();
      }
    }
}
