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
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class DprJsonlTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<Integer> reader = new DprJsonlTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.dpr.wq.test.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(2032, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("what does jamaican people speak?", topics.get(topics.firstKey()).get("title"));
    assertEquals("[\"Jamaican Creole English Language\",\"Jamaican English\"]", topics.get(topics.firstKey()).get("answers"));
    assertEquals(2031, (int) topics.lastKey());
    assertEquals("when was father chris riley born?", topics.get(topics.lastKey()).get("title"));
    assertEquals("[\"1967\"]", topics.get(topics.lastKey()).get("answers"));
  }
}
