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

public class NTFIDF implements FeatureExtractor {
  private String field;
  private String qfield;

  public NTFIDF() {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public NTFIDF(String field, String qfield) {
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    float score = 0;
    long numDocs = context.numDocs;
    long docSize = context.docSize;
//    double doc_norm = 1.0 / dlen;
//    double w_dq = 1.0 + std::log(d_f);
//    double w_Qq = std::log(1.0 + ((double)num_docs / t_idf));
    for (String queryToken : queryFieldContext.queryTokens) {
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
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_%s_NTFIDF", field, qfield);
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
    return new NTFIDF(field, qfield);
  }
}
