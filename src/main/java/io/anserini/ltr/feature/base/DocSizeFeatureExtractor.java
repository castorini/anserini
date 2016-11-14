package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.io.IOException;

/**
 * Returns the size of the document
 */
public class DocSizeFeatureExtractor implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(DocSizeFeatureExtractor.class);

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    float score;
    try {
      score = (float)terms.getSumTotalTermFreq();
      if (score == -1) {
        // try to iterate over the terms
        TermsEnum termsEnum = terms.iterator();
        score = 0.0f;
        while (termsEnum.next()!= null) {
          score += termsEnum.totalTermFreq();
        }
      }
    } catch (IOException e) {
      score = 0.0f;
    }
    return score;
  }

  @Override
  public String getName() {
    return "DocSize";
  }
}
