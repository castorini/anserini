package io.anserini.rerank;

import java.util.Set;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class RerankerContext {
  private final IndexSearcher searcher;
  private final Query query;
  private final String queryId;
  private final String queryText;
  private final Set<String> queryTokens;
  private final Filter filter;
 
  public RerankerContext(IndexSearcher searcher, Query query, String queryId, String queryText,
      Set<String> queryTokens, Filter filter) {
    this.searcher = searcher;
    this.query = query;
    this.queryId = queryId;
    this.queryText = queryText;
    this.queryTokens = queryTokens;
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

  public String getQueryId() {
    return queryId;
  }

  public String getQueryText() {
    return queryText;
  }

  public Set<String> getQueryTokens() {
    return queryTokens;
  }
}
