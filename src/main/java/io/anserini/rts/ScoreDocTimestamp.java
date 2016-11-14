package io.anserini.rts;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

public class ScoreDocTimestamp extends ScoreDoc {
  long timestamp;
  Document fullDocument;

  public ScoreDocTimestamp(int doc, float score) {
    super(doc, score);
    // TODO Auto-generated constructor stub
  }

  public ScoreDocTimestamp(int doc, float score, long timestamp, Document fullDocument) {
    super(doc, score);
    this.timestamp = timestamp;
    this.fullDocument = fullDocument;

    // TODO Auto-generated constructor stub
  }

}
