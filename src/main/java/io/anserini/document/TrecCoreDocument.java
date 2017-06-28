package io.anserini.document;

import io.anserini.document.nyt.NYTCorpusDocument;
import io.anserini.document.nyt.NYTCorpusDocumentParser;

import java.io.File;
import java.io.IOException;

/**
 * A TREC Core document.
 */
public class TrecCoreDocument implements  SourceDocument{
  private String id;
  private String contents;

  public SourceDocument readNextRecord(File fileName) throws IOException {
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
