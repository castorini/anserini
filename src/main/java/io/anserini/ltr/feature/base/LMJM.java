package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

public class LMJM implements FeatureExtractor {
  private String field;
  private String qfield;

  private double lambda = 0.1;

  public LMJM() {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public LMJM(double lambda) {
    if(lambda<=0) throw new IllegalArgumentException("lambda must be greater than 0");
    this.lambda = lambda;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public LMJM(double lambda, String field, String qfield) {
    if(lambda<=0) throw new IllegalArgumentException("lambda must be greater than 0");
    this.lambda = lambda;
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
      double documentProb = (double)termFreq/docSize;
      //todo need discuss this
      if(collectProb==0) continue;
      score += Math.log((1-lambda)*documentProb+lambda*collectProb);
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
    return String.format("%s_%s_LMJM_lambda_%.2f", field, qfield, lambda);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  public double getLambda() { return lambda; }

  @Override
  public FeatureExtractor clone() {
    return new LMJM(lambda, field, qfield);
  }
}
