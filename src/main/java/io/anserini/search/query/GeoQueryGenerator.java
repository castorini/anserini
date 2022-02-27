package io.anserini.search.query;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;

public class GeoQueryGenerator extends QueryGenerator {
  @Override
  public Query buildQuery(String field, Analyzer analyzer, String queryText) {
    return null;
  }
}
