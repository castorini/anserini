package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;

public class normalizedDocSizeStat implements FeatureExtractor {
  private String field;
  private String qfield;

  Pooler collectFun;
  public normalizedDocSizeStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public normalizedDocSizeStat(Pooler collectFun, String field, String qfield) {
    this.collectFun = collectFun;
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    List<Float> score;
    if(context.statsCache.containsKey("normalizedDocSize")){
      score = context.statsCache.get("normalizedDocSize");
    } else {
      score = new ArrayList<>();
      long docSize = context.docSize;

      for (String queryToken : queryFieldContext.queryTokens) {
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
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_%s_normalizedDocSize_%s",field, qfield, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new normalizedDocSizeStat(newFun, field, qfield);
  }
}