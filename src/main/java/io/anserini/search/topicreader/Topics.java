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

package io.anserini.search.topicreader;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A registry entry for a standard set of topics from various evaluations.
 */
public final class Topics {
  private static final String COMMIT_ID = "43add835e20bd66b48f9a640be9bad95a4762d82";
  public static final String URL = "https://raw.githubusercontent.com/castorini/eval/" + COMMIT_ID + "/topics/";

  private static final String TOPICS_URL = "https://raw.githubusercontent.com/castorini/eval/" + COMMIT_ID + "/topics.json";
  private static final String TOPICS_ALIASES_URL = "https://raw.githubusercontent.com/castorini/eval/" + COMMIT_ID + "/topics-aliases.json";

  // Staging area before merging into external GitHub repo; keep "dummy" for testing.
  private static final String LOCAL_TOPICS = "topics/local-topics.json";
  private static final String LOCAL_TOPICS_ALIASES = "topics/local-topics-aliases.json";

  private static final ObjectMapper MAPPER = new ObjectMapper();

  // Contains only canonical topic names; used for enumeration by entries() and names().
  private static volatile Map<String, Topics> canonicalRegistryCache;

  // Contains every accepted lookup key, including canonical names, aliases, paths, and filenames; used by get().
  private static volatile Map<String, Topics> registryCache;

  public final String path;
  public final Class<? extends TopicReader<?>> readerClass;

  private final String name;

  private Topics(String name, Class<? extends TopicReader<?>> readerClass, String path) {
    this.name = name;
    this.readerClass = readerClass;
    this.path = path;
  }

  public String name() {
    return name;
  }

  public static Topics get(String name) {
    return registry().get(name);
  }

  public static Map<String, Topics> entries() {
    registry();
    return canonicalRegistryCache;
  }

  public static Set<String> names() {
    return entries().keySet();
  }

  public static Class<? extends TopicReader<?>> getTopicReaderClassForPath(String path) {
    Topics topic = Topics.get(path);
    if (topic == null) {
      topic = Topics.get(Path.of(path).getFileName().toString());
    }
    return topic == null ? null : topic.readerClass;
  }

  public static <K> SortedMap<K, Map<String, String>> load(String topics) {
    Topics ref = Topics.get(topics);
    if (ref == null) {
      throw new IllegalArgumentException(String.format("\"%s\" does not refer to valid topics.", topics));
    }
    return load(ref);
  }

  public static <K> SortedMap<K, Map<String, String>> load(Topics topics) {
    try {
      return TopicReader.load(topics);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to read topics \"%s\".", topics.name()), e);
    }
  }

  private static Map<String, Topics> registry() {
    Map<String, Topics> registry = registryCache;
    if (registry == null) {
      synchronized (Topics.class) {
        registry = registryCache;
        if (registry == null) {
          registry = loadRegistry();
          registryCache = registry;
        }
      }
    }
    return registry;
  }

  private static Map<String, Topics> loadRegistry() {
    Map<String, TopicMetadata> metadata = loadMetadata();
    metadata.putAll(loadLocalMetadata());
    Map<String, Topics> canonical = new LinkedHashMap<>();
    Map<String, Topics> lookup = new LinkedHashMap<>();

    for (Map.Entry<String, TopicMetadata> entry : metadata.entrySet()) {
      String name = entry.getKey();
      TopicMetadata value = entry.getValue();
      if (value.path == null || value.path.isBlank()) {
        throw new IllegalStateException("Topic metadata path is missing: " + name);
      }
      if (value.reader_class == null || value.reader_class.isBlank()) {
        throw new IllegalStateException("Topic metadata reader_class is missing: " + name);
      }

      Topics topic = new Topics(name, loadReaderClass(value.reader_class), value.path);
      canonical.put(name, topic);
      addLookupEntry(lookup, name, topic);
      addLookupEntry(lookup, value.path, topic);
      addLookupEntry(lookup, Path.of(value.path).getFileName().toString(), topic);
    }

    addAliasesToRegistry(canonical, lookup, loadAliasesMetadata(TOPICS_ALIASES_URL));
    Map<String, List<String>> localAliases = loadLocalAliasesMetadata();
    addAliasesToRegistry(canonical, lookup, localAliases);
    localAliases.values().forEach(aliases -> aliases.forEach(canonical::remove));

    canonicalRegistryCache = Collections.unmodifiableMap(canonical);
    return Collections.unmodifiableMap(lookup);
  }

  private static void addAliasesToRegistry(Map<String, Topics> canonical, Map<String, Topics> lookup,
      Map<String, List<String>> aliases) {
    for (Map.Entry<String, List<String>> entry : aliases.entrySet()) {
      Topics topic = canonical.get(entry.getKey());
      if (topic == null) {
        throw new IllegalStateException("Topic alias canonical name is not registered: " + entry.getKey());
      }
      for (String alias : entry.getValue()) {
        addLookupEntry(lookup, alias, topic);
      }
    }
  }

  private static Map<String, TopicMetadata> loadMetadata() {
    try (InputStream inputStream = new URI(TOPICS_URL).toURL().openStream()) {
      return MAPPER.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load topic metadata from " + TOPICS_URL, e);
    }
  }

  private static Map<String, TopicMetadata> loadLocalMetadata() {
    try (InputStream inputStream = Topics.class.getClassLoader().getResourceAsStream(LOCAL_TOPICS)) {
      if (inputStream == null) {
        return Map.of();
      }
      return MAPPER.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load topic metadata from " + LOCAL_TOPICS, e);
    }
  }

  private static Map<String, List<String>> loadAliasesMetadata(String url) {
    try (InputStream inputStream = new URI(url).toURL().openStream()) {
      return MAPPER.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load topic aliases metadata from " + url, e);
    }
  }

  private static Map<String, List<String>> loadLocalAliasesMetadata() {
    try (InputStream inputStream = Topics.class.getClassLoader().getResourceAsStream(LOCAL_TOPICS_ALIASES)) {
      if (inputStream == null) {
        return Map.of();
      }
      return MAPPER.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load topic aliases metadata from " + LOCAL_TOPICS_ALIASES, e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends TopicReader<?>> loadReaderClass(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      if (!TopicReader.class.isAssignableFrom(clazz)) {
        throw new IllegalStateException("Topic reader class does not extend TopicReader: " + className);
      }
      return (Class<? extends TopicReader<?>>) clazz;
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Topic reader class not found: " + className, e);
    }
  }

  private static void addLookupEntry(Map<String, Topics> lookup, String name, Topics topic) {
    Topics existing = lookup.get(name);
    if (existing != null && existing != topic) {
      throw new IllegalStateException("Topic name maps to conflicting topics: " + name);
    }
    lookup.put(name, topic);
  }

  private static class TopicMetadata {
    public String path;
    public String reader_class;
  }
}
