package io.anserini.util.mapper;


import io.anserini.collection.SourceDocument;
import io.anserini.util.MapCollections;

import java.util.MissingResourceException;

public abstract class DocumentMapper {

  protected MapCollections.Args args;

  public DocumentMapper(MapCollections.Args args) {
    this.args = args;
  }

  public boolean isCountDocumentMapper() {
    boolean isCountDocumentMapper;
    try {
      isCountDocumentMapper = this.getClass().equals(Class.forName("io.anserini.util.mapper.CountDocumentMapper"));
    } catch (ClassNotFoundException e) {
      throw new MissingResourceException("ClassNotFoundException", "CountDocumentMapper", "ClassNotFoundException");
    }
    return isCountDocumentMapper;
  }

  public abstract boolean process(SourceDocument d);

  public abstract void printResult(long durationMillis);
}
