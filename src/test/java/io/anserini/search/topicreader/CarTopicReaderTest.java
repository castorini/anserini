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

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.EnglishStemmingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class CarTopicReaderTest {

  @Test
  public void test() throws IOException {
    TopicReader<String> reader = new CarTopicReader(
        Paths.get("src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt"));

    SortedMap<String, Map<String, String>> topics = reader.read();

    assertEquals(2254, topics.keySet().size());
    assertEquals("enwiki:Aftertaste", topics.firstKey());
    assertEquals("Aftertaste", topics.get(topics.firstKey()).get("title"));

    assertEquals("enwiki:Yellowstone%20National%20Park/Recreation", topics.lastKey());
    String query = topics.get(topics.lastKey()).get("title");
    assertEquals("Yellowstone National Park/Recreation", query);

    // Make sure that the slash is properly tokenized.
    Analyzer analyzer = new EnglishStemmingAnalyzer("porter");
    List<String> tokens =  AnalyzerUtils.tokenize(analyzer, query);
    assertEquals(4, tokens.size());
    assertEquals("recreat", tokens.get(3));
  }
}
