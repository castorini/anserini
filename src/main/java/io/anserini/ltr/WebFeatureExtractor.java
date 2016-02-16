package io.anserini.ltr;

import com.google.common.collect.Sets;
import io.anserini.index.IndexWebCollection;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.MatchingTermCount;
import io.anserini.ltr.feature.base.QueryLength;
import io.anserini.ltr.feature.base.SumMatchingTf;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import java.util.Map;
import java.util.Set;

/**
 * Feature extractor for the gov two collection
 */
public class WebFeatureExtractor extends BaseFeatureExtractor {
    private static final Logger LOG = LogManager.getLogger(WebFeatureExtractor.class);

    private QueryParser parser;
    public WebFeatureExtractor(IndexReader reader, Qrels qrels, Map<String, String> topics) {
        super(reader, qrels, topics);
        this.parser = new QueryParser(getTermVectorField(), getAnalyzer());
        LOG.debug("Web Feature extractor initialized.");
    }

    @Override
    protected String getIdField() {
        return IndexWebCollection.FIELD_ID;
    }

    @Override
    protected String getTermVectorField() {
        return IndexWebCollection.FIELD_BODY;
    }

    @Override
    protected FeatureExtractors constructExtractors() {
        FeatureExtractors extractorsChain = new FeatureExtractors();
        extractorsChain.add(new MatchingTermCount());
        extractorsChain.add(new QueryLength());
        extractorsChain.add(new SumMatchingTf());
        return extractorsChain;
    }

    @Override
    protected Analyzer getAnalyzer() {
        return new EnglishAnalyzer();
    }

    @Override
    protected Set<String> getFieldsToLoad() {
        return Sets.newHashSet(getIdField(), getTermVectorField());
    }

    @Override
    protected Query parseQuery(String queryText) {
        try {
            return this.parser.parse(queryText);
        } catch (ParseException e) {
            LOG.error(String.format("Unable to parse query for query text %s, error %s",
                    queryText, e));
            return null;
        }
    }

}
