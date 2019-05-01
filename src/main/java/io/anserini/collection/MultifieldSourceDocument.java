package io.anserini.collection;

import java.util.Map;

public interface MultifieldSourceDocument extends SourceDocument {
  /**
   * Returns a map of fields associated with this document.
   *
   * @return a map of fields associated with this document
   */
  Map<String, String> fields();
}
