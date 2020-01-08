/**
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

package io.anserini.ann.lexlsh;

import io.anserini.ann.FeatureVectorsTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link LexicalLshFeaturePositionTokenFilter}
 */
public class LexicalLshFeaturePositionTokenFilterTest {

  @Test
  public void testFiltering() throws Exception {
    StringReader reader = new StringReader("-0.10 0.20 0.30 0.40");
    Tokenizer stream = new FeatureVectorsTokenizer();
    stream.setReader(reader);
    LexicalLshFeaturePositionTokenFilter filter = new LexicalLshFeaturePositionTokenFilter(stream);
    filter.reset();
    List<String> expectedTokens = new LinkedList<>();
    expectedTokens.add("1_-0.10");
    expectedTokens.add("2_0.20");
    expectedTokens.add("3_0.30");
    expectedTokens.add("4_0.40");
    int i = 0;
    while (filter.incrementToken()) {
      CharTermAttribute charTermAttribute = filter.getAttribute(CharTermAttribute.class);
      String token = new String(charTermAttribute.buffer(), 0, charTermAttribute.length());
      assertEquals(expectedTokens.get(i), token);
      i++;
    }
    filter.close();
  }

  @Test
  public void testFilteringWithOffset() throws Exception {
    StringReader reader = new StringReader("-0.104123 0.20435 0.3042366 0.41243241");
    Tokenizer stream = new FeatureVectorsTokenizer();
    stream.setReader(reader);
    LexicalLshFeaturePositionTokenFilter filter = new LexicalLshFeaturePositionTokenFilter(stream, 2);
    filter.reset();
    List<String> expectedTokens = new LinkedList<>();
    expectedTokens.add("1_-104123");
    expectedTokens.add("2_20435");
    expectedTokens.add("3_3042366");
    expectedTokens.add("4_41243241");
    int i = 0;
    while (filter.incrementToken()) {
      CharTermAttribute charTermAttribute = filter.getAttribute(CharTermAttribute.class);
      String token = new String(charTermAttribute.buffer(), 0, charTermAttribute.length());
      assertEquals(expectedTokens.get(i), token);
      i++;
    }
    filter.close();
  }

}