package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/**
 * Computes the sum of the term frequencies of the matching terms. That is, if there are two query
 * terms and the first occurs twice in the document and the second occurs once in the document, the
 * sum of the matching term frequencies is three.
 */
public class SumMatchingTf implements FeatureExtractor {

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    try {
      List<String> queryTokens = context.getQueryTokens();
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

    @Override
    public String getName() {
        return "SumMatchingTf";
    }
}
