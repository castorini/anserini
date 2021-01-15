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

public class DFR_GL2 implements FeatureExtractor {

  private String field;
  private String qfield;

  public DFR_GL2() {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public DFR_GL2(String field, String qfield) {
    this.field = field;
    this.qfield = qfield;
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
    float score = 0;

    for (String queryToken : queryFieldContext.queryTokens) {
      double tfn = context.getTermFreq(queryToken)*log2(1+avgFL/docSize);
      //todo need discuss this
      if(tfn==0) continue;
      double logSuccess = Math.log(1+(double)context.getCollectionFreq(queryToken)/numDocs);
      double logFail = Math.log(1+(double)numDocs/context.getCollectionFreq(queryToken));
      score += (logSuccess+tfn*logFail)/(tfn+1.0);
    }
    return score;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_%s_DFR_GL2", field, qfield);
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
    return new DFR_GL2(field, qfield);
  }

}
