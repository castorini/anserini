/**
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

import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.ltr.feature.base.BM25FeatureExtractor;
import io.anserini.ltr.feature.base.DocSizeFeatureExtractor;
import io.anserini.ltr.feature.base.MatchingTermCount;
import io.anserini.ltr.feature.base.QueryLength;
import io.anserini.ltr.feature.base.TFIDFFeatureExtractor;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MSMarcoFeatureExtractor extends BaseFeatureExtractor<Integer>  {
    private QueryParser parser;
    private static final Logger LOG = LogManager.getLogger(MSMarcoFeatureExtractor.class);
    private static final FeatureExtractors DEFAULT_EXTRACTOR_CHAIN = FeatureExtractors.
            createFeatureExtractorChain(
                    new BM25FeatureExtractor(),
                    new DocSizeFeatureExtractor(),
                    new MatchingTermCount(),
                    new QueryLength(),
                    new TFIDFFeatureExtractor()
            );

    /**
     * Constructor that requires a reader to the index, the qrels and the topics
     *
     * @param reader
     * @param qrels
     * @param topics
     */
    public MSMarcoFeatureExtractor(IndexReader reader, Qrels qrels, Map<Integer, Map<String, String>> topics) {
        super(reader, qrels, topics, getDefaultExtractors());
        LOG.debug("MSMarco Feature Extractor initialized.");
    }

    /**
     * Constructor that requires a reader to the index, the qrels and the topics
     *
     * @param reader
     * @param qrels
     * @param topics
     */
    public MSMarcoFeatureExtractor(IndexReader reader, Qrels qrels,
                                   Map<Integer, Map<String, String>> topics, FeatureExtractors featureExtractors) {
        super(reader, qrels, topics, featureExtractors == null ? getDefaultExtractors() : featureExtractors);
        this.parser = new QueryParser(getTermVectorField(), getAnalyzer());
        LOG.debug("MSMarco Feature Extractor initialized with custom feature extractors.");
    }

    @Override
    protected String getIdField() {
        return LuceneDocumentGenerator.FIELD_ID;
    }

    @Override
    protected String getTermVectorField() {
        return LuceneDocumentGenerator.FIELD_BODY;
    }

    public static FeatureExtractors getDefaultExtractors() {
        return DEFAULT_EXTRACTOR_CHAIN;
    }

    @Override
    protected Analyzer getAnalyzer() {
        return new EnglishAnalyzer();
    }

    @Override
    protected Set<String> getFieldsToLoad() {
        return new HashSet<>(Arrays.asList(getIdField(), getTermVectorField()));
    }

    @Override
    protected Query parseQuery(String queryText) {
        try {
            return this.parser.parse(queryText);
        } catch (ParseException e) {
            LOG.error(String.format(
                    "Unable to parse query for query text %s, error %s",
                    queryText,
                    e));
            return null;
        }
    }

    @Override
    protected Query docIdQuery(String docId) {
        return new TermQuery(new Term(getIdField(), docId));
    }
}