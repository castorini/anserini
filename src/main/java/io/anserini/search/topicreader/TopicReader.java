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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.zip.GZIPInputStream;

/**
 * A reader of topics, i.e., information needs or queries, in a variety of standard formats.
 *
 * @param <K> type of the topic id
 */
public abstract class TopicReader<K> {
  protected final int BUFFER_SIZE = 1 << 16; // 64K
  protected Path topicFile;

  static private final Map<String, Class<? extends TopicReader>> TOPIC_FILE_TO_TYPE = new HashMap<>();

  static {
    // Inverts the "Topic" enum to populate the lookup table that maps topics filename to reader class.
    for (Topics topic : Topics.values()) {
      String pathParts[] = topic.path.split("\\/");
      TOPIC_FILE_TO_TYPE.put(pathParts[1], topic.readerClass);
    }
  }

  /**
   * Returns the {@link TopicReader} class corresponding to a known topics file, or <code>null</code> if unknown.
   *
   * @param file topics file
   * @return the {@link TopicReader} class corresponding to a known topics file, or <code>null</code> if unknown.
   */
  public static Class<? extends TopicReader> getTopicReaderClassByFile(String file) {
    // If we're given something that looks like a path with directories, pull out only the file name at the end.
    if (file.contains("/")) {
      String[] parts = file.split("/");
      file = parts[parts.length-1];
    }

    if (TOPIC_FILE_TO_TYPE.containsKey(file)) {
      return TOPIC_FILE_TO_TYPE.get(file);
    }

    return null;
  }

  public TopicReader(Path topicFile) {
    this.topicFile = topicFile;
  }

  /**
   * Returns a sorted map of ids to topics. Each topic is represented as a map, where the keys are the standard TREC
   * topic fields "title", "description", and "narrative". For topic formats that do not provide this three-way
   * elaboration, the "title" key is used to hold the "query".
   *
   * @return sorted map of ids to topics
   * @throws IOException if error encountered reading topics
   */
  public SortedMap<K, Map<String, String>> read() throws IOException {
    BufferedReader bufferedReader;
    if (topicFile.toString().endsWith(".gz")) {
      InputStream stream = new GZIPInputStream(Files.newInputStream(topicFile, StandardOpenOption.READ), BUFFER_SIZE);
      bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    } else {
      InputStream topics = Files.newInputStream(topicFile, StandardOpenOption.READ);
      bufferedReader = new BufferedReader(new InputStreamReader(topics, StandardCharsets.UTF_8));
    }

    return read(bufferedReader);
  }

  public SortedMap<K, Map<String, String>> read(String str) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
    return read(bufferedReader);
  }

  abstract public SortedMap<K, Map<String, String>> read(BufferedReader bRdr) throws IOException;

  /**
   * Returns a standard set of evaluation topics.
   *
   * @param topics topics
   * @param <K> type of topic id
   * @return evaluation topics
   */
  @SuppressWarnings("unchecked")
  public static <K> SortedMap<K, Map<String, String>> getTopics(Topics topics) {
    try {
      String raw;
      InputStream inputStream;
      if (topics.path.endsWith(".gz")) {
        inputStream = new GZIPInputStream(TopicReader.class.getClassLoader().getResourceAsStream(topics.path));
      } else {
        inputStream = TopicReader.class.getClassLoader().getResourceAsStream(topics.path);
      }
      raw = new String(inputStream.readAllBytes());
      inputStream.close();

      // Get the constructor
      Constructor[] ctors = topics.readerClass.getDeclaredConstructors();
      // The one we want is always the zero-th one; pass in a dummy Path.
      TopicReader<K> reader = (TopicReader<K>) ctors[0].newInstance(Paths.get("."));
      return reader.read(raw);

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns a set of evaluation topics, automatically trying to infer its type and format.
   *
   * @param file topics file
   * @param <K> type of topic id
   * @return evaluation topics
   */
  @SuppressWarnings("unchecked")
  public static <K> SortedMap<K, Map<String, String>> getTopicsByFile(String file) {
    try {
      // Get the constructor
      Constructor[] ctors = getTopicReaderClassByFile(file).getDeclaredConstructors();
      // The one we want is always the zero-th one; pass in a dummy Path.
      TopicReader<K> reader = (TopicReader<K>) ctors[0].newInstance(Paths.get(file));
      return reader.read();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns a standard set of evaluation topics, with strings as topic ids. This method is primarily meant for calling
   * from Python via Pyjnius. The conversion to string topic ids is necessary because Pyjnius has trouble with generics.
   *
   * @param topics topics
   * @return evaluation topics, with strings as topic ids
   */
  public static Map<String, Map<String, String>> getTopicsWithStringIds(Topics topics) {
    SortedMap<?, Map<String, String>> originalTopics = getTopics(topics);
    if (originalTopics == null)
      return null;

    Map<String, Map<String, String>> t = new HashMap<>();
    for (Object key : originalTopics.keySet()) {
      t.put(key.toString(), originalTopics.get(key));
    }

    return t;
  }

  /**
   * Returns a set of evaluation topics, reading from a file using a particular {@code TopicReader} class (as String).
   * This ridiculous method name is necessary for proper Python bindings via Pyjnius.
   *
   * @param className {@code TopicReader} class
   * @param file topics file
   * @return evaluation topics, with strings as topic ids
   */
  public static Map<String, Map<String, String>> getTopicsWithStringIdsFromFileWithTopicReaderClass(String className,
                                                                                                    String file) {
    try {
      Class clazz = Class.forName(className);
      Constructor[] ctors = clazz.getDeclaredConstructors();
      TopicReader<?> reader = (TopicReader<?>) ctors[0].newInstance(Paths.get(file));

      SortedMap<?, Map<String, String>> originalTopics = reader.read();
      if (originalTopics == null)
        return null;

      Map<String, Map<String, String>> t = new HashMap<>();
      for (Object key : originalTopics.keySet()) {
        t.put(key.toString(), originalTopics.get(key));
      }

      return t;
    } catch (Exception e) {
      return null;
    }
  }
}
