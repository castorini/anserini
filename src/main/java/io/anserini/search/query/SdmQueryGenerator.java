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

import io.anserini.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import java.util.List;

/* Build the Term Dependency query. See:
 * D. Metzler and W. B. Croft. A markov random field model for term dependencies. In SIGIR ’05.
 */
public class SdmQueryGenerator extends QueryGenerator {
  private final float termWeight;
  private final float orderWindowWeight;
  private final float unorderWindowWeight;
  
  public SdmQueryGenerator() {
    this.termWeight = 0.85f;
    this.orderWindowWeight = 0.1f;
    this.unorderWindowWeight = 0.05f;
  }
  
  public SdmQueryGenerator(float termWeight, float orderWindowWeight, float unorderWindowWeight) {
    this.termWeight = termWeight;
    this.orderWindowWeight = orderWindowWeight;
    this.unorderWindowWeight = unorderWindowWeight;
  }
  
  /*
  * Sequential Dependency Model
  */
  @Override
  public Query buildQuery(String field, Analyzer analyzer, String queryText) {
    List<String> tokens = AnalyzerUtils.tokenize(analyzer, queryText);
    
    BooleanQuery.Builder termsBuilder = new BooleanQuery.Builder();
    if (tokens.size() == 1) {
      termsBuilder.add(new TermQuery(new Term(field, tokens.get(0))), BooleanClause.Occur.SHOULD);
      return termsBuilder.build();
    }
    
    BooleanQuery.Builder orderedWindowBuilder = new BooleanQuery.Builder();
    BooleanQuery.Builder unorderedWindowBuilder = new BooleanQuery.Builder();
    for (int i = 0; i < tokens.size()-1; i++) {
      termsBuilder.add(new TermQuery(new Term(field, tokens.get(i))), BooleanClause.Occur.SHOULD);
      
      SpanTermQuery t1 = new SpanTermQuery(new Term(field, tokens.get(i)));
      SpanTermQuery t2 = new SpanTermQuery(new Term(field, tokens.get(i+1)));
      SpanNearQuery orderedQ = new SpanNearQuery(new SpanQuery[] {t1, t2}, 1, true);
      SpanNearQuery unorderedQ = new SpanNearQuery(new SpanQuery[] {t1, t2}, 8, false);
      
      orderedWindowBuilder.add(orderedQ, BooleanClause.Occur.SHOULD);
      unorderedWindowBuilder.add(unorderedQ, BooleanClause.Occur.SHOULD);
    }
    termsBuilder.add(new TermQuery(new Term(field, tokens.get(tokens.size()-1))), BooleanClause.Occur.SHOULD);
    
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(new BoostQuery(termsBuilder.build(), termWeight), BooleanClause.Occur.SHOULD);
    builder.add(new BoostQuery(orderedWindowBuilder.build(), orderWindowWeight), BooleanClause.Occur.SHOULD);
    builder.add(new BoostQuery(unorderedWindowBuilder.build(), unorderWindowWeight), BooleanClause.Occur.SHOULD);
    
    return builder.build();
  }
}
