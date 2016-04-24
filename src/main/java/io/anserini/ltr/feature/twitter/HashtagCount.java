package io.anserini.ltr.feature.twitter;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

public class HashtagCount implements FeatureExtractor {

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    String str = doc.getField(StatusField.TEXT.name).stringValue();
    final String matchStr = "#";

    int lastIndex = 0;
    int count = 0;

    while (lastIndex != -1) {
      lastIndex = str.indexOf(matchStr, lastIndex);
      if (lastIndex != -1) {
        count++;
        lastIndex += matchStr.length();
      }
    }

    return (float) count;
  }

    @Override
    public String getName() {
        return "TwitterHashtagCount";
    }
}
