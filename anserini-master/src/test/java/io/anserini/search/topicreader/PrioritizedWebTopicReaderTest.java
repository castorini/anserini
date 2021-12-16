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

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class PrioritizedWebTopicReaderTest {
  @Test
  public void testMillionQueryTopics3() throws IOException {
    Path resource = Paths.get("src/main/resources/topics-and-qrels/topics.mq.20001-60000.txt");
    TopicReader<Integer> reader = new PrioritizedWebTopicReader(resource);
    
    SortedMap<Integer, Map<String, String>> topics = reader.read();
    
    assertEquals(20001, (int) topics.firstKey());
    assertEquals("obama family tree", topics.get(topics.firstKey()).get("title").trim());
    assertEquals("1", topics.get(topics.firstKey()).get("priority").trim());

    assertEquals(60000, (int) topics.lastKey());
    assertEquals("bird shingles", topics.get(topics.lastKey()).get("title").trim());
    assertEquals("4", topics.get(topics.lastKey()).get("priority").trim());

    assertEquals(40000, topics.keySet().size());
  }
}
