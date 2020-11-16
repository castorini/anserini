package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

public class LMDir implements FeatureExtractor {
  private String field;
  private double mu = 1000;

  public LMDir() {
    this.field = IndexArgs.CONTENTS;
  }

  public LMDir(double mu) {
    if(mu<=0) throw new IllegalArgumentException("mu must be greater than 0");
    this.mu = mu;
    this.field = IndexArgs.CONTENTS;
  }

  public LMDir(double mu, String field) {
    if(mu<=0) throw new IllegalArgumentException("mu must be greater than 0");
    this.mu = mu;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      double collectProb = (double)context.getCollectionFreq(queryToken)/totalTermFreq;
      //todo need discuss this
      if(collectProb==0) continue;
      score += Math.log((termFreq+mu*collectProb)/(mu+docSize));
    }
    return score;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_LMD_mu_%.0f", field, mu);
  }

  @Override
  public String getField() {
    return field;
  }

  public double getMu() {
    return mu;
  }

  @Override
  public FeatureExtractor clone() {
    return new LMDir(mu, field);
  }
}
