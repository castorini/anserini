package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;
/* try to avoid duplicatiton with scq
todo discuss tfidf
*/
public class tfIdfStat implements FeatureExtractor {
  private String field;

  Pooler collectFun;
  public tfIdfStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = IndexArgs.CONTENTS;
  }

  public tfIdfStat(Pooler collectFun, String field) {
    this.collectFun = collectFun;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    List<Float> score;
    if(context.statsCache.containsKey("TFIDF")){
      score = context.statsCache.get("TFIDF");
    } else {
      long numDocs = context.numDocs;
      score = new ArrayList<>();

      for (String queryToken : queryContext.queryTokens) {
        int docFreq = context.getDocFreq(queryToken);
        long termFreq = context.getTermFreq(queryToken);
        if(termFreq==0) {
          score.add(0f);
          continue;
        }
        double idf = Math.log(numDocs/docFreq);
        score.add((float)(idf*termFreq));
      }
    }
    return collectFun.pool(score);
  }

  @Override
  public String getName() {
    return String.format("%s_TFIDF_%s", field, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new tfIdfStat(newFun, field);
  }
}
