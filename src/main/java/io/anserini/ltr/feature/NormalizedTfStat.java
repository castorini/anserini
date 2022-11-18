/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.ltr.feature;

import io.anserini.index.Constants;
import io.anserini.ltr.DocumentContext;
import io.anserini.ltr.DocumentFieldContext;
import io.anserini.ltr.FeatureExtractor;
import io.anserini.ltr.Pooler;
import io.anserini.ltr.QueryContext;
import io.anserini.ltr.QueryFieldContext;

import java.util.ArrayList;
import java.util.List;

public class NormalizedTfStat implements FeatureExtractor {
  private String field;
  private String qfield;

  Pooler collectFun;
  public NormalizedTfStat(Pooler collectFun) {
    this.collectFun = collectFun;
    this.field = Constants.CONTENTS;
    this.qfield = "analyzed";
  }

  public NormalizedTfStat(Pooler collectFun, String field, String qfield) {
    this.collectFun = collectFun;
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    List<Float> score = new ArrayList<>();
    long docSize = context.docSize;

    for (String queryToken : queryFieldContext.queryTokens) {
      long termFreq = context.getTermFreq(queryToken);
      double tfn;
      if(termFreq==0) {
        tfn = (double) docSize / 0.5;
      } else {
        tfn = (double) docSize / termFreq;
      }
      if (tfn == 0) continue;
      score.add((float)Math.log(tfn));
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
    String name = this.getClass().getSimpleName();
    return String.format("%s_%s_%s_%s",field, qfield, name, collectFun.getName());
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new NormalizedTfStat(newFun, field, qfield);
  }
}
