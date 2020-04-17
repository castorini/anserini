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
import org.apache.lucene.analysis.CharArraySet;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EnglishCovidAnalyzerTest {

  private static final String INPUT1 = "Recent structural characterizations of S proteins in Covid-19.";
  private static final String INPUT2 = "Incubation period was 5.2 days (95% confidence interval [CI], 4.1 to 7.0).";

  private static final List<String> s1 = List.of("recent", "structural", "characterizations", "of", "s",
                                                 "proteins", "in", "covid-19");
  private static final List<String> s2 = List.of("incubation", "period", "was", "5.2", "days", "95", "confidence",
                                                 "interval", "ci", "4.1", "to", "7.0");

  @Test
  public void testTokens() {
    Analyzer analyzer = EnglishCovidAnalyzer.newInstance(CharArraySet.EMPTY_SET);
    assertEquals(s1, AnalyzerUtils.analyze(analyzer, INPUT1));
    assertEquals(s2, AnalyzerUtils.analyze(analyzer, INPUT2));
  }
}