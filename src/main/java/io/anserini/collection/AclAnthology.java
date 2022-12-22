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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A YAML document collection for the ACL anthology.
 */
public class AclAnthology extends DocumentCollection<AclAnthology.Document> {
  private JsonNode volumes;
  private static final Logger LOG = LogManager.getLogger(AclAnthology.class);

  public AclAnthology() {
  }

  public AclAnthology(Path path) {
    this.path = Paths.get(path.toString(), "/papers"); // Path containing files to iterate
    this.allowedFileSuffix = Set.of(".yaml");

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      this.volumes = mapper.readValue(new File(path.toString(), "/volumes.yaml"), JsonNode.class);
    } catch (IOException e) {
      LOG.error("Unable to open volumes.yaml");
      return;
    }
  }

  @Override
  public FileSegment<AclAnthology.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<AclAnthology.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }

  /**
   * A file in a YAML collection for ACL papers containing multiple entries.
   */
  public class Segment extends FileSegment<AclAnthology.Document> {
    private Map.Entry<String, JsonNode> nodeEntry = null;
    private Iterator<Map.Entry<String, JsonNode>> iter = null; // iterator for JSON document object
    private String rawContent = null; // raw content from buffered string


    public Segment(Path path) throws IOException {
      super(path);

      // read YAML file into JsonNode format
      bufferedReader = new BufferedReader(new FileReader(path.toString()));
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      MappingIterator<JsonNode> iterator = mapper.readerFor(JsonNode.class).readValues(bufferedReader);

      if (iterator.hasNext()) {
        JsonNode root = iterator.next();
        iter = root.fields();
        if (iter.hasNext()) {
          nodeEntry = iter.next();
        }
      }
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      rawContent = bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (nodeEntry == null && rawContent == null) {
        throw new NoSuchElementException("JsonNode and Raw content are empty");
      } else {
        if (rawContent != null) {
          bufferedRecord = new AclAnthology.Document(rawContent);
          atEOF = true; // there is no more JSON object in the bufferedReader
        } else {
          bufferedRecord = new AclAnthology.Document(nodeEntry);
          if (iter.hasNext()) {
            nodeEntry = iter.next();
          } else {
            atEOF = true; // there is no more JSON object in the bufferedReader
          }
        }
      }
    }
  }

  /**
   * A document in a JSON collection.
   */
  public class Document implements SourceDocument {
    private String id;
    private String contents;
    private JsonNode paper;
    private JsonNode volume;
    private List<String> authors;
    private List<String> venues;
    private List<String> sigs;

    public Document(Map.Entry<String, JsonNode> jsonEntry) {
      id = jsonEntry.getKey();
      paper = jsonEntry.getValue();
      contents = getOrDefault(paper, "title") + " " + getOrDefault(paper, "abstract_html");

      // Process author facets
      authors = new ArrayList<>();
      if (paper.has("author")) {
        ArrayNode authorNode = (ArrayNode) paper.get("author");
        authorNode.elements().forEachRemaining(node ->
          authors.add(((ObjectNode) node).get("full").asText())
        );
      }

      // Retrieve parent volume metadata
      String parentVolumeId = paper.get("parent_volume_id").asText();
      volume = AclAnthology.this.volumes.get(parentVolumeId);

      // Process venue facets
      venues = new ArrayList<>();
      ArrayNode venuesNode = (ArrayNode) volume.get("venues");
      venuesNode.elements().forEachRemaining(node -> venues.add(node.asText()));

      // Process SIG facets
      sigs = new ArrayList<>();
      ArrayNode sigsNode = (ArrayNode) volume.get("sigs");
      sigsNode.elements().forEachRemaining(node -> sigs.add(node.asText()));
    }

    public Document(String rawContent) {
      contents = rawContent;
    }

    @Override
    public String id() {
      if (id == null) {
        throw new RuntimeException("JSON document has no \"id\" field");
      }
      return id;
    }

    @Override
    public String contents() {
      if (contents == null) {
        throw new RuntimeException("JSON document has no \"contents\" field");
      }
      return contents;
    }

    @Override
    public String raw() {
      if (contents == null) {
        throw new RuntimeException("JSON document has no \"contents\" field");
      }
      return contents;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public JsonNode paper() {
      return paper;
    }

    public JsonNode volume() {
      return volume;
    }

    public List<String> authors() {
      return authors;
    }

    public List<String> venues() {
      return venues;
    }

    public List<String> sigs() {
      return sigs;
    }

    private String getOrDefault(JsonNode json, String key) {
      if (json.has(key)) {
        return json.get(key).asText();
      }
      return "";
    }
  }
}
