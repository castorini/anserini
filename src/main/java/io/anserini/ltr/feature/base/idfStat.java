package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class idfStat implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25.class);
  Pooler collectFun;
  public idfStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long numDocs = context.numDocs;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryContext.queryTokens) {
      int docFreq = context.getDocFreq(queryToken);
      double idf = Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
      score.add((float)idf);
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return "IDF"+collectFun.getName();
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new idfStat(newFun);
  }
}
