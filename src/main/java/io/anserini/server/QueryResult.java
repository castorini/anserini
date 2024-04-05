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