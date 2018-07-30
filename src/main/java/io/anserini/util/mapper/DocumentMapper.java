package io.anserini.util.mapper;


import java.util.MissingResourceException;

public abstract class DocumentMapper {

  public boolean isCountDocumentMapper() {
    boolean isCountDocumentMapper;
    try {
      isCountDocumentMapper = this.getClass().equals(Class.forName("io.anserini.util.mapper.CountDocumentMapper"));
    } catch (ClassNotFoundException e) {
      throw new MissingResourceException("ClassNotFoundException", "CountDocumentMapper", "ClassNotFoundException");
    }
    return isCountDocumentMapper;
  }

}
