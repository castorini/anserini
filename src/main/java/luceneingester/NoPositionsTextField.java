package luceneingester;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/** A tokenized field with stored=false and without positions (only frequencies) */

public final class NoPositionsTextField extends Field {

  public static final FieldType TYPE_NOT_STORED = new FieldType();

  static {
    TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    TYPE_NOT_STORED.setTokenized(true);
    TYPE_NOT_STORED.freeze();
  }

  public NoPositionsTextField(String name, String value) {
    super(name, value, TYPE_NOT_STORED);
  }
}