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
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

/**
 * SCS = sum (P[t|q]) * log(P[t|q] / P[t|D])
 * page 20 of Carmel, Yom-Tov 2010
 */
public class SCS implements FeatureExtractor {
  private String field;

  public SCS() { this.field = IndexArgs.CONTENTS; }

  public SCS(String field) { this.field = field; }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    long termCount = context.totalTermFreq;
    float score = 0.0f;
    for (String token : queryContext.queryFreqs.keySet()) {
      float prtq = queryContext.queryFreqs.get(token) / (float) queryContext.querySize;
      long tf = context.getCollectionFreq(token);
      float prtd = (float)tf/termCount;
      if (prtd == 0) continue;
      score += prtq*Math.log(prtq/prtd);
    }
    return score;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_SCS",field);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    return new SCS(field);
  }
}
