package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ictfStat implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25.class);
  Pooler collectFun;
  public ictfStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long cf = context.totalTermFreq;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryContext.queryTokens) {
      long tf = context.getTermFreq(queryToken);
      double ictf = Math.log((double)(cf+1)/(tf+0.5));
      score.add((float)ictf);
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return "ICTF"+collectFun.getName();
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new ictfStat(newFun);
  }
}
