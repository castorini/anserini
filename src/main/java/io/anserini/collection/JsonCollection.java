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

package io.anserini.collection;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.anserini.document.SourceDocument;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Class representing a document collection in JSON.
 *
 * This class reads "*.json" files in the input directory.
 * Inside each file is either a JSON Object (one document) or a JSON Array (multiple documents) or
 * a JSON Document on each line (not actually valid Json String)
 * Example of JSON Object:
 * <pre>
 * {
 *   "id": "doc1",
 *   "contents": "this is the contents."
 * }
 * </pre>
 * Example of JSON Array:
 * <pre>
 * [
 *   {
 *     "id": "doc1",
 *     "contents": "this is the contents 1."
 *   },
 *   {
 *     "id": "doc2",
 *     "contents": "this is the contents 2."
 *   }
 * ]
 * </pre>
 * Example of JSON objects, each per line (not actually valid Json String):
 * <pre>
 * {"id": "doc1", "contents": "this is the contents 1."}
 * {"id": "doc2", "contents": "this is the contents 2."}
 * </pre>
 *
 */
public class JsonCollection extends Collection<JsonCollection.Document> {

  public class FileSegment extends Collection<JsonCollection.Document>.FileSegment {
    protected FileSegment(Path path) throws IOException {
      dType = new Document(path.toString());
      bufferedReader = new BufferedReader(new FileReader(path.toString()));
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".json"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET, allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

  /**
   *
   * Here we actually read the whole json file at once.
   * If there are multiple JSON objects we use a global index i to track the progress.
   *
   */
  public static class Document implements SourceDocument {
    protected String id;
    protected String contents;
    private ArrayNode node;
    private int i;

    public Document(String path) {
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
    public Document readNextRecord(BufferedReader bRdr) throws IOException {
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
}
