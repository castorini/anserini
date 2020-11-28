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

import static org.junit.Assert.assertEquals;

public class MicroblogTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<Integer> reader =
        new MicroblogTopicReader(Paths.get("src/main/resources/topics-and-qrels/topics.microblog2011.txt"));

    Map<Integer, Map<String, String>> topics = reader.read();
    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(50, topics.keySet().size());

    assertEquals(1, (int) firstKey);
    assertEquals("BBC World Service staff cuts", topics.get(firstKey).get("title"));
    assertEquals("34952194402811904", topics.get(firstKey).get("time"));

    assertEquals(50, (int) lastKey);
    assertEquals("war prisoners, Hatch Act", topics.get(lastKey).get("title"));
    assertEquals("29723425576587264", topics.get(lastKey).get("time"));
  }
}
