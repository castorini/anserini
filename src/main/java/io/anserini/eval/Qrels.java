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

package io.anserini.eval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.util.CacheDirectoryResolver;

public class Qrels {
  private static final String TOPICS_AND_QRELS_COMMIT = "12982126736f2ed7dc45bf30acb2af9fed13c0ef";
  private static final String TOPICS_AND_QRELS_URL = "https://raw.githubusercontent.com/castorini/anserini-tools/" + TOPICS_AND_QRELS_COMMIT + "/topics-and-qrels/";
  private static final String DEFAULT_METADATA_URL = TOPICS_AND_QRELS_URL + "_metadata_qrels.json";
  private static final String DEFAULT_ALIASES_METADATA_URL = TOPICS_AND_QRELS_URL + "_metadata_qrels_aliases.json";
  private static final String SERVER_PATH = TOPICS_AND_QRELS_URL;

  // Staging area for aliases that will eventually be moved into separate GitHub repo.
  private static final String LOCAL_ALIASES_METADATA_RESOURCE = "topics-and-qrels/_local_metadata_qrels_aliases.json";

  private static final ObjectMapper mapper = new ObjectMapper();
  private static volatile Map<String, String> registryCache;

  private final String name;
  private final Path path;
  private final Map<String, Map<String, Integer>> qrels;

  protected Qrels(String file) throws IOException {
    this(null, Path.of(file));
  }

  protected Qrels(String name, Path path) throws IOException {
    this.name = name == null ? path.getFileName().toString() : name;
    this.path = path;
    this.qrels = loadQrels(path);
  }

  private static Map<String, Map<String, Integer>> loadQrels(Path file) throws IOException {
    Map<String, Map<String, Integer>> qrels = new LinkedHashMap<>();
    Path qrelsPath = resolveQrelsPath(file.toString());

    try (BufferedReader br = new BufferedReader(new FileReader(qrelsPath.toString()))) {
      String line;
      String[] arr;
      while ((line = br.readLine()) != null) {
        arr = line.split("[\\s\\t]+");
        String qid = arr[0];
        String docno = arr[2];
        int grade = Integer.parseInt(arr[3]);
        if (qrels.containsKey(qid)) {
          qrels.get(qid).put(docno, grade);
        } else {
          Map<String, Integer> t = new LinkedHashMap<>();
          t.put(docno, grade);
          qrels.put(qid, t);
        }
      }
    } catch (IOException e) {
      throw new IOException("Could not read qrels file: " + file + " (resolved to " + qrelsPath + ")", e);
    }
    return qrels;
  }

  public static Qrels get(String name) throws IOException {
    String path = registry().get(name);
    if (path == null) {
      throw new IllegalArgumentException("Unknown qrels name: " + name);
    }
    return new Qrels(name, Path.of(path));
  }

  public static Qrels loadFromFile(String file) throws IOException {
    return new Qrels(file);
  }

  public static Map<String, String> registry() {
    Map<String, String> registry = registryCache;
    if (registry == null) {
      synchronized (Qrels.class) {
        registry = registryCache;
        if (registry == null) {
          registry = loadRegistry();
          registryCache = registry;
        }
      }
    }
    return registry;
  }

  public static Set<String> names() {
    return registry().keySet();
  }

  private static Map<String, String> loadRegistry() {
    try (InputStream inputStream = new URI(DEFAULT_METADATA_URL).toURL().openStream()) {
      Map<String, String> registry = mapper.readValue(inputStream, new TypeReference<>() {});
      Map<String, String> registryWithAliases = new LinkedHashMap<>(registry);
      addAliasesToRegistry(registryWithAliases, loadAliasesMetadata(DEFAULT_ALIASES_METADATA_URL));
      addAliasesToRegistry(registryWithAliases, loadLocalAliasesMetadata());
      return Collections.unmodifiableMap(registryWithAliases);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load qrels metadata from " + DEFAULT_METADATA_URL, e);
    }
  }

  private static Map<String, List<String>> loadAliasesMetadata(String url) {
    try (InputStream inputStream = new URI(url).toURL().openStream()) {
      return mapper.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load qrels aliases metadata from " + url, e);
    }
  }

