package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
/*
*  todo discuss logarithm
*/
public class normalizedTfStat implements FeatureExtractor {

  Pooler collectFun;
  public normalizedTfStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    List<Float> score;
    if(context.statsCache.containsKey("NormalizedTF")){
      score = context.statsCache.get("NormalizedTF");
    } else {
      score = new ArrayList<>();
      long docSize = context.docSize;

      for (String queryToken : queryContext.queryTokens) {
        long termFreq = context.getTermFreq(queryToken);
        if(termFreq==0) {
          score.add(0f);
          continue;
        }
        double tfn = (double)termFreq/docSize;
        score.add((float)Math.log(tfn));
      }
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return "NormalizedTF"+collectFun.getName();
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new normalizedTfStat(newFun);
  }
}
