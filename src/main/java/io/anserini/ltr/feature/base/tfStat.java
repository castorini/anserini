package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;

public class tfStat implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public tfStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public tfStat(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    List<Float> score;
    if(context.statsCache.containsKey("TF")){
      score = context.statsCache.get("TF");
    } else {
      score = new ArrayList<>();

      for (String queryToken : queryContext.queryTokens) {
        long termFreq = context.getTermFreq(queryToken);
        if(termFreq==0) {
          score.add(0f);
          continue;
        }
        score.add((float)termFreq);
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
    return String.format("%s_TF_%s", field, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new tfStat(newFun, field);
  }
}
