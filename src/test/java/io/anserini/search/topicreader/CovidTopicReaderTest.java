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

public class CovidTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<Integer> reader = new CovidTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.covid-round1.xml"));

    SortedMap<Integer, Map<String, String>> topics = reader.read();

    assertEquals(30, topics.keySet().size());

    assertEquals(1, (int) topics.firstKey());
    assertEquals("coronavirus origin", topics.get(topics.firstKey()).get("query"));
    assertEquals("what is the origin of COVID-19", topics.get(topics.firstKey()).get("question"));
    assertEquals("seeking range of information about the SARS-CoV-2 virus's origin, " +
        "including its evolution, animal source, and first transmission into humans",
        topics.get(topics.firstKey()).get("narrative"));

    assertEquals(30, (int) topics.lastKey());
    assertEquals("coronavirus remdesivir", topics.get(topics.lastKey()).get("query"));
    assertEquals("is remdesivir an effective treatment for COVID-19",
        topics.get(topics.lastKey()).get("question"));
    assertEquals(
        "seeking specific information on clinical outcomes in COVID-19 patients treated with remdesivir",
        topics.get(topics.lastKey()).get("narrative"));
  }
}
