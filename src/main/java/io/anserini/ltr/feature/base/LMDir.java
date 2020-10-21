package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LMDir implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(LMDir.class);

  private double mu = 1000;

  public LMDir() { }

  public LMDir(double mu) {
    this.mu = mu;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {
      double termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) continue;
      double collectProb = (double)context.getCollectionFreq(queryToken)/totalTermFreq;
      score += Math.log((termFreq+mu*collectProb)/(mu+docSize));
    }
    return score;
  }

  @Override
  public String getName() {
    return String.format("LMD_mu_%.0f",mu);
  }

  @Override
  public String getField() {
    return null;
  }

  public double getMu() { return mu; }

  @Override
  public FeatureExtractor clone() {
    return new LMDir(this.mu);
  }
}
