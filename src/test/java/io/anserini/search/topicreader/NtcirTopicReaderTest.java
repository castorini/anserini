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
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class NtcirTopicReaderTest {

  @Test
  public void test_www1_en() throws IOException {

    Path path1 = Paths.get("src/main/resources/topics-and-qrels/topics.www1.english.txt");
    TopicReader<Integer> reader1 = new NtcirTopicReader(path1);

    SortedMap<Integer, Map<String, String>> topics1 = reader1.read();

    assertEquals(100, topics1.keySet().size());
    assertEquals(1, (int) topics1.firstKey());
    assertEquals("ascii code", topics1.get(topics1.firstKey()).get("title"));

    assertEquals(100, (int) topics1.lastKey());
    assertEquals("weight loss", topics1.get(topics1.lastKey()).get("title"));

  }

  @Test
  public void test_www2_en() throws IOException {
    Path path = Paths.get("src/main/resources/topics-and-qrels/topics.www2.english.txt");
    TopicReader<Integer> reader = new NtcirTopicReader(path);

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(80, topics.keySet().size());
    assertEquals(1, (int) topics.firstKey());
    assertEquals("Halloween picture", topics.get(topics.firstKey()).get("title"));
    assertEquals("Halloween is coming. You want to find some pictures about" +
            " Halloween to introduce it to your children.",
        topics.get(topics.firstKey()).get("description"));


    assertEquals(80, (int) topics.lastKey());
    assertEquals("www.gardenburger.com", topics.get(topics.lastKey()).get("title"));
    assertEquals("You want to find the website &quot;www.gardenburger.com&quot;",
        topics.get(topics.lastKey()).get("description"));

  }
}

