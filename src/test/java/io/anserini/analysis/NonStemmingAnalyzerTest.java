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

public class NonStemmingAnalyzerTest {
  @Test
  public void test_non_stemming() {
    Analyzer analyzer = new NonStemmingAnalyzer();
    List<String> tokens = AnalyzerUtils.tokenize(analyzer, "these words should not have stemming");

    assertEquals(6, tokens.size());
    assertEquals("these", tokens.get(0));
    assertEquals("words", tokens.get(1));
    assertEquals("should", tokens.get(2));
    assertEquals("not", tokens.get(3));
    assertEquals("have", tokens.get(4));
    assertEquals("stemming", tokens.get(5));
  }

  @Test
  public void test_tokenize() {
    Analyzer analyzer = new NonStemmingAnalyzer();
    List<String> tokens = AnalyzerUtils.tokenize(analyzer, "tokenize :: { punctuation -> between } :: words");

    assertEquals(4, tokens.size());
    assertEquals("tokenize", tokens.get(0));
    assertEquals("punctuation", tokens.get(1));
    assertEquals("between", tokens.get(2));
    assertEquals("words", tokens.get(3));
  }
}
