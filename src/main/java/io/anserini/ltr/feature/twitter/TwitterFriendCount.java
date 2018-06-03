package io.anserini.ltr.feature.twitter;

import io.anserini.index.generator.TweetGenerator.StatusField;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

public class TwitterFriendCount implements FeatureExtractor {
  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    return (float) (int) doc.getField(StatusField.FRIENDS_COUNT.name).numericValue();
  }

  @Override
  public String getName() {
    return "TwitterFriendCount";
  }
}
