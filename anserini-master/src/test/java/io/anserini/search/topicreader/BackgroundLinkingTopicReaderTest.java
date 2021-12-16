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
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class BackgroundLinkingTopicReaderTest {

  @Test
  public void test2018() throws IOException {
    TopicReader<Integer> reader = new BackgroundLinkingTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(50, topics.keySet().size());
    assertEquals(321, (int) topics.firstKey());
    assertEquals("9171debc316e5e2782e0d2404ca7d09d", topics.get(topics.firstKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/news/worldviews/wp/2016/09/01/" +
        "women-are-half-of-the-world-but-only-22-percent-of-its-parliaments/",
        topics.get(topics.firstKey()).get("url"));

    assertEquals(825, (int) topics.lastKey());
    assertEquals("a1c41a70-35c7-11e3-8a0e-4e2cf80831fc", topics.get(topics.lastKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/business/economy/" +
        "cellulosic-ethanol-once-the-way-of-the-future-is-off-to-a-delayed-boisterous-start/" +
        "2013/11/08/a1c41a70-35c7-11e3-8a0e-4e2cf80831fc_story.html", topics.get(topics.lastKey()).get("url"));
  }

  @Test
  public void test2019() throws IOException {
    TopicReader<Integer> reader = new BackgroundLinkingTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.backgroundlinking19.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(60, topics.keySet().size());
    assertEquals(826, (int) topics.firstKey());
    assertEquals("96ab542e-6a07-11e6-ba32-5a4bf5aad4fa", topics.get(topics.firstKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/sports/nationals/" +
        "the-minor-leagues-life-in-pro-baseballs-shadowy-corner/" +
        "2016/08/26/96ab542e-6a07-11e6-ba32-5a4bf5aad4fa_story.html", topics.get(topics.firstKey()).get("url"));

    assertEquals(885, (int) topics.lastKey());
    assertEquals("5ae44bfd66a49bcad7b55b29b55d63b6", topics.get(topics.lastKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/news/capital-weather-gang/wp/2017/07/14/" +
        "sun-erupts-to-mark-another-bastille-day-aurora-possible-in-new-england-sunday-night/",
        topics.get(topics.lastKey()).get("url"));
  }
}
