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

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class PrebuiltImpactIndex {
  private static final String RESOURCE_DIR = "prebuilt-indexes";
  private static final String RESOURCE_SUFFIX = ".json";
  private static final TypeReference<List<Entry>> ENTRY_LIST_TYPE = new TypeReference<List<Entry>>() {};
  private static final ObjectMapper MAPPER = JsonMapper.builder()
      .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build();

  // This is the singleton instance of this class.
  private static PrebuiltImpactIndex INSTANCE;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Entry {
    @JsonProperty("name")
    public String name;

    @JsonProperty("type")
    public String type;

    @JsonProperty("corpus_index")
    public String corpusIndex;

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

  private PrebuiltImpactIndex() {
    List<Entry> loadedEntries = new ArrayList<>();
    ClassLoader classLoader = PrebuiltInvertedIndex.class.getClassLoader();

    try {
      Enumeration<URL> urls = classLoader.getResources(RESOURCE_DIR);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
          Path dir = Paths.get(url.toURI());
          try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + RESOURCE_SUFFIX)) {
            for (Path path : stream) {
              try (InputStream input = Files.newInputStream(path)) {
                List<Entry> entriesInFile = MAPPER.readValue(input, ENTRY_LIST_TYPE);
                for (Entry entry : entriesInFile) {
                  if (entry != null && "impact".equals(entry.type)) {
                    loadedEntries.add(entry);
                  }
                }
              }
            }
          }
        } else if ("jar".equals(protocol)) {
          JarURLConnection connection = (JarURLConnection) url.openConnection();
          String entryPrefix = connection.getEntryName();
          if (entryPrefix == null) {
            entryPrefix = RESOURCE_DIR;
          }
          if (!entryPrefix.endsWith("/")) {
            entryPrefix = entryPrefix + "/";
          }
          try (JarFile jarFile = connection.getJarFile()) {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
              JarEntry jarEntry = jarEntries.nextElement();
              if (jarEntry.isDirectory()) {
                continue;
              }
              String name = jarEntry.getName();
              if (!name.startsWith(entryPrefix) || !name.endsWith(RESOURCE_SUFFIX)) {
                continue;
              }
              try (InputStream input = jarFile.getInputStream(jarEntry)) {
                List<Entry> entriesInFile = MAPPER.readValue(input, ENTRY_LIST_TYPE);
                for (Entry entry : entriesInFile) {
                  if (entry != null && "impact".equals(entry.type)) {
                    loadedEntries.add(entry);
                  }
                }
              }
            }
          }
        } else {
          throw new IllegalStateException("Unsupported resource protocol: " + protocol);
        }
      }
    } catch (IOException | URISyntaxException e) {
      throw new IllegalStateException("Failed to read resources under " + RESOURCE_DIR, e);
    }

    entries = Collections.unmodifiableList(loadedEntries);

    Map<String, Entry> map = new HashMap<>(Math.max(16, entries.size() * 2));
    for (Entry entry : entries) {
      if (entry != null && entry.name != null) {
        map.put(entry.name, entry);
      }
    }

    this.byName = Collections.unmodifiableMap(map);
  }

  public static List<Entry> entries() {
    // Implementation of this class follows the singleton pattern. There should only be one instance.
    // If it isn't initialized, initialize it; otherwise, return the singleton instance.
    if (INSTANCE == null) {
      INSTANCE = new PrebuiltImpactIndex();
    }

    return INSTANCE.entries;
  }

  public static Entry get(String name) {
    // Implementation of this class follows the singleton pattern. There should only be one instance.
    // If it isn't initialized, initialize it; otherwise, return the singleton instance.
    if (INSTANCE == null) {
      INSTANCE = new PrebuiltImpactIndex();
    }

    return INSTANCE.byName.get(name);
  }
}
