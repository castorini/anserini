package io.anserini.ltr;

import com.google.common.collect.Sets;
import io.anserini.index.IndexTweets;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.MatchingTermCount;
import io.anserini.ltr.feature.base.QueryLength;
import io.anserini.ltr.feature.base.SumMatchingTf;
import io.anserini.ltr.feature.twitter.*;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

import java.util.Map;
import java.util.Set;

/**
 * Feature extractor for the twitter collections. Does not require performing searches
 */
public class TwitterFeatureExtractor extends BaseFeatureExtractor{
    private static final Logger LOG = LogManager.getLogger(TwitterFeatureExtractor.class);
    /**
     * Constructor that requires a reader to the index, the qrels and the topics
     *
     * @param reader
     * @param qrels
     * @param topics
     */
    public TwitterFeatureExtractor(IndexReader reader, Qrels qrels, Map<String, String> topics) {
        super(reader, qrels, topics);
        LOG.debug("Twitter Feature Extractor initialized.");
    }

    @Override
    protected String getIdField() {
        return IndexTweets.StatusField.ID.name;
    }

    @Override
    protected String getTermVectorField() {
        return IndexTweets.StatusField.TEXT.name;
    }

    @Override
    protected FeatureExtractors constructExtractors() {
        FeatureExtractors extractors = new FeatureExtractors();
        extractors.add(new MatchingTermCount());
        extractors.add(new SumMatchingTf());
        extractors.add(new QueryLength());
        extractors.add(new TwitterFollowerCount());
        extractors.add(new TwitterFriendCount());
        extractors.add(new IsTweetReply());
        extractors.add(new HashtagCount());
        extractors.add(new LinkCount());
        return extractors;
    }

    @Override
    protected Analyzer getAnalyzer() {
        return IndexTweets.ANALYZER;
    }

    @Override
    protected Set<String> getFieldsToLoad() {
        return Sets.newHashSet(getIdField(), getTermVectorField(),
                IndexTweets.StatusField.FOLLOWERS_COUNT.name,
                IndexTweets.StatusField.FRIENDS_COUNT.name,
                IndexTweets.StatusField.IN_REPLY_TO_STATUS_ID.name);
    }

    @Override
    protected Query parseQuery(String queryText) {
        LOG.debug(String.format("Parsing query: %s", queryText) );
        return AnalyzerUtils.buildBagOfWordsQuery(IndexTweets.StatusField.TEXT.name, IndexTweets.ANALYZER, queryText);
    }
}
