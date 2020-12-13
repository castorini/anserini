package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;

public class normalizedDocSizeStat implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public normalizedDocSizeStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public normalizedDocSizeStat(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    List<Float> score;
    if(context.statsCache.containsKey("normalizedDocSize")){
      score = context.statsCache.get("normalizedDocSize");
    } else {
      score = new ArrayList<>();
      long docSize = context.docSize;

      for (String queryToken : queryContext.queryTokens) {
        long termFreq = context.getTermFreq(queryToken);
        if(termFreq==0) {
          score.add(0f);
          continue;
        }
        double tfn = (double)docSize/termFreq;
        score.add((float)tfn);
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
    return String.format("%s_normalizedDocSize_%s",field, collectFun.getName());
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