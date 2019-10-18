/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.anserini.analysis.vectors.fw;

import io.anserini.analysis.vectors.FeatureVectorsTokenizer;
import org.apache.lucene.analysis.*;

/**
 * {@link Analyzer} that encodes input vectors as "fake words", see paper "Large scale indexing
 * and searching deep convolutional neural network features" from Amato et al. (DaWaK 2016).
 */
public class FakeWordsEncoderAnalyzer extends Analyzer {

  static final String REMOVE_IT = "_";
  private final int q;

  private CharArraySet set = new CharArraySet(1, false);

  public FakeWordsEncoderAnalyzer(int q) {
    this.q = q;
    this.set.add(REMOVE_IT);
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer t = new FeatureVectorsTokenizer();
    TokenFilter filter = new EncodeAndQuantizeFilter(t, q);
    filter = new StopFilter(filter, set);
    return new TokenStreamComponents(t, filter);
  }
}
