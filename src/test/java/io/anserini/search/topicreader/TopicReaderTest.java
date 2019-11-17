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

import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class TopicReaderTest {

  @Test
  public void test1() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(TopicReader.Topics.ROBUST04);

    assertEquals(250, topics.keySet().size());
    assertEquals(301, (int) topics.firstKey());
    assertEquals("International Organized Crime", topics.get(topics.firstKey()).get("title"));

    assertEquals(700, (int) topics.lastKey());
    assertEquals("gasoline tax U.S.", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(TopicReader.Topics.ROBUST05);

    assertEquals(50, topics.keySet().size());
    assertEquals(303, (int) topics.firstKey());
    assertEquals("Hubble Telescope Achievements", topics.get(topics.firstKey()).get("title"));

    assertEquals(689, (int) topics.lastKey());
    assertEquals("family-planning aid", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(TopicReader.Topics.CORE17);

    assertEquals(50, topics.keySet().size());
    assertEquals(307, (int) topics.firstKey());
    assertEquals("New Hydroelectric Projects", topics.get(topics.firstKey()).get("title"));

    assertEquals(690, (int) topics.lastKey());
    assertEquals("college education advantage", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(TopicReader.Topics.CORE18);

    assertEquals(50, topics.keySet().size());
    assertEquals(321, (int) topics.firstKey());
    assertEquals("Women in Parliaments", topics.get(topics.firstKey()).get("title"));

    assertEquals(825, (int) topics.lastKey());
    assertEquals("ethanol and food prices", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void test2() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(TopicReader.Topics.MSMARCO_DOC_DEV);

    assertEquals(5193, topics.keySet().size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("androgen receptor define", topics.get(topics.firstKey()).get("title"));

    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(TopicReader.Topics.MSMARCO_PASSAGE_DEV_SUBSET);

    assertEquals(6980, topics.keySet().size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));

    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));
  }
}
