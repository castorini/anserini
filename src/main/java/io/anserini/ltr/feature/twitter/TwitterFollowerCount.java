package io.anserini.ltr.feature.twitter;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

public class TwitterFollowerCount implements FeatureExtractor {
  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    return (float) (int) doc.getField(StatusField.FOLLOWERS_COUNT.name).numericValue();
  }

    @Override
    public String getName() {
        return "TwitterFollowerCount";
    }
}
