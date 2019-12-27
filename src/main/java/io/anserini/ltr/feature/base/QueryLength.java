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

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

import java.util.List;

/**
 * QueryCount
 * Compute the query length (number of terms in the query).
 */
public class QueryLength<T> implements FeatureExtractor<T> {

  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    List<String> queryTokens = context.getQueryTokens();
    return queryTokens.size();
  }

  @Override
  public String getName() {
    return "QueryLength";
  }
}
