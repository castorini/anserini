package io.anserini.search;

import org.apache.lucene.document.Document;

public class Result {
  public String docid;
  public int lucene_docid;
  public float score;
  public Document lucene_document;

  public Result(String docid, int lucene_docid, float score, Document lucene_document) {
    this.docid = docid;
    this.lucene_docid = lucene_docid;
    this.score = score;
    this.lucene_document = lucene_document;
  }
}
