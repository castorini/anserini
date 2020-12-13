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
//    double doc_norm = 1.0 / dlen;
//    double w_dq = 1.0 + std::log(d_f);
//    double w_Qq = std::log(1.0 + ((double)num_docs / t_idf));
    for (String queryToken : queryContext.queryTokens) {
      int docFreq = context.getDocFreq(queryToken);
      long termFreq = context.getTermFreq(queryToken);
      if(termFreq==0) continue;
      double idf = Math.log(1+numDocs/docFreq);
      score+=(float)(idf*(1+Math.log(termFreq))/docSize);
    }
    return score;
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
