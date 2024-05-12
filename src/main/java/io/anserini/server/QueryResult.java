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

package io.anserini.server;

public class QueryResult {
  private String docid;
  private String content;
  private float score;

  public QueryResult(String docid, String content, float score) {
    this.docid = docid;
    this.content = content;
    this.score = score;
  }

  // Getters
  public String getDocid() {
    return docid;
  }

  public String getContent() {
    return content;
  }

  public float getScore() {
    return score;
  }

  // Setters
  public void setDocid(String docid) {
    this.docid = docid;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setRank(int rank) {
    this.score = rank;
  }
}