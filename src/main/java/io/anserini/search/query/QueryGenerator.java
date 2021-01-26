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

package io.anserini.search.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;

import java.util.Map;

public abstract class QueryGenerator {
  public abstract Query buildQuery(String field, Analyzer analyzer, String queryText);

  public Query buildQuery(Map<String, Float> fields, Analyzer analyzer, String queryText) {
    throw new UnsupportedOperationException("The query generator does not support multi-field searches.");
  }
}
