package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LMJM implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(LMJM.class);

  private double lambda = 0.1;

  public LMJM() {}

  public LMJM(double lambda) {
    this.lambda = lambda;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) continue;
      double collectProb = (double)context.getCollectionFreq(queryToken)/totalTermFreq;
      double documentProb = (double)termFreq/docSize;
      score += Math.log((1-lambda)*documentProb+lambda*collectProb);
    }
    return score;
  }

  @Override
  public String getName() {
    return String.format("LMJM_lambda_%.2f",lambda);
  }

  @Override
  public String getField() {
    return null;
  }

  public double getLambda() { return lambda; }

  @Override
  public FeatureExtractor clone() {
    return new LMJM(this.lambda);
  }
}
