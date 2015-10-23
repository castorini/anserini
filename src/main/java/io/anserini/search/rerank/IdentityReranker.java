package io.anserini.search.rerank;

public class IdentityReranker implements Reranker {
  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    return docs;
  }
}
