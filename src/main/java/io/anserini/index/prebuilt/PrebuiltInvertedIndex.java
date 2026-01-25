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

package io.anserini.index.prebuilt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrebuiltInvertedIndex {
  private static final String RESOURCE_PATH = "prebuilt/msmarco-v1-inverted.json";
  private static final ObjectMapper MAPPER = JsonMapper.builder()
      .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build();

  private static PrebuiltInvertedIndex INSTANCE;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Entry {
    @JsonProperty("name")
    public String name;

    @JsonProperty("description")
    public String description;

    @JsonProperty("filename")
    public String filename;

    @JsonProperty("readme")
    public String readme;

    @JsonProperty("urls")
    public String[] urls;

    @JsonProperty("md5")
    public String md5;

    @JsonProperty("compressed_size")
    public long compressedSize;

    @JsonProperty("total_terms")
    public long totalTerms;

    @JsonProperty("documents")
    public int documents;

    @JsonProperty("unique_terms")
    public long uniqueTerms;
  }

  private final List<Entry> entries;
  private final Map<String, Entry> byName;

  private PrebuiltInvertedIndex() {
    try (InputStream input = PrebuiltInvertedIndex.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
      if (input == null) {
        throw new IllegalStateException("Resource not found: " + RESOURCE_PATH);
      }
      entries = Collections.unmodifiableList(MAPPER.readValue(input, new TypeReference<List<Entry>>() {}));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read " + RESOURCE_PATH, e);
    }

    Map<String, Entry> map = new HashMap<>(Math.max(16, entries.size() * 2));
    for (Entry entry : entries) {
      if (entry != null && entry.name != null) {
        map.put(entry.name, entry);
      }
    }
    this.byName = Collections.unmodifiableMap(map);
  }

  public static List<Entry> entries() {
    if (INSTANCE == null) {
      INSTANCE = new PrebuiltInvertedIndex();
    }
    return INSTANCE.entries;
  }

  public static Entry get(String name) {
    if (INSTANCE == null) {
      INSTANCE = new PrebuiltInvertedIndex();
    }

    return INSTANCE.byName.get(name);
  }
}
