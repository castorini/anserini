/**
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

package io.anserini.search.topicreader;

import java.io.BufferedReader;
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
import java.util.Map;
import java.util.SortedMap;

/**
 * A reader of topics, i.e., information needs or queries, in a variety of standard formats.
 *
 * @param <K> type of the topic id
 */
public abstract class TopicReader<K> {
  protected Path topicFile;

  public TopicReader() {}

  public TopicReader(Path topicFile) {
    this.topicFile = topicFile;
  }

  /**
   * Returns a sorted map of ids to topics. Each topic is represented as a map, where the keys are the standard TREC
   * topic fields "title", "description", and "narrative". For topic formats that do not provide this three-way
   * elaboration, the "title" key is used to hold the "query".
   *
   * @return a sorted map of ids to topics
   * @throws IOException if error encountered reading topics
   */
  public SortedMap<K, Map<String, String>> read() throws IOException {
    InputStream topics = Files.newInputStream(topicFile, StandardOpenOption.READ);
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(topics, StandardCharsets.UTF_8));
    return read(bRdr);
  }

  public SortedMap<K, Map<String, String>> read(String str) throws IOException {
    BufferedReader bRdr = new BufferedReader(new StringReader(str));
    return read(bRdr);
  }

  abstract public SortedMap<K, Map<String, String>> read(BufferedReader bRdr) throws IOException;

  public enum Topics {
    ROBUST04(TrecTopicReader.class, "topics-and-qrels/topics.robust04.txt"),
    ROBUST05(TrecTopicReader.class, "topics-and-qrels/topics.robust05.txt"),
    CORE17(TrecTopicReader.class, "topics-and-qrels/topics.core17.txt"),
    CORE18(TrecTopicReader.class, "topics-and-qrels/topics.core18.txt"),
    MSMARCO_DOC_DEV(TsvIntTopicReader.class,"topics-and-qrels/topics.msmarco-doc.dev.txt"),
    MSMARCO_PASSAGE_DEV_SUBSET(TsvIntTopicReader.class, "topics-and-qrels/topics.msmarco-passage.dev-subset.txt");

    public final String path;
    public final Class readerClass;
    Topics(Class c, String path) {
      this.readerClass = c;
      this.path = path;
    }
  }

  @SuppressWarnings("unchecked")
  public static <K> SortedMap<K, Map<String, String>> getTopics(Topics topics) {
    try {
      InputStream inputStream = TopicReader.class.getClassLoader().getResourceAsStream(topics.path);
      String raw = new String(inputStream.readAllBytes());

      // Get the constructor
      Constructor[] ctors = topics.readerClass.getDeclaredConstructors();
      // The one we want is always the zero-th one; pass in a dummy Path.
      TopicReader<K> reader = (TopicReader<K>) ctors[0].newInstance(Paths.get("."));
      return reader.read(raw);

    } catch (Exception e) {
      return null;
    }
  }
}
