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

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link LexicalLshTruncateTokenFilter}
 */
public class LexicalLshTruncateTokenFilterTest {

  @Test
  public void testFiltering() throws Exception {
    StringReader reader = new StringReader("0.10123 0.20412412 -0.3042141 0.4123123");
    Tokenizer stream = new WhitespaceTokenizer();
    stream.setReader(reader);
    LexicalLshTruncateTokenFilter filter = new LexicalLshTruncateTokenFilter(stream, 3);
    filter.reset();
    List<String> expectedTokens = new LinkedList<>();
    expectedTokens.add("0.101");
    expectedTokens.add("0.204");
    expectedTokens.add("-0.304");
    expectedTokens.add("0.412");
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