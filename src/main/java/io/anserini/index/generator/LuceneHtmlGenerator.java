package io.anserini.index.generator;

import io.anserini.document.SourceDocument;
import io.anserini.index.transform.LuceneHtmlParserTransform;

public class LuceneHtmlGenerator extends LuceneDocumentGenerator<SourceDocument> {
  public LuceneHtmlGenerator() {
    super(new LuceneHtmlParserTransform());
  }
}