package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Computes the sum of termfrequencies for each query token
 */
public class TermFrequencyFeatureExtractor implements FeatureExtractor{
  private static final Logger LOG = LogManager.getLogger(TermFrequencyFeatureExtractor.class);

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {

    TermsEnum termsEnum = null;
    try {
      termsEnum = terms.iterator();
    } catch (IOException e) {
      LOG.warn("No terms enum found");
      return 0.0f;
    }

    Map<String, Long> termFreqMap = new HashMap<>();
    Set<String> queryTokens = new HashSet<>(context.getQueryTokens());
    try {
      while (termsEnum.next() != null) {
        String termString = termsEnum.term().utf8ToString();
        if (queryTokens.contains(termString)) {
          termFreqMap.put(termString, termsEnum.totalTermFreq());
        }
      }
    } catch (IOException e) {
      LOG.warn("Error retrieving total term freq");
    }

    float score = 0.0f;
    for (String queryToken : queryTokens) {
      if (termFreqMap.containsKey(queryToken)) {
        score += termFreqMap.get(queryToken);
      } else {
        score += 0.0f;
      }
    }
    return score;
  }

  @Override
  public String getName() {
    return "SumTermFrequency";
  }
}
