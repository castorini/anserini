package io.anserini.document;

import io.anserini.document.nyt.NYTCorpusDocument;
import io.anserini.document.nyt.NYTCorpusDocumentParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

/**
 * A TREC Core document.
 */
public class TrecCoreDocument implements SourceDocument {
  protected String id;
  protected String contents;
  protected File file;

  public TrecCoreDocument(File file) {
    this.file = file;
  }

  @Override
  public TrecCoreDocument readNextRecord(BufferedReader bRdr) throws Exception {
    return readNextRecord(file);
  }

  public TrecCoreDocument readNextRecord(File fileName) throws IOException {
    NYTCorpusDocumentParser nytParser = new NYTCorpusDocumentParser();
    NYTCorpusDocument nytDoc = nytParser.parseNYTCorpusDocumentFromFile(fileName, false);

    id = String.valueOf(nytDoc.getGuid());
    contents = nytDoc.getBody();

    return this;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String content() {
    return contents;
  }

  @Override
  public boolean indexable() {
    return true;
  }
}
