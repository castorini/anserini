/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.ArrayList;
import java.util.List;

public class LMJMStat implements FeatureExtractor {
  private String field;
  private String qfield;
  Pooler collectFun;

  private double lambda = 0.1;

  public LMJMStat(Pooler collectFun) {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
    this.collectFun = collectFun;
  }

  public LMJMStat(Pooler collectFun, double lambda) {
    if(lambda<=0) throw new IllegalArgumentException("lambda must be greater than 0");
    this.lambda = lambda;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
    this.collectFun = collectFun;
  }

  public LMJMStat(Pooler collectFun, double lambda, String field, String qfield) {
    if(lambda<=0) throw new IllegalArgumentException("lambda must be greater than 0");
    this.lambda = lambda;
    this.field = field;
    this.qfield = qfield;
    this.collectFun = collectFun;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryFieldContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      double collectProb = (double)context.getCollectionFreq(queryToken)/totalTermFreq;
      double documentProb = (double)termFreq/docSize;
      //todo need discuss this
      if(collectProb==0) continue;
      score.add((float) Math.log((1-lambda)*documentProb+lambda*collectProb));
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
    return String.format("%s_%s_LMJM_lambda_%.2f", field, qfield, lambda);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  public double getLambda() { return lambda; }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new LMJMStat(newFun, lambda, field, qfield);
  }
}
