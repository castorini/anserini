package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

public class QueryCoverageRatio implements FeatureExtractor {
  private String field;

  public QueryCoverageRatio() { this.field = IndexArgs.CONTENTS; }

  public QueryCoverageRatio(String field) { this.field = field; }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    int matching = 0;
    for(String queryToken : queryContext.queryTokens) {
      long tf = context.getTermFreq(queryToken);
      if(tf!=0)
        matching++;
    }
    return (float)matching/queryContext.querySize;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_QueryCoverageRatio", field);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    return new QueryCoverageRatio(field);
  }

}
