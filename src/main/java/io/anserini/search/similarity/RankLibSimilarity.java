/**
 * Anserini: An information retrieval toolkit built on Lucene
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
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;

import java.io.IOException;

/**
 * Similarity that uses a Ranklib ranker to compute the score
 */
public class RankLibSimilarity extends Similarity {
  @Override
  public long computeNorm(FieldInvertState fieldInvertState) {
    return 0;
  }

  @Override
  public SimWeight computeWeight(CollectionStatistics collectionStatistics, TermStatistics... termStatisticses) {
    return null;
  }

  @Override
  public SimScorer simScorer(SimWeight simWeight, LeafReaderContext leafReaderContext) throws IOException {
    return null;
  }
}
