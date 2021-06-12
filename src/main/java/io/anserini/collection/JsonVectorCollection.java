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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class JsonVectorCollection extends DocumentCollection<JsonVectorCollection.Document> {
  public JsonVectorCollection(Path path) {
    this.path = path;
  }

  @Override
  public FileSegment<JsonVectorCollection.Document> createFileSegment(Path p) throws IOException {
    return new JsonVectorCollection.Segment<>(p);
  }

  public static class Segment<T extends JsonVectorCollection.Document> extends JsonCollection.Segment<T> {
    public Segment(Path path) throws IOException {
      super(path);
    }

    @Override
    protected Document createNewDocument(JsonNode json) {
      return new Document(json);
    }
  }

  public static class Document extends JsonCollection.Document {
    private String contents;

    public Document(JsonNode json) {
      super(json);

      JsonNode vectorNode = json.get("vector");

      System.out.println("######");
      System.out.println(vectorNode.toString());

      final StringBuilder sb = new StringBuilder();
      vectorNode.fields().forEachRemaining( e -> {
        int cnt = e.getValue().asInt();
        for (int i=0; i<cnt; i++ ) {
          sb.append(e.getKey() + " ");
        }
      });

      System.out.println(super.raw());
      System.out.println(sb.toString());

      this.contents = sb.toString();
    }

    @Override
    public String contents() {
      return contents;
    }
  }
}
