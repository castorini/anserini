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

package io.anserini.ann.lexlsh;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

import java.io.IOException;

/**
 * {@link TokenFilter} which truncates a number beyond {#length} decimals.
 */
class LexicalLshTruncateTokenFilter extends TokenFilter {

  private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

  private final int decimals;

  LexicalLshTruncateTokenFilter(TokenStream input, int decimals) {
    super(input);
    if (decimals < 1) {
      throw new IllegalArgumentException("'decimals' parameter must be a positive number: " + decimals);
    }
    this.decimals = decimals;
  }

  @Override
  public final boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      String s = termAttribute.toString();
      int decimalPlaceSeparatorIndex = s.indexOf(".");
      int threshold = decimalPlaceSeparatorIndex + 1 + decimals;
      if (!keywordAttr.isKeyword() && termAttribute.length() > threshold) {
        termAttribute.setLength(threshold);
      }
      return true;
    } else {
      return false;
    }
  }
}