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

/**
 * A JSON document collection where the user can specify directly the vector to be indexed.
 */
public class JsonVectorCollection extends DocumentCollection<JsonVectorCollection.Document> {
  public JsonVectorCollection(Path path) {
    this.path = path;
  }

  public JsonVectorCollection() {
  }

  @Override
  public FileSegment<JsonVectorCollection.Document> createFileSegment(Path p) throws IOException {
    return new JsonVectorCollection.Segment<>(p);
  }

  @Override
  public FileSegment<JsonVectorCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new JsonVectorCollection.Segment<>(bufferedReader);
  }

  public static class Segment<T extends JsonVectorCollection.Document> extends JsonCollection.Segment<T> {
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
    private final String contents;

    public Document(JsonNode json) {
      super(json);

      // We're going to take the map associated with "vector" and generate pseudo-document.
      JsonNode vectorNode = json.get("vector");

      // Iterate through the features:
      final StringBuilder sb = new StringBuilder();
      vectorNode.fields().forEachRemaining( e -> {
        int cnt = e.getValue().asInt();
        // Generate pseudo-document by appending the feature cnt times,
        // where cnt is the value of the feature
        for (int i=0; i<cnt; i++ ) {
          sb.append(e.getKey()).append(" ");
        }
      });

      this.contents = sb.toString();
    }

    @Override
    public String contents() {
      return contents;
    }
  }
}
