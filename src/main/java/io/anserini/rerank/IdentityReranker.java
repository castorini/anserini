package io.anserini.rerank;

public class IdentityReranker implements Reranker {
  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    return docs;
  }
}
