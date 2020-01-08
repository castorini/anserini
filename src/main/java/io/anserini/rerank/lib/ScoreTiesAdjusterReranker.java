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

package io.anserini.rerank.lib;

import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;

/**
 * Reranker that perturbs score ties a tiny bit so that the rank order is consistent
 * with the score sort order.
 */
public class ScoreTiesAdjusterReranker implements Reranker {
  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {

    if (context != null && context.getSearchArgs().arbitraryScoreTieBreak) {
      return docs;
    }

    int dup = 0;
    for (int i=0; i<docs.documents.length; i++) {
      docs.scores[i] = Math.round(docs.scores[i] * 1e4f) / 1e4f;

      // If we encounter ties, we want to perturb the final score a tiny bit.
      // Here's the basic approach, by example. Say our starting ranked list was:
      //
      //   1 docA 23.439316
      //   2 docS 22.087432
      //   3 docT 22.087432
      //   4 docZ 21.602508
      //
      // The point is that we want to perturb the scores in a small way such that
      // the scores give us the exact sort order we want, independent of how any
      // external evaluation tool (e.g., trec_eval) breaks ties. We accomplish this
      // by rounding all scores to 1e-4, and then subtracting a minor delta of 1e-6
      // for each tie. So, the above becomes:
      //
      //   1 docA 23.4393   (dup=0)
      //   2 docS 22.0874   (dup=0)
      //   3 docT 22.0874 - (dup=1)*1e-6
      //   4 docZ 21.6025   (dup=0)
      //
      // Note that we can't use equality comparison directly to detect duplicates,
      // because in the case of multiple ties, we would have perturbed the scores,
      // leading the scores to not be equal (hence we check for score difference
      // greater than 1e-4).
      //
      // Why 1e-4 and 1e-6? If we make the former larger, than we lose score resolution
      // in the original score. If we make 1e-4 smaller we have to make 1e-6 smaller,
      // in which case we start bumping into floating point precision issues during
      // subtraction.
      if ( i == 0 || docs.scores[i-1] - docs.scores[i] > 1e-4f ) {
        dup = 0;
      } else {
        dup++;
        docs.scores[i] -= 1e-6f * dup;
      }
    }

    return docs;
  }
  
  @Override
  public String tag() { return ""; }
}
