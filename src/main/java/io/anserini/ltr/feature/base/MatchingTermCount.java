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
 * Computes the number of query terms that are found in the document. If there are three terms in
 * the query and all three terms are found in the document, the feature value is three.
 */
public class MatchingTermCount implements FeatureExtractor {
  private String field;

  public MatchingTermCount() { this.field = IndexArgs.CONTENTS; }

  public MatchingTermCount(String field) { this.field = field; }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    int matching = 0;
    for(String queryToken : queryContext.queryTokens) {
      long tf = context.getTermFreq(queryToken);
      if(tf!=0)
        matching++;
    }
    return matching;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return String.format("%s_MatchingTermCount", field);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    return new MatchingTermCount(field);
  }
}
