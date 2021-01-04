package io.anserini.ltr.feature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexReaderUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.*;

public class DocumentContext {
  private IndexReader reader;
  private IndexSearcher searcher;
  public Document doc;
  public String docId;
  public Map<String, DocumentFieldContext> fieldContexts;
  private Set<String> fieldsToLoad;
  private String entityJson;
  public Map<String, String> entities;


  public DocumentContext(IndexReader reader, IndexSearcher searcher, Set<String> fieldsToLoad){
    this.reader = reader;
    this.searcher = searcher;
    this.fieldsToLoad = fieldsToLoad;
    this.entityJson = null;
    this.entities = new HashMap<>();

    fieldContexts = new HashMap<>();
    for(String fieldName: fieldsToLoad)
      fieldContexts.put(fieldName, new DocumentFieldContext(reader, searcher, fieldName));
  }



  public void updateDoc(String docId, int internalId) throws IOException {
    doc = reader.document(internalId);
    this.docId = docId;
    this.entityJson = doc.get(IndexArgs.ENTITY);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode entityRoot = mapper.readValue(entityJson, JsonNode.class);
    for (Iterator<Map.Entry<String, JsonNode>> it = entityRoot.fields(); it.hasNext(); ) {
      Map.Entry<String, JsonNode> entity = it.next();
      String text = entity.getKey();
      String type = entity.getValue().asText();
      entities.put(text, type);
    }
    for(String fieldName: fieldsToLoad)
      fieldContexts.get(fieldName).updateDoc(internalId);
  }

  public void generateBM25Stat(List<String> terms) throws IOException {
    for(String fieldName: fieldsToLoad)
      fieldContexts.get(fieldName).generateBM25Stat(terms);
  }
}
