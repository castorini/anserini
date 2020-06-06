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

/**
 * A IsoJSON document collection.
 * This class reads all <code>.json</code> files in the input directory.
 * Inside each file is either a JSON Object (one document) or a JSON Array (multiple documents) or
 * a JSON Document on each line (not actually valid Json String)
 * Example of IsoJSON Object:
 * <pre>
 * {
 *   "id": "doc1",
 *   "title": "this is the title.",
 *   "abstract": "this is the abstract"
 * }
 * </pre>
 * Example of JSON Array:
 * <pre>
 * [
 *   {
 *     "id": "doc1",
 *     "title": "this is the title.",
 *     "abstract": "this is the abstract"
 *   },
 *   {
 *     "id": "doc2",
 *     "title": "this is the title 2.",
 *     "abstract": "this is the abstract 2"
 *   }
 * ]
 * </pre>
 * Example of JSON objects, each per line (not actually valid Json String):
 * <pre>
 * {"id": "doc1", "title": "this is the title 1.", "abstract": "this is the abstract 1."}
 * {"id": "doc2", "title": "this is the title 2.", "abstract": "this is the abstract 2."}
 * </pre>
 *
 */

public class IsoJsonCollection extends JsonCollection{
  public IsoJsonCollection(Path path) {
    super(path);
  }

  public static class Document extends MultifieldSourceDocument {
    private String id;
    private String contents;
    private Map<String, String> fields;

    public Document(JsonNode json) {
      this.fields = new HashMap<>();

      json.fields().forEachRemaining( e -> {
          if ("id".equals(e.getKey())) {
              this.id = json.get("id").asText();
          } else {
              this.fields.put(e.getKey(), e.getValue().asText());
          }
      });

      this.fields.forEach((k,v) -> this.contents += v + "\n");
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
          throw new RuntimeException("JSON document has no \"contents\" inside it");
      }
      return contents;
    }

    @Override
    public String raw() {
      if (contents == null) {
          throw new RuntimeException("JSON document has no \"contents\" inside it");
      }
      return contents;
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
