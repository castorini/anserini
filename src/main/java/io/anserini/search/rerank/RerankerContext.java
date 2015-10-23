package io.anserini.search.rerank;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class RerankerContext {
  private final IndexSearcher searcher;
  private final Query query;
  private final String queryText;
  private final Filter filter;
 
  public RerankerContext(IndexSearcher searcher, Query query, String queryText, Filter filter) {
    this.searcher = searcher;
    this.query = query;
    this.queryText = queryText;
    this.filter = filter;
  }

  public IndexSearcher getIndexSearcher() {
    return searcher;
  }

  public Filter getFilter() {
    return filter;
  }

  public Query getQuery() {
    return query;
  }

  public String getQueryText() {
    return queryText;
  }
}
