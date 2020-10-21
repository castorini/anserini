package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class tfStat implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25.class);
  Pooler collectFun;
  public tfStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) {
        score.add(0f);
        continue;
      }
      score.add((float)termFreq);
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return "TF"+collectFun.getName();
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new tfStat(newFun);
  }
}
