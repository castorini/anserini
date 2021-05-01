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

package io.anserini.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EnglishStemmingAnalyzerTest {
  private static final String INPUT = "City buses are running on schedule.";

  private static final List<String> PORTER_STOP_DEFAULT = List.of("citi", "buse", "run", "schedul");
  private static final List<String> PORTER_STOP_CUSTOM = List.of("citi", "buse", "run", "on", "schedul");
  private static final List<String> PORTER_NOSTOP = List.of("citi", "buse", "ar", "run", "on", "schedul");

  private static final List<String> KROVETZ_STOP_DEFAULT = List.of("city", "bus", "running", "schedule");
  private static final List<String> KROVETZ_STOP_CUSTOM = List.of("city", "bus", "running", "on", "schedule");
  private static final List<String> KROVETZ_NOSTOP = List.of("city", "bus", "are", "running", "on", "schedule");

  private static final List<String> NOSTEM_STOP_DEFAULT = List.of("city", "buses", "running", "schedule");
  private static final List<String> NOSTEM_STOP_CUSTOM = List.of("city", "buses", "running", "on", "schedule");
  private static final List<String> NOSTEM_NOSTOP = List.of("city", "buses", "are", "running", "on", "schedule");

  private Analyzer analyzer;

  @Test
  public void test1() {
    // Default is Porter stemming.
    analyzer = DefaultEnglishAnalyzer.newDefaultInstance();
    assertEquals(PORTER_STOP_DEFAULT, AnalyzerUtils.analyze(analyzer, INPUT));

    analyzer = DefaultEnglishAnalyzer.newStemmingInstance("porter");
    assertEquals(PORTER_STOP_DEFAULT, AnalyzerUtils.analyze(analyzer, INPUT));

    analyzer = DefaultEnglishAnalyzer.newStemmingInstance("porter",
        new CharArraySet(List.of("are"), true));
    assertEquals(PORTER_STOP_CUSTOM, AnalyzerUtils.analyze(analyzer, INPUT));

    analyzer = DefaultEnglishAnalyzer.newStemmingInstance("porter", CharArraySet.EMPTY_SET);
    assertEquals(PORTER_NOSTOP, AnalyzerUtils.analyze(analyzer, INPUT));
}

  @Test
  public void test2() {
    analyzer = DefaultEnglishAnalyzer.newStemmingInstance("krovetz");
    assertEquals(KROVETZ_STOP_DEFAULT, AnalyzerUtils.analyze(analyzer, INPUT));

    analyzer = DefaultEnglishAnalyzer.newStemmingInstance("krovetz",
        new CharArraySet(List.of("are"), true));
    assertEquals(KROVETZ_STOP_CUSTOM, AnalyzerUtils.analyze(analyzer, INPUT));

    analyzer = DefaultEnglishAnalyzer.newStemmingInstance("krovetz", CharArraySet.EMPTY_SET);
    assertEquals(KROVETZ_NOSTOP, AnalyzerUtils.analyze(analyzer, INPUT));
  }

  @Test
  public void test3() {
    analyzer = DefaultEnglishAnalyzer.newNonStemmingInstance();
    assertEquals(NOSTEM_STOP_DEFAULT, AnalyzerUtils.analyze(analyzer, INPUT));

    analyzer = DefaultEnglishAnalyzer.newNonStemmingInstance(new CharArraySet(List.of("are"), true));
    assertEquals(NOSTEM_STOP_CUSTOM, AnalyzerUtils.analyze(analyzer, INPUT));

    analyzer = DefaultEnglishAnalyzer.newNonStemmingInstance(CharArraySet.EMPTY_SET);
    assertEquals(NOSTEM_NOSTOP, AnalyzerUtils.analyze(analyzer, INPUT));
  }
}
