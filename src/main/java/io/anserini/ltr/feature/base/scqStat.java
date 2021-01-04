package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;
/**
 * This feature computes collection query similarity, SCQ defined as
 * (1 + log(tf(t,D))) * idf(t) found on page 33 of Carmel, Yom-Tov 2010
 * D is the collection term frequency
 */
public class scqStat implements FeatureExtractor {
  private String field;
  private String qfield;

  Pooler collectFun;
  public scqStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public scqStat(Pooler collectFun, String field, String qfield) {
    this.collectFun = collectFun;
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    List<Float> score = new ArrayList<>();
    long numDocs = context.numDocs;

    for (String queryToken : queryFieldContext.queryTokens) {
      long docFreq = context.getDocFreq(queryToken);
      long termFreq = context.getCollectionFreq(queryToken);
      if (termFreq == 0) {
        score.add(0f);
        continue;
      }
      double scq = (1 + Math.log(termFreq)) * Math.log(numDocs / docFreq);
      score.add((float) scq);
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
    return String.format("%s_%s_SCQ_%s", field, qfield, collectFun.getName());
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
    return new scqStat(newFun, field, qfield);
  }
}
