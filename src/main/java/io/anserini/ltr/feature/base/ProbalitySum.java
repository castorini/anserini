package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.QueryContext;

public class ProbalitySum implements FeatureExtractor {
  private String field;

  public ProbalitySum() {
    this.field = IndexArgs.CONTENTS;
  }

  public ProbalitySum(String field) {
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    long docSize = context.docSize;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      score += ((double)termFreq)/docSize;
    }
    return score;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_Prob", field);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    return new ProbalitySum(field);
  }
}
