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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Sequential Dependence term dependence model
 */
public class SequentialDependenceModel<T> implements FeatureExtractor<T> {
  private static final Logger LOG = LogManager.getLogger(SequentialDependenceModel.class);

  private static final String NAME = "SDM";
  private static final int WINDOW_SIZE = 8;

  private float lambdaT = 0.5f;
  private float lambdaO = 0.2f;
  private float lambdaU = 0.3f;

  public SequentialDependenceModel(float lambdaT, float lambdaO, float lambdaU) {
    this.lambdaT = lambdaT;
    this.lambdaO = lambdaO;
    this.lambdaU = lambdaU;
  }

  private float computeUnorderedFrequencyScore(Document doc, Terms terms, RerankerContext<T> context) throws IOException {
    List<String> queryTokens = context.getQueryTokens();

    // Construct token stream with offset 0
    TokenStream stream = new TokenStreamFromTermVector(terms, 0);
    CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);
    Map<String, String> queryPairMap = new HashMap<>();
    Map<String, Integer> phraseCountMap = new HashMap<>();
    Map<String, Integer> singleCountMap = new HashMap<>();

    // Construct a count map and a map of phrase pair x y, x->y
    for (int i = 0; i < queryTokens.size() -1; i++)  {
      queryPairMap.put(queryTokens.get(i), queryTokens.get(i+1));
      phraseCountMap.put(queryTokens.get(i), 0);
      // This will serve as our smoothing param
      singleCountMap.put(queryTokens.get(i), 1);
    }

    int docSize = 0;
    // We will maintain a fifo queue of window size
    LinkedList<String> window = new LinkedList<>();
    while (stream.incrementToken() && docSize <= WINDOW_SIZE * 2) {
      // First construct the window that we need to test on
      docSize ++;
      String token = termAttribute.toString();
      window.add(token);
    }

    // Now we can construct counts for up to index WINDOW_SIZE -1
    // But we need to account for the case when the tokenstream just doesn't have that many tokens
    for (int i = 0; i < Math.min(WINDOW_SIZE -1, docSize); i++) {
      String firstToken = window.get(i);
      if (queryPairMap.containsKey(firstToken) && window.contains(queryPairMap.get(firstToken))) {
        phraseCountMap.put(firstToken, phraseCountMap.get(firstToken) + 1);
        singleCountMap.put(firstToken, singleCountMap.get(firstToken) + 1);
      }
    }

    // Now we continue
    while (stream.incrementToken()) {
      docSize ++;
      String token = termAttribute.toString();
      window.add(token);
      // Move the window along
      // The window at this point is guaranteed to be of size WINDOW_SIZE * 2 because of the previous loop
      // if there are not enough tokens this would not even execute
      window.removeFirst();
      // Now test for the phrase at the test index WINDOW_SIZE -1
      String firstToken = window.get(WINDOW_SIZE-1);
      if (queryPairMap.containsKey(firstToken) && window.contains(queryPairMap.get(firstToken))) {
        phraseCountMap.put(firstToken, phraseCountMap.get(firstToken) + 1);
        singleCountMap.put(firstToken, singleCountMap.get(firstToken) + 1);
      }
    }

    float score = 0.0f;
    // Smoothing count of 1
    docSize ++;
    for (String queryToken : phraseCountMap.keySet()) {
      float countToUse = phraseCountMap.get(queryToken);
      if (countToUse == 0) {
        countToUse = singleCountMap.get(queryToken);
      }
      score += Math.log(countToUse/ (float) docSize);
    }

    return score;
  }

  private float computeOrderedFrequencyScore(Document doc, Terms terms, RerankerContext<T> context) throws IOException {
    List<String> queryTokens = context.getQueryTokens();
    Map<String, String> queryPairMap = new HashMap<>();
    Map<String, Integer> phraseCountMap = new HashMap<>();
    Map<String, Integer> singleCountMap = new HashMap<>();

    // Construct a count map and a map of phrase pair x y, x->y
    for (int i = 0; i < queryTokens.size() -1; i++)  {
      queryPairMap.put(queryTokens.get(i), queryTokens.get(i+1));
      phraseCountMap.put(queryTokens.get(i), 0);
      // This will serve as our smoothing param
      singleCountMap.put(queryTokens.get(i), 1);
    }

    // Construct token stream with offset 0
    TokenStream stream = new TokenStreamFromTermVector(terms, 0);
    CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);
    float docSize = 0.0f;
    // Use these to track which token we need to see to increment count
    // count tracked on the first token
    String expectedToken = "";
    String tokenToIncrement = "";
    while (stream.incrementToken()) {
      docSize++;
      String token = termAttribute.toString();
      if (token.equalsIgnoreCase(expectedToken)) {
        phraseCountMap.put(tokenToIncrement, phraseCountMap.get(tokenToIncrement) + 1);
      }

      // Check now if this token could be the start of an ordered phrase
      if (queryPairMap.containsKey(token)) {
        expectedToken = queryPairMap.get(token);
        singleCountMap.put(token, singleCountMap.get(token) + 1);
        tokenToIncrement = token;
      } else {
        expectedToken = "";
        tokenToIncrement = "";
      }
    }
    float score = 0.0f;
    // Smoothing count of 1
    docSize ++;
    for (String queryToken : phraseCountMap.keySet()) {
      score += Math.log((float) (phraseCountMap.get(queryToken) + 1) / docSize);
    }

    return score;
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

    TokenStream stream = new TokenStreamFromTermVector(terms, 0);
    CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);

    float docSize =0;
    // Count all the tokens
    while (stream.incrementToken()) {
      docSize++;
      String token = termAttribute.toString();
      if (termCount.containsKey(token)) {
        termCount.put(token, termCount.get(token) + 1);
      } else {
        termCount.put(token, 1);
      }
    }
    float score = 0.0f;
    // Smoothing count of 1
    docSize ++;
    // Only compute the score for what's in term count all else 0
    for (String queryToken : termCount.keySet()) {
      score += Math.log((float) (termCount.get(queryToken)  +1) / docSize);
    }
    return score;
  }

  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    float orderedWindowScore = 0.0f;
    float unorderedDependenceScore = 0.0f;
    float independentScore = 0.0f;

    try {
      independentScore = computeFullIndependenceScore(doc, terms, context);
      orderedWindowScore = computeOrderedFrequencyScore(doc, terms, context);
      unorderedDependenceScore = computeUnorderedFrequencyScore(doc, terms, context);
      LOG.debug(String.format("independent: %f, ordered: %f, unordered: %f", independentScore, orderedWindowScore, unorderedDependenceScore));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lambdaT * independentScore + lambdaO * orderedWindowScore + lambdaU * unorderedDependenceScore;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
