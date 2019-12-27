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

import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.index.generator.TweetGenerator.StatusField;
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
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    return new HashSet<>(Arrays.asList(
        getIdField(),
        getTermVectorField(),
        StatusField.FOLLOWERS_COUNT.name,
        StatusField.FRIENDS_COUNT.name,
        StatusField.IN_REPLY_TO_STATUS_ID.name)
    );
  }

  @Override
  protected Query parseQuery(String queryText) {
    LOG.debug(String.format("Parsing query: %s", queryText) );
    return new BagOfWordsQueryGenerator().buildQuery(TweetGenerator.FIELD_BODY, new TweetAnalyzer(), queryText);
  }

  @Override
  protected Query docIdQuery(String docId) {
    long docIdLong = Long.parseLong(docId);
    return LongPoint.newRangeQuery(getIdField(), docIdLong, docIdLong);
  }
}
