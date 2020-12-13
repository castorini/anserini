package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.Pooler;
import io.anserini.ltr.feature.QueryContext;

public class ContextDFR_GL2 implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public ContextDFR_GL2(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public ContextDFR_GL2(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    return 0;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return collectFun.pool(queryContext.getOthersLog(context.docId, String.format("%s_DFR_GL2", field)));
  }

  @Override
  public String getName() {
    return String.format("%s_ContextDFR_GL2_%s", field, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new ContextDFR_GL2(newFun, field);
  }
}
