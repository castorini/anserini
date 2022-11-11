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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * A document collection for BEIR corpora flattens the BEIR fields.
 * The "title" and "text" fields are concatenated into the "contents" field for indexing.
 */
public class BeirFlatCollection extends DocumentCollection<BeirFlatCollection.Document> {

  public BeirFlatCollection(Path path) {
    this.path = path;
  }

  public BeirFlatCollection() {
  }

  @Override
  public FileSegment<BeirFlatCollection.Document> createFileSegment(Path p) throws IOException {
    return new BeirFlatCollection.Segment<>(p);
  }

  @Override
  public FileSegment<BeirFlatCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new BeirFlatCollection.Segment<>(bufferedReader);
  }

  public static class Segment<T extends BeirFlatCollection.Document> extends JsonCollection.Segment<T> {
    public Segment(Path path) throws IOException {
      super(path);
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
    }

    @Override
    protected Document createNewDocument(JsonNode json) {
      return new Document(json);
    }
  }

  public static class Document extends JsonCollection.Document {
    private final String id;
    private final String contents;
    private final String raw;
    private Map<String, String> fields;

    public Document(JsonNode json) {
      super();

      this.raw = json.toPrettyString();
      this.id = json.get("_id").asText();

      this.contents = new StringBuilder().append(json.get("title").asText())
          .append("\n").append(json.get("text").asText()).toString();

      // We're not going to index any other fields, so just initialize an empty map.
      this.fields = new HashMap<>();
    }

    @Override
    public String id() {
      if (id == null) {
        throw new RuntimeException("JSON document has no \"_id\" field!");
      }
      return id;
    }

    @Override
    public String contents() {
      if (contents == null) {
        throw new RuntimeException("JSON document has no contents that could be parsed!");
      }
      return contents;
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public Map<String, String> fields() {
      return fields;
    }
  }
}
