package io.anserini.ltr;

import com.google.common.collect.Sets;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.index.generator.TweetGenerator.StatusField;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.OrderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.UnigramFeatureExtractor;
import io.anserini.ltr.feature.UnorderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.base.*;
import io.anserini.ltr.feature.twitter.*;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;


/**
 * Feature extractor for the twitter collections. Does not require performing searches
 */
public class TwitterFeatureExtractor extends BaseFeatureExtractor<Integer> {
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
  public TwitterFeatureExtractor(IndexReader reader, Qrels qrels, Map<Integer, Map<String, String>> topics) {
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
  public TwitterFeatureExtractor(IndexReader reader, Qrels qrels,
                 Map<Integer, Map<String, String>> topics, FeatureExtractors featureExtractors) {
    super(reader, qrels, topics, featureExtractors == null ? getDefaultExtractors() : featureExtractors);
    LOG.debug("Twitter Feature Extractor initialized with custom feature extractors.");
  }



  @Override
  protected String getIdField() {
    return TweetGenerator.FIELD_ID;
  }

  @Override
  protected String getTermVectorField() {
    return TweetGenerator.FIELD_BODY;
  }

  public static FeatureExtractors getDefaultExtractors() {
    return DEFAULT_EXTRACTOR_CHAIN;
  }

  @Override
  protected Analyzer getAnalyzer() {
    return new TweetAnalyzer();
  }

  @Override
  protected Set<String> getFieldsToLoad() {
    return Sets.newHashSet(getIdField(), getTermVectorField(),
            StatusField.FOLLOWERS_COUNT.name,
            StatusField.FRIENDS_COUNT.name,
            StatusField.IN_REPLY_TO_STATUS_ID.name);
  }

  @Override
  protected Query parseQuery(String queryText) {
    LOG.debug(String.format("Parsing query: %s", queryText) );
    return AnalyzerUtils.buildBagOfWordsQuery(TweetGenerator.FIELD_BODY, new TweetAnalyzer(), queryText);
  }

  @Override
  protected Query docIdQuery(String docId) {
    long docIdLong = Long.parseLong(docId);
    return LongPoint.newRangeQuery(getIdField(), docIdLong, docIdLong);
  }
}
