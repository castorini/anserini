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

public class TrecTopicReaderTest {

  @Test
  public void test1() throws IOException {
    TopicReader<Integer> reader = new TrecTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.robust04.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(250, topics.keySet().size());
    assertEquals(301, (int) topics.firstKey());
    assertEquals("International Organized Crime", topics.get(topics.firstKey()).get("title"));

    assertEquals(700, (int) topics.lastKey());
    assertEquals("gasoline tax U.S.", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void test2() throws IOException {
    TopicReader<Integer> reader = new TrecTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.core18.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(50, topics.keySet().size());
    assertEquals(321, (int) topics.firstKey());
    assertEquals("Women in Parliaments", topics.get(topics.firstKey()).get("title"));

    assertEquals(825, (int) topics.lastKey());
    assertEquals("ethanol and food prices", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void test3() throws IOException {
    TopicReader<Integer> reader = new TrecTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.fire12bn.176-225.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(50, topics.keySet().size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("ওয়াই এস আর রেড্ডির মৃত্যু", topics.get(topics.firstKey()).get("title"));

    assertEquals(225, (int) topics.lastKey());
    assertEquals("স্যাটানিক ভার্সেস বিতর্ক", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void test4() throws IOException {
    // Note that this file has a formatting error - make sure we can handle it.
    TopicReader<Integer> reader = new TrecTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.fire12hi.176-225.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(50, topics.keySet().size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("वाई एस आर रेड्डी की मौत", topics.get(topics.firstKey()).get("title"));

    assertEquals("2002 नेटवेस्ट शृंखला का परिणाम", topics.get(200).get("title"));
    assertEquals("इराक का प्रथम चुनाव", topics.get(201).get("title"));

    assertEquals(225, (int) topics.lastKey());
    assertEquals("सेटेनिक वर्सेज विवाद", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void test5() throws IOException {
    TopicReader<Integer> reader = new TrecTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.trec02ar-ar.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(50, topics.keySet().size());
    assertEquals(26, (int) topics.firstKey());
    assertEquals("مجلس المقاومة الوطني الكردستاني", topics.get(topics.firstKey()).get("title"));

    assertEquals(75, (int) topics.lastKey());
    assertEquals("فيروسات الكمبيوتر في الوطن العربي", topics.get(topics.lastKey()).get("title"));
  }

}
