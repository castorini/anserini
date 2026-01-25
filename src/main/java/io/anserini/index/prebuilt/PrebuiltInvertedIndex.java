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

public class PrebuiltInvertedIndex {
  private static final String RESOURCE_DIR = "prebuilt";
  private static final String RESOURCE_SUFFIX = ".json";
  private static final TypeReference<List<Entry>> ENTRY_LIST_TYPE = new TypeReference<List<Entry>>() {};
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
    List<Entry> loadedEntries = new ArrayList<>();
    ClassLoader classLoader = PrebuiltInvertedIndex.class.getClassLoader();
    boolean foundResource = false;
    try {
      Enumeration<URL> urls = classLoader.getResources(RESOURCE_DIR);
      while (urls.hasMoreElements()) {
        foundResource = true;
        URL url = urls.nextElement();
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
          Path dir = Paths.get(url.toURI());
          try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + RESOURCE_SUFFIX)) {
            for (Path path : stream) {
              try (InputStream input = Files.newInputStream(path)) {
                loadedEntries.addAll(MAPPER.readValue(input, ENTRY_LIST_TYPE));
              }
            }
          }
        } else if ("jar".equals(protocol)) {
          JarURLConnection connection = (JarURLConnection) url.openConnection();
          try (JarFile jar = connection.getJarFile()) {
            Enumeration<JarEntry> jarEntries = jar.entries();
            while (jarEntries.hasMoreElements()) {
              JarEntry jarEntry = jarEntries.nextElement();
              String name = jarEntry.getName();
              if (!jarEntry.isDirectory()
                  && name.startsWith(RESOURCE_DIR + "/")
                  && name.endsWith(RESOURCE_SUFFIX)) {
                try (InputStream input = jar.getInputStream(jarEntry)) {
                  loadedEntries.addAll(MAPPER.readValue(input, ENTRY_LIST_TYPE));
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
    if (!foundResource || loadedEntries.isEmpty()) {
      throw new IllegalStateException("Resource not found: " + RESOURCE_DIR);
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
