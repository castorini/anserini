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

import io.anserini.ltr.feature.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Count of unique query terms
 */
public class UniqueTermCount implements FeatureExtractor {
  private String qfield;
  public UniqueTermCount() { this.qfield = "analyzed";}

  public UniqueTermCount(String qfield) { this.qfield = qfield; }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    Set<String> queryTokenSet = new HashSet<>(queryFieldContext.queryTokens);
    float uniqueQueryTerms = queryTokenSet.size();
    return uniqueQueryTerms;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_UniqueQueryTerms", qfield);
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
    return new UniqueTermCount(qfield);
  }
}
