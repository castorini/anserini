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

public class DfrInExpB2Stat implements FeatureExtractor {

  private String field;
  private String qfield;
  Pooler collectFun;

  public DfrInExpB2Stat(Pooler collectFun) {
    this.field = Constants.CONTENTS;
    this.qfield = "analyzed";
    this.collectFun = collectFun;
  }

  public DfrInExpB2Stat(Pooler collectFun, String field, String qfield) {
    this.field = field;
    this.qfield = qfield;
    this.collectFun = collectFun;
  }

  double log2(double x){
    return Math.log(x)/Math.log(2);
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    long numDocs = context.numDocs;
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    double avgFL = (double)totalTermFreq/numDocs;
    List<Float> score = new ArrayList<>();

    for (String queryToken : queryFieldContext.queryTokens) {
      if (docSize==0) continue;
      double tfn = context.getTermFreq(queryToken)*log2(1+avgFL/docSize);
      if(tfn==0) continue;
      double cf = context.getCollectionFreq(queryToken);
      double ne = numDocs*(1-Math.pow((double)(numDocs-1)/numDocs, cf));
      double ine = log2(((double)numDocs+1)/(ne+0.5));
      score.add((float) (tfn*ine*((cf+1)/((double)context.getDocFreq(queryToken)*(tfn+1)))));
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
    return String.format("%s_%s_%s_%s", field, qfield, name,  collectFun.getName());
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
    return new DfrInExpB2Stat(newFun, field, qfield);
  }
}
