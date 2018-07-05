/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 *
 * Here we actually read the whole json file at once.
 * If there are multiple JSON objects we use a global index i to track the progress.
 *
 */
public class JsonDocument implements SourceDocument {
  protected String id;
  protected String contents;
  private ArrayNode node;
  private int i;

  public JsonDocument(String path) {
    try {
      JsonParser jsonParser = new JsonFactory().createParser(new BufferedReader(new FileReader(path)));
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
      node = objectMapper.readTree(jsonParser);
    } catch (IOException e) {
      node = null; // When the json file does not contain any json objects, set node to null
    }
    i = 0;
  }

  @Override
  public JsonDocument readNextRecord(BufferedReader bRdr) throws IOException {
    if (node == null) {
      // try to read one JSON Object per line
      String line;
      while ((line = bRdr.readLine()) != null) {
        JsonNode json = new JsonFactory().createParser(line).readValueAsTree();
        id = json.get("id").asText();
        contents = json.get("contents").asText();
        return this;
      }
    } else if (i < node.size()) {
      JsonNode json = node.get(i);
      id = json.get("id").asText();
      contents = json.get("contents").asText();
      i++;
      return this;
    }
    return null;
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
