package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/**
 * Compute query features for LTR (as described by Macdonald et al., CIKM 2012)
 * But just # of tokens for now :-(
 */
public class QueryFeatures implements IntFeatureExtractor {

  @Override
  public int extract(Terms terms, RerankerContext context) {
      Set<String> queryTokens = context.getQueryTokens();
      return queryTokens.size();
  }
}
