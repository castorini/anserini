package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class normalizedDocSizeStat implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25.class);
  Pooler collectFun;
  public normalizedDocSizeStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    List<Float> score = new ArrayList<>();
    long docSize = context.docSize;

    for (String queryToken : queryContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) {
        score.add(0f);
        continue;
      }
      double sizen = (double)docSize/termFreq;
      score.add((float)sizen);
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
    return new normalizedDocSizeStat(newFun);
  }
}
