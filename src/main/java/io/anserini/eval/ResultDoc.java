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
  protected boolean long_docids;
  protected boolean docid_desc; // ranking by docid in descending order when there is score tie

  public ResultDoc(String docid, double score, boolean long_docids, boolean docid_desc) {
    this.docid = docid;
    this.score = score;
    this.long_docids = long_docids;
    this.docid_desc = docid_desc;
  }

  public ResultDoc(ResultDoc resultDoc, boolean long_docids, boolean docid_desc) {
    this(resultDoc.getDocid(), resultDoc.getScore(), long_docids, docid_desc);
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
      if (long_docids) {
        Long id1 = Long.parseLong(r.getDocid());
        Long id2 = Long.parseLong(this.docid);
        return docid_desc ? Long.compare(id1, id2) : Long.compare(id2, id1);
      } else {
        return r.getDocid().compareTo(this.docid);
      }
    }
    return otherScore.compareTo(thisScore);
  }
}
