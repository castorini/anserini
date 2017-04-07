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

package io.anserini.qa.passage;

public class ScoredPassage implements Comparable<ScoredPassage> {
  String sentence;
  double score;
  float docScore;

  public ScoredPassage(String sentence, double score, float docScore) {
    this.sentence = sentence;
    this.score = score;
    this.docScore = docScore;
  }

  public String getSentence() {
    return sentence;
  }

  public double getScore() {
    return score;
  }

  public double getDocScore() {
    return  docScore;
  }

  @Override
  public int compareTo(ScoredPassage o) {
    return -1 * Double.compare(score, o.score);
  }
}
