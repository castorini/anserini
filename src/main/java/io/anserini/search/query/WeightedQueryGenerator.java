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
import io.anserini.analysis.DefaultEnglishAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Query;

import java.util.List;

/**
 * Weighted query generator
 */
public class WeightedQueryGenerator extends QueryGenerator {

  final private BooleanQuery.Builder builder;
  private String field;
  private Analyzer analyzer;

  public WeightedQueryGenerator() {
    this.analyzer = DefaultEnglishAnalyzer.newDefaultInstance();
    this.field = "contents";
    this.builder = new BooleanQuery.Builder();
  }

  /**
   * Add query terms with default weight 1.0 and analyzer that is currently setted on this object
   * @param queryTerms query terms to add
   */
  public void addTerms(String queryTerms) {
    addTermsWithWeightWithAnalyzer(queryTerms, 1.0F, this.analyzer);
  }

  /**
   * Add query terms with given weight and analyzer that is currently setted on this object
   * @param queryTerms query terms to add
   * @param weight weight used per individual query term
   */
  public void addTermsWithWeight(String queryTerms, float weight) {
    addTermsWithWeightWithAnalyzer(queryTerms, weight, this.analyzer);
  }

  /**
   * Add query terms with default weight 1.0 and given analyzer
   * @param queryTerms query terms to add
   * @param analyzer analyzer to use to preprocess terms
   */
  public void addTermsWithAnalyzer(String queryTerms, Analyzer analyzer) {
    addTermsWithWeightWithAnalyzer(queryTerms, 1.0F, analyzer);
  }

  /**
   * Add query terms with given weight and given analyzer
   * @param queryTerms query terms to add
   * @param weight weight used per individual query term
   * @param analyzer analyzer to sue to preprocess terms
   */
  public void addTermsWithWeightWithAnalyzer(String queryTerms, float weight, Analyzer analyzer) {
    List<String> tokens = AnalyzerUtils.analyze(analyzer, queryTerms);
    for (String t : tokens) {
      TermQuery termQuery = new TermQuery(new Term(this.field, t));
      this.builder.add(new BoostQuery(termQuery, weight), BooleanClause.Occur.SHOULD);
    }
  }

  /**
   * field setter
   * @param field field to analyze on
   */
  public void setField(String field) {
    this.field = field;
  }

  /**
   * field getter
   * @return field
   */
  public String getField() {
    return this.field;
  }

  /**
   * analyzer setter
   * @param analyzer analyzer to use to preprocess query terms
   */
  public void setAnalyzer(Analyzer analyzer) {
    this.analyzer = analyzer;
  }

  /**
   * analyzer getter
   * @return analyzer
   */
  public Analyzer getAnalyzer() {
    return this.analyzer;
  }

  /**
   * Build the weighted query.
   * QueryText is a mandatory parameter as it is inherited from the superclass. All query terms given here will get
   * the default weight of 1.0
   *
   * @param field field used when giving query text here
   * @param analyzer analyzer used when giving query text here
   * @param queryText additional query text
   * @return
   */
  @Override
  public Query buildQuery(String field, Analyzer analyzer, String queryText) {
    List<String> tokens = AnalyzerUtils.analyze(analyzer, queryText);
    for (String t : tokens) {
      TermQuery termQuery = new TermQuery(new Term(this.field, t));
      this.builder.add(new BoostQuery(termQuery, 1.0F), BooleanClause.Occur.SHOULD);
    }
    return this.builder.build();
  }
}
