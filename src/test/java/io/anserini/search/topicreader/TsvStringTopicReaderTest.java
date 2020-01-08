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

public class TsvStringTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<String> reader = new TsvStringTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.ntcir8en.eval.txt"));

    SortedMap<String, Map<String, String>> topics = reader.read();

    assertEquals(73, topics.keySet().size());
    assertEquals("ACLIA2-CS-0002", topics.firstKey());
    assertEquals("What is the relationship between the movie \"Riding Alone for Thousands of Miles\" and ZHANG Yimou?",
        topics.get(topics.firstKey()).get("title"));

    assertEquals("ACLIA2-CS-0100", topics.lastKey());
    assertEquals("Why did U.S. troops occupy Baghdad?", topics.get(topics.lastKey()).get("title"));
  }
}
