/**
 * Anserini: A toolkit for reproducible information retrieval research built on Lucene
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
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
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
    StandardFilter result = new StandardFilter(source);
    EnglishPossessiveFilter result2 = new EnglishPossessiveFilter(result);
    LowerCaseFilter result3 = new LowerCaseFilter(result2);
    Object result4 = new StopFilter(result3, this.stopwords);
    /* ASCIIFoldingFilter is used for accent folding. This will norma. ize the characters
    * with accent such as latin characters, which enhances the search */
    result4 = new ASCIIFoldingFilter((TokenStream) result4);
    if(!this.stemExclusionSet.isEmpty()) {
      result4 = new SetKeywordMarkerFilter((TokenStream)result4, this.stemExclusionSet);
    }

    PorterStemFilter result1 = new PorterStemFilter((TokenStream)result4);
    return new TokenStreamComponents(source, result1);
  }

  protected TokenStream normalize(String fieldName, TokenStream in) {
    StandardFilter result = new StandardFilter(in);
    LowerCaseFilter result1 = new LowerCaseFilter(result);
    return result1;
  }

  private static class DefaultSetHolder {
    static final CharArraySet DEFAULT_STOP_SET;

    private DefaultSetHolder() {
    }

    static {
      DEFAULT_STOP_SET = StandardAnalyzer.STOP_WORDS_SET;
    }
  }
}