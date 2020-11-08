package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;
/**
 * IDF
 * todo discuss laplace law of succesion
 */
public class idfStat implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public idfStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public idfStat(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    long numDocs = context.numDocs;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryContext.queryTokens) {
      int docFreq = context.getDocFreq(queryToken);
      double idf = Math.log((double) numDocs/(docFreq+1));
      score.add((float)idf);
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return String.format("%s_IDF_%s", field, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new idfStat(newFun, field);
  }
}
