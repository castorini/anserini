package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

public class DFR_In_expB2 implements FeatureExtractor {

  private String field;

  public DFR_In_expB2() { this.field = IndexArgs.CONTENTS; }

  public DFR_In_expB2(String field) { this.field = field; }

  double log2(double x){
    return Math.log(x)/Math.log(2);
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    long numDocs = context.numDocs;
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    double avgFL = (double)totalTermFreq/numDocs;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {
      double tfn = context.getTermFreq(queryToken)*log2(1+avgFL/docSize);
      if(tfn==0) continue;
      double cf = context.getCollectionFreq(queryToken);
      double ne = numDocs*(1-Math.pow((double)(numDocs-1)/numDocs, cf));
      double ine = log2(((double)numDocs+1)/(ne+0.5));
      score += tfn*ine*((cf+1)/((double)context.getDocFreq(queryToken)*(tfn+1)));
    }
    return score;
  }

  @Override
  public String getName() {
    return String.format("%s_DFR_In_expB2", field);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    return new DFR_In_expB2(field);
  }
}
