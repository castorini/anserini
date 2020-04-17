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
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;

/**
 * Analyzer targeted for Covid-19 (CORS) data, not splitting tokens at hyphens (covid-19, sars-cov-2, etc.).
 */
public class EnglishCovidAnalyzer extends Analyzer {

  private final CharArraySet stopWords;

  private EnglishCovidAnalyzer(CharArraySet stopWords) {
    this.stopWords = stopWords;
  }

  public static Analyzer newInstance(CharArraySet stopWords) {
    return new EnglishCovidAnalyzer(stopWords);
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer source = new ClassicTokenizer();
    TokenStream result;
    result = source;
    result = new EnglishPossessiveFilter(result);
    result = new LowerCaseFilter(result);
    result = new StopFilter(result, this.stopWords);
    return new TokenStreamComponents(source, result);
  }
}