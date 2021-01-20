/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  private IndexReaderUtils readerUtils;
  public Document doc;
  public String docId;
  public Map<String, List<String>> entities;
  public Map<String, DocumentFieldContext> fieldContexts;
  private Set<String> fieldsToLoad;
  private ObjectMapper mapper = new ObjectMapper();

  public DocumentContext(IndexReader reader, IndexSearcher searcher, Set<String> fieldsToLoad){
    this.reader = reader;
    this.searcher = searcher;
    this.fieldsToLoad = fieldsToLoad;
    this.entities = new HashMap<>();

    fieldContexts = new HashMap<>();
    for(String fieldName: fieldsToLoad)
      fieldContexts.put(fieldName, new DocumentFieldContext(reader, searcher, fieldName));
  }


  public void updateDoc(String docId, int internalId) throws IOException {
    doc = reader.document(internalId);
    this.docId = docId;
    String entityJson = doc.get(IndexArgs.ENTITY);
    if (entityJson != null) {
      JsonNode root = mapper.readValue(entityJson, JsonNode.class);
      Iterator<Map.Entry<String, JsonNode>> ents = root.fields();
      while (ents.hasNext()) {
        Map.Entry<String, JsonNode> ent = ents.next();
        String entText = ent.getKey();
        String nameEnt = ent.getValue().asText();
        List<String> temp;
        if (entities.containsKey(nameEnt)) {
          temp = entities.get(nameEnt);
        } else {
          temp = new ArrayList<>();
        }
        temp.add(entText);
        entities.put(nameEnt, temp);
      }
    }
    for(String fieldName: fieldsToLoad)
      fieldContexts.get(fieldName).updateDoc(internalId);
  }

  public void generateBM25Stat(List<String> terms) throws IOException {
    for(String fieldName: fieldsToLoad)
      fieldContexts.get(fieldName).generateBM25Stat(terms);
  }
}
