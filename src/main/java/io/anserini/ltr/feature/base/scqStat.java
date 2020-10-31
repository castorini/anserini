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
 * This feature computes collection query similarity, SCQ defined as
 * (1 + log(tf(t,D))) * idf(t) found on page 33 of Carmel, Yom-Tov 2010
 * D is the collection term frequency
 */
public class scqStat implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25.class);
  Pooler collectFun;
  public scqStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {

    List<Float> score;
    if(context.statsCache.containsKey("SCQ")){
      score = context.statsCache.get("SCQ");
    } else {
      long numDocs = context.numDocs;
      score = new ArrayList<>();

      for (String queryToken : queryContext.queryTokens) {
        long docFreq = context.getDocFreq(queryToken);
        long termFreq = context.getCollectionFreq(queryToken);
        if (termFreq == 0) {
          score.add(0f);
          continue;
        }
        double scq = (1 + Math.log(termFreq)) * Math.log(numDocs / docFreq);
        score.add((float) scq);
      }
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return "SCQ"+collectFun.getName();
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new scqStat(newFun);
  }
}
