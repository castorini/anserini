/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

/**
 * A JSON document collection.
 * This class reads all <code>.json</code> files in the input directory.
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
public class JsonCollection extends DocumentCollection<JsonCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(JsonCollection.class);

  public JsonCollection() {
  }

  public JsonCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl", ".gz"));
  }

  @SuppressWarnings("unchecked")
  @Override
  public FileSegment<JsonCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<JsonCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }

  /**
   * A file in a JSON collection, typically containing multiple documents.
   */
  public static class Segment<T extends Document> extends FileSegment<T> {
    private JsonNode node = null;
    private Iterator<JsonNode> iter = null; // iterator for JSON document array
    private MappingIterator<JsonNode> iterator; // iterator for JSON line objects

    public Segment(Path path) throws IOException {
      super(path);

      if (path.toString().endsWith(".gz")) {
        InputStream stream = new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
        bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } else {
        bufferedReader = new BufferedReader(new FileReader(path.toString()));
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

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      ObjectMapper mapper = new ObjectMapper();
      iterator = mapper.readerFor(JsonNode.class).readValues(bufferedReader);
      if (iterator.hasNext()) {
        node = iterator.next();
        if (node.isArray()) {
          iter = node.elements();
        }
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readNext() throws NoSuchElementException {
      if (node == null) {
        throw new NoSuchElementException("JsonNode is empty");
      } else if (node.isObject()) {
        bufferedRecord = (T) createNewDocument(node);
        if (iterator.hasNext()) { // if bufferedReader contains JSON line objects, we parse the next JSON into node
          node = iterator.next();
        } else {
          atEOF = true; // there is no more JSON object in the bufferedReader
        }
      } else {
        LOG.error("Error: invalid JsonNode type");
        throw new NoSuchElementException("Invalid JsonNode type");
      }
    }

    protected Document createNewDocument(JsonNode json) {
      return new Document(node);
    }
  }

  /**
   * A document in a JSON collection.
   */
  public static class Document extends MultifieldSourceDocument {
    private String id;
    private String contents;
    private String raw;
    private Map<String, String> fields;

    public static Document fromString(String raw) throws IOException {
      ObjectMapper mapper = new ObjectMapper();
      MappingIterator<JsonNode> iterator =
          mapper.readerFor(JsonNode.class).readValues(new ByteArrayInputStream(raw.getBytes()));
      if (iterator.hasNext()) {
        return new Document(iterator.next());
      }

      return null;
    }

    protected Document() {
      // This is no-op constructor for sub-classes that want to do everything themselves.
    }

    public Document(JsonNode json) {
      this.raw = json.toPrettyString();
      this.fields = new HashMap<>();

      json.fields().forEachRemaining( e -> {
        if ("id".equals(e.getKey())) {
          this.id = json.get("id").asText();
        } else if ("contents".equals(e.getKey())) {
          this.contents = json.get("contents").asText();
        } else {
          this.fields.put(e.getKey(), e.getValue().asText());
        }
      });
    }

    @Override
    public String id() {
      if (id == null) {
        throw new RuntimeException("JSON document has no \"id\" field!");
      }
      return id;
    }

    @Override
    public String contents() {
      if (contents == null) {
        throw new RuntimeException("JSON document has no \"contents\" field!");
      }
      return contents;
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    @Override
    public Map<String, String> fields() {
      return fields;
    }
  }
}
