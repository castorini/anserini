package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
/**
 * Inverse DocumentCollection Term Frequency as defined in
 * Carmel, Yom-Tov Estimating query difficulty for Information Retrieval
 * log(|D| / tf)
 * todo discuss laplace law of succesion
 */
public class ictfStat implements FeatureExtractor {

  Pooler collectFun;
  public ictfStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long collectionSize = context.totalTermFreq;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryContext.queryTokens) {
      long collectionFreq = context.getCollectionFreq(queryToken);
      double ictf = Math.log((double)collectionSize/(collectionFreq+1));
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