  private static Map<String, List<String>> loadLocalAliasesMetadata() {
    try (InputStream inputStream = Qrels.class.getClassLoader().getResourceAsStream(LOCAL_ALIASES_METADATA_RESOURCE)) {
      if (inputStream == null) {
        return Map.of();
      }
      return mapper.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load qrels aliases metadata from " + LOCAL_ALIASES_METADATA_RESOURCE, e);
    }
  }

  private static void addAliasesToRegistry(Map<String, String> registryWithAliases, Map<String, List<String>> canonicalToAliases) {
    for (Map.Entry<String, List<String>> entry : canonicalToAliases.entrySet()) {
      String canonicalName = entry.getKey();
      if (!registryWithAliases.containsKey(canonicalName)) {
        throw new IllegalStateException("Qrels alias canonical name is not registered: " + canonicalName);
      }

      String qrelsPath = registryWithAliases.get(canonicalName);
      for (String alias : entry.getValue()) {
        String existingQrelsPath = registryWithAliases.get(alias);
        if (existingQrelsPath != null && !existingQrelsPath.equals(qrelsPath)) {
          throw new IllegalStateException("Qrels alias maps to conflicting qrels: " + alias);
        }
        registryWithAliases.put(alias, qrelsPath);
      }
    }
  }

  public String name() {
    return name;
  }

  public Path path() {
    return path;
  }

  public boolean isDocJudged(String qid, String docid) {
    if (!qrels.containsKey(qid)) {
      return false;
    }

    if (!qrels.get(qid).containsKey(docid)) {
      return false;
    } else {
      return true;
    }
  }

  public int getRelevanceGrade(String qid, String docid) {
    if (!qrels.containsKey(qid)) {
      return 0;
    }

    if (!qrels.get(qid).containsKey(docid)) {
      return 0;
    }

    if (qrels.get(qid).get(docid) <= 0)
      return 0;
    return qrels.get(qid).get(docid);
  }

  public Set<String> getQids() {
    return this.qrels.keySet();
  }

  public Map<String, Integer> getDocMap(String qid) {
    if (this.qrels.containsKey(qid)) {
      return this.qrels.get(qid);
    } else {
      return null;
    }
  }

  /**
   * Resolves a qrels reference to a local path.
   *
   * <p>The {@code qrels} argument may be a local path, a registered qrels name, a registered qrels filename, or an
   * unregistered filename already present in the local topics-and-qrels cache. Registered qrels are downloaded into the
   * cache if needed. Unknown qrels are returned as paths unchanged, leaving the caller to handle any missing-file
   * failure.</p>
   *
   * @param qrels qrels name, filename, or path
   * @return local path for the qrels reference
   * @throws IOException if a registered qrels file cannot be downloaded
   */
  public static Path resolveQrelsPath(String qrels) throws IOException {
    Path qrelsPath = Path.of(qrels);
    if (Files.exists(qrelsPath)) {
      return qrelsPath;
    }

    Map<String, String> registry = registry();
    String qrelsFileName = qrelsPath.getFileName().toString();
    String registeredPath = registry.get(qrels);

    if (registeredPath != null) {
      qrelsPath = Path.of(registeredPath);
    } else if (registry.containsValue(qrelsFileName)) {
      qrelsPath = Path.of(qrelsFileName);
    } else {
      // If the qrels file is not in the list of known qrels, we assume it is a local file.
      Path tempPath = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve(qrelsPath.getFileName());
      if (Files.exists(tempPath)) {
        // if it is an unregistered qrels in the Qrels registry, but it is in the cache, we use it.
        return tempPath;
      }
      return qrelsPath;
    }

    Path resultPath = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve(qrelsPath.getFileName());
    if (!Files.exists(resultPath)) {
      resultPath = downloadQrels(qrelsPath);
    }
    return resultPath;
  }

  public static Path downloadQrels(Path qrelsPath) throws IOException {
    String qrelsURL = SERVER_PATH + qrelsPath.getFileName().toString();
    System.out.println("Downloading qrels from " + qrelsURL);
    Path localQrelsPath = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve(qrelsPath.getFileName());

    try {
      FileUtils.copyURLToFile(new URI(qrelsURL).toURL(), localQrelsPath.toFile());
    } catch (Exception e) {
      throw new IOException("Error downloading qrels from " + qrelsURL);
    }
    return localQrelsPath;
  }
}
