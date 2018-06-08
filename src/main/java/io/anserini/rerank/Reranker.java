package io.anserini.rerank;

import org.apache.lucene.document.Document;

public interface Reranker {

  public<K> ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context);
}
