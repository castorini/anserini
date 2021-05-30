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

import io.anserini.ltr.DocumentContext;
import io.anserini.ltr.FeatureExtractor;
import io.anserini.ltr.QueryContext;
import io.anserini.ltr.QueryFieldContext;

/**
 * QueryCount
 * Compute the query length (number of terms in the query).
 */
public class QueryLength implements FeatureExtractor {
  private String qfield;
  public QueryLength() {
    this.qfield = "analyzed";
  }

  public QueryLength(String qfield){
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.queryTokens.size();
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    String name = this.getClass().getSimpleName();
    return String.format("%s_%s",qfield,name);
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  @Override
  public FeatureExtractor clone() {
    return new QueryLength(qfield);
  }
}
