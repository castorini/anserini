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

package io.anserini.collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tukaani.xz.XZInputStream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A document collection in the
 * <a href="https://core.ac.uk/services/dataset/">CORE Dataset</a>
 * format of research outputs.
 * This class reads all <code>.json.xz</code> files in the input directory.
 * Inside each file should be a bunch of JSON objects, each per line:
 *
 * <pre>
 * {"id": "doc1", "contents": "this is the contents 1.", "year": 2019, "topics": ["topic 1"]}
 * {"id": "doc2", "contents": "this is the contents 2.", "year": 2020, "topics": ["topic 2"]}
 * </pre>
 */
public class CoreCollection extends DocumentCollection<CoreCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(CoreCollection.class);

  public CoreCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = Set.of(".json.xz", "json");
  }

  @Override
  public FileSegment<CoreCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file in a Core collection, typically containing multiple documents.
   */
  public static class Segment extends FileSegment<CoreCollection.Document> {
    private JsonNode node = null;
    private Iterator<JsonNode> iter = null; // iterator for JSON document array
    private MappingIterator<JsonNode> iterator; // iterator for JSON line objects

    public Segment(Path path) throws IOException {
      super(path);

      if (path.toString().endsWith(".xz")) {
        bufferedReader = new BufferedReader(new InputStreamReader(
          new XZInputStream(new FileInputStream(path.toString()))));
      } else {
        bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(path.toString())));
      }

      ObjectMapper mapper = new ObjectMapper();
      iterator = mapper.readerFor(JsonNode.class).readValues(bufferedReader);
      if (iterator.hasNext()) {
        node = iterator.next();
        if (node.isArray()) {
          iter = node.elements();
        }
      }
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (node == null) {
        throw new NoSuchElementException("JsonNode is empty");
      } else if (node.isObject()) {
        bufferedRecord = new CoreCollection.Document(node);
        if (iterator.hasNext()) { // if bufferedReader contains JSON line objects, we parse the next JSON into node
          node = iterator.next();
        } else {
          atEOF = true; // there is no more JSON object in the bufferedReader
        }
      } else if (node.isArray()) {
        if (iter != null && iter.hasNext()) {
          bufferedRecord = new CoreCollection.Document(node);
        } else {
          throw new NoSuchElementException("Reached end of JsonNode iterator");
        }
      } else {
        LOG.error("Error: invalid JsonNode type");
        throw new NoSuchElementException("Invalid JsonNode type");
      }
    }
  }

  /**
   * A document in a Core collection.
   */
  public static class Document implements SourceDocument {
    private String id;
    private String contents;
    private JsonNode jsonNode;

    public Document(JsonNode json) {
      id = (getJsonValue(json, "doi").equals("")) ?
        getJsonValue(json, "coreId") : getJsonValue(json, "doi");
      contents = getJsonValue(json, "title") + " " + getJsonValue(json, "abstract");
      jsonNode = json;
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String contents() {
      return contents;
    }

    @Override
    public String raw() {
      return contents;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public JsonNode jsonNode() {
      return jsonNode;
    }

    public String getJsonValue(JsonNode json, String key) {
      if (!json.has(key) || json.get(key).asText() == "null") {
        return "";
      }
      return json.get(key).asText();
    }
  }
}
