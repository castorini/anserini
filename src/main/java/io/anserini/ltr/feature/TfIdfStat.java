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

public class TfIdfStat implements FeatureExtractor {
  private String field;
  private String qfield;
  private Boolean subLinearTF;

  Pooler collectFun;
  public TfIdfStat(Pooler collectFun) {
    this.subLinearTF = true;
    this.collectFun = collectFun;
    this.field = Constants.CONTENTS;
    this.qfield = "analyzed";
  }

  public TfIdfStat(Boolean subLinearTF, Pooler collectFun, String field, String qfield) {
    this.subLinearTF = subLinearTF;
    this.collectFun = collectFun;
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    List<Float> score = new ArrayList<>();
    long numDocs = context.numDocs;

    for (String queryToken : queryFieldContext.queryTokens) {
      int docFreq = context.getDocFreq(queryToken);
      double termFreq = context.getTermFreq(queryToken);

      if(termFreq==0) {
        score.add(0f);
        continue;
      }

      if(subLinearTF)
        termFreq = 1 + Math.log(termFreq);

      double idf = Math.log(numDocs/docFreq);
      score.add((float)(idf*termFreq));
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
    if (subLinearTF)
      return String.format("%s_%s_L%s_%s", field, qfield, name, collectFun.getName());
    else
      return String.format("%s_%s_%s_%s", field, qfield, name, collectFun.getName());
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
    return new TfIdfStat(subLinearTF, newFun, field, qfield);
  }
}
