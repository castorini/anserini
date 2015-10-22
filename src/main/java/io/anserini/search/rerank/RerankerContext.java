package io.anserini.search.rerank;

import org.apache.lucene.search.IndexSearcher;

public class RerankerContext {
  public final IndexSearcher searcher;
  public RerankerContext(IndexSearcher searcher) {
    this.searcher = searcher;
  }

  public IndexSearcher getIndexSearcher() {
    return searcher;
  }
}
