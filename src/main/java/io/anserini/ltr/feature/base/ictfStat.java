package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;
/**
 * Inverse DocumentCollection Term Frequency as defined in
 * Carmel, Yom-Tov Estimating query difficulty for Information Retrieval
 * log(|D| / tf)
 * todo discuss laplace law of succesion
 */
public class ictfStat implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public ictfStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public ictfStat(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    long collectionSize = context.totalTermFreq;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryContext.queryTokens) {
      long collectionFreq = context.getCollectionFreq(queryToken);
      double ictf = Math.log((double)collectionSize/(collectionFreq+1));
      score.add((float)ictf);
    }
    return collectFun.pool(score);
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_ICTF_%s", field, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new ictfStat(newFun, field);
  }
}
