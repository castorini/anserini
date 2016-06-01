package io.anserini.ltr;

import com.google.common.collect.Sets;
import io.anserini.index.IndexWebCollection;
import io.anserini.ltr.feature.*;
import io.anserini.ltr.feature.base.*;
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

import java.util.Map;
import java.util.Set;

/**
 * Feature extractor for the gov two collection
 */
public class WebFeatureExtractor extends BaseFeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(WebFeatureExtractor.class);

  //**************************************************
  //**************************************************
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
                  new SCQFeatureExtractor()
          );

  //**************************************************
  //**************************************************

  private QueryParser parser;

  public WebFeatureExtractor(IndexReader reader, Qrels qrels, Map<String, String> topics) {
    this(reader, qrels, topics, getDefaultExtractors());
    LOG.debug("Web Feature extractor initialized.");
  }

  /**
   * FeatureExtractor constructor requires an index reader, qrels, and topics
   * also takes in optional customExtractors, if null, the default will be used
   * @param reader
   * @param qrels
   * @param topics
   * @param customExtractors
   */
  public WebFeatureExtractor(IndexReader reader, Qrels qrels, Map<String, String> topics,
                             FeatureExtractors customExtractors) {
    super(reader, qrels, topics, customExtractors == null ? getDefaultExtractors() : customExtractors);
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

  public static FeatureExtractors getDefaultExtractors() {
    return DEFAULT_EXTRACTOR_CHAIN;
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

  @Override
  protected Query docIdQuery(String docId) {
    return new TermQuery(new Term(getIdField(), docId));
  }

}
