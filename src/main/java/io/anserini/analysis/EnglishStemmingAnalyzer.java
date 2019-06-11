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

package io.anserini.analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class EnglishStemmingAnalyzer extends StopwordAnalyzerBase {
  private final String stemmer;
  private final CharArraySet stemExclusionSet;
  
  public EnglishStemmingAnalyzer() {
    this("", EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
  }
  
  public EnglishStemmingAnalyzer(String stemmer) {
    this(stemmer, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET, CharArraySet.EMPTY_SET);
  }
  
  public EnglishStemmingAnalyzer(CharArraySet stopwords) {
    this("", stopwords, CharArraySet.EMPTY_SET);
  }
  
  public EnglishStemmingAnalyzer(String stemmer, CharArraySet stopwords) {
    this(stemmer, stopwords, CharArraySet.EMPTY_SET);
  }
  
  public EnglishStemmingAnalyzer(String stemmer, CharArraySet stopwords, CharArraySet stemExclusionSet) {
    super(stopwords);
    this.stemmer = stemmer;
    this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
  }
  
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer source = new StandardTokenizer();
    TokenStream result = null;
    result = source;
    result = new EnglishPossessiveFilter(result);
    result = new LowerCaseFilter(result);
    result = new StopFilter(result, this.stopwords);
    if (!this.stemExclusionSet.isEmpty()) {
      result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionSet);
    }
    
    if (this.stemmer.compareToIgnoreCase("porter") == 0 || this.stemmer.compareToIgnoreCase("p") == 0) {
      result = new PorterStemFilter((TokenStream)result);
    } else if (this.stemmer.compareToIgnoreCase("krovetz") == 0 || this.stemmer.compareToIgnoreCase("k") == 0) {
      result = new KStemFilter((TokenStream)result);
    }
    
    return new TokenStreamComponents(source, result);
  }
  
  protected TokenStream normalize(String fieldName, TokenStream in) {
    TokenStream result = in;
    result = new LowerCaseFilter(result);
    return result;
  }
}
