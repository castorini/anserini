package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DFR_GL2 implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25FeatureExtractor.class);

  public DFR_GL2() { }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long numDocs = context.numDocs;
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    double avgFL = (double)totalTermFreq/numDocs;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {
      double tfn = context.getTermFreq(queryToken)*Math.log(1+avgFL/docSize);
      double logSuccess = Math.log(1+(double)context.getCollectionFreq(queryToken)/numDocs);
      double logFail = Math.log(1+(double)numDocs/context.getCollectionFreq(queryToken));
      score += (logSuccess+tfn*logFail)/(tfn+1.0);
    }
    return score;
  }

  @Override
  public String getName() {
    return String.format("DFR_GL2");
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new DFR_GL2();
  }

}
