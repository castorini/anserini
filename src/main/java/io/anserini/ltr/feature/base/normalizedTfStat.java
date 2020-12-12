package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;
/*
*  todo discuss logarithm
*/
public class normalizedTfStat implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public normalizedTfStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public normalizedTfStat(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    List<Float> score;
    if(context.statsCache.containsKey("NormalizedTF")){
      score = context.statsCache.get("NormalizedTF");
    } else {
      score = new ArrayList<>();
      long docSize = context.docSize;

      for (String queryToken : queryContext.queryTokens) {
        long termFreq = context.getTermFreq(queryToken);
        double tfn;
        //todo need discuss this
        if(termFreq==0) {
          tfn = (double) docSize / 0.5;
        } else {
          tfn = (double) docSize / termFreq;
        }
        score.add((float)Math.log(tfn));
      }
    }
    return collectFun.pool(score);
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_NormalizedTF_%s",field, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new normalizedTfStat(newFun, field);
  }
}
