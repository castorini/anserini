package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/**
 * Computes the sum of the term frequencies of the matching terms. That is, if there are two query
 * terms and the first occurs twice in the document and the second occurs once in the document, the
 * sum of the matching term frequencies is three.
 */
public class SumMatchingTf implements IntFeatureExtractor {

  @Override
  public int extract(Terms terms, RerankerContext context) {
    try {
      Set<String> queryTokens = context.getQueryTokens();
      TermsEnum termsEnum = terms.iterator();
      int sum = 0;

      BytesRef text = null;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        if (queryTokens.contains(term)) {
          sum += (int) termsEnum.totalTermFreq();
        }
      }
      return sum;

    } catch (IOException e) {
      return 0;
    }
  }
}
