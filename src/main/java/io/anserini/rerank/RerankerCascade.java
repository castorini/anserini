/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.rerank;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a cascade of rerankers, applied in sequence.
 */
public class RerankerCascade {
  final List<Reranker> rerankers = new ArrayList<>();

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
   * @param context reranker context
   * @return reranked results
   */
  @SuppressWarnings("unchecked")
  public ScoredDocuments run(ScoredDocuments docs, RerankerContext context) {
    ScoredDocuments results = docs;

    for (Reranker reranker : rerankers) {
      results = reranker.rerank(results, context);
    }

    return results;
  }
}
