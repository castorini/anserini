package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/**
 * Computes the number of query terms that are found in the document. If there are three terms in
 * the query and all three terms are found in the document, the feature value is three.
 */
public class MatchingTermCount implements IntFeatureExtractor {

  @Override
  public int extract(Terms terms, RerankerContext context) {
    try {
      Set<String> queryTokens = context.getQueryTokens();
      TermsEnum termsEnum = terms.iterator();
      int matching = 0;

      BytesRef text = null;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        if (queryTokens.contains(term)) {
          matching++;
        }
      }
      return matching;

    } catch (IOException e) {
      return 0;
    }
  }
}
