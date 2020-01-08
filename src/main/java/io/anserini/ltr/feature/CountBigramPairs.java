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

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.highlight.TokenStreamFromTermVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Static method that will count bigram pairs since this is used
 * by a couple of features
 */
public class CountBigramPairs {
  public static class PhraseCounter {
    public Map<String, Integer> phraseCountMap = new HashMap<>();

    public void incrementCount(String firstToken) {
      if (phraseCountMap.containsKey(firstToken)) {
        phraseCountMap.put(firstToken, phraseCountMap.get(firstToken) + 1);
      } else {
        phraseCountMap.put(firstToken, 1);
      }
    }
  }

  /**
   * Method will count coocurrence of pairs specified in queryPairMap
   * and store counts for each window size in counters.
   * NOTE method mutates inputs
   * @param singleCountMap    a count of single tokens as we encounter them, useful if any smoothing
   * @param queryPairMap      all pairs of strings we are looking for
   * @param gapSizes          list of window sizes to compute for
   * @param counters          window size to counter map
   * @param terms             query terms
   * @throws IOException if error encountered reading index
   */
  public static void countPairs(Map<String, Integer> singleCountMap, Map<String, Set<String>> queryPairMap,
                                ArrayList<Integer> gapSizes,
                                Map<Integer, PhraseCounter> counters,
                                Terms terms) throws IOException {
    countPairs(singleCountMap, queryPairMap, Collections.<String, Set<String>>emptyMap(), gapSizes, counters, terms);
  }

    /**
     * Method will count coocurrence of pairs specified in queryPairMap
     * and store counts for each window size in counters
     * NOTE method mutates inputs
     * @param singleCountMap    a count of single tokens as we encounter them, useful if any smoothing
     * @param queryPairMap      all pairs of strings we are looking for
     * @param backQueryPairMap  all pairs of reverse pairs, ei if query is test query, this would include query test
     * @param gapSizes          list of window sizes to compute for
     * @param counters          Window size to counter map
     * @param terms             query terms
     * @throws IOException if error encountered reading index
     */
  public static void countPairs(Map<String, Integer> singleCountMap, Map<String, Set<String>> queryPairMap,
                                Map<String, Set<String>> backQueryPairMap,
                                ArrayList<Integer> gapSizes,
                                Map<Integer, PhraseCounter> counters,
                                Terms terms) throws IOException {

    // Construct token stream with offset 0
    TokenStreamFromTermVector stream = new TokenStreamFromTermVector(terms, -1);
    CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);

    int docSize = 0;
    int maxGapSize = 0;
    for (Integer windowSize : gapSizes) {
      if (windowSize > maxGapSize) {
        maxGapSize = windowSize;
      }
    }

    // We will maintain a fifo queue of window size
    LinkedList<String> window = new LinkedList<>();
    // add to the window first and process the first tokens
    stream.reset();
    while (docSize < maxGapSize * 2 +2 && stream.incrementToken()) {
      // First construct the window that we need to test on
      docSize++;
      String token = termAttribute.toString();
      window.add(token);
    }

    // Now we can construct counts for up to index WINDOW_SIZE
    // But we need to account for the case when the tokenstream just doesn't have that many tokens
    for (int i = 0; i < Math.min(maxGapSize + 1, docSize); i++) {
      String firstToken = window.get(i);
      // Look ahead for token
      if (queryPairMap.containsKey(firstToken)) {
        // Count unigram for this token
        singleCountMap.put(firstToken, singleCountMap.get(firstToken) + 1);
        for (int j = i + 1; j < Math.min(i + maxGapSize + 1, docSize); j++) {
          if (queryPairMap.get(firstToken).contains(window.get(j))) {
            for (int windowSize : counters.keySet()) {
              if (j - i <= windowSize) counters.get(windowSize).incrementCount(firstToken);
            }
          }
        }
      }

      if (backQueryPairMap.containsKey(firstToken)) {
        // Count unigram for this token
        for (int j = i+1; j < Math.min(i + maxGapSize + 1, docSize); j++) {
          if (backQueryPairMap.get(firstToken).contains(window.get(j))) {
            for (int windowSize : counters.keySet()) {
              if (j - i <= windowSize) counters.get(windowSize).incrementCount(window.get(j));
            }
          }
        }
      }
    }

    // Now we continue
    while (stream.incrementToken()) {
      docSize++;
      String token = termAttribute.toString();
      window.add(token);
      // Move the window along
      // The window at this point is guaranteed to be of size WINDOW_SIZE * 2 because of the previous loop
      // if there are not enough tokens this would not even execute
      window.removeFirst();
      // Now test for the phrase at the test index WINDOW_SIZE
      String firstToken = window.get(maxGapSize);
      if (queryPairMap.containsKey(firstToken)) {
        // Count unigram for this token
        singleCountMap.put(firstToken, singleCountMap.get(firstToken) + 1);
        for (int j = maxGapSize +1; j < maxGapSize*2 + 2; j++) {
          if (queryPairMap.get(firstToken).contains(window.get(j))) {
            for (int windowSize : counters.keySet()) {
              if (j - maxGapSize<= windowSize) counters.get(windowSize).incrementCount(firstToken);
            }
          }
        }
      }

      if (backQueryPairMap.containsKey(firstToken)) {
        // Count unigram for this token
        for (int j = maxGapSize +1; j < maxGapSize*2 + 2; j++) {

          if (backQueryPairMap.get(firstToken).contains(window.get(j))) {
            for (int windowSize : counters.keySet()) {
              if (j - maxGapSize<= windowSize) counters.get(windowSize).incrementCount(window.get(j));
            }
          }
        }
      }

    }

    // Now we do the tail end of the window, now that no more tokens are added:
    // the unprocessed portion is the last maxGap + 1 -> end
    for (int i = maxGapSize +1; i < Math.min(maxGapSize*2 + 1,docSize); i++) {
      String firstToken = window.get(i);
      if (queryPairMap.containsKey(firstToken)) {
        // Count unigram for this token
        singleCountMap.put(firstToken, singleCountMap.get(firstToken) + 1);
        for (int j = i +1; j < Math.min(maxGapSize*2 + 2,docSize) ; j++) {
          if (queryPairMap.get(firstToken).contains(window.get(j))) {
            for (int windowSize : counters.keySet()) {
              if (j -i <= windowSize) counters.get(windowSize).incrementCount(firstToken);
            }
          }
        }
      }

      if (backQueryPairMap.containsKey(firstToken)) {
        // Count unigram for this token
        for (int j = i +1; j < Math.min(maxGapSize*2 + 2,docSize); j++) {

          if (backQueryPairMap.get(firstToken).contains(window.get(j))) {
            for (int windowSize : counters.keySet()) {
              if (j - i<= windowSize) counters.get(windowSize).incrementCount(window.get(j));
            }
          }
        }
      }
    }

    stream.end();
    stream.close();
  }



}
