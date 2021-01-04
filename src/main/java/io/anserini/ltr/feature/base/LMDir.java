package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

public class LMDir implements FeatureExtractor {
  private String field;
  private String qfield;
  private double mu = 1000;

  public LMDir() {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public LMDir(double mu) {
    if(mu<=0) throw new IllegalArgumentException("mu must be greater than 0");
    this.mu = mu;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public LMDir(double mu, String field, String qfield) {
    if(mu<=0) throw new IllegalArgumentException("mu must be greater than 0");
    this.mu = mu;
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    float score = 0;

    for (String queryToken : queryFieldContext.queryTokens) {
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
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_%s_LMD_mu_%.0f", field, qfield, mu);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  public double getMu() {
    return mu;
  }

  @Override
  public FeatureExtractor clone() {
    return new LMDir(mu, field, qfield);
  }
}
