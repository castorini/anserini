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

import java.io.IOException;

/**
 * {@link TokenFilter} which prepends the token / feature position plus underscore to the token itself
 * (possibly with some offset)
 */
final class LexicalLshFeaturePositionTokenFilter extends TokenFilter {

  private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
  private final int start;
  private int tokenCount = 0;

  LexicalLshFeaturePositionTokenFilter(TokenStream stream) {
    this(stream, 0);
  }

  LexicalLshFeaturePositionTokenFilter(TokenStream stream, int start) {
    super(stream);
    this.start = start;
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      tokenCount++;
      String token = new String(termAttribute.buffer(), 0, termAttribute.length());
      termAttribute.setEmpty();
      termAttribute.append(String.valueOf(tokenCount));
      termAttribute.append("_");
      if (start > 0 && start < token.length()) {
        if (token.startsWith("-")) {
          termAttribute.append("-");
          termAttribute.append(token.substring(start + 1));
        } else {
          termAttribute.append(token.substring(start));
        }
      } else {
        termAttribute.append(token);
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    tokenCount = 0;
  }

}