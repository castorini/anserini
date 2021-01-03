package io.anserini.ltr.feature;

import io.anserini.index.IndexReaderUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.*;

public class DocumentContext {
  private IndexReader reader;
  private IndexSearcher searcher;
  private IndexReaderUtils readerUtils;
  public Document doc;
  public String docId;
  public String entityJson;
  public Map<String, FieldContext> fieldContexts;
  private Set<String> fieldsToLoad;

  public DocumentContext(IndexReader reader, IndexSearcher searcher, Set<String> fieldsToLoad){
    this.reader = reader;
    this.searcher = searcher;
    this.fieldsToLoad = fieldsToLoad;
    this.entityJson = null;

    fieldContexts = new HashMap<>();
    for(String fieldName: fieldsToLoad)
      fieldContexts.put(fieldName, new FieldContext(reader, searcher, fieldName));
  }


  public void updateDoc(String docId, int internalId) throws IOException {
    doc = reader.document(internalId, fieldsToLoad);
    this.docId = docId;
    this.entityJson = readerUtils.documentEntity(reader,docId);
    for(String fieldName: fieldsToLoad)
      fieldContexts.get(fieldName).updateDoc(internalId);
  }

  public void generateBM25Stat(List<String> terms) throws IOException {
    for(String fieldName: fieldsToLoad)
      fieldContexts.get(fieldName).generateBM25Stat(terms);
  }
}
