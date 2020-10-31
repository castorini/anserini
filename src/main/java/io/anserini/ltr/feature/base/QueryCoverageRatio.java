package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

public class QueryCoverageRatio implements FeatureExtractor {

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    int matching = 0;
    for(String queryToken : queryContext.queryTokens) {
      long tf = context.getTermFreq(queryToken);
      if(tf!=0)
        matching++;
    }
    return (float)matching/queryContext.querySize;
  }

  @Override
  public String getName() {
    return "QueryCoverageRatio";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new QueryCoverageRatio();
  }

}
