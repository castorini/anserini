package io.anserini.ltr.feature;

import com.carrotsearch.ant.tasks.junit4.dependencies.com.google.common.collect.ImmutableMap$Builder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import io.anserini.ltr.feature.base.*;
import io.anserini.ltr.feature.twitter.*;
import io.anserini.rerank.RerankerContext;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

import java.util.Map;

/**
 * A feature extractor.
 */
public interface FeatureExtractor {
  //********************************************************
  // TODO normalize names
  Map<String, Class<?>> EXTRACTOR_MAP = new ImmutableMap.Builder<String, Class<?>>()
          .put("AvgICTF", AvgICTFFeatureExtractor.class)
          .put("SimplifiedClarityScore", SimplifiedClarityFeatureExtractor.class)
          .put("PMIFeature", PMIFeatureExtractor.class)
          .put("AvgSCQ", SCQFeatureExtractor.class)
          .put("SumMatchingTf", SumMatchingTf.class)
          .put("UnigramsFeatureExtractor", UnigramFeatureExtractor.class)
          .put("AvgIDF", AvgIDFFeatureExtractor.class)
          .put("BM25Feature", BM25FeatureExtractor.class)
          .put("DocSize", DocSizeFeatureExtractor.class)
          .put("MatchingTermCount", MatchingTermCount.class)
          .put("QueryLength", QueryLength.class)
          .put("SumTermFrequency", TermFrequencyFeatureExtractor.class)
          .put("TFIDF", TFIDFFeatureExtractor.class)
          .put("UniqueQueryTerms", UniqueTermCount.class)
          .put("UnorderedSequentialPairs", UnorderedSequentialPairsFeatureExtractor.class)
          .put("OrderedSequentialPairs", OrderedSequentialPairsFeatureExtractor.class)
          .put("TwitterHashtagCount", HashtagCount.class)
          .put("IsTweetReply", IsTweetReply.class)
          .put("TwitterLinkCount", LinkCount.class)
          .put("TwitterFollowerCount", TwitterFollowerCount.class)
          .put("TwitterFriendCount", TwitterFriendCount.class).build();

  GsonBuilder BUILDER = new GsonBuilder()
          .registerTypeAdapter(OrderedSequentialPairsFeatureExtractor.class, new OrderedSequentialPairsFeatureExtractor.Deserializer())
          .registerTypeAdapter(OrderedQueryPairsFeatureExtractor.class, new OrderedQueryPairsFeatureExtractor.Deserializer())
          .registerTypeAdapter(UnorderedQueryPairsFeatureExtractor.class, new UnorderedQueryPairsFeatureExtractor.Deserializer())
          .registerTypeAdapter(UnorderedSequentialPairsFeatureExtractor.class, new UnorderedSequentialPairsFeatureExtractor.Deserializer());

  float extract(Document doc, Terms terms, RerankerContext context);

  String getName();

}
