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
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;

public final class TweetAnalyzer extends Analyzer {
  private final boolean stemming;

  public TweetAnalyzer(boolean stemming) {
    this.stemming = stemming;
  }

  public TweetAnalyzer() {
    this(true);
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer source = new WhitespaceTokenizer();
    TokenStream filter = new TweetLowerCaseEntityPreservingFilter(source);
    if (stemming) {
      // Porter stemmer ignores words which are marked as keywords
      filter = new PorterStemFilter(filter);
    }
    return new TokenStreamComponents(source, filter);
  }

}