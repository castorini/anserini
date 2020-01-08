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

package io.anserini.ann.fw;

import io.anserini.ann.FeatureVectorsTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link FakeWordsEncodeAndQuantizeFilter}
 */
public class FakeWordsEncodeAndQuantizeFilterTest {

  @Test
  public void testFiltering() throws Exception {
    StringReader reader = new StringReader("-0.10 0.20 0.30 0.40");
    Tokenizer stream = new FeatureVectorsTokenizer();
    stream.setReader(reader);
    FakeWordsEncodeAndQuantizeFilter filter = new FakeWordsEncodeAndQuantizeFilter(stream, 20);
    filter.reset();
    List<String> expectedTokens = new LinkedList<>();
    expectedTokens.add("_"); // quantization leads to zero
    expectedTokens.add("f2"); // quantization leads to 4 tokens
    expectedTokens.add("f2");
    expectedTokens.add("f2");
    expectedTokens.add("f2");
    expectedTokens.add("f3"); // quantization leads to 6 tokens
    expectedTokens.add("f3");
    expectedTokens.add("f3");
    expectedTokens.add("f3");
    expectedTokens.add("f3");
    expectedTokens.add("f3");
    expectedTokens.add("f4"); // quantization leads to 16 tokens
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
    expectedTokens.add("f4");
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