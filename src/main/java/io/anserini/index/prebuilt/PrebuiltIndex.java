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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class PrebuiltIndex {
  // We pin to a specific commit id so that changes to the prebuilt-indexes repo does not inadvertently break code, especially for published artifacts.
  protected static final String METADATA_COMMIT = "367560b4de7d9c3486f666dcc2df7783ca7758f2";
  protected static final String DEFAULT_METADATA_URL = "https://api.github.com/repos/castorini/prebuilt-indexes/contents/lucene?ref=" + METADATA_COMMIT;
  protected static final String METADATA_SUFFIX = ".json";
  private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(60);
  private static final String USER_AGENT = "anserini-prebuilt-index-loader";

  private static final ObjectMapper MAPPER = JsonMapper.builder()
      .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build();

  public enum IndexType {
    IMPACT("impact"),
    INVERTED("inverted"),
    FLAT("flat"),
    HNSW("hnsw");

    private final String id;

    IndexType(String id) {
      this.id = id;
    }
  }

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

    @JsonProperty("size")
    public long size;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class MetadataFile {
    @JsonProperty("name")
    public String name;

    @JsonProperty("type")
    public String type;

    @JsonProperty("download_url")
    public String downloadUrl;
  }

  private static class MetadataFilesHolder {
    private static final List<MetadataFile> FILES = Collections.unmodifiableList(fetchMetadataFiles());
  }

  // This method looks through all metadata files and filters based on type. This makes the method resilient
  // wrt filename changes, but the tradeoff is more scanning of files than absolutely necessary.
  protected static <T extends PrebuiltIndex.Entry> List<T> loadEntries(IndexType type, Class<T> entryClass) {
    try {
      List<T> loadedEntries = new ArrayList<>();
      String metadataTypeSuffix = "-" + type.id + METADATA_SUFFIX;
      for (MetadataFile file : MetadataFilesHolder.FILES) {
        if (!file.name.endsWith(metadataTypeSuffix)) {
          continue;
        }
        loadedEntries.addAll(parseEntriesFromJson(type, entryClass, readUrl(file.downloadUrl)));
      }
      return loadedEntries;
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read prebuilt index metadata from " + DEFAULT_METADATA_URL, e);
    }
  }

  protected static <T extends PrebuiltIndex.Entry> List<T> parseEntriesFromJson(IndexType type, Class<T> entryClass, String metadataJson) throws IOException {
    List<T> loadedEntries = new ArrayList<>();
    JavaType entryListType = MAPPER.getTypeFactory().constructCollectionType(List.class, entryClass);
    List<T> entriesInFile = MAPPER.readValue(metadataJson, entryListType);

    for (T entry : entriesInFile) {
      if (entry != null && type.id.equals(entry.type)) {
        loadedEntries.add(entry);
      }
    }

    return loadedEntries;
  }

  private static List<MetadataFile> fetchMetadataFiles() {
    try {
      String response = readUrl(DEFAULT_METADATA_URL);
      JavaType metadataFileListType = MAPPER.getTypeFactory().constructCollectionType(List.class, MetadataFile.class);
      List<MetadataFile> metadataFiles = MAPPER.readValue(response, metadataFileListType);
      metadataFiles.sort(Comparator.comparing(file -> file.name == null ? "" : file.name));

      List<MetadataFile> files = new ArrayList<>();
      for (MetadataFile file : metadataFiles) {
        if (file == null || !"file".equals(file.type) || file.name == null || !file.name.endsWith(METADATA_SUFFIX) ||
            file.downloadUrl == null) {
          continue;
        }
        files.add(file);
      }

      if (files.isEmpty()) {
        throw new IOException("No metadata files found at " + DEFAULT_METADATA_URL);
      }

      return files;
    } catch (IOException e) {
      throw new IllegalStateException("Failed to fetch prebuilt index metadata from " + DEFAULT_METADATA_URL, e);
    }
  }

  private static String readUrl(String urlString) throws IOException {
    URL url = URI.create(urlString).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout((int) HTTP_TIMEOUT.toMillis());
    connection.setReadTimeout((int) HTTP_TIMEOUT.toMillis());
    connection.setRequestProperty("Accept", "application/vnd.github+json");
    connection.setRequestProperty("User-Agent", USER_AGENT);

    try {
      int statusCode = connection.getResponseCode();
      if (statusCode < 200 || statusCode >= 300) {
        throw new IOException(String.format("GET %s returned HTTP %s", urlString, statusCode));
      }
      try (InputStream inputStream = connection.getInputStream()) {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      }
    } finally {
      connection.disconnect();
    }
  }

  /**
   * Returns the {@link PrebuiltIndex.Entry} corresponding to a prebuilt index or <tt>null</tt> if it doesn't exist.
   *
   * @param name prebuilt index
   * @return the {@link PrebuiltIndex.Entry} corresponding to a prebuilt index or <tt>null</tt> if it doesn't exist
   */
  public static PrebuiltIndex.Entry get(String name) {
    PrebuiltIndex.Entry entry;

    if ((entry = PrebuiltInvertedIndex.get(name)) != null) {
      return entry;
    } else if ((entry = PrebuiltImpactIndex.get(name)) != null) {
      return entry;
    } else if ((entry = PrebuiltFlatIndex.get(name)) != null) {
      return entry;
    } else if ((entry = PrebuiltHnswIndex.get(name)) != null) {
      return entry;
    }

    return null;
  }
}
