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

package io.anserini.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EnglishStemmingAnalyzerTest {
  @Test
  public void test1() {
    Analyzer analyzer = new EnglishStemmingAnalyzer("porter");
    List<String> tokens = AnalyzerUtils.tokenize(analyzer, "city buses are running on schedule");

    assertEquals(4, tokens.size());
    assertEquals("citi", tokens.get(0));
    assertEquals("buse", tokens.get(1));
    assertEquals("run", tokens.get(2));
    assertEquals("schedul", tokens.get(3));
  }

  @Test
  public void test2() {
    Analyzer analyzer = new EnglishStemmingAnalyzer("krovetz");
    List<String> tokens = AnalyzerUtils.tokenize(analyzer, "city buses are running on schedule");

    assertEquals(4, tokens.size());
    assertEquals("city", tokens.get(0));
    assertEquals("bus", tokens.get(1));
    assertEquals("running", tokens.get(2));
    assertEquals("schedule", tokens.get(3));
  }

}
