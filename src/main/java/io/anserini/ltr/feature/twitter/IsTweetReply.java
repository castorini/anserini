package io.anserini.ltr.feature.twitter;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

public class IsTweetReply implements FeatureExtractor {
  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    return doc.getField(StatusField.IN_REPLY_TO_STATUS_ID.name) == null ? 0.0f : 1.0f;
  }

    @Override
    public String getName() {
        return "IsTweetReply";
    }
}
