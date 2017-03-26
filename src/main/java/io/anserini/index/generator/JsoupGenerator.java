package io.anserini.index.generator;

import io.anserini.document.SourceDocument;
import io.anserini.index.transform.JsoupStringTransform;

public class JsoupGenerator extends LuceneDocumentGenerator<SourceDocument> {
  public JsoupGenerator() {
    super(new JsoupStringTransform());
  }
}
