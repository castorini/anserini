package io.anserini.rerank;

public interface Reranker {
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context);
}
