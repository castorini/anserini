package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

import java.util.HashSet;
import java.util.Set;

/**
 * Count of unique query terms
 */
public class UniqueTermCount<T> implements FeatureExtractor<T> {
  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    Set<String> queryTokens = new HashSet<>(context.getQueryTokens());
    return queryTokens.size();
  }

  @Override
  public String getName() {
    return "UniqueQueryTerms";
  }
}
