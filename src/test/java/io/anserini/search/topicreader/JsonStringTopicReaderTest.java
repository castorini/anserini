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

public class JsonStringTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<String> reader = new JsonStringTopicReader(
        Paths.get("src/test/resources/sample_topics/stringID_topics.jsonl"));

    SortedMap<String, Map<String, String>> topics = reader.read();

    assertEquals(2, topics.keySet().size());
    assertEquals("topic1", topics.firstKey());
    assertEquals("topic2", topics.lastKey());
    assertEquals("this is the contents 1.", topics.get(topics.firstKey()).get("contents"));
    assertEquals("topic1 field1 content", topics.get(topics.firstKey()).get("field1"));
    assertEquals("topic1 field2 content", topics.get(topics.firstKey()).get("field2"));
    assertEquals("this is the contents 2.", topics.get(topics.lastKey()).get("contents"));
    assertEquals("topic2 field1 content", topics.get(topics.lastKey()).get("field1"));
    assertEquals("topic2 field2 content", topics.get(topics.lastKey()).get("field2"));

  }
}
