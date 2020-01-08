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
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public final class FreebaseAnalyzer extends StopwordAnalyzerBase {
  private final CharArraySet stemExclusionSet;

  public static CharArraySet getDefaultStopSet() {
    return FreebaseAnalyzer.DefaultSetHolder.DEFAULT_STOP_SET;
  }

  public FreebaseAnalyzer() {
    this(FreebaseAnalyzer.DefaultSetHolder.DEFAULT_STOP_SET);
  }

  public FreebaseAnalyzer(CharArraySet stopwords) {
    this(stopwords, CharArraySet.EMPTY_SET);
  }

  public FreebaseAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet) {
    super(stopwords);
    this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
  }

  protected TokenStreamComponents createComponents(String fieldName) {
    StandardTokenizer source = new StandardTokenizer();
    TokenStream result = source;
    result = new EnglishPossessiveFilter(result);
    result = new LowerCaseFilter(result);
    result = new StopFilter(result, this.stopwords);
    result = new ASCIIFoldingFilter(result);

    if(!this.stemExclusionSet.isEmpty()) {
      result = new SetKeywordMarkerFilter(result, this.stemExclusionSet);
    }

    result = new PorterStemFilter(result);
    return new TokenStreamComponents(source, result);
  }

  protected TokenStream normalize(String fieldName, TokenStream in) {
    TokenStream result = in;
    result = new LowerCaseFilter(result);
    return result;
  }

  private static class DefaultSetHolder {
    static final CharArraySet DEFAULT_STOP_SET;

    private DefaultSetHolder() {
    }

    static {
      DEFAULT_STOP_SET = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
  }
}
