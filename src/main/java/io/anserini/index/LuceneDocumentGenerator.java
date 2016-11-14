package io.anserini.index;

import io.anserini.document.SourceDocument;
import org.apache.lucene.document.Document;

public abstract class LuceneDocumentGenerator<T extends SourceDocument> {
  public static final String FIELD_BODY = "contents";
  public static final String FIELD_ID = "id";

  protected MultithreadedIndexer.Counters counters;
  protected IndexArgs args;

  public void config(IndexArgs args) {
    this.args = args;
  }

  public void setCounters(MultithreadedIndexer.Counters counters) {
    this.counters = counters;
  }

  public abstract Document transform(T src);
}
