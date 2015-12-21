package io.anserini.index;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * TermVectors enabled {@link org.apache.lucene.document.TextField}, which is indexed, tokenized, not stored, with term vectors.
 */
public final class TermVectorsTextField extends Field {

  /**
   * Indexed, tokenized, not stored, term vectors enabled
   */
  public static final FieldType TYPE_NOT_STORED = new FieldType();

  /**
   *  Copied from {@link org.apache.lucene.document.TextField}
   */
  static {
    TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    TYPE_NOT_STORED.setTokenized(true);
    TYPE_NOT_STORED.setStoreTermVectors(true);
    TYPE_NOT_STORED.freeze();
  }

  public TermVectorsTextField(String name, String value) {
    super(name, value, TYPE_NOT_STORED);
  }
}
