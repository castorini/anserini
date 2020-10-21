package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class tfIdfStat implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25.class);
  Pooler collectFun;
  public tfIdfStat(Pooler collectFun) {
    this.collectFun = collectFun;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long numDocs = context.numDocs;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryContext.queryTokens) {
      int docFreq = context.getDocFreq(queryToken);
      long termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) {
        score.add(0f);
        continue;
      }
      double tfn = 1+Math.log(termFreq);
      double idf = Math.log(1+(numDocs/docFreq));
      score.add((float)(idf*tfn));
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return "TFIDF"+collectFun.getName();
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new tfIdfStat(newFun);
  }
}
