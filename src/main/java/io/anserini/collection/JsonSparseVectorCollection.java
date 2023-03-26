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

/**xxw
 * A JSON document collection where the user can specify directly the vector to be indexed.
 */
public class JsonTermWeightCollection extends DocumentCollection<JsonTermWeightCollection.Document> {
  public JsonTermWeightCollection(Path path) {
    this.path = path;
  }

  @Override
  public FileSegment<JsonTermWeightCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new JsonTermWeightCollection.Segment<>(bufferedReader);
  }
  @Override
  public FileSegment<JsonTermWeightCollection.Document> createFileSegment(Path path) throws IOException {
    return new JsonTermWeightCollection.Segment<>(path);
  }
  public static class Segment<T extends JsonTermWeightCollection.Document> extends JsonCollection.Segment<T> {
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

  public static class Document extends JsonCollection.Document implements SourceTermWeightDocument {
    private Map<String, Float> vector;
    public Document(JsonNode json) {
      super(json);
      this.vector = new HashMap<>();
      // We're going to take the map associated with "vector" and generate pseudo-document.
      JsonNode vectorNode = json.get("vector");

      // Iterate through the features:
      final StringBuilder sb = new StringBuilder();
      vectorNode.fields().forEachRemaining( e -> {
        Float cnt = e.getValue().floatValue();
        // Generate pseudo-document by appending the feature cnt times,
        // where cnt is the value of the feature
        this.vector.put(e.getKey(), cnt);
      });
    }

    @Override
    public Map<String, Float> vector() {
      return this.vector;
    }
  }
}
