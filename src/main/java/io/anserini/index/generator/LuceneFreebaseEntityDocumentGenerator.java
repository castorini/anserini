package io.anserini.index.generator;

import io.anserini.document.FreebaseEntityDocument;
import io.anserini.document.RDFDocument;
import io.anserini.document.SourceDocument;
import io.anserini.index.IndexFreebaseEntityCollection;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;

/**
 * Converts a {@link RDFDocument} into
 * a Lucene {@link Document}.
 */
public class LuceneFreebaseEntityDocumentGenerator {
  public static final String FIELD_ENTITY = "entity";
  public static final String FIELD_TITLE = "title";
  public static final String FIELD_TEXT = "text";


  protected IndexFreebaseEntityCollection.Counters counters;
  protected IndexFreebaseEntityCollection.Args args;

  public void config(IndexFreebaseEntityCollection.Args args) {
    this.args = args;
  }

  public void setCounters(IndexFreebaseEntityCollection.Counters counters) {
    this.counters = counters;
  }

  public Document createDocument(SourceDocument src) {
    if (!(src instanceof FreebaseEntityDocument)) {
      throw new IllegalArgumentException("Cannot create FreebaseEntity document from source document of type: " + src.getClass().getSimpleName());
    }

    FreebaseEntityDocument tripleDoc = (FreebaseEntityDocument) src;

    // Convert the triple doc to lucene doc
    Document doc = new Document();

    // Index subject as a StringField to allow searching
    Field entityField = new StringField(FIELD_ENTITY, cleanUri(tripleDoc.getEntityId()), Field.Store.YES);
    doc.add(entityField);

    FieldType fieldType = new FieldType();
    fieldType.setStored(true);
    fieldType.setStoreTermVectors(true);
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);

    Field titleField = new Field(FIELD_TITLE, tripleDoc.getTitle(), fieldType);
    doc.add(titleField);

    Field textField = new Field(FIELD_TEXT, tripleDoc.getText(), fieldType);
    doc.add(textField);

    tripleDoc.clear();
    return doc;
  }

  /**
   * Removes '<', '>' if they exist, lower case
   * <p>
   * TODO - replace ':' with '_' because query parser doesn't like it
   *
   * @param uri
   * @return
   */
  public static String cleanUri(String uri) {
    if (uri.charAt(0) == '<')
      return uri.substring(1, uri.length() - 1).toLowerCase();
    else
      return uri;
  }

}
