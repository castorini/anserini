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

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.junit.Test;

import java.util.Arrays;

import static io.anserini.analysis.DefaultEnglishAnalyzer.fromArguments;
import static org.junit.Assert.assertEquals;

public class DefaultEnglishAnalyzerTest {

  @Test
  public void testKeepStopwords() throws Exception {
    DefaultEnglishAnalyzer defaultAnalyzer = fromArguments("porter", false, null);
    assertEquals(EnglishAnalyzer.getDefaultStopSet(), defaultAnalyzer.getStopwordSet());

    DefaultEnglishAnalyzer analyzer = fromArguments("porter", true, null);
    assertEquals(CharArraySet.EMPTY_SET, analyzer.getStopwordSet());
  }

  @Test
  public void testStopwordsLoading() throws Exception {
    DefaultEnglishAnalyzer analyzer = fromArguments("porter", false, "src/test/resources/test-stopwords.txt");
    CharArraySet expectedStopwords = new CharArraySet(Arrays.asList("some", "very", "common", "words"), false);
    assertEquals(expectedStopwords, analyzer.getStopwordSet());
  }
}
