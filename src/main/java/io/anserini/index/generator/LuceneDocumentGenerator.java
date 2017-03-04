package io.anserini.index.generator;

import io.anserini.document.SourceDocument;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.Document;

public abstract class LuceneDocumentGenerator<T extends SourceDocument> {
  public static final String FIELD_RAW = "raw";
  public static final String FIELD_BODY = "contents";
  public static final String FIELD_ID = "id";

  protected IndexCollection.Counters counters;
  protected IndexCollection.Args args;

  public void config(IndexCollection.Args args) {
    this.args = args;
  }

  public void setCounters(IndexCollection.Counters counters) {
    this.counters = counters;
  }

  public abstract Document transform(T src);
}
