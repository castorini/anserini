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

public class LMDirStat implements FeatureExtractor {
  private String field;
  private String qfield;
  private double mu = 1000;

  Pooler collectFun;

  public LMDirStat(Pooler collectFun) {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
    this.collectFun = collectFun;
  }

  public LMDirStat(Pooler collectFun, double mu) {
    if(mu<=0) throw new IllegalArgumentException("mu must be greater than 0");
    this.mu = mu;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
    this.collectFun = collectFun;
  }

  public LMDirStat(Pooler collectFun, double mu, String field, String qfield) {
    if(mu<=0) throw new IllegalArgumentException("mu must be greater than 0");
    this.mu = mu;
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
      if(collectProb==0) continue;
      score.add((float) Math.log((termFreq+mu*collectProb)/(mu+docSize)));
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
    return String.format("%s_%s_%s_LMD_mu_%.0f", field, qfield, collectFun.getName(), mu);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  public double getMu() {
    return mu;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new LMDirStat(newFun, mu, field, qfield);
  }
}
