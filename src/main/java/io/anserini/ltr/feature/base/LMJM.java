package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

public class LMJM implements FeatureExtractor {
  private String field;

  private double lambda = 0.1;

  public LMJM() {
    this.field = IndexArgs.CONTENTS;
  }

  public LMJM(double lambda) {
    if(lambda<=0) throw new IllegalArgumentException("lambda must be greater than 0");
    this.lambda = lambda;
    this.field = IndexArgs.CONTENTS;
  }

  public LMJM(double lambda, String field) {
    if(lambda<=0) throw new IllegalArgumentException("lambda must be greater than 0");
    this.lambda = lambda;
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
      double documentProb = (double)termFreq/docSize;
      //todo need discuss this
      if(collectProb==0) continue;
      score += Math.log((1-lambda)*documentProb+lambda*collectProb);
    }
    return score;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_LMJM_lambda_%.2f", field, lambda);
  }

  @Override
  public String getField() {
    return field;
  }

  public double getLambda() { return lambda; }

  @Override
  public FeatureExtractor clone() {
    return new LMJM(lambda, field);
  }
}
