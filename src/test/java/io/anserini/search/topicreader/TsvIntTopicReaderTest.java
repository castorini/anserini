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

public class TsvIntTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<Integer> reader = new TsvIntTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(6980, topics.keySet().size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));

    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));
  }
}
