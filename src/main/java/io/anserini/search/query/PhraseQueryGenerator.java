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

package io.anserini.search.query;

import io.anserini.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

import java.util.List;

/*
 * Phrase query generator
 */
public class PhraseQueryGenerator extends QueryGenerator {
  @Override
  public Query buildQuery(String field, Analyzer analyzer, String queryText) {
    List<String> tokens = AnalyzerUtils.analyze(analyzer, queryText);
  
    PhraseQuery.Builder builder = new PhraseQuery.Builder();
    for (String t : tokens) {
      builder.add(new Term(field, t));
    }
  
    return builder.build();
  }
}
