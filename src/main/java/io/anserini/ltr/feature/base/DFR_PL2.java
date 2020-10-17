package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DFR_PL2 implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(DFR_PL2.class);

  public DFR_PL2() { }

  double log2(double x){
    return Math.log(x)/Math.log(2);
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long numDocs = context.numDocs;
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    double avgFL = (double)totalTermFreq/numDocs;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {

    }
    return score;
  }

  @Override
  public String getName() {
    return "DFR_PL2";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new DFR_PL2();
  }
}
