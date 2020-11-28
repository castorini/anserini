/*
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

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class WebTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<Integer> reader = new WebTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.terabyte05.efficiency.txt"));

    Map<Integer, Map<String, String>> topics = reader.read();
    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(50000, topics.keySet().size());

    assertEquals(1, (int) firstKey);
    assertEquals("pierson s twin lakes marina", topics.get(firstKey).get("title"));

    assertEquals(50000, (int) lastKey);
    assertEquals("senator durbin", topics.get(lastKey).get("title"));
  }

  @Test
  public void testMillionQueryTopics1() throws IOException {
    Path resource = Paths.get("src/main/resources/topics-and-qrels/topics.mq.1-10000.txt");
    TopicReader<Integer> reader = new WebTopicReader(resource);

    Map<Integer, Map<String, String>> topics = reader.read();

    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(1, (int) firstKey);
    assertEquals("after school program evaluation", topics.get(firstKey).get("title").trim());

    assertEquals(10000, (int) lastKey);
    assertEquals("californa mission", topics.get(lastKey).get("title").trim());
    assertEquals(10000, topics.keySet().size());
  }

  @Test
  public void testMillionQueryTopics2() throws IOException {
    Path resource = Paths.get("src/main/resources/topics-and-qrels/topics.mq.10001-20000.txt");
    TopicReader<Integer> reader = new WebTopicReader(resource);

    Map<Integer, Map<String, String>> topics = reader.read();

    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(10001, (int) firstKey);
    assertEquals("comparability of pay analyses", topics.get(firstKey).get("title").trim());

    assertEquals(20000, (int) lastKey);
    assertEquals("manchester city hall", topics.get(lastKey).get("title").trim());
    assertEquals(10000, topics.keySet().size());
  }
}
