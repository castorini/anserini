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

public class CacmTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<Integer> reader = new CacmTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.cacm.txt"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(64, topics.keySet().size());

    assertEquals(1, (int) topics.firstKey());
    assertEquals("What articles exist which deal with TSS (Time Sharing System), an\n" +
        "operating system for IBM computers?", topics.get(topics.firstKey()).get("title").trim());

    assertEquals(64, (int) topics.lastKey());
    assertEquals("List all articles on EL1 and ECL (EL1 may be given as EL/1; I don't\n" +
        "remember how they did it.", topics.get(topics.lastKey()).get("title").trim());
  }
}
