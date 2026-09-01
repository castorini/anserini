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
import java.util.ArrayList;
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
  private static final String COMMIT_ID = "5b819c1a45c8a0a3ec9426f476694f44a29dfaf3";
  public static final String URL = "https://raw.githubusercontent.com/castorini/eval/" + COMMIT_ID + "/qrels/";

  private static final String QRELS_URL = "https://raw.githubusercontent.com/castorini/eval/" + COMMIT_ID + "/qrels.json";
  private static final String QREL_ALIASES_URL = "https://raw.githubusercontent.com/castorini/eval/" + COMMIT_ID + "/qrels-aliases.json";

  // Staging area before merging into external GitHub repo; keep "dummy" for testing.
  private static final String LOCAL_QRELS = "qrels/local-qrels.json";
  private static final String LOCAL_QRELS_ALIASES = "qrels/local-qrels-aliases.json";

  private static final ObjectMapper mapper = new ObjectMapper();
  private static volatile Registry registryCache;

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
    String path = registry().lookup.get(name);
    if (path == null) {
      throw new IllegalArgumentException("Unknown qrels name: " + name);
    }
    return new Qrels(name, Path.of(path));
  }

  public static Path resolveRegisteredQrelsPath(String name) throws IOException {
    String path = getRegisteredPath(name);
    return resolveQrelsPath(path);
  }

  public static String getRegisteredPath(String name) {
    String path = registry().lookup.get(name);
    if (path == null) {
      throw new IllegalArgumentException("Unknown qrels name: " + name);
    }
    return path;
  }

  public static String getCanonicalName(String name) {
    String path = getRegisteredPath(name);
    return registry().canonical.entrySet().stream()
        .filter(entry -> entry.getValue().equals(path))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No canonical qrels name for: " + name));
  }

  public static List<String> aliases(String name) {
    return registry().aliases.getOrDefault(getCanonicalName(name), List.of());
  }

  public static Qrels loadFromFile(String file) throws IOException {
    return new Qrels(file);
  }

  private static Registry registry() {
    Registry registry = registryCache;
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
    return registry().canonical.keySet();
  }

  private static Registry loadRegistry() {
    try (InputStream inputStream = new URI(QRELS_URL).toURL().openStream()) {
      Map<String, String> registry = mapper.readValue(inputStream, new TypeReference<>() {});
      registry.putAll(loadLocalMetadata());
      Map<String, List<String>> aliases = new LinkedHashMap<>();
      mergeAliases(aliases, loadAliasesMetadata(QREL_ALIASES_URL));
      Map<String, List<String>> localAliases = loadLocalAliasesMetadata();
      mergeAliases(aliases, localAliases);
      Map<String, String> canonicalRegistry = new LinkedHashMap<>(registry);
      localAliases.values().forEach(aliasValues -> aliasValues.forEach(canonicalRegistry::remove));
      Map<String, String> canonical = Collections.unmodifiableMap(canonicalRegistry);
      Map<String, String> lookup = new LinkedHashMap<>(registry);
      addAliasesToRegistry(lookup, aliases);
      aliases.replaceAll((name, values) -> List.copyOf(values));
      return new Registry(canonical, Collections.unmodifiableMap(lookup), Collections.unmodifiableMap(aliases));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load qrels metadata from " + QRELS_URL, e);
    }
  }

  private static void mergeAliases(Map<String, List<String>> destination, Map<String, List<String>> source) {
    source.forEach((name, aliases) ->
        destination.computeIfAbsent(name, ignored -> new ArrayList<>()).addAll(aliases));
  }

  private static Map<String, String> loadLocalMetadata() {
    try (InputStream inputStream = Qrels.class.getClassLoader().getResourceAsStream(LOCAL_QRELS)) {
      if (inputStream == null) {
        return Map.of();
      }
      return mapper.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load qrels metadata from " + LOCAL_QRELS, e);
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
    try (InputStream inputStream = Qrels.class.getClassLoader().getResourceAsStream(LOCAL_QRELS_ALIASES)) {
      if (inputStream == null) {
        return Map.of();
      }
      return mapper.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load qrels aliases metadata from " + LOCAL_QRELS_ALIASES, e);
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
   * unregistered filename already present in the local qrels cache. Registered qrels are downloaded into the
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

    Map<String, String> registry = registry().lookup;
    String qrelsFileName = qrelsPath.getFileName().toString();
    String registeredPath = registry.get(qrels);

    if (registeredPath != null) {
      qrelsPath = Path.of(registeredPath);
    } else if (registry.containsValue(qrelsFileName)) {
      qrelsPath = Path.of(qrelsFileName);
    } else {
      // If the qrels file is not in the list of known qrels, we assume it is a local file.
      Path tempPath = CacheDirectoryResolver.getQrelsCachePath().resolve(qrelsPath.getFileName());
      if (Files.exists(tempPath)) {
        // if it is an unregistered qrels in the Qrels registry, but it is in the cache, we use it.
        return tempPath;
      }
      return qrelsPath;
    }

    Path resultPath = CacheDirectoryResolver.getQrelsCachePath().resolve(qrelsPath.getFileName());
    if (!Files.exists(resultPath)) {
      resultPath = downloadQrels(qrelsPath);
    }
    return resultPath;
  }

  public static Path downloadQrels(Path qrelsPath) throws IOException {
    String qrelsURL = URL + qrelsPath.getFileName().toString();
    System.err.println("Downloading qrels from " + qrelsURL);
    Path localQrelsPath = CacheDirectoryResolver.getQrelsCachePath().resolve(qrelsPath.getFileName());

    try {
      FileUtils.copyURLToFile(new URI(qrelsURL).toURL(), localQrelsPath.toFile());
    } catch (Exception e) {
      throw new IOException("Error downloading qrels from " + qrelsURL);
    }
    return localQrelsPath;
  }

  private static final class Registry {
    private final Map<String, String> canonical;
    private final Map<String, String> lookup;
    private final Map<String, List<String>> aliases;

    private Registry(Map<String, String> canonical, Map<String, String> lookup, Map<String, List<String>> aliases) {
      this.canonical = canonical;
      this.lookup = lookup;
      this.aliases = aliases;
    }
  }
}
