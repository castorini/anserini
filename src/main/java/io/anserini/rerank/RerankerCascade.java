package io.anserini.rerank;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Representation of a cascade of rerankers, applied in sequence.
 */
public class RerankerCascade {
  final List<Reranker> rerankers = Lists.newArrayList();

  /**
   * Adds a reranker to this cascade.
   *
   * @param reranker reranker to add
   * @return this cascade for method chaining
   */
  public RerankerCascade add(Reranker reranker) {
    rerankers.add(reranker);

    return this;
  }

  /**
   * Runs this cascade.
   *
   * @param docs input documents
   * @return reranked results
   */
  public ScoredDocuments run(ScoredDocuments docs, RerankerContext context) {
    ScoredDocuments results = docs;

    for (Reranker reranker : rerankers) {
      results = reranker.rerank(results, context);
    }

    return results;
  }
}
