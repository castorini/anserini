package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

public class ContextDPH implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public ContextDPH(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public ContextDPH(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    return 0;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return collectFun.pool(queryContext.getOthersLog(context.docId, getName()));
  }

  @Override
  public String getName() {
    return String.format("%s_ContextDPH_%s", field, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new ContextDPH(newFun, field);
  }
}
