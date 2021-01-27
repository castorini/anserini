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

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultEnglishAnalyzer extends StopwordAnalyzerBase {
  private final boolean stem;
  private final String stemmer;
  private final CharArraySet stemExclusionSet = CharArraySet.EMPTY_SET;

  // Constructors are private - use static factory methods to construct.
  private DefaultEnglishAnalyzer(String stemmer, CharArraySet stopwords) {
    super(stopwords);
    this.stem = true;
    this.stemmer = stemmer;
  }

  // Constructors are private - use static factory methods to construct.
  private DefaultEnglishAnalyzer(CharArraySet stopwords) {
    super(stopwords);
    this.stem = false;
    this.stemmer = null;
  }

  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer source = new StandardTokenizer();
    TokenStream result;
    result = source;
    result = new EnglishPossessiveFilter(result);
    result = new LowerCaseFilter(result);
    result = new StopFilter(result, this.stopwords);

    if (!this.stemExclusionSet.isEmpty()) {
      result = new SetKeywordMarkerFilter(result, this.stemExclusionSet);
    }

    if (stem) {
      if (this.stemmer.compareToIgnoreCase("porter") == 0 ||
          this.stemmer.compareToIgnoreCase("p") == 0) {
        result = new PorterStemFilter(result);
      } else if (this.stemmer.compareToIgnoreCase("krovetz") == 0 ||
          this.stemmer.compareToIgnoreCase("k") == 0) {
        result = new KStemFilter(result);
      }
    }

    return new TokenStreamComponents(source, result);
  }

  protected TokenStream normalize(String fieldName, TokenStream in) {
    TokenStream result = in;
    result = new LowerCaseFilter(result);
    return result;
  }

  /**
   * Creates a new instance with all defaults: Porter stemming, Lucene's default stopwords.
   *
   * @return analyzer as configured
   */
  public static final DefaultEnglishAnalyzer newDefaultInstance() {
    return new DefaultEnglishAnalyzer("porter", EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
  }

  /**
   * Creates a new instance with a specified stemmer and Lucene's default stopwords.
   *
   * @param stemmer either "porter" or "krovetz"
   * @return analyzer as configured
   */
  public static final DefaultEnglishAnalyzer newStemmingInstance(String stemmer) {
    return new DefaultEnglishAnalyzer(stemmer, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
  }

  /**
   * Creates a new instance with a specified stemmer and a custom stopwords list.
   *
   * @param stemmer either "porter" or "krovetz"
   * @param stopwords stopwords list
   * @return analyzer as configured
   */
  public static final DefaultEnglishAnalyzer newStemmingInstance(String stemmer, CharArraySet stopwords) {
    return new DefaultEnglishAnalyzer(stemmer, stopwords);
  }

  /**
   * Creates a new instance that does not stem but filters based on Lucene's default stopwords.
   *
   * @return analyzer as configured
   */
  public static final DefaultEnglishAnalyzer newNonStemmingInstance() {
    return new DefaultEnglishAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
  }

  /**
   * Creates a new instance that does not stem and filters based on a custom stopwords list.
   *
   * @param stopwords stopwords list
   * @return analyzer as configured
   */
  public static final DefaultEnglishAnalyzer newNonStemmingInstance(CharArraySet stopwords) {
    return new DefaultEnglishAnalyzer(stopwords);
  }

  /**
   * Creates an analyzer given common command line arguments.
   * @param stemmer either "porter" or "krovetz"
   * @param keepStopwords flag that allows keeping all stopwords. If true, then stopwordsFile must be null.
   * @param stopwordsFile a text file with one stopword per line. If null, the default stopwords set is used.
   *
   * @return analyzer as configured
   * @throws IOException if there's an error reading the stopwords file
   */
  public static DefaultEnglishAnalyzer fromArguments(String stemmer, boolean keepStopwords, String stopwordsFile) throws IOException {
    if (keepStopwords) {
      assert stopwordsFile == null;
      return DefaultEnglishAnalyzer.newStemmingInstance(stemmer, CharArraySet.EMPTY_SET);
    } else if (stopwordsFile != null) {
      List<String> stopWords = FileUtils.readLines(new File(stopwordsFile), "utf-8");
      CharArraySet stopWordsSet = new CharArraySet(stopWords, false);
      return DefaultEnglishAnalyzer.newStemmingInstance(stemmer, CharArraySet.unmodifiableSet(stopWordsSet));
    } else {
      return DefaultEnglishAnalyzer.newStemmingInstance(stemmer);
    }
  }
}
