package io.anserini.ltr.feature;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.util.*;

public class DocumentContext {
  private IndexReader reader;
  public Document doc;
  public Map<String, FieldContext> fieldContexts;
  private Set<String> fieldsToLoad;

  public DocumentContext(IndexReader reader, Set<String> fieldsToLoad){
    this.reader = reader;
    this.fieldsToLoad = fieldsToLoad;

    fieldContexts = new HashMap<>();
    for(String fieldName: fieldsToLoad)
      fieldContexts.put(fieldName, new FieldContext(reader, fieldName));
  }


  public void updateDoc(int internalId) throws IOException {
    doc = reader.document(internalId, fieldsToLoad);
    for(String fieldName: fieldsToLoad)
      fieldContexts.get(fieldName).updateDoc(internalId);
  }
}
