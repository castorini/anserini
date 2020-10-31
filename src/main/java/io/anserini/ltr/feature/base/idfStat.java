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
 * IDF
 * todo discuss laplace law of succesion
 */
public class idfStat implements FeatureExtractor {

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
      double idf = Math.log((double) numDocs/(docFreq+1));
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
