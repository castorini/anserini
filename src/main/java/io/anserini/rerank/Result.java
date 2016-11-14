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