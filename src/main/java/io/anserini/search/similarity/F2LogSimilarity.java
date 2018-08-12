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

public class F2LogSimilarity extends AxiomaticSimilarity {
  /**
   * F2Log with the supplied parameter values.
   * @param s Controls to what degree document length normalizes tf values.
   * @throws IllegalArgumentException if {@code s} is infinite or if {@code s} is
   *         not within the range {@code [0..1]}
   */
  public F2LogSimilarity(float s) {
    super(s);
  }

  /** F2Log with these default values:
   * <ul>
   *   <li>{@code s = 0.5}</li>
   * </ul>
   */
  public F2LogSimilarity() {
    this(0.5f);
  }
  
  @Override
  float idf(long docFreq, long docCount) {
    return (float) Math.log((1.0f + docCount) / docFreq);
  }

  @Override
  public String toString() {
    return "F2Log(s=" + s +")";
  }
}
