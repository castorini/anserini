/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AutoCompositeAnalyzer {
  private static final Logger LOG = LogManager.getLogger(CompositeAnalyzer.class);

  private static final Map<String, String> tokenizerMap = new HashMap<>() {
    {
      put("ar", "castorini/bert-base-uncased-arabic-cc");
      put("bn", "castorini/bert-base-uncased-bengali-cc");
      put("en", "bert-base-multilingual-uncased");
      put("fa", "castorini/bert-base-uncased-persian-cc");
      put("fi", "castorini/bert-base-uncased-finnish-cc");
      put("hi", "castorini/bert-base-uncased-hindi-cc");
      put("id", "castorini/bert-base-uncased-indonesian-cc");
      put("ja", "castorini/bert-base-uncased-japanese-cc");
      put("ko", "castorini/bert-base-uncased-korean-cc");
      put("ru", "castorini/bert-base-uncased-russian-cc");
      put("sw", "castorini/bert-base-uncased-swahili-cc");
      put("te", "castorini/bert-base-uncased-telugu-cc");
      put("th", "castorini/bert-base-uncased-thai-cc");
      put("yo", "castorini/bert-base-uncased-yoruba-cc");
    }
  };

  public static Analyzer getAnalyzer(String language) throws IOException {
    return getAnalyzer(language, null);
  }

  public static Analyzer getAnalyzer(String language, String analyzeWithHuggingFaceTokenizer) throws IOException {
    final Analyzer languageSpecificAnalyzer;
    String hfTokenizer = null;

    if (AnalyzerMap.analyzerMap.containsKey(language)) {
      languageSpecificAnalyzer = AnalyzerMap.getLanguageSpecificAnalyzer(language);
    } else if (language.equals("en")) {
      languageSpecificAnalyzer = DefaultEnglishAnalyzer.fromArguments("porter", false, null);
    } else {
      languageSpecificAnalyzer = new WhitespaceAnalyzer();
    }

    if (analyzeWithHuggingFaceTokenizer == null && tokenizerMap.containsKey(language)) {
      hfTokenizer = tokenizerMap.get(language);
    } else {
      hfTokenizer = analyzeWithHuggingFaceTokenizer;
    }
    
    if (languageSpecificAnalyzer.getClass().getName().equals("org.apache.lucene.analysis.core.WhitespaceAnalyzer")) {
      if (hfTokenizer == null ) {
        // Case (3): No Lucene analyzer exists & no monolingual tokenizer exists
        String message = "Using CompositeAnalyzer with HF Tokenizer: %s & Analyzer %s";
        LOG.info(String.format(message, "bert-base-multilingual-uncased", languageSpecificAnalyzer.getClass().getName()));
        return new CompositeAnalyzer("bert-base-multilingual-uncased", languageSpecificAnalyzer);
      } else {
        // Case (2): No Lucene analyzer but monolingual tokenizer exists
        LOG.info("Using HF Tokenizer: " + hfTokenizer);
        return new HuggingFaceTokenizerAnalyzer(hfTokenizer);
      }
    } else {
      if (hfTokenizer == null ) {
        // Case (4): Lucene analyzer exists but no monolingual tokenizer
        LOG.info("Using language-specific analyzer");
        LOG.info("Language: " + language);
        return languageSpecificAnalyzer;
      } else {
        // Case (1): Both Lucene analyzer & monolingual tokenizer exist
        String message = "Using CompositeAnalyzer with HF Tokenizer: %s & Analyzer %s";
        LOG.info(String.format(message, hfTokenizer, languageSpecificAnalyzer.getClass().getName()));
        return new CompositeAnalyzer(hfTokenizer, languageSpecificAnalyzer);
      }
    }
  }
}
