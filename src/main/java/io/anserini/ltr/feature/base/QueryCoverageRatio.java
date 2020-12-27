package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

public class QueryCoverageRatio implements FeatureExtractor {
  private String field;
  private String qfield;

  public QueryCoverageRatio() {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public QueryCoverageRatio(String field, String qfield) {
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    int matching = 0;
    for(String queryToken : queryFieldContext.queryTokens) {
      long tf = context.getTermFreq(queryToken);
      if(tf!=0)
        matching++;
    }
    return (float)matching/queryFieldContext.querySize;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_%s_QueryCoverageRatio", field, qfield);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  @Override
  public FeatureExtractor clone() {
    return new QueryCoverageRatio(field, qfield);
  }

}
