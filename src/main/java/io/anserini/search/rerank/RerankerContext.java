package io.anserini.search.rerank;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class RerankerContext {
  private final IndexSearcher searcher;
  private final Query query;
  private final Filter filter;
 
  public RerankerContext(IndexSearcher searcher) {
    this.searcher = searcher;
    this.filter = null;
    this.query = null;
  }

  public RerankerContext(IndexSearcher searcher, Query query, Filter filter) {
    this.searcher = searcher;
    this.query = query;
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
}
