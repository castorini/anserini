/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.search.similarity;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;

public class ImpactSimilarity extends Similarity {

  public ImpactSimilarity() {
  }

  protected float idf(long docFreq, long docCount) {
    return 1.0f;
  }

  @Override
  public final long computeNorm(FieldInvertState state) {
    return 1;
  }

  @Override
  public final SimScorer scorer(float boost, CollectionStatistics collectionStats, TermStatistics... termStats) {
    return new ImpactScorer(boost);
  }

  @Override
  public String toString() {
    return "Impact()";
  }

  private static class ImpactScorer extends SimScorer {
    private final float boost;

    ImpactScorer(float boost) {
      this.boost = boost;
    }

    @Override
    public float score(float freq, long norm) {
      return freq*boost;
    }

    @Override
    public Explanation explain(Explanation freq, long encodedNorm) {
      return Explanation.match(freq.getValue(), "impact(freq=" + freq.getValue() + ")");
    }
  }
}
