package io.anserini.search.rerank;

public interface Reranker {
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context);
}
