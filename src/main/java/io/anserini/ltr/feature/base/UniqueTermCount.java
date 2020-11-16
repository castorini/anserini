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

import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Count of unique query terms
 */
public class UniqueTermCount implements FeatureExtractor {

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    if(queryContext.cache.containsKey(getName())){
      return queryContext.cache.get(getName());
    } else {
      Set<String> queryTokenSet = new HashSet<>(queryContext.queryTokens);
      float uniqueQueryTerms = queryTokenSet.size();
      queryContext.cache.put(getName(), uniqueQueryTerms);
      return uniqueQueryTerms;
    }
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    return queryContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    return "UniqueQueryTerms";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new UniqueTermCount();
  }
}
