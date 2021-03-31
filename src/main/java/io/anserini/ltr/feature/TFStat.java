package io.anserini.ltr.feature;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.*;

import java.util.ArrayList;
import java.util.List;

public class TFStat implements FeatureExtractor {
  private String field;
  private String qfield;

  Pooler collectFun;
  public TFStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public TFStat(Pooler collectFun, String field, String qfield) {
    this.collectFun = collectFun;
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryFieldContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) {
        score.add(0f);
        continue;
      }
      score.add((float)termFreq);
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
    String className = this.getClass().getName();
    String name = className.substring(24,className.length());
    return String.format("%s_%s_%s_%s", field, qfield, name, collectFun.getName());
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
    return new TFStat(newFun, field, qfield);
  }
}
