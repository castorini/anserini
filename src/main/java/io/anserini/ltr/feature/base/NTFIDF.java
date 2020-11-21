package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;

public class NTFIDF implements FeatureExtractor {
  private String field;

  public NTFIDF() {
    this.field = IndexArgs.CONTENTS;
  }

  public NTFIDF(String field) {
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    float score = 0;
    long numDocs = context.numDocs;
    long docSize = context.docSize;

    for (String queryToken : queryContext.queryTokens) {
      int docFreq = context.getDocFreq(queryToken);
      long termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) continue;
      double idf = Math.log(numDocs/docFreq);
      score+=(float)(idf*termFreq);
    }
    return score/docSize;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_NTFIDF", field);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    return new NTFIDF(field);
  }
}
