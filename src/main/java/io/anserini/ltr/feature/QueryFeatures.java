package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

/**
 * Compute query features for LTR (as described by Macdonald et al., CIKM 2012)
 * But just # of tokens for now :-(
 */
public class QueryFeatures implements FeatureExtractor {

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
      Set<String> queryTokens = context.getQueryTokens();
      return queryTokens.size();
  }
}
