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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A registry entry for a standard set of topics from various evaluations.
 */
public final class Topics {
  private static final String LOCAL_METADATA_RESOURCE = "topics-and-qrels/_local_metadata_topics.json";
  private static final String LOCAL_ALIASES_METADATA_RESOURCE = "topics-and-qrels/_local_metadata_topics_aliases.json";
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static volatile Registry registryCache;

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
    return registryData().get(name);
  }

  public static Map<String, Topics> registry() {
    return registryData().canonical;
  }

  public static Set<String> names() {
    return Collections.unmodifiableSet(registryData().lookup.keySet());
  }

  public static Topics getBaseTopics(String name) {
    name = name.replaceFirst("^topics\\.", "");
    String regex = "^(.*?)(?:[.-](bge|cohere|splade|unicoil|cosdpr|txt|tsv|v\\d+|v\\d+\\.\\d+)).*";
    return Topics.get(name.replaceAll(regex, "$1"));
  }

  public static <K> SortedMap<K, Map<String, String>> resolve(String topics) {
    return resolve(topics, null);
  }

  @SuppressWarnings("unchecked")
  public static <K> SortedMap<K, Map<String, String>> resolve(String topics, String topicReader) {
    Path topicsPath = Paths.get(topics);
    if (!Files.exists(topicsPath) || !Files.isRegularFile(topicsPath) || !Files.isReadable(topicsPath)) {
      Topics ref = Topics.get(topics);
      if (ref == null) {
        throw new IllegalArgumentException(String.format("\"%s\" does not refer to valid topics.", topicsPath));
      }

      try {
        return TopicReader.getTopics(ref);
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format("Unable to read topics \"%s\".", topics), e);
      }
    }

    if (topicReader == null) {
      throw new IllegalArgumentException("Must specify the topic reader using -topicReader.");
    }

    try {
      TopicReader<K> tr = (TopicReader<K>) Class
          .forName(String.format("io.anserini.search.topicreader.%sTopicReader", topicReader))
          .getConstructor(Path.class).newInstance(topicsPath);
      return tr.read();
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load topic reader \"%s\".", topicReader));
    }
  }

  public static <K> SortedMap<K, Map<String, String>> resolve(String[] topicsArray, String topicReader) {
    SortedMap<K, Map<String, String>> topics = new TreeMap<>();

    for (String topicsFile : topicsArray) {
      topics.putAll(resolve(topicsFile, topicReader));
    }

    return topics;
  }

  private static Registry registryData() {
    Registry registry = registryCache;
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

  private static Registry loadRegistry() {
    Map<String, TopicMetadata> metadata = loadMetadata();
    Map<String, List<String>> aliases = loadAliasesMetadata();
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
      addLookup(lookup, name, topic);
      addLookup(lookup, value.path, topic);
      addLookup(lookup, Path.of(value.path).getFileName().toString(), topic);
    }

    for (Map.Entry<String, List<String>> entry : aliases.entrySet()) {
      Topics topic = canonical.get(entry.getKey());
      if (topic == null) {
        throw new IllegalStateException("Topic alias canonical name is not registered: " + entry.getKey());
      }
      for (String alias : entry.getValue()) {
        addLookup(lookup, alias, topic);
      }
    }

    return new Registry(Collections.unmodifiableMap(canonical), Collections.unmodifiableMap(lookup));
  }

  private static Map<String, TopicMetadata> loadMetadata() {
    try (InputStream inputStream = Topics.class.getClassLoader().getResourceAsStream(LOCAL_METADATA_RESOURCE)) {
      if (inputStream == null) {
        throw new IllegalStateException("Topic metadata resource not found: " + LOCAL_METADATA_RESOURCE);
      }
      return MAPPER.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load topic metadata from " + LOCAL_METADATA_RESOURCE, e);
    }
  }

  private static Map<String, List<String>> loadAliasesMetadata() {
    try (InputStream inputStream = Topics.class.getClassLoader().getResourceAsStream(LOCAL_ALIASES_METADATA_RESOURCE)) {
      if (inputStream == null) {
        return Map.of();
      }
      return MAPPER.readValue(inputStream, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load topic aliases metadata from " + LOCAL_ALIASES_METADATA_RESOURCE, e);
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

  private static void addLookup(Map<String, Topics> lookup, String name, Topics topic) {
    Topics existing = lookup.get(name);
    if (existing != null && existing != topic) {
      throw new IllegalStateException("Topic name maps to conflicting topics: " + name);
    }
    lookup.put(name, topic);
  }

  private static final class Registry {
    private final Map<String, Topics> canonical;
    private final Map<String, Topics> lookup;

    private Registry(Map<String, Topics> canonical, Map<String, Topics> lookup) {
      this.canonical = canonical;
      this.lookup = lookup;
    }

    private Topics get(String name) {
      return lookup.get(name);
    }
  }

  private static class TopicMetadata {
    public String path;
    public String reader_class;
  }
}
