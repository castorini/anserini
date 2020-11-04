package io.anserini.index.generator;

import org.apache.lucene.document.StoredField;

import io.anserini.collection.WarcBaseDocument;
import io.anserini.index.IndexArgs;
import org.apache.lucene.document.*;

public class WarcGenerator extends DefaultLuceneDocumentGenerator<WarcBaseDocument> {
  protected IndexArgs args;

  public enum WarcField {
    DATE("date"),
    URL("url");

    public final String name;

    WarcField(String s) {
      name = s;
    }
  }

  public WarcGenerator(IndexArgs args) {
    super(args);
    this.args = args;
  }

  public Document createDocument(WarcBaseDocument doc) throws GeneratorException {
    Document document = super.createDocument(doc);
    document.add(new StringField(WarcField.DATE.name, doc.getDate(), Field.Store.YES));
    document.add(new StringField(WarcField.URL.name, doc.getURL(), Field.Store.YES));
    return document;
  }
}

