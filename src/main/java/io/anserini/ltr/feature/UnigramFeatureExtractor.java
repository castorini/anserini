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

package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.highlight.TokenStreamFromTermVector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Counts unigrams
 */
public class UnigramFeatureExtractor<T> implements FeatureExtractor<T> {
  private static final Logger LOG = LogManager.getLogger(UnigramFeatureExtractor.class);

  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    try {
      return computeFullIndependenceScore(doc, terms, context);
    } catch (IOException e) {
      LOG.error("IOException while counting unigrams, returning 0");
      return 0.0f;
    }
  }

  /**
   * The single term scoring function: lambda* log( (1-alpha) tf/ |D|)
   * @param doc
   * @param terms
   * @param context
   * @return
   */
  private float computeFullIndependenceScore(Document doc, Terms terms, RerankerContext<T> context) throws IOException {
    // tf can be calculated by iterating over terms, number of times a term occurs in doc
    // |D| total number of terms can be calculated by iterating over stream
    IndexReader reader = context.getIndexSearcher().getIndexReader();
    List<String> queryTokenList = context.getQueryTokens();
    Map<String, Integer> termCount = new HashMap<>();

    for (String queryToken : queryTokenList) {
      termCount.put(queryToken, 0);
    }
    TokenStream stream = new TokenStreamFromTermVector(terms, -1);
    CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);

    stream.reset();
    float docSize =0;
    // Count all the tokens
    while (stream.incrementToken()) {
      docSize++;
      String token = termAttribute.toString();
      if (termCount.containsKey(token)) {
        termCount.put(token, termCount.get(token) + 1);
      }
    }
    float score = 0.0f;
    // Smoothing count of 1
    docSize++;
    // Only compute the score for what's in term count all else 0
    for (String queryToken : termCount.keySet()) {
      score += termCount.get(queryToken);
    }

    stream.end();
    stream.close();
    return score;
  }

  @Override
  public String getName() {
    return "UnigramsFeatureExtractor";
  }
}
