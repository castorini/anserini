package io.anserini.document;

import org.apache.lucene.document.Document;

public interface SourceDocumentTransformer<T extends SourceDocument> {
  Document transform(T src);
}
