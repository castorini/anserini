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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class MsMarcoV2DocCollection extends DocumentCollection<MsMarcoV2DocCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(JsonCollection.class);

  public MsMarcoV2DocCollection(Path path) {
    this.path = path;
  }

  public MsMarcoV2DocCollection() {
  }

  @SuppressWarnings("unchecked")
  @Override
  public FileSegment<MsMarcoV2DocCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<MsMarcoV2DocCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
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
      }
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);

      String jsonString = bufferedReader.lines().collect(Collectors.joining("\n"));

      ObjectMapper mapper = new ObjectMapper();
      node = mapper.readTree(jsonString);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readNext() throws NoSuchElementException {
      if (node == null) {
        throw new NoSuchElementException("JsonNode is empty");
      } else if (node.isObject()) {
        bufferedRecord = (T) createNewDocument(node);
        if (iterator != null && iterator.hasNext()) { // if bufferedReader contains JSON line objects, we parse the next JSON into node
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
  public static class Document implements SourceDocument {
    private String id;
    private String raw;
    private Map<String, String> fields;

    public Document(JsonNode json) {
      this.raw = json.toPrettyString();
      this.fields = new HashMap<>();

      json.fields().forEachRemaining(e -> {
        if ("docid".equals(e.getKey())) {
          this.id = json.get("docid").asText();
        } else {
          this.fields.put(e.getKey(), e.getValue().asText());
        }
      });
    }

    @Override
    public String id() {
      if (id == null) {
        throw new RuntimeException("Document does not have the required \"docid\" field!");
      }
      return id;
    }

    @Override
    public String contents() {
      // We handle both the original corpus as well as our segmented variant.
      // They differ in the key they use to store the main contents:
      // The original corpus uses "body", while our variant uses "segment".
      // This difference allows us to easily figure out which version we're processing,
      // and to (potentially) process them differently (although we're not doing so currently).
      if (!fields.containsKey("url") || !fields.containsKey("title") || !fields.containsKey("headings") ||
              (!fields.containsKey("body") && !fields.containsKey("segment")) ) {
        throw new RuntimeException("Document is missing required fields!");
      }

      // Here, in both the original corpus and the segmented corpus, we index all the fields concatenated together.
      // Empirically, this has proven to be the most effective (based on the dev queries).
      // It might be nice to make "what to index" configurable, but there's not a good way to specify this
      // in the indexing driver via command-line parameters.
      if (fields.containsKey("body")) {
        return fields.get("url") + " " + fields.get("title") + " " + fields.get("headings") + " " + fields.get("body");
      }

      return fields.get("url") + " " + fields.get("title") + " " + fields.get("headings") + " " + fields.get("segment");
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public boolean indexable() {
      return true;
    }
  }
}
