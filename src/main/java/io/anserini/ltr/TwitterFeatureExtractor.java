package io.anserini.ltr;

import com.google.common.collect.Sets;
import io.anserini.index.IndexTweets;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.OrderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.UnigramFeatureExtractor;
import io.anserini.ltr.feature.UnorderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.base.AvgICTFFeatureExtractor;
import io.anserini.ltr.feature.base.AvgIDFFeatureExtractor;
import io.anserini.ltr.feature.base.BM25FeatureExtractor;
import io.anserini.ltr.feature.base.DocSizeFeatureExtractor;
import io.anserini.ltr.feature.base.MatchingTermCount;
import io.anserini.ltr.feature.base.PMIFeatureExtractor;
import io.anserini.ltr.feature.base.QueryLength;
import io.anserini.ltr.feature.base.SCQFeatureExtractor;
import io.anserini.ltr.feature.base.SimplifiedClarityFeatureExtractor;
import io.anserini.ltr.feature.base.SumMatchingTf;
import io.anserini.ltr.feature.base.TFIDFFeatureExtractor;
import io.anserini.ltr.feature.base.TermFrequencyFeatureExtractor;
import io.anserini.ltr.feature.base.UniqueTermCount;
import io.anserini.ltr.feature.twitter.HashtagCount;
import io.anserini.ltr.feature.twitter.IsTweetReply;
import io.anserini.ltr.feature.twitter.LinkCount;
import io.anserini.ltr.feature.twitter.TwitterFollowerCount;
import io.anserini.ltr.feature.twitter.TwitterFriendCount;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

import java.util.Map;
import java.util.Set;

/**
 * Feature extractor for the twitter collections. Does not require performing searches
 */
public class TwitterFeatureExtractor extends BaseFeatureExtractor{
  private static final Logger LOG = LogManager.getLogger(TwitterFeatureExtractor.class);
  private static final FeatureExtractors DEFAULT_EXTRACTOR_CHAIN = FeatureExtractors.
          createFeatureExtractorChain(new UnigramFeatureExtractor(),
                  new UnorderedSequentialPairsFeatureExtractor(6),
                  new UnorderedSequentialPairsFeatureExtractor(8),
                  new UnorderedSequentialPairsFeatureExtractor(10),
                  new OrderedSequentialPairsFeatureExtractor(6),
                  new OrderedSequentialPairsFeatureExtractor(8),
                  new OrderedSequentialPairsFeatureExtractor(10),
                  new MatchingTermCount(),
                  new QueryLength(),
                  new SumMatchingTf(),
                  new TermFrequencyFeatureExtractor(),
                  new BM25FeatureExtractor(),
                  new TFIDFFeatureExtractor(),
                  new UniqueTermCount(),
                  new DocSizeFeatureExtractor(),
                  new AvgICTFFeatureExtractor(),
                  new AvgIDFFeatureExtractor(),
                  new SimplifiedClarityFeatureExtractor(),
                  new PMIFeatureExtractor(),
                  new SCQFeatureExtractor(),
                  new LinkCount(),
                  new TwitterFollowerCount(),
                  new TwitterFriendCount(),
                  new IsTweetReply(),
                  new HashtagCount()
          );
  /**
   * Constructor that requires a reader to the index, the qrels and the topics
   *
   * @param reader
   * @param qrels
   * @param topics
   */
  public TwitterFeatureExtractor(IndexReader reader, Qrels qrels, Map<String, String> topics) {
    super(reader, qrels, topics, getDefaultExtractors());
    LOG.debug("Twitter Feature Extractor initialized.");
  }

  /**
   * Constructor that requires a reader to the index, the qrels and the topics
   *
   * @param reader
   * @param qrels
   * @param topics
   */
  public TwitterFeatureExtractor(IndexReader reader, Qrels qrels, Map<String, String> topics,
                                 FeatureExtractors featureExtractors) {
    super(reader, qrels, topics, featureExtractors == null ? getDefaultExtractors() : featureExtractors);
    LOG.debug("Twitter Feature Extractor initialized with custom feature extractors.");
  }



  @Override
  protected String getIdField() {
    return IndexTweets.StatusField.ID.name;
  }

  @Override
  protected String getTermVectorField() {
    return IndexTweets.StatusField.TEXT.name;
  }

  public static FeatureExtractors getDefaultExtractors() {
    return DEFAULT_EXTRACTOR_CHAIN;
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

  @Override
  protected Query docIdQuery(String docId) {
    long docIdLong = Long.parseLong(docId);
    return NumericRangeQuery.newLongRange(getIdField(), docIdLong, docIdLong, true, true);
  }
}
