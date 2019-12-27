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

import org.apache.lucene.document.Document;

/**
 * Shared Result object used by rerankers to sort and rerank the documents
 */
// Sort by score, break ties by higher docid first (i.e., more temporally recent first)
public class Result implements Comparable<Result> {
  public float score;
  public long docid;
  public int id;
  public Document document;

  public Result() {}

  public Result(Document doc, int id, float score, long docId) {
    this.docid = docId;
    this.document = doc;
    this.id = id;
    this.score = score;
  }

  public int compareTo(Result other) {
    if (this.score > other.score) {
      return -1;
    } else if (this.score < other.score) {
      return 1;
    } else {
      if (this.docid > other.docid) {
        return -1;
      } else if (this.docid < other.docid) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  public boolean equals(Object other) {
    if (other == null) {
      return false;
    } if (other.getClass() != this.getClass()) {
      return false;
    }

    return ((Result) other).docid == this.docid;
  }
}