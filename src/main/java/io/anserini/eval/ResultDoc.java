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

package io.anserini.eval;

/**
 * single document in the ranking results
 */
public class ResultDoc implements Comparable<ResultDoc> {
  protected double score;
  protected String docid;

  public ResultDoc(String docid, double score) {
    this.docid = docid;
    this.score = score;
  }

  public ResultDoc(ResultDoc resultDoc) {
    this(resultDoc.getDocid(), resultDoc.getScore());
  }

  public double getScore() {
    return score;
  }

  public String getDocid() {
    return docid;
  }

  @Override
  public int compareTo(ResultDoc r) {
    Double thisScore = new Double(this.score);
    Double otherScore = new Double(r.getScore());

    // first compare the score then compare the docid
    // We sort it REVERSELLY!!!
    if (thisScore.equals(otherScore)) {
      return r.getDocid().compareTo(this.docid);
    }
    return otherScore.compareTo(thisScore);
  }
}
