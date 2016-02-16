package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

/**
 * QueryCount
 * Compute the query length (number of terms in the query).
 */
public class QueryLength implements FeatureExtractor {

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
      List<String> queryTokens = context.getQueryTokens();
      return queryTokens.size();
  }

    @Override
    public String getName() {
        return "QueryLength";
    }
}
