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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static io.anserini.ann.fw.FakeWordsEncoderAnalyzer.REMOVE_IT;

/**
 * {@link TokenFilter} that encodes a real valued token into a stream of "fake word" tokens proportional to
 * the corresponding quantized input (real) value.
 *
 * Note: if quantization leads to zero, a "to-be-removed" token is added (and eventually taken care of by a
 * {@link org.apache.lucene.analysis.StopFilter} in the downstream text analysis chain.
 */
public final class FakeWordsEncodeAndQuantizeFilter extends TokenFilter {

  private static final String PREFIX = "f";
  private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
  private final int q;
  private final List<String> fs = new LinkedList<>();
  private int tokenCount = 0;

  FakeWordsEncodeAndQuantizeFilter(TokenStream input, int q) {
    super(input);
    this.q = q;
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (!fs.isEmpty()) {
      termAttribute.setEmpty();
      termAttribute.append(fs.remove(0));
      return true;
    }
    if (input.incrementToken()) {
      tokenCount++;
      String token = new String(termAttribute.buffer(), 0, termAttribute.length());
      int qv = (int) (Double.parseDouble(token) * q);
      String fw = PREFIX + tokenCount;
      for (int i = 0; i < qv - 1; i++) {
        fs.add(fw);
      }
      termAttribute.setEmpty();
      if (qv > 0) {
        termAttribute.append(fw);
      } else {
        termAttribute.append(REMOVE_IT);
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    this.fs.clear();
    this.tokenCount = 0;
  }
}
