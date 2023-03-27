package io.anserini.search.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;

public interface FeatureGenerator{
  /**
   *  Generate queries with terms as features
   * @param field
   * @param analyzer
   * @param queryText
   * @return
   */
  Query buildFeatureQuery(String field, Analyzer analyzer, String queryText);
}
